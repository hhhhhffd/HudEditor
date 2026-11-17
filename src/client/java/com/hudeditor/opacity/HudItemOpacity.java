package com.hudeditor.opacity;

import net.minecraft.client.gui.render.state.GuiItemRenderState;

/** Deferred GUI elements need an opacity value captured when queued, not at draw time. */
public interface HudItemOpacity {
    int hudeditor$opacity();

    static int of(GuiItemRenderState item) {
        return ((HudItemOpacity) (Object) item).hudeditor$opacity();
    }

    static int premultiplied(int argb, int opacity) {
        if (opacity == 255) return argb;
        return ((argb >>> 24) * opacity + 127) / 255 << 24
                | (((argb >>> 16) & 255) * opacity + 127) / 255 << 16
                | (((argb >>> 8) & 255) * opacity + 127) / 255 << 8
                | ((argb & 255) * opacity + 127) / 255;
    }

    static int straight(int argb, int opacity) {
        if (opacity == 255) return argb;
        return ((argb >>> 24) * opacity + 127) / 255 << 24 | (argb & 0x00FFFFFF);
    }
}
