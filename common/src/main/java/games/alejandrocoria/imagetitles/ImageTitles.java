package games.alejandrocoria.imagetitles;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.joml.Matrix4f;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ImageTitles {
    private static final String EXTENSION_V1 = ".png.mcdata";

    private static final Map<String, TitleData> images = new HashMap<>();
    private static TitleData current = null;
    private static boolean needAnnounceDeprecated = false;

    public static void init() {
        Constants.LOG.info("imageTitles init");
    }

    public static void loadImageFiles(ResourceManager resourceManager) {
        clearAllImages();

        List<ResourceLocation> files = new ArrayList<>(resourceManager.listResources("textures/title",
                path -> path.getPath().endsWith(EXTENSION_V1) || path.getPath().endsWith(".png")).keySet());

        List<ResourceLocation> v1Resources = new ArrayList<>();
        Gson gson = new Gson();
        MetadataSerializer metadataSerializer = new MetadataSerializer(gson);
        for (ResourceLocation location : files) {
            try {
                Resource resource = resourceManager.getResourceOrThrow(location);

                TitleJson titleJson;
                if (location.getPath().endsWith(EXTENSION_V1)) {
                    titleJson = gson.fromJson(resource.openAsReader(), TitleJson.class);
                    v1Resources.add(location);
                } else {
                    Optional<TitleJson> titleJsonOpt = resource.metadata().getSection(metadataSerializer);
                    if (titleJsonOpt.isPresent()) {
                        titleJson = titleJsonOpt.get();
                    } else {
                        continue;
                    }
                }

                ResourceLocation imagePath = ResourceLocation.tryBuild(location.getNamespace(), location.getPath().replace(EXTENSION_V1, ".png"));
                images.put(titleJson.title, new TitleData(imagePath, titleJson.x, titleJson.y, titleJson.width, titleJson.height));
            } catch (IOException e) {
                Constants.LOG.error("Error loading file \"{}\": {}", location, e);
            } catch (JsonSyntaxException e) {
                Constants.LOG.error("Error loading json data in file \"{}\": {}", location, e);
            }
        }

        if (!v1Resources.isEmpty()) {
            needAnnounceDeprecated = true;
            Constants.LOG.warn("Resources with metadata in .mcdata files were detected. This is deprecated; please upgrade to version 2: https://github.com/alejandrocoria/ImageTitles-Example");
            Constants.LOG.warn("Resources: {}", v1Resources);
            announceDeprecated();
        }
    }

    public static void announceDeprecated() {
        if (needAnnounceDeprecated) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                MutableComponent prefix = Component.literal("ImageTitles: ").withStyle(style -> style.withBold(true).withColor(0xFFFFFF00));
                MutableComponent message = Component.literal("Resources with metadata in .mcdata files were detected. This is deprecated; please upgrade to version 2: ").withStyle(style -> style.withBold(false).withColor(0xFFFFFFFF));
                MutableComponent button = Component.literal("https://github.com/alejandrocoria/ImageTitles-Example");
                button.withStyle(style -> style.withUnderlined(true).withBold(false).withColor(0xFFFFFFFF));
                button.withStyle(style -> style
                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/alejandrocoria/ImageTitles-Example")));

                player.sendSystemMessage(prefix.append(message).append(button));
                needAnnounceDeprecated = false;
            }
        }
    }

    public static void setCurrent(Component title) {
        current = images.get(title.getString());
    }

    public static void clearAllImages() {
        images.clear();
        current = null;
    }

    public static boolean renderImage(GuiGraphics guiGraphics, int alpha) {
        if (current == null) {
            return false;
        }

        guiGraphics.pose().pushPose();

        float guiScale = (float) Minecraft.getInstance().getWindow().getGuiScale();
        guiGraphics.pose().scale(1.f / guiScale, 1.f / guiScale, 1.f);

        int color = 0xFFFFFF | (alpha << 24);
        int width = current.width;
        int height = current.height;
        int x = (int) (guiGraphics.guiWidth() * guiScale * current.x - width / 2.f);
        int y = (int) (guiGraphics.guiHeight() * guiScale * current.y - height / 2.f);

        RenderSystem.setShaderTexture(0, current.texture);
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        RenderSystem.enableBlend();
        Matrix4f matrix4f = guiGraphics.pose().last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
        bufferbuilder.vertex(matrix4f, x, y, 0).color(color).uv(0, 0).endVertex();
        bufferbuilder.vertex(matrix4f, x, y + height, 0).color(color).uv(0, 1).endVertex();
        bufferbuilder.vertex(matrix4f, x + width, y + height, 0).color(color).uv(1, 1).endVertex();
        bufferbuilder.vertex(matrix4f, x + width, y, 0).color(color).uv(1, 0).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
        RenderSystem.disableBlend();

        guiGraphics.pose().popPose();
        return true;
    }


    static class TitleJson {
        String title;
        float x = 0.5f;
        float y = 0.25f;
        int width;
        int height;
    }

    static class TitleData {
        public ResourceLocation texture;
        float x;
        float y;
        public int width;
        public int height;

        public TitleData(ResourceLocation texture, float x, float y, int width, int height) {
            this.texture = texture;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }

    @MethodsReturnNonnullByDefault
    static class MetadataSerializer implements MetadataSectionSerializer<TitleJson> {
        private final Gson gson;

        public MetadataSerializer(Gson gson) {
            this.gson = gson;
        }

        public String getMetadataSectionName() {
            return Constants.MOD_ID;
        }

        public TitleJson fromJson(JsonObject json) {
            return gson.fromJson(json, TitleJson.class);
        }
    }
}
