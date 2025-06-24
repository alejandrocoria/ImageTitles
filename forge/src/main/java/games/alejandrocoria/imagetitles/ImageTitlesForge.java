package games.alejandrocoria.imagetitles;

import games.alejandrocoria.imagetitles.client.ImageTitlesClientForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(Constants.MOD_ID)
public class ImageTitlesForge {
    public ImageTitlesForge(FMLJavaModLoadingContext context) {
        if (FMLEnvironment.dist.isClient()) {
            ImageTitlesClientForge.clientSetup(context);
        }
    }
}
