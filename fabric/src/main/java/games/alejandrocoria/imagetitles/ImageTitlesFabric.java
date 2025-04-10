package games.alejandrocoria.imagetitles;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ImageTitlesFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        ImageTitles.init();

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return ResourceLocation.fromNamespaceAndPath("imagetitles", "titles");
            }

            @Override
            public CompletableFuture<Void> load(ResourceManager resourceManager, Executor executor) {
                return CompletableFuture.supplyAsync(() -> {
                    List<ResourceLocation> files = new ArrayList<>();
                    files.addAll(resourceManager.listResources("textures/title", path -> path.getPath().endsWith(".mcdata")).keySet());
                    ImageTitles.loadImageFiles(resourceManager, files);
                    return null;
                }, executor);
            }

            @Override
            public CompletableFuture<Void> apply(Object o, ResourceManager resourceManager, Executor executor) {
                return CompletableFuture.supplyAsync(() -> null);
            }
        });
    }
}
