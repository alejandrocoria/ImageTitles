package games.alejandrocoria.imagetitles.mixin;

import games.alejandrocoria.imagetitles.ImageTitles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ForgeGui.class)
public class ForgeMixinGui {
    @ModifyVariable(method = "renderTitle",
                    at = @At(value = "INVOKE",
                            target = "Lnet/minecraft/util/Mth;clamp(III)I",
                            shift = At.Shift.BY,
                            by = 2),
                    ordinal = 2)
    private int getKi(int opacity) {
        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        boolean imageRendered = ImageTitles.renderImage(new GuiGraphics(Minecraft.getInstance(), buffers), opacity);

        if (imageRendered) {
            return 0;
        }
        return opacity;
    }
}
