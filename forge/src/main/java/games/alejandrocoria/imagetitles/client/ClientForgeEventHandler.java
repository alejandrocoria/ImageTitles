package games.alejandrocoria.imagetitles.client;

import games.alejandrocoria.imagetitles.Constants;
import games.alejandrocoria.imagetitles.ImageTitles;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeEventHandler {
    @SubscribeEvent
    public static void entityJoinLevelEvent(EntityJoinLevelEvent event) {
        if (event.getEntity() == Minecraft.getInstance().player) {
            ImageTitles.announceDeprecated();
        }
    }
}
