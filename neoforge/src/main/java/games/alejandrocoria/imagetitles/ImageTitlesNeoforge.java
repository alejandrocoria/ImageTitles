package games.alejandrocoria.imagetitles;

import games.alejandrocoria.imagetitles.client.ClientModEventHandler;
import games.alejandrocoria.imagetitles.client.ImageTitlesClientNeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(Constants.MOD_ID)
public class ImageTitlesNeoforge {
    public ImageTitlesNeoforge(IEventBus eventBus) {
        eventBus.addListener(ClientModEventHandler::registerClientReloadListenersEvent);
        eventBus.addListener((FMLClientSetupEvent event) -> ImageTitlesClientNeoForge.clientSetup(event, eventBus));
    }
}
