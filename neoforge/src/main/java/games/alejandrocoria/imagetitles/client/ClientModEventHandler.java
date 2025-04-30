package games.alejandrocoria.imagetitles.client;

import games.alejandrocoria.imagetitles.Constants;
import games.alejandrocoria.imagetitles.ImageTitles;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ClientModEventHandler {
    public static void registerClientReloadListenersEvent(AddClientReloadListenersEvent event) {
        event.addListener(
                ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "titles_reload_listener"),
                new SimplePreparableReloadListener<>() {
            @Override
            protected Object prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
                ImageTitles.loadImageFiles(resourceManager);
                return null;
            }

            @Override
            protected void apply(Object o, ResourceManager resourceManager, ProfilerFiller profilerFiller) {

            }
        });
    }
}
