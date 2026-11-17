package com.hudeditor;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public final class HudKeys {
    public static final KeyMapping OPEN = new KeyMapping("key.hudeditor.open", GLFW.GLFW_KEY_F8, KeyMapping.Category.MISC);
    private HudKeys() { }

    public static void tick(Minecraft minecraft) {
        if (minecraft.screen == null && minecraft.player != null && OPEN.consumeClick()) {
            minecraft.setScreen(new HudEditorScreen(null));
        }
    }
}
