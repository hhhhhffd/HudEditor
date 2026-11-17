package com.hudeditor.mixin;

import com.hudeditor.HudLayer;
import com.hudeditor.HudRender;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.contextualbar.ContextualBarRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
abstract class GuiMixin {
    private static final String SPRITE = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V";

    @Inject(method = "renderHearts", at = @At("HEAD"), cancellable = true)
    private void hudeditor$pushHearts(GuiGraphics g, Player player, int left, int y, int row, int blink, float health, int lastHealth, int currentHealth, int absorption, boolean highlight, CallbackInfo ci) {
        if (HudRender.skip(HudLayer.HEALTH)) { ci.cancel(); return; }
        HudRender.push(g, HudLayer.HEALTH);
    }

    @Inject(method = "renderHearts", at = @At("RETURN"))
    private void hudeditor$popHearts(GuiGraphics g, Player player, int left, int y, int row, int blink, float health, int lastHealth, int currentHealth, int absorption, boolean highlight, CallbackInfo ci) {
        HudRender.pop(g);
    }

    @Inject(method = "renderFood", at = @At("HEAD"), cancellable = true)
    private void hudeditor$pushFood(GuiGraphics g, Player player, int y, int right, CallbackInfo ci) {
        if (HudRender.skip(HudLayer.HUNGER)) { ci.cancel(); return; }
        HudRender.push(g, HudLayer.HUNGER);
    }

    @Inject(method = "renderFood", at = @At("RETURN"))
    private void hudeditor$popFood(GuiGraphics g, Player player, int y, int right, CallbackInfo ci) {
        var food = player.getFoodData();
        HudRender.currentSaturation(g, y, right, food.getSaturationLevel(), food.getFoodLevel());
        HudRender.pop(g);
    }

    @Inject(method = "renderArmor", at = @At("HEAD"), cancellable = true)
    private static void hudeditor$pushArmor(GuiGraphics g, Player player, int y, int rows, int rowHeight, int left, CallbackInfo ci) {
        if (HudRender.skip(HudLayer.ARMOR)) { ci.cancel(); return; }
        HudRender.push(g, HudLayer.ARMOR);
    }

    @Inject(method = "renderArmor", at = @At("RETURN"))
    private static void hudeditor$popArmor(GuiGraphics g, Player player, int y, int rows, int rowHeight, int left, CallbackInfo ci) {
        HudRender.pop(g);
    }

    @Inject(method = "renderItemHotbar", at = @At("HEAD"), cancellable = true)
    private void hudeditor$pushHotbar(GuiGraphics g, DeltaTracker timer, CallbackInfo ci) {
        if (HudRender.skip(HudLayer.HOTBAR)) { ci.cancel(); return; }
        HudRender.push(g, HudLayer.HOTBAR);
        HudRender.hotbarItemAlpha(HudRender.alpha(HudLayer.HOTBAR));
    }

    @Inject(method = "renderItemHotbar", at = @At("RETURN"))
    private void hudeditor$popHotbar(GuiGraphics g, DeltaTracker timer, CallbackInfo ci) {
        HudRender.pop(g);
        HudRender.hotbarItemAlpha(255);
    }

    @Redirect(method = "renderHeart", at = @At(value = "INVOKE", target = SPRITE))
    private void hudeditor$HealthAlpha(GuiGraphics g, RenderPipeline pipeline, Identifier sprite,
                           int x, int y, int w, int h) {
        HudRender.blit(g, pipeline, sprite, x, y, w, h, HudRender.alpha(HudLayer.HEALTH));
    }

    @Redirect(method = "renderFood", at = @At(value = "INVOKE", target = SPRITE))
    private void hudeditor$HungerAlpha(GuiGraphics g, RenderPipeline pipeline, Identifier sprite,
                           int x, int y, int w, int h) {
        HudRender.blit(g, pipeline, sprite, x, y, w, h, HudRender.alpha(HudLayer.HUNGER));
    }

