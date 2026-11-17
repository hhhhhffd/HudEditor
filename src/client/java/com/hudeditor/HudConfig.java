package com.hudeditor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.LoggerFactory;

final class HudConfig {
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("hudeditor.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static HudLayout live;

    static HudLayout get() {
        if (live == null) live = load();
        return live;
    }

    private static HudLayout load() {
        if (Files.isRegularFile(FILE)) {
            try (var reader = Files.newBufferedReader(FILE)) {
                HudLayout layout = GSON.fromJson(reader, HudLayout.class);
                if (layout != null && layout.elements != null) {
                    layout.masterOpacity = HudMath.percent(layout.masterOpacity);
                    layout.saturationOpacity = HudMath.percent(layout.saturationOpacity);
                    for (HudLayer layer : HudLayer.values()) {
                        var element = layout.element(layer);
                        element.x = Double.isFinite(element.x) ? Math.clamp(element.x, -1, 1) : 0;
                        element.y = Double.isFinite(element.y) ? Math.clamp(element.y, -1, 1) : 0;
                        element.opacity = HudMath.percent(element.opacity);
                    }
                    return layout;
                }
            } catch (RuntimeException | IOException e) {
                LoggerFactory.getLogger("HUD Editor").warn("Could not read HUD preferences", e);
            }
        }
        return new HudLayout();
    }

    static void save(HudLayout draft) {
        live = draft.copy();
        Path temp = null;
        try {
            Files.createDirectories(FILE.getParent());
            temp = Files.createTempFile(FILE.getParent(), "hudeditor-", ".tmp");
            try (var writer = Files.newBufferedWriter(temp)) { GSON.toJson(live, writer); }
            try {
                Files.move(temp, FILE, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temp, FILE, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            LoggerFactory.getLogger("HUD Editor").warn("Could not save HUD preferences", e);
        } finally {
            if (temp != null) {
                try { Files.deleteIfExists(temp); } catch (IOException ignored) { }
            }
        }
    }
}
