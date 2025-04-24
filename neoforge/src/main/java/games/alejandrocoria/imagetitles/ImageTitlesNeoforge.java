package games.alejandrocoria.imagetitles;


import games.alejandrocoria.imagetitles.client.ClientModEventHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class ImageTitlesNeoforge {
    public ImageTitlesNeoforge(IEventBus eventBus) {
        ImageTitles.init();

        eventBus.addListener(ClientModEventHandler::registerClientReloadListenersEvent);
    }
}