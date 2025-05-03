package games.alejandrocoria.imagetitles;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ImageTitlesFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ImageTitles.init();

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleResourceReloadListener<>() {
            @Override
            public ResourceLocation getFabricId() {
                return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "titles_reload_listener");
            }

            @Override
            public CompletableFuture<Object> load(ResourceManager resourceManager, ProfilerFiller profiler, Executor executor) {
                return CompletableFuture.supplyAsync(() -> {
                    ImageTitles.loadImageFiles(resourceManager);
                    return null;
                }, executor);
            }

            @Override
            public CompletableFuture<Void> apply(Object data, ResourceManager manager, ProfilerFiller profiler, Executor executor) {
                return CompletableFuture.supplyAsync(() -> null);
            }
        });
    }
}
