# HUD Editor 1.0.0

A vanilla-style, client-side HUD editor for Minecraft Java **1.21.11**
and Fabric Loader **0.18.4+**. Put the JAR in the client `mods` folder.
Fabric API and server installation are not required.

## Open and edit

Press **F8** in-game. Change it in **Options → Controls → Key Binds →
Miscellaneous → Open HUD Editor**. The same editor opens through optional
**Mod Menu 17 → HUD Editor → Configure**.

- Drag hearts, hunger, armor, hotbar, XP bar, and XP level separately.
  Choose a group in the toolbar or by grabbing its preview.
- **HUD opacity** fades the movable groups together, including item icons and
  their count/durability in the hotbar. The selected group's own opacity
  is multiplied by the master slider. The master also dims vanilla air,
  vehicle hearts and crosshair sprites; it does not change other mods' HUDs,
  chat, sidebar, the world, or UI screens.
- Toggle **Armor** and **XP bar** separately. Hiding the XP bar leaves the
  XP level available. Vanilla locator and mount/jump bars remain independent.
- **Saturation** controls a separate translucent gold layer drawn on the
  hunger icons, following their movement and opacity; 0% disables the gold.
- **Undo** or **Ctrl+Z** undoes an edit. Arrow keys nudge a selected preview
  when no text/control is focused (hold Shift for five pixels).
- **Reset HUD** restores the defaults in the draft; **Done** saves. **Cancel**
  or Esc discards the draft.

Settings are written to `config/hudeditor.json`. The default positions and
the vanilla HUD's normal mode and F1 behavior are retained. Saturation uses
the latest value vanilla has synchronized for the local player; on an
unmodded multiplayer server, changes above zero can appear late when neither
hunger nor health updates.

## Build

Use Java 21 and run `./gradlew build` or `gradlew.bat build`.

Standalone checks:

```sh
javac -d build/hud-tests src/client/java/com/hudeditor/{HudLayer,HudLayout,HudMath}.java tests/HudMathTest.java
java -cp build/hud-tests HudMathTest
```
