package com.hudeditor;

import java.util.EnumMap;

public final class HudLayout {
    public int masterOpacity = 100;
    public int saturationOpacity = 38;
    public boolean hideArmor;
    public boolean hideXpBar;
    public EnumMap<HudLayer, Element> elements = new EnumMap<>(HudLayer.class);

    public HudLayout() {
        for (HudLayer layer : HudLayer.values()) elements.put(layer, new Element());
    }

    public static final class Element {
        public double x;
        public double y;
        public int opacity = 100;
        private Element copy() {
            Element e = new Element();
            e.x = x;
            e.y = y;
            e.opacity = opacity;
            return e;
        }
    }

    public Element element(HudLayer layer) {
        return elements.computeIfAbsent(layer, ignored -> new Element());
    }

    public boolean hidden(HudLayer layer) {
        return (layer == HudLayer.ARMOR && hideArmor) || (layer == HudLayer.XP_BAR && hideXpBar);
    }

    public int alpha(HudLayer layer) { return HudMath.alpha(masterOpacity, element(layer).opacity); }
    public int dx(HudLayer layer, int width) { return HudMath.offset(element(layer).x, width); }
    public int dy(HudLayer layer, int height) { return HudMath.offset(element(layer).y, height); }

    public HudLayout copy() {
        HudLayout draft = new HudLayout();
        draft.masterOpacity = masterOpacity;
        draft.saturationOpacity = saturationOpacity;
        draft.hideArmor = hideArmor;
        draft.hideXpBar = hideXpBar;
        for (HudLayer layer : HudLayer.values()) draft.elements.put(layer, element(layer).copy());
        return draft;
    }
}
