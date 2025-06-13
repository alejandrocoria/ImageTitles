package games.alejandrocoria.imagetitles.client;

import games.alejandrocoria.imagetitles.ImageTitles;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

public class ImageTitlesClientNeoForge {
    public static void clientSetup(FMLClientSetupEvent event, IEventBus eventBus) {
        ImageTitles.init();

        NeoForge.EVENT_BUS.addListener(ClientNeoForgeEventHandler::entityJoinLevelEvent);
    }
}
