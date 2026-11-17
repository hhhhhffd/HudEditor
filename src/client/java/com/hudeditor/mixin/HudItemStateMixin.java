package com.hudeditor.mixin;

import com.hudeditor.HudRender;
import com.hudeditor.opacity.HudItemOpacity;
import net.minecraft.client.gui.render.state.GuiItemRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiItemRenderState.class)
abstract class HudItemStateMixin implements HudItemOpacity {
    @Unique private int hudeditor$itemOpacity = 255;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void hudeditor$captureHotbarOpacity(CallbackInfo ci) {
        hudeditor$itemOpacity = HudRender.hotbarItemAlpha();
    }

    @Override
    public int hudeditor$opacity() {
        return hudeditor$itemOpacity;
    }
}
