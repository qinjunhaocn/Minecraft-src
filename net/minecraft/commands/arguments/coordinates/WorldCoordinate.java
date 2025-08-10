/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 */
package net.minecraft.commands.arguments.coordinates;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;

public class WorldCoordinate {
    private static final char PREFIX_RELATIVE = '~';
    public static final SimpleCommandExceptionType ERROR_EXPECTED_DOUBLE = new SimpleCommandExceptionType((Message)Component.translatable("argument.pos.missing.double"));
    public static final SimpleCommandExceptionType ERROR_EXPECTED_INT = new SimpleCommandExceptionType((Message)Component.translatable("argument.pos.missing.int"));
    private final boolean relative;
    private final double value;

    public WorldCoordinate(boolean $$0, double $$1) {
        this.relative = $$0;
        this.value = $$1;
    }

    public double get(double $$0) {
        if (this.relative) {
            return this.value + $$0;
        }
        return this.value;
    }

    public static WorldCoordinate parseDouble(StringReader $$0, boolean $$1) throws CommandSyntaxException {
        if ($$0.canRead() && $$0.peek() == '^') {
            throw Vec3Argument.ERROR_MIXED_TYPE.createWithContext((ImmutableStringReader)$$0);
        }
        if (!$$0.canRead()) {
            throw ERROR_EXPECTED_DOUBLE.createWithContext((ImmutableStringReader)$$0);
        }
        boolean $$2 = WorldCoordinate.isRelative($$0);
        int $$3 = $$0.getCursor();
        double $$4 = $$0.canRead() && $$0.peek() != ' ' ? $$0.readDouble() : 0.0;
        String $$5 = $$0.getString().substring($$3, $$0.getCursor());
        if ($$2 && $$5.isEmpty()) {
            return new WorldCoordinate(true, 0.0);
        }
        if (!$$5.contains(".") && !$$2 && $$1) {
            $$4 += 0.5;
        }
        return new WorldCoordinate($$2, $$4);
    }

    public static WorldCoordinate parseInt(StringReader $$0) throws CommandSyntaxException {
        double $$3;
        if ($$0.canRead() && $$0.peek() == '^') {
            throw Vec3Argument.ERROR_MIXED_TYPE.createWithContext((ImmutableStringReader)$$0);
        }
        if (!$$0.canRead()) {
            throw ERROR_EXPECTED_INT.createWithContext((ImmutableStringReader)$$0);
        }
        boolean $$1 = WorldCoordinate.isRelative($$0);
        if ($$0.canRead() && $$0.peek() != ' ') {
            double $$2 = $$1 ? $$0.readDouble() : (double)$$0.readInt();
        } else {
            $$3 = 0.0;
        }
        return new WorldCoordinate($$1, $$3);
    }

    public static boolean isRelative(StringReader $$0) {
        boolean $$2;
        if ($$0.peek() == '~') {
            boolean $$1 = true;
            $$0.skip();
        } else {
            $$2 = false;
        }
        return $$2;
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if (!($$0 instanceof WorldCoordinate)) {
            return false;
        }
        WorldCoordinate $$1 = (WorldCoordinate)$$0;
        if (this.relative != $$1.relative) {
            return false;
        }
        return Double.compare($$1.value, this.value) == 0;
    }

    public int hashCode() {
        int $$0 = this.relative ? 1 : 0;
        long $$1 = Double.doubleToLongBits(this.value);
        $$0 = 31 * $$0 + (int)($$1 ^ $$1 >>> 32);
        return $$0;
    }

    public boolean isRelative() {
        return this.relative;
    }
}

