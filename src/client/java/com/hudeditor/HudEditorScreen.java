package com.hudeditor;

import java.util.ArrayDeque;
import java.util.function.IntConsumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.lwjgl.glfw.GLFW;

public final class HudEditorScreen extends Screen {
    private static final int MINT = 0xFF88EFCD;
    private static final int TEXT = 0xFFF2F7FA;
    private static final int MUTE = 0xFFABC1C2;
    private static final int BORDER = 0xFF466266;
    private static final int PANEL_H = 117;
    private static final HudLayer[] LAYERS = HudLayer.values();
    private static final Identifier HEART_CONTAINER = sprite("heart/container");
    private static final Identifier HEART_FULL = sprite("heart/full");
    private static final Identifier HEART_HALF = sprite("heart/half");
    private static final Identifier FOOD_EMPTY = sprite("food_empty");
    private static final Identifier FOOD_FULL = sprite("food_full");
    private static final Identifier FOOD_HALF = sprite("food_half");
    private static final Identifier ARMOR_EMPTY = sprite("armor_empty");
    private static final Identifier ARMOR_FULL = sprite("armor_full");
    private static final Identifier ARMOR_HALF = sprite("armor_half");
    private static final Identifier HOTBAR = sprite("hotbar");
    private static final Identifier HOTBAR_SELECTION = sprite("hotbar_selection");
    private static final Identifier XP_BG = sprite("experience_bar_background");
    private static final Identifier XP_PROGRESS = sprite("experience_bar_progress");

    private final Screen parent;
    private final ArrayDeque<HudLayout> undo = new ArrayDeque<>();
    private HudLayout draft = HudConfig.get().copy();
    private HudLayer selected = HudLayer.HOTBAR;
    private boolean dragging;
    private double dragOffsetX, dragOffsetY;
    private int x, y, w;
    private Button undoButton;

    public HudEditorScreen(Screen parent) {
        super(Component.literal("HUD Editor"));
        this.parent = parent;
    }

    @Override protected void init() {
        w = Math.min(432, width - 16);
        x = (width - w) / 2;
        y = 7;
        int inner = w - 16;
        int left = x + 8;

        for (int i = 0; i < LAYERS.length; i++) {
            HudLayer layer = LAYERS[i];
            button(tab(layer), left + inner * i / 6, y + 31, inner * (i + 1) / 6 - inner * i / 6 - 3, 17, b -> {
                selected = layer;
                rebuildWidgets();
            });
        }
        int third = inner / 3;
        addRenderableWidget(new OpacitySlider(left, y + 51, third - 4, "HUD opacity", draft.masterOpacity, value -> {
            if (draft.masterOpacity == value) return;
            remember();
            draft.masterOpacity = value;
        }));
        addRenderableWidget(new OpacitySlider(left + third, y + 51, third - 4, selected.label + " opacity",
                draft.element(selected).opacity, value -> {
            if (draft.element(selected).opacity == value) return;
            remember();
            draft.element(selected).opacity = value;
        }));
        addRenderableWidget(new OpacitySlider(left + third * 2, y + 51, inner - third * 2 - 3, "Saturation",
                draft.saturationOpacity, value -> {
            if (draft.saturationOpacity == value) return;
            remember();
            draft.saturationOpacity = value;
        }));

        button(armorText(), left, y + 72, third - 4, 17, b -> {
            remember();
            draft.hideArmor = !draft.hideArmor;
            b.setMessage(Component.literal(armorText()));
        });
        button(xpText(), left + third, y + 72, third - 4, 17, b -> {
            remember();
            draft.hideXpBar = !draft.hideXpBar;
            b.setMessage(Component.literal(xpText()));
        });
        undoButton = button("Undo", left + third * 2, y + 72, inner - third * 2 - 3, 17, b -> undo());
        undoButton.active = !undo.isEmpty();

        button("Reset HUD", left, y + 94, third - 4, 17, b -> {
            remember();
            draft = new HudLayout();
            rebuildWidgets();
        });
        button("Done", left + third, y + 94, third - 4, 17, b -> {
            HudConfig.save(draft);
            onClose();
        });
        button("Cancel", left + third * 2, y + 94, inner - third * 2 - 3, 17, b -> onClose());
    }

