/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  org.joml.Vector2i
 *  org.lwjgl.glfw.GLFWDropCallback
 */
package net.minecraft.client;

import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mojang.logging.LogUtils;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.InputType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ScrollWheelHandler;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Mth;
import net.minecraft.util.SmoothDouble;
import net.minecraft.world.entity.player.Inventory;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFWDropCallback;
import org.slf4j.Logger;

public class MouseHandler {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Minecraft minecraft;
    private boolean isLeftPressed;
    private boolean isMiddlePressed;
    private boolean isRightPressed;
    private double xpos;
    private double ypos;
    private int fakeRightMouse;
    private int activeButton = -1;
    private boolean ignoreFirstMove = true;
    private int clickDepth;
    private double mousePressedTime;
    private final SmoothDouble smoothTurnX = new SmoothDouble();
    private final SmoothDouble smoothTurnY = new SmoothDouble();
    private double accumulatedDX;
    private double accumulatedDY;
    private final ScrollWheelHandler scrollWheelHandler;
    private double lastHandleMovementTime = Double.MIN_VALUE;
    private boolean mouseGrabbed;

    public MouseHandler(Minecraft $$0) {
        this.minecraft = $$0;
        this.scrollWheelHandler = new ScrollWheelHandler();
    }

    private void onPress(long $$0, int $$1, int $$2, int $$3) {
        int $$6;
        boolean $$5;
        block32: {
            Window $$4 = this.minecraft.getWindow();
            if ($$0 != $$4.getWindow()) {
                return;
            }
            this.minecraft.getFramerateLimitTracker().onInputReceived();
            if (this.minecraft.screen != null) {
                this.minecraft.setLastInputType(InputType.MOUSE);
            }
            boolean bl = $$5 = $$2 == 1;
            if (Minecraft.ON_OSX && $$1 == 0) {
                if ($$5) {
                    if (($$3 & 2) == 2) {
                        $$1 = 1;
                        ++this.fakeRightMouse;
                    }
                } else if (this.fakeRightMouse > 0) {
                    $$1 = 1;
                    --this.fakeRightMouse;
                }
            }
            $$6 = $$1;
            if ($$5) {
                if (this.minecraft.options.touchscreen().get().booleanValue() && this.clickDepth++ > 0) {
                    return;
                }
                this.activeButton = $$6;
                this.mousePressedTime = Blaze3D.getTime();
            } else if (this.activeButton != -1) {
                if (this.minecraft.options.touchscreen().get().booleanValue() && --this.clickDepth > 0) {
                    return;
                }
                this.activeButton = -1;
            }
            if (this.minecraft.getOverlay() == null) {
                if (this.minecraft.screen == null) {
                    if (!this.mouseGrabbed && $$5) {
                        this.grabMouse();
                    }
                } else {
                    double $$7 = this.getScaledXPos($$4);
                    double $$8 = this.getScaledYPos($$4);
                    Screen $$9 = this.minecraft.screen;
                    if ($$5) {
                        $$9.afterMouseAction();
                        try {
                            if ($$9.mouseClicked($$7, $$8, $$6)) {
                                return;
                            }
                            break block32;
                        } catch (Throwable $$10) {
                            CrashReport $$11 = CrashReport.forThrowable($$10, "mouseClicked event handler");
                            $$9.fillCrashDetails($$11);
                            CrashReportCategory $$12 = $$11.addCategory("Mouse");
                            this.fillMousePositionDetails($$12, $$4);
                            $$12.setDetail("Button", $$6);
                            throw new ReportedException($$11);
                        }
                    }
                    try {
                        if ($$9.mouseReleased($$7, $$8, $$6)) {
                            return;
                        }
                    } catch (Throwable $$13) {
                        CrashReport $$14 = CrashReport.forThrowable($$13, "mouseReleased event handler");
                        $$9.fillCrashDetails($$14);
                        CrashReportCategory $$15 = $$14.addCategory("Mouse");
                        this.fillMousePositionDetails($$15, $$4);
                        $$15.setDetail("Button", $$6);
                        throw new ReportedException($$14);
                    }
                }
            }
        }
        if (this.minecraft.screen == null && this.minecraft.getOverlay() == null) {
            if ($$6 == 0) {
                this.isLeftPressed = $$5;
            } else if ($$6 == 2) {
                this.isMiddlePressed = $$5;
            } else if ($$6 == 1) {
                this.isRightPressed = $$5;
            }
            KeyMapping.set(InputConstants.Type.MOUSE.getOrCreate($$6), $$5);
            if ($$5) {
                if (this.minecraft.player.isSpectator() && $$6 == 2) {
                    this.minecraft.gui.getSpectatorGui().onMouseMiddleClick();
                } else {
                    KeyMapping.click(InputConstants.Type.MOUSE.getOrCreate($$6));
                }
            }
        }
    }

