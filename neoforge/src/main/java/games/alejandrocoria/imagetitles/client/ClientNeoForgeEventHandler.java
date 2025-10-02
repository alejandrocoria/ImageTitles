package games.alejandrocoria.imagetitles.client;

import games.alejandrocoria.imagetitles.ImageTitles;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ClientNeoForgeEventHandler {
    public static void entityJoinLevelEvent(EntityJoinLevelEvent event) {
        if (event.getEntity() == Minecraft.getInstance().player) {
            ImageTitles.announceDeprecated();
        }
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        ImageTitles.announceDeprecated();
    }
}