    @Override protected void repositionElements() { rebuildWidgets(); }

    private Button button(String title, int bx, int by, int bw, int bh, Button.OnPress action) {
        return addRenderableWidget(Button.builder(Component.literal(title), action).bounds(bx, by, bw, bh).build());
    }

    private String tab(HudLayer layer) { return (selected == layer ? "› " : "") + layer.label; }
    private String armorText() { return draft.hideArmor ? "Armor: hidden" : "Armor: shown"; }
    private String xpText() { return draft.hideXpBar ? "XP bar: hidden" : "XP bar: shown"; }

    private void remember() {
        if (undo.size() >= 64) undo.removeFirst();
        undo.addLast(draft.copy());
        if (undoButton != null) undoButton.active = true;
    }

    private void undo() {
        if (undo.isEmpty()) return;
        draft = undo.removeLast();
        rebuildWidgets();
    }

    private static Identifier sprite(String path) { return Identifier.withDefaultNamespace("hud/" + path); }

    @Override public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, width, height, 0x38081720);
    }

    @Override public void render(GuiGraphics g, int mx, int my, float tickDelta) {
        if (dragging) {
            g.fill(width / 2, 0, width / 2 + 1, height, 0x3984DDBB);
            g.fill(0, height / 2, width, height / 2 + 1, 0x3984DDBB);
        }
        for (HudLayer layer : LAYERS) drawGroup(g, layer, mx, my);
        g.nextStratum();
        g.fill(x, y, x + w, y + PANEL_H, 0xF0122228);
        g.renderOutline(x, y, w, PANEL_H, BORDER);
        g.fill(x + 1, y + 1, x + w - 1, y + 3, MINT);
        g.drawString(font, getTitle(), x + 8, y + 9, TEXT);
        g.drawString(font, "Drag a group  •  Arrow keys nudge  •  Ctrl+Z undo", x + 8, y + 21, MUTE);
        super.render(g, mx, my, tickDelta);
    }

    private void drawGroup(GuiGraphics g, HudLayer layer, int mx, int my) {
        int bx = baseX(layer) + draft.dx(layer, width), by = baseY(layer) + draft.dy(layer, height);
        int alpha = draft.hidden(layer) ? 0 : draft.alpha(layer);
        boolean hovered = near(layer, mx, my);
        int edge = selected == layer ? MINT : hovered ? 0xFFCFDFDE : 0x7850767A;
        if (alpha == 0) {
            g.fill(bx, by, bx + layer.width, by + layer.height, 0x50223742);
        } else {
            switch (layer) {
                case HEALTH -> drawRow(g, bx, by, HEALTH_POINTS(), HEART_CONTAINER, HEART_FULL, HEART_HALF, alpha, false);
                case ARMOR -> drawRow(g, bx, by, ARMOR_POINTS(), ARMOR_EMPTY, ARMOR_FULL, ARMOR_HALF, alpha, false);
                case HUNGER -> {
                    int hunger = FOOD_POINTS();
                    drawRow(g, bx, by, hunger, FOOD_EMPTY, FOOD_FULL, FOOD_HALF, alpha, true);
                    float sat = minecraft != null && minecraft.player != null
                            ? minecraft.player.getFoodData().getSaturationLevel() : 7;
                    HudRender.saturation(g, by, bx + 81, sat, hunger, draft);
                }
                case XP_BAR -> {
                    float progress = minecraft != null && minecraft.player != null
                            ? minecraft.player.experienceProgress : .55f;
                    if (progress <= 0) progress = .55f;
                    HudRender.blit(g, RenderPipelines.GUI_TEXTURED, XP_BG, bx, by, 182, 5, alpha);
                    HudRender.blitCrop(g, RenderPipelines.GUI_TEXTURED, XP_PROGRESS, 182, 5, 0, 0, bx, by,
                            Math.min(182, Math.max(1, Math.round(progress * 182))), 5, alpha);
                }
                case XP_LEVEL -> {
                    int level = minecraft != null && minecraft.player != null
                            ? Math.max(1, minecraft.player.experienceLevel) : 17;
                    g.drawCenteredString(font, Integer.toString(level), bx + 11, by + 1, HudRender.textTint(0xFF80FF20, Math.max(4, alpha)));
                }
                case HOTBAR -> {
                    int slot = minecraft != null && minecraft.player != null ? minecraft.player.getInventory().getSelectedSlot() : 0;
                    HudRender.blit(g, RenderPipelines.GUI_TEXTURED, HOTBAR, bx, by, 182, 22, alpha);
                    HudRender.blit(g, RenderPipelines.GUI_TEXTURED, HOTBAR_SELECTION, bx - 1 + 20 * slot, by - 1, 24, 23, alpha);
                    HudRender.hotbarItemAlpha(alpha);
                    try {
                        for (int i = 0; i < 9; i++) {
                            ItemStack item = minecraft != null && minecraft.player != null ? minecraft.player.getInventory().getItem(i)
                                    : i == 0 ? new ItemStack(Items.IRON_SWORD) : ItemStack.EMPTY;
                            if (!item.isEmpty()) {
                                int ix = bx + 3 + 20 * i;
                                g.renderItem(item, ix, by + 3);
                                g.renderItemDecorations(font, item, ix, by + 3);
                            }
                        }
                    } finally { HudRender.hotbarItemAlpha(255); }
                }
            }
        }
        g.renderOutline(bx - 2, by - 2, layer.width + 4, layer.height + 4, edge);
        if ((selected == layer && dragging) || (hovered && !dragging)) {
            String label = draft.hidden(layer) ? layer.label + " (hidden)" : layer.label;
            int tx = bx + (layer.width - font.width(label)) / 2;
            g.fill(tx - 3, by - 14, tx + font.width(label) + 3, by - 3, 0xE511242D);
            g.drawString(font, label, tx, by - 13, TEXT);
        }
    }

    private int HEALTH_POINTS() {
        return minecraft == null || minecraft.player == null ? 17 : Math.clamp((int)Math.ceil(minecraft.player.getHealth()), 0, 20);
    }

    private int ARMOR_POINTS() {
        return minecraft == null || minecraft.player == null ? 12 : Math.clamp(minecraft.player.getArmorValue(), 0, 20);
    }

    private int FOOD_POINTS() {
        return minecraft == null || minecraft.player == null ? 18 : minecraft.player.getFoodData().getFoodLevel();
    }

    private static void drawRow(GuiGraphics g, int x, int y, int points, Identifier empty, Identifier full,
                                Identifier half, int alpha, boolean rtl) {
        for (int i = 0; i < 10; i++) {
            int px = x + (rtl ? (9 - i) : i) * 8;
            if (empty != ARMOR_EMPTY || i * 2 >= points) {
                HudRender.blit(g, RenderPipelines.GUI_TEXTURED, empty, px, y, 9, 9, alpha);
            }
            if (i * 2 < points) {
                HudRender.blit(g, RenderPipelines.GUI_TEXTURED, i * 2 + 1 == points ? half : full, px, y, 9, 9, alpha);
            }
        }
    }

    private int baseX(HudLayer layer) { return width / 2 + layer.originX; }
    private int baseY(HudLayer layer) { return height + layer.originY; }

    private boolean near(HudLayer layer, double mx, double my) {
        int bx = baseX(layer) + draft.dx(layer, width), by = baseY(layer) + draft.dy(layer, height);
        return mx >= bx - 2 && mx < bx + layer.width + 2 && my >= by - 2 && my < by + layer.height + 2;
    }

    private boolean toolbar(double mx, double my) {
        return mx >= x && mx < x + w && my >= y && my < y + PANEL_H;
    }

    @Override public boolean mouseClicked(MouseButtonEvent e, boolean doubleClick) {
        if (e.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT && !toolbar(e.x(), e.y())) {
            HudLayer hit = near(selected, e.x(), e.y()) ? selected : null;
            if (hit == null) for (int i = LAYERS.length - 1; i >= 0; i--) {
                if (near(LAYERS[i], e.x(), e.y())) { hit = LAYERS[i]; break; }
            }
            if (hit != null) {
                selected = hit;
                clearFocus();
                remember();
                dragOffsetX = e.x() - baseX(hit) - draft.dx(hit, width);
                dragOffsetY = e.y() - baseY(hit) - draft.dy(hit, height);
                dragging = true;
                return true;
            }
        }
        return super.mouseClicked(e, doubleClick);
    }

    @Override public boolean mouseDragged(MouseButtonEvent e, double dx, double dy) {
        if (dragging && e.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            move((int)Math.round(e.x() - dragOffsetX), (int)Math.round(e.y() - dragOffsetY), true);
            return true;
        }
        return super.mouseDragged(e, dx, dy);
    }

    @Override public boolean mouseReleased(MouseButtonEvent e) {
        if (dragging && e.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            dragging = false;
            rebuildWidgets();
            return true;
        }
        return super.mouseReleased(e);
    }

    private void move(int bx, int by, boolean snap) {
        if (snap) {
            if (Math.abs(bx + selected.width / 2 - width / 2) <= 3) bx = (width - selected.width) / 2;
            if (Math.abs(by + selected.height / 2 - height / 2) <= 3) by = (height - selected.height) / 2;
        }
        bx = Math.clamp(bx, 0, Math.max(0, width - selected.width));
        by = Math.clamp(by, 0, Math.max(0, height - selected.height));
        draft.element(selected).x = (bx - baseX(selected)) / (double)width;
        draft.element(selected).y = (by - baseY(selected)) / (double)height;
    }

    @Override public boolean keyPressed(KeyEvent e) {
        if (e.key() == GLFW.GLFW_KEY_Z && (e.modifiers() & (GLFW.GLFW_MOD_CONTROL | GLFW.GLFW_MOD_SUPER)) != 0) {
            undo();
            return true;
        }
        if (getFocused() == null && !dragging) {
            int delta = (e.modifiers() & GLFW.GLFW_MOD_SHIFT) != 0 ? 5 : 1;
            int stepX = e.key() == GLFW.GLFW_KEY_LEFT ? -delta : e.key() == GLFW.GLFW_KEY_RIGHT ? delta : 0;
            int stepY = e.key() == GLFW.GLFW_KEY_UP ? -delta : e.key() == GLFW.GLFW_KEY_DOWN ? delta : 0;
            if (stepX != 0 || stepY != 0) {
                remember();
                move(baseX(selected) + draft.dx(selected, width) + stepX, baseY(selected) + draft.dy(selected, height) + stepY, false);
                return true;
            }
        }
        return super.keyPressed(e);
    }

    @Override public void onClose() { if (minecraft != null) minecraft.setScreen(parent); }

    private static final class OpacitySlider extends AbstractSliderButton {
        private final String name;
        private final IntConsumer change;
        private int last;

        private OpacitySlider(int x, int y, int width, String name, int value, IntConsumer change) {
            super(x,y,width,17,label(name,value),value / 100.0);
            this.name = name;
            this.change = change;
            this.last = value;
        }

        private int percent() { return Math.round((float)(value * 100)); }

        private static Component label(String name, int value) {
            return Component.literal(name + ": " + value + "%");
        }

        @Override protected void updateMessage() { setMessage(label(name, percent())); }

        @Override protected void applyValue() {
            int current = percent();
            if (current == last) return;
            last = current;
            change.accept(current);
        }
    }
}
