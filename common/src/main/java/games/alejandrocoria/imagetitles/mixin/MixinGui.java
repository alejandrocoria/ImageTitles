package games.alejandrocoria.imagetitles.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import games.alejandrocoria.imagetitles.ImageTitles;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
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
                    shift = At.Shift.BY,
                    by = 2),
            method = "render")
    private void render(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci, @Local(ordinal = 0) LocalIntRef i) {
        boolean imageRendered = ImageTitles.renderImage(guiGraphics, i.get());

        if (imageRendered) {
            i.set(0);
        }
    }
}
