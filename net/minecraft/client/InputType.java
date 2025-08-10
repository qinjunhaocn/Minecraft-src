/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client;

public final class InputType
extends Enum<InputType> {
    public static final /* enum */ InputType NONE = new InputType();
    public static final /* enum */ InputType MOUSE = new InputType();
    public static final /* enum */ InputType KEYBOARD_ARROW = new InputType();
    public static final /* enum */ InputType KEYBOARD_TAB = new InputType();
    private static final /* synthetic */ InputType[] $VALUES;

    public static InputType[] values() {
        return (InputType[])$VALUES.clone();
    }

    public static InputType valueOf(String $$0) {
        return Enum.valueOf(InputType.class, $$0);
    }

    public boolean isMouse() {
        return this == MOUSE;
    }

    public boolean isKeyboard() {
        return this == KEYBOARD_ARROW || this == KEYBOARD_TAB;
    }

    private static /* synthetic */ InputType[] c() {
        return new InputType[]{NONE, MOUSE, KEYBOARD_ARROW, KEYBOARD_TAB};
    }

    static {
        $VALUES = InputType.c();
    }
}

