package games.alejandrocoria.imagetitles.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import games.alejandrocoria.imagetitles.Constants;
import games.alejandrocoria.imagetitles.ImageTitles;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.profiling.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
            cancellable = true)
    private void renderTitle(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci, @Local int i) {
        boolean imageRendered = ImageTitles.renderImage(guiGraphics, i);

        if (imageRendered) {
            ci.cancel();
            Profiler.get().pop();
        }
    }
}
