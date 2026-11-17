package com.hudeditor.mixin;

import com.hudeditor.HudLayer;
import com.hudeditor.HudRender;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.contextualbar.ExperienceBarRenderer;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceBarRenderer.class)
abstract class ExperienceBarMixin {
    @Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
    private void hudeditor$push(GuiGraphics g, DeltaTracker delta, CallbackInfo ci) {
        if (HudRender.skip(HudLayer.XP_BAR)) { ci.cancel(); return; }
        HudRender.push(g, HudLayer.XP_BAR);
    }

    @Inject(method = "renderBackground", at = @At("RETURN"))
    private void hudeditor$pop(GuiGraphics g, DeltaTracker delta, CallbackInfo ci) {
        HudRender.pop(g);
    }

    @Redirect(method = "renderBackground", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"))
    private void hudeditor$background(GuiGraphics g, RenderPipeline p, Identifier sprite, int x, int y, int w, int h) {
        HudRender.blit(g, p, sprite, x, y, w, h, HudRender.alpha(HudLayer.XP_BAR));
    }

    @Redirect(method = "renderBackground", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIIIIIII)V"))
    private void hudeditor$progress(GuiGraphics g, RenderPipeline p, Identifier sprite,
            int tw, int th, int u, int v, int x, int y, int w, int h) {
        HudRender.blitCrop(g, p, sprite, tw, th, u, v, x, y, w, h, HudRender.alpha(HudLayer.XP_BAR));
    }
}
