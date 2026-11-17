import com.hudeditor.HudLayer;
import com.hudeditor.HudLayout;
import com.hudeditor.HudMath;

public class HudMathTest {
    private static int checks;
    private static void test(boolean yes, String label) {
        if (!yes) throw new AssertionError(label);
        ++checks;
    }

    public static void main(String[] args) {
        var layout = new HudLayout();
        for (var layer : HudLayer.values()) {
            test(layout.alpha(layer) == 255, "default opacity " + layer);
            test(layout.dx(layer, 640) == 0 && layout.dy(layer, 360) == 0, "vanilla placement " + layer);
            test(!layout.hidden(layer), "default visibility " + layer);
        }
        test(HudMath.alpha(50, 50) == 64, "master and per-layer alpha multiply");
        test(HudMath.alpha(100, 0) == 0 && HudMath.alpha(0, 100) == 0, "zero is hidden");
        test(HudMath.alpha(101, 999) == 255, "safe clamp");
        test(HudMath.offset(.125, 640) == 80 && HudMath.offset(.125, 1280) == 160, "resolution-normalized position");
        test(HudMath.offset(Double.NaN, 360) == 0, "invalid offset uses vanilla");
        test(HudMath.saturationIcon(7, 20, 0) == 1f && HudMath.saturationIcon(7, 20, 2) == 1f
                && HudMath.saturationIcon(7, 20, 3) == .5f && HudMath.saturationIcon(7, 20, 4) == 0f,
                "three full and one half saturation icon");
        test(HudMath.saturationIcon(20, 5, 2) == .5f && HudMath.saturationIcon(20, 5, 3) == 0f, "can't show saturation beyond food");
        layout.hideXpBar = true;
        layout.hideArmor = true;
        test(layout.hidden(HudLayer.XP_BAR) && layout.hidden(HudLayer.ARMOR), "independent hide");
        test(!layout.hidden(HudLayer.XP_LEVEL), "XP text remains");
        layout.element(HudLayer.HOTBAR).x = -.25;
        var draft = layout.copy();
        draft.element(HudLayer.HOTBAR).x = .25;
        test(layout.dx(HudLayer.HOTBAR, 640) == -160 && draft.dx(HudLayer.HOTBAR, 640) == 160, "isolated undo snapshot");
        System.out.println("Passed " + checks + " HUD placement, transparency, and saturation checks.");
    }
}
