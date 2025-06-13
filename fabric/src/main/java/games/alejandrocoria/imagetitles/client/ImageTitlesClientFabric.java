package games.alejandrocoria.imagetitles.client;

import games.alejandrocoria.imagetitles.Constants;
import games.alejandrocoria.imagetitles.ImageTitles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ImageTitlesClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ImageTitles.init();

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleResourceReloadListener<>() {
            @Override
            public ResourceLocation getFabricId() {
                return ResourceLocation.tryBuild(Constants.MOD_ID, "titles_reload_listener");
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

        ClientEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity == Minecraft.getInstance().player) {
                ImageTitles.announceDeprecated();
            }
        });
    }
}
