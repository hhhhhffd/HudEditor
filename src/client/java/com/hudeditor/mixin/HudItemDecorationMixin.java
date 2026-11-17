package com.hudeditor.mixin;

import com.hudeditor.HudRender;
import com.hudeditor.opacity.HudItemOpacity;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GuiGraphics.class)
abstract class HudItemDecorationMixin {
    @ModifyArg(method = "renderItemCount", index = 4,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)V"))
    private int hudeditor$tintItemCount(int argb) {
        return HudItemOpacity.straight(argb, HudRender.hotbarItemAlpha());
    }

    @ModifyArg(method = {"renderItemBar", "renderItemCooldown"}, index = 5,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fill(Lcom/mojang/blaze3d/pipeline/RenderPipeline;IIIII)V"))
    private int hudeditor$tintItemDecoration(int argb) {
        return HudItemOpacity.straight(argb, HudRender.hotbarItemAlpha());
    }
}