    @Redirect(method = "renderArmor", at = @At(value = "INVOKE", target = SPRITE))
    private static void hudeditor$ArmorAlpha(GuiGraphics g, RenderPipeline pipeline, Identifier sprite,
                           int x, int y, int w, int h) {
        HudRender.blit(g, pipeline, sprite, x, y, w, h, HudRender.alpha(HudLayer.ARMOR));
    }

    @Redirect(method = "renderItemHotbar", at = @At(value = "INVOKE", target = SPRITE))
    private void hudeditor$HotbarAlpha(GuiGraphics g, RenderPipeline pipeline, Identifier sprite,
                           int x, int y, int w, int h) {
        HudRender.blit(g, pipeline, sprite, x, y, w, h, HudRender.alpha(HudLayer.HOTBAR));
    }

    @Redirect(method = "renderAirBubbles", at = @At(value = "INVOKE", target = SPRITE), require = 0)
    private void hudeditor$AirAlpha(GuiGraphics g, RenderPipeline pipeline, Identifier sprite,
                           int x, int y, int w, int h) {
        HudRender.blit(g, pipeline, sprite, x, y, w, h, HudRender.masterAlpha());
    }

    @Redirect(method = "renderVehicleHealth", at = @At(value = "INVOKE", target = SPRITE))
    private void hudeditor$VehicleAlpha(GuiGraphics g, RenderPipeline pipeline, Identifier sprite,
                           int x, int y, int w, int h) {
        HudRender.blit(g, pipeline, sprite, x, y, w, h, HudRender.masterAlpha());
    }

    @Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", target = SPRITE), require = 0)
    private void hudeditor$CrosshairAlpha(GuiGraphics g, RenderPipeline pipeline, Identifier sprite,
                           int x, int y, int w, int h) {
        HudRender.blit(g, pipeline, sprite, x, y, w, h, HudRender.masterAlpha());
    }

    @Redirect(method = "renderItemHotbar", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIIIIIII)V"), require = 0)
    private void hudeditor$hotbarCrop(GuiGraphics g, RenderPipeline p, Identifier sprite,
            int tw, int th, int u, int v, int x, int y, int w, int h) {
        HudRender.blitCrop(g, p, sprite, tw, th, u, v, x, y, w, h, HudRender.alpha(HudLayer.HOTBAR));
    }

    @Redirect(method = "renderCrosshair", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIIIIIII)V"), require = 0)
    private void hudeditor$crosshairCrop(GuiGraphics g, RenderPipeline p, Identifier sprite,
            int tw, int th, int u, int v, int x, int y, int w, int h) {
        HudRender.blitCrop(g, p, sprite, tw, th, u, v, x, y, w, h, HudRender.masterAlpha());
    }

    @Redirect(method = "renderHotbarAndDecorations", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/contextualbar/ContextualBarRenderer;renderExperienceLevel(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Font;I)V"))
    private void hudeditor$experienceLevel(GuiGraphics g, Font font, int level) {
        if (HudRender.skip(HudLayer.XP_LEVEL)) return;
        HudRender.push(g, HudLayer.XP_LEVEL);
        try {
            int alpha = HudRender.alpha(HudLayer.XP_LEVEL);
            if (alpha == 255) {
                ContextualBarRenderer.renderExperienceLevel(g, font, level);
            } else {
                Component text = Component.translatable("gui.experience.level", level);
                int x = (g.guiWidth() - font.width(text)) / 2;
                int y = g.guiHeight() - 35;
                int shadow = HudRender.textTint(0xFF000000, Math.max(4, alpha));
                g.drawString(font,text,x+1,y,shadow,false);
                g.drawString(font,text,x-1,y,shadow,false);
                g.drawString(font,text,x,y+1,shadow,false);
                g.drawString(font,text,x,y-1,shadow,false);
                g.drawString(font,text,x,y,HudRender.textTint(0xFF80FF20,Math.max(4,alpha)),false);
            }
        } finally { HudRender.pop(g); }
    }
}

