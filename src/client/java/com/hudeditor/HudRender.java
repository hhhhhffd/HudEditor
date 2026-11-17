package com.hudeditor;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;

public final class HudRender {
    private static final String[] FOOD_SHAPE = {
        ".........", "..##.....", ".####....", ".#####...", "..####...",
        "...###...", "......#..", ".......#.", "........."
    };
    private static int hotbarItemAlpha = 255;

    private HudRender() { }

    public static boolean editorOpen() { return Minecraft.getInstance().screen instanceof HudEditorScreen; }

    public static boolean skip(HudLayer layer) {
        return editorOpen() || HudConfig.get().hidden(layer) || alpha(layer) <= 0;
    }

    public static int alpha(HudLayer layer) { return HudConfig.get().alpha(layer); }

    public static int masterAlpha() { return HudMath.alpha(HudConfig.get().masterOpacity, 100); }

    public static void push(GuiGraphics graphics, HudLayer layer) {
        var cfg = HudConfig.get();
        graphics.pose().pushMatrix();
        graphics.pose().translate(cfg.dx(layer, graphics.guiWidth()), cfg.dy(layer, graphics.guiHeight()));
    }

    public static void pop(GuiGraphics graphics) { graphics.pose().popMatrix(); }
    public static int hotbarItemAlpha() { return hotbarItemAlpha; }
    public static void hotbarItemAlpha(int alpha) { hotbarItemAlpha = alpha; }

    public static void blit(GuiGraphics g, RenderPipeline pipeline, Identifier id,
                            int x, int y, int w, int h, int alpha) {
        if (alpha == 255) g.blitSprite(pipeline,id,x,y,w,h);
        else if (alpha > 0) g.blitSprite(pipeline,id,x,y,w,h,alpha << 24 | 0xFFFFFF);
    }

    public static void blitCrop(GuiGraphics g, RenderPipeline pipeline, Identifier id,
                                int totalW, int totalH, int u, int v, int x, int y, int w, int h, int alpha) {
        if (alpha == 255) g.blitSprite(pipeline,id,totalW,totalH,u,v,x,y,w,h);
        else if (alpha > 0) g.blitSprite(pipeline,id,totalW,totalH,u,v,x,y,w,h,alpha << 24 | 0xFFFFFF);
    }

    public static int textTint(int base, int alpha) {
        return ((base >>> 24) * alpha / 255) << 24 | base & 0xFFFFFF;
    }

    public static void currentSaturation(GuiGraphics g, int y, int right, float saturation, int food) {
        saturation(g, y, right, saturation, food, HudConfig.get());
    }

    public static void saturation(GuiGraphics g, int y, int right, float sat, int food, HudLayout settings) {
        if (sat <= 0 || settings.saturationOpacity <= 0) return;
        int alpha = settings.alpha(HudLayer.HUNGER) * settings.saturationOpacity / 100;
        if (alpha <= 0) return;
        for (int icon = 0; icon < 10; icon++) {
            float fill = HudMath.saturationIcon(sat, food, icon);
            if (fill <= 0) break;
            int x = right - icon * 8 - 9;
            // Soft gold pigment within the drumstick shape, on its vanilla position.
            for (int py = 1; py <= 7; py++) {
                for (int px = 1; px <= 7; px++) {
                    if (FOOD_SHAPE[py].charAt(px) != '#' || px - 1 >= 7 * fill) continue;
                    boolean rim = FOOD_SHAPE[py - 1].charAt(px) != '#' || FOOD_SHAPE[py + 1].charAt(px) != '#'
                            || FOOD_SHAPE[py].charAt(px - 1) != '#' || FOOD_SHAPE[py].charAt(px + 1) != '#';
                    int pigment = Math.round(alpha * (rim ? 0.94f : 0.65f));
                    g.fill(x + px, y + py, x + px + 1, y + py + 1, pigment << 24 | (rim ? 0xFFE6A0 : 0xFFD064));
                }
            }
        }
    }
}
