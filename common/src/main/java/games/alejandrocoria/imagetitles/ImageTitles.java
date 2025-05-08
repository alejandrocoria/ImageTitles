package games.alejandrocoria.imagetitles;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.joml.Matrix4f;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageTitles {
    private static final Map<String, TitleData> images = new HashMap<>();
    private static TitleData current = null;

    public static void init() {
        Constants.LOG.info("imageTitles init");
    }

    public static void loadImageFiles(ResourceManager resourceManager) {
        clearAllImages();

        List<ResourceLocation> files = new ArrayList<>(resourceManager.listResources("textures/title", path -> path.getPath().endsWith(".png.mcdata")).keySet());

        Gson gson = new Gson();
        for (ResourceLocation location : files) {
            try {
                Resource resource = resourceManager.getResourceOrThrow(location);
                TitleJson titleJson = gson.fromJson(resource.openAsReader(), TitleJson.class);
                ResourceLocation imagePath = ResourceLocation.tryBuild(location.getNamespace(), location.getPath().replace(".png.mcdata", ".png"));
                images.put(titleJson.title, new TitleData(imagePath, titleJson.x, titleJson.y, titleJson.width, titleJson.height));
            } catch (IOException e) {
                Constants.LOG.error("Error loading file \"{}\": {}", location, e);
            } catch (JsonSyntaxException e) {
                Constants.LOG.error("Error loading json data in file \"{}\": {}", location, e);
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
}
