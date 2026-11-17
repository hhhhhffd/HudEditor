package com.hudeditor;

public final class HudMath {
    private HudMath() { }

    public static int alpha(int tripA, int tripB) {
        return Math.round(255f * percent(tripA) * percent(tripB) / 10000f);
    }

    public static int percent(int percent) {
        return Math.clamp(percent, 0, 100);
    }

    public static int offset(double relative, int extent) {
        return Double.isFinite(relative) ? (int)Math.round(Math.clamp(relative, -1, 1) * extent) : 0;
    }

    public static float saturationIcon(float sat, int food, int index) {
        if (!Float.isFinite(sat) || index < 0 || index >= 10) return 0;
        return Math.clamp((Math.min(sat, food) - index * 2f) / 2f, 0f, 1f);
    }
}
