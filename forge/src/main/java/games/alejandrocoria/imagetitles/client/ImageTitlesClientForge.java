package games.alejandrocoria.imagetitles.client;

import games.alejandrocoria.imagetitles.ImageTitles;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ImageTitlesClientForge {
    public static void clientSetup(FMLJavaModLoadingContext context) {
        ImageTitles.init();

        RegisterClientReloadListenersEvent.BUS.addListener(event ->
            event.registerReloadListener(new SimplePreparableReloadListener<>() {
                @Override
                protected Object prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
                    ImageTitles.loadImageFiles(resourceManager);
                    return null;
                }

                @Override
                protected void apply(Object o, ResourceManager resourceManager, ProfilerFiller profilerFiller) {

                }
            })
        );

        TickEvent.ClientTickEvent.Post.BUS.addListener(event -> ImageTitles.announceDeprecated());
    }
}
