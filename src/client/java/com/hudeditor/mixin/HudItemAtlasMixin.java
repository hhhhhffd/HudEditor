package com.hudeditor.mixin;

import com.hudeditor.opacity.HudItemOpacity;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.state.GuiItemRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(GuiRenderer.class)
abstract class HudItemAtlasMixin {
    @ModifyConstant(method = "submitBlitFromItemAtlas", constant = @Constant(intValue = -1))
    private int hudeditor$tintAtlasBlit(int white, GuiItemRenderState item, float u, float v, int size, int atlasSize) {
        // Vanilla's atlas blit uses a premultiplied-alpha pipeline: scale RGB and A, not A alone.
        return HudItemOpacity.premultiplied(white, HudItemOpacity.of(item));
    }
}
