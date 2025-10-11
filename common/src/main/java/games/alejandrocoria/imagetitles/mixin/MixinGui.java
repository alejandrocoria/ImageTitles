package games.alejandrocoria.imagetitles.mixin;

import games.alejandrocoria.imagetitles.ImageTitles;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.profiling.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Gui.class)
public class MixinGui {
    @Inject(at = @At("TAIL"),
            method = "setTitle")
    private void setTitle(Component title, CallbackInfo ci) {
        ImageTitles.setCurrent(title);
    }

    @Inject(at = @At(value = "INVOKE",
                    target="Lnet/minecraft/util/Mth;clamp(III)I",
                    shift = At.Shift.AFTER),
            method = "renderTitle",
            locals = LocalCapture.CAPTURE_FAILSOFT,
            cancellable = true)
    private void renderTitle(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci, Font font, float f, int i) {
        boolean imageRendered = ImageTitles.renderImage(guiGraphics, i);

        if (imageRendered) {
            ci.cancel();
            Profiler.get().pop();
        }
    }
}
