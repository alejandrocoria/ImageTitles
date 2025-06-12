package games.alejandrocoria.imagetitles;

import games.alejandrocoria.imagetitles.client.ClientModEventHandler;
import games.alejandrocoria.imagetitles.client.ClientNeoForgeEventHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(Constants.MOD_ID)
public class ImageTitlesNeoforge {
    public ImageTitlesNeoforge(IEventBus eventBus) {
        ImageTitles.init();

        eventBus.addListener(ClientModEventHandler::registerClientReloadListenersEvent);
        NeoForge.EVENT_BUS.addListener(ClientNeoForgeEventHandler::entityJoinLevelEvent);
    }
}
