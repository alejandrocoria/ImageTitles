package games.alejandrocoria.imagetitles;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

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
        for (ResourceLocation location : files) {
            try {
                Resource resource = resourceManager.getResourceOrThrow(location);
                ResourceLocation imagePath = ResourceLocation.fromNamespaceAndPath(location.getNamespace(), location.getPath().replace(EXTENSION_V1, ".png"));

                if (location.getPath().endsWith(EXTENSION_V1)) {
                    TitleJson titleJson = gson.fromJson(resource.openAsReader(), TitleJson.class);
                    v1Resources.add(location);
                    images.put(titleJson.title, new TitleData(imagePath, titleJson.x, titleJson.y, titleJson.width, titleJson.height));
                } else {
                    Optional<ImageTitlesMetadataSection> metadataOpt = resource.metadata().getSection(ImageTitlesMetadataSection.TYPE);
                    metadataOpt.ifPresent(metadata -> {
                        images.put(metadata.title, new TitleData(imagePath, metadata.x, metadata.y, metadata.width, metadata.height));
                    });
                }
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

                player.displayClientMessage(prefix.append(message).append(button), false);
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
        guiGraphics.blit(RenderType::guiTextured, current.texture, x, y, 0, 0, width, height, width, height, color);

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
    record ImageTitlesMetadataSection(String title, float x, float y, int width, int height) {
        public static final Codec<ImageTitlesMetadataSection> CODEC = RecordCodecBuilder.create(
                p_377176_ -> p_377176_.group(
                                Codec.STRING.fieldOf("title").forGetter(ImageTitlesMetadataSection::title),
                                Codec.FLOAT.optionalFieldOf("x", 0.5f).forGetter(ImageTitlesMetadataSection::x),
                                Codec.FLOAT.optionalFieldOf("y", 0.25f).forGetter(ImageTitlesMetadataSection::y),
                                Codec.INT.fieldOf("width").forGetter(ImageTitlesMetadataSection::width),
                                Codec.INT.fieldOf("height").forGetter(ImageTitlesMetadataSection::height)
                        )
                        .apply(p_377176_, ImageTitlesMetadataSection::new)
        );
        public static final MetadataSectionType<ImageTitlesMetadataSection> TYPE = new MetadataSectionType<>("imagetitles", CODEC);
    }
}
