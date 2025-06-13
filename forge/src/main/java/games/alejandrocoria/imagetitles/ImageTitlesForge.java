package games.alejandrocoria.imagetitles;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class ImageTitlesForge {
    public ImageTitlesForge() {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ImageTitles::init);
    }
}
