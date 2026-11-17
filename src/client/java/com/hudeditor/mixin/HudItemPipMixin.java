package com.hudeditor.mixin;

import com.hudeditor.opacity.HudItemOpacity;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.pip.OversizedItemRenderState;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(PictureInPictureRenderer.class)
abstract class HudItemPipMixin {
    @ModifyConstant(method = "blitTexture", constant = @Constant(intValue = -1))
    private int hudeditor$tintOversizedItem(int white, PictureInPictureRenderState state, GuiRenderState renderState) {
        return state instanceof OversizedItemRenderState item
                ? HudItemOpacity.premultiplied(white, HudItemOpacity.of(item.guiItemRenderState()))
                : white;
    }
}