    public void fillMousePositionDetails(CrashReportCategory $$0, Window $$1) {
        $$0.setDetail("Mouse location", () -> String.format(Locale.ROOT, "Scaled: (%f, %f). Absolute: (%f, %f)", MouseHandler.getScaledXPos($$1, this.xpos), MouseHandler.getScaledYPos($$1, this.ypos), this.xpos, this.ypos));
        $$0.setDetail("Screen size", () -> String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %f", $$1.getGuiScaledWidth(), $$1.getGuiScaledHeight(), $$1.getWidth(), $$1.getHeight(), $$1.getGuiScale()));
    }

    private void onScroll(long $$0, double $$1, double $$2) {
        if ($$0 == Minecraft.getInstance().getWindow().getWindow()) {
            this.minecraft.getFramerateLimitTracker().onInputReceived();
            boolean $$3 = this.minecraft.options.discreteMouseScroll().get();
            double $$4 = this.minecraft.options.mouseWheelSensitivity().get();
            double $$5 = ($$3 ? Math.signum($$1) : $$1) * $$4;
            double $$6 = ($$3 ? Math.signum($$2) : $$2) * $$4;
            if (this.minecraft.getOverlay() == null) {
                if (this.minecraft.screen != null) {
                    double $$7 = this.getScaledXPos(this.minecraft.getWindow());
                    double $$8 = this.getScaledYPos(this.minecraft.getWindow());
                    this.minecraft.screen.mouseScrolled($$7, $$8, $$5, $$6);
                    this.minecraft.screen.afterMouseAction();
                } else if (this.minecraft.player != null) {
                    int $$10;
                    Vector2i $$9 = this.scrollWheelHandler.onMouseScroll($$5, $$6);
                    if ($$9.x == 0 && $$9.y == 0) {
                        return;
                    }
                    int n = $$10 = $$9.y == 0 ? -$$9.x : $$9.y;
                    if (this.minecraft.player.isSpectator()) {
                        if (this.minecraft.gui.getSpectatorGui().isMenuActive()) {
                            this.minecraft.gui.getSpectatorGui().onMouseScrolled(-$$10);
                        } else {
                            float $$11 = Mth.clamp(this.minecraft.player.getAbilities().getFlyingSpeed() + (float)$$9.y * 0.005f, 0.0f, 0.2f);
                            this.minecraft.player.getAbilities().setFlyingSpeed($$11);
                        }
                    } else {
                        Inventory $$12 = this.minecraft.player.getInventory();
                        $$12.setSelectedSlot(ScrollWheelHandler.getNextScrollWheelSelection($$10, $$12.getSelectedSlot(), Inventory.getSelectionSize()));
                    }
                }
            }
        }
    }

    private void onDrop(long $$0, List<Path> $$1, int $$2) {
        this.minecraft.getFramerateLimitTracker().onInputReceived();
        if (this.minecraft.screen != null) {
            this.minecraft.screen.onFilesDrop($$1);
        }
        if ($$2 > 0) {
            SystemToast.onFileDropFailure(this.minecraft, $$2);
        }
    }

    public void setup(long $$02) {
        InputConstants.setupMouseCallbacks($$02, ($$0, $$1, $$2) -> this.minecraft.execute(() -> this.onMove($$0, $$1, $$2)), ($$0, $$1, $$2, $$3) -> this.minecraft.execute(() -> this.onPress($$0, $$1, $$2, $$3)), ($$0, $$1, $$2) -> this.minecraft.execute(() -> this.onScroll($$0, $$1, $$2)), ($$0, $$1, $$2) -> {
            ArrayList<Path> $$3 = new ArrayList<Path>($$1);
            int $$4 = 0;
            for (int $$5 = 0; $$5 < $$1; ++$$5) {
                String $$6 = GLFWDropCallback.getName((long)$$2, (int)$$5);
                try {
                    $$3.add(Paths.get($$6, new String[0]));
                    continue;
                } catch (InvalidPathException $$7) {
                    ++$$4;
                    LOGGER.error("Failed to parse path '{}'", (Object)$$6, (Object)$$7);
                }
            }
            if (!$$3.isEmpty()) {
                int $$8 = $$4;
                this.minecraft.execute(() -> this.onDrop($$0, $$3, $$8));
            }
        });
    }

    private void onMove(long $$0, double $$1, double $$2) {
        if ($$0 != Minecraft.getInstance().getWindow().getWindow()) {
            return;
        }
        if (this.ignoreFirstMove) {
            this.xpos = $$1;
            this.ypos = $$2;
            this.ignoreFirstMove = false;
            return;
        }
        if (this.minecraft.isWindowActive()) {
            this.accumulatedDX += $$1 - this.xpos;
            this.accumulatedDY += $$2 - this.ypos;
        }
        this.xpos = $$1;
        this.ypos = $$2;
    }

    public void handleAccumulatedMovement() {
        double $$0 = Blaze3D.getTime();
        double $$1 = $$0 - this.lastHandleMovementTime;
        this.lastHandleMovementTime = $$0;
        if (this.minecraft.isWindowActive()) {
            boolean $$3;
            Screen $$2 = this.minecraft.screen;
            boolean bl = $$3 = this.accumulatedDX != 0.0 || this.accumulatedDY != 0.0;
            if ($$3) {
                this.minecraft.getFramerateLimitTracker().onInputReceived();
            }
            if ($$2 != null && this.minecraft.getOverlay() == null && $$3) {
                Window $$4 = this.minecraft.getWindow();
                double $$5 = this.getScaledXPos($$4);
                double $$6 = this.getScaledYPos($$4);
                try {
                    $$2.mouseMoved($$5, $$6);
                } catch (Throwable $$7) {
                    CrashReport $$8 = CrashReport.forThrowable($$7, "mouseMoved event handler");
                    $$2.fillCrashDetails($$8);
                    CrashReportCategory $$9 = $$8.addCategory("Mouse");
                    this.fillMousePositionDetails($$9, $$4);
                    throw new ReportedException($$8);
                }
                if (this.activeButton != -1 && this.mousePressedTime > 0.0) {
                    double $$10 = MouseHandler.getScaledXPos($$4, this.accumulatedDX);
                    double $$11 = MouseHandler.getScaledYPos($$4, this.accumulatedDY);
                    try {
                        $$2.mouseDragged($$5, $$6, this.activeButton, $$10, $$11);
                    } catch (Throwable $$12) {
                        CrashReport $$13 = CrashReport.forThrowable($$12, "mouseDragged event handler");
                        $$2.fillCrashDetails($$13);
                        CrashReportCategory $$14 = $$13.addCategory("Mouse");
                        this.fillMousePositionDetails($$14, $$4);
                        throw new ReportedException($$13);
                    }
                }
                $$2.afterMouseMove();
            }
            if (this.isMouseGrabbed() && this.minecraft.player != null) {
                this.turnPlayer($$1);
            }
        }
        this.accumulatedDX = 0.0;
        this.accumulatedDY = 0.0;
    }

    public static double getScaledXPos(Window $$0, double $$1) {
        return $$1 * (double)$$0.getGuiScaledWidth() / (double)$$0.getScreenWidth();
    }

    public double getScaledXPos(Window $$0) {
        return MouseHandler.getScaledXPos($$0, this.xpos);
    }

    public static double getScaledYPos(Window $$0, double $$1) {
        return $$1 * (double)$$0.getGuiScaledHeight() / (double)$$0.getScreenHeight();
    }

    public double getScaledYPos(Window $$0) {
        return MouseHandler.getScaledYPos($$0, this.ypos);
    }

    private void turnPlayer(double $$0) {
        double $$11;
        double $$10;
        double $$1 = this.minecraft.options.sensitivity().get() * (double)0.6f + (double)0.2f;
        double $$2 = $$1 * $$1 * $$1;
        double $$3 = $$2 * 8.0;
        if (this.minecraft.options.smoothCamera) {
            double $$4 = this.smoothTurnX.getNewDeltaValue(this.accumulatedDX * $$3, $$0 * $$3);
            double $$5 = this.smoothTurnY.getNewDeltaValue(this.accumulatedDY * $$3, $$0 * $$3);
            double $$6 = $$4;
            double $$7 = $$5;
        } else if (this.minecraft.options.getCameraType().isFirstPerson() && this.minecraft.player.isScoping()) {
            this.smoothTurnX.reset();
            this.smoothTurnY.reset();
            double $$8 = this.accumulatedDX * $$2;
            double $$9 = this.accumulatedDY * $$2;
        } else {
            this.smoothTurnX.reset();
            this.smoothTurnY.reset();
            $$10 = this.accumulatedDX * $$3;
            $$11 = this.accumulatedDY * $$3;
        }
        int $$12 = 1;
        if (this.minecraft.options.invertYMouse().get().booleanValue()) {
            $$12 = -1;
        }
        this.minecraft.getTutorial().onMouse($$10, $$11);
        if (this.minecraft.player != null) {
            this.minecraft.player.turn($$10, $$11 * (double)$$12);
        }
    }

    public boolean isLeftPressed() {
        return this.isLeftPressed;
    }

    public boolean isMiddlePressed() {
        return this.isMiddlePressed;
    }

    public boolean isRightPressed() {
        return this.isRightPressed;
    }

    public double xpos() {
        return this.xpos;
    }

    public double ypos() {
        return this.ypos;
    }

    public void setIgnoreFirstMove() {
        this.ignoreFirstMove = true;
    }

    public boolean isMouseGrabbed() {
        return this.mouseGrabbed;
    }

    public void grabMouse() {
        if (!this.minecraft.isWindowActive()) {
            return;
        }
        if (this.mouseGrabbed) {
            return;
        }
        if (!Minecraft.ON_OSX) {
            KeyMapping.setAll();
        }
        this.mouseGrabbed = true;
        this.xpos = this.minecraft.getWindow().getScreenWidth() / 2;
        this.ypos = this.minecraft.getWindow().getScreenHeight() / 2;
        InputConstants.grabOrReleaseMouse(this.minecraft.getWindow().getWindow(), 212995, this.xpos, this.ypos);
        this.minecraft.setScreen(null);
        this.minecraft.missTime = 10000;
        this.ignoreFirstMove = true;
    }

    public void releaseMouse() {
        if (!this.mouseGrabbed) {
            return;
        }
        this.mouseGrabbed = false;
        this.xpos = this.minecraft.getWindow().getScreenWidth() / 2;
        this.ypos = this.minecraft.getWindow().getScreenHeight() / 2;
        InputConstants.grabOrReleaseMouse(this.minecraft.getWindow().getWindow(), 212993, this.xpos, this.ypos);
    }

    public void cursorEntered() {
        this.ignoreFirstMove = true;
    }

    public void drawDebugMouseInfo(Font $$0, GuiGraphics $$1) {
        Window $$2 = this.minecraft.getWindow();
        double $$3 = this.getScaledXPos($$2);
        double $$4 = this.getScaledYPos($$2) - 8.0;
        String $$5 = String.format(Locale.ROOT, "%.0f,%.0f", $$3, $$4);
        $$1.drawString($$0, $$5, (int)$$3, (int)$$4, -1);
    }
}

