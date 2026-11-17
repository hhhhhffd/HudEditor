package com.hudeditor;

public enum HudLayer {
    HEALTH("Health", -91, -39, 81, 9),
    HUNGER("Hunger", 10, -39, 81, 9),
    ARMOR("Armor", -91, -49, 81, 9),
    HOTBAR("Hotbar", -91, -22, 182, 22),
    XP_BAR("XP bar", -91, -29, 182, 5),
    XP_LEVEL("XP level", -11, -35, 22, 10);

    public final String label;
    public final int originX, originY, width, height;

    HudLayer(String label, int originX, int originY, int width, int height) {
        this.label = label;
        this.originX = originX;
        this.originY = originY;
        this.width = width;
        this.height = height;
    }
}
