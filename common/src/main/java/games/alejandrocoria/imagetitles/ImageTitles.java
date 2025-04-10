package games.alejandrocoria.imagetitles;

import com.google.gson.Gson;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageTitles {
    private static Map<String, ResourceLocation> images = new HashMap<>();
    private static ResourceLocation current = null;
    private static int width;
    private static int height;

    public static void init() {
        images.put("test", ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/test.png"));
    }

    public static void loadImageFiles(ResourceManager manager, List<ResourceLocation> files) {
        Gson gson = new Gson();
        for (ResourceLocation location : files) {
            try {
                Resource resource = manager.getResourceOrThrow(location);
                TitleJson titleJson = gson.fromJson(resource.openAsReader(), TitleJson.class);
                ResourceLocation imagePath = ResourceLocation.fromNamespaceAndPath(location.getNamespace(), location.getPath().replace(".mcdata", ".png"));
                Minecraft.getInstance().getTextureManager().getTexture(imagePath);
                images.put(titleJson.title, imagePath);
            } catch (FileNotFoundException e) {
            } catch (IOException e) {
            }
        }
    }

    public static void setCurrent(Component title) {
        current = images.get(title.getString());
        AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(current);
        texture.getId();

    }

    public static boolean renderImage(GuiGraphics guiGraphics, int alpha) {
        if (current == null) {
            return false;
        }

        int color = 0xFFFFFF | (alpha << 24);
        int width = 300;
        int height = 300;
        int x = (guiGraphics.guiWidth() - width) / 2;
        int y = (guiGraphics.guiHeight() / 2 - height) / 2;
        guiGraphics.blit(RenderType::guiTextured, current, x, y, 0, 0, width, height, width, height, color);
        return true;
    }

    static class TitleJson {
        String title;
    }
}
