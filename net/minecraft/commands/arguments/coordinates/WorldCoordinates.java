/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 */
package net.minecraft.commands.arguments.coordinates;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.coordinates.WorldCoordinate;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class WorldCoordinates
implements Coordinates {
    private final WorldCoordinate x;
    private final WorldCoordinate y;
    private final WorldCoordinate z;

    public WorldCoordinates(WorldCoordinate $$0, WorldCoordinate $$1, WorldCoordinate $$2) {
        this.x = $$0;
        this.y = $$1;
        this.z = $$2;
    }

    @Override
    public Vec3 getPosition(CommandSourceStack $$0) {
        Vec3 $$1 = $$0.getPosition();
        return new Vec3(this.x.get($$1.x), this.y.get($$1.y), this.z.get($$1.z));
    }

    @Override
    public Vec2 getRotation(CommandSourceStack $$0) {
        Vec2 $$1 = $$0.getRotation();
        return new Vec2((float)this.x.get($$1.x), (float)this.y.get($$1.y));
    }

    @Override
    public boolean isXRelative() {
        return this.x.isRelative();
    }

    @Override
    public boolean isYRelative() {
        return this.y.isRelative();
    }

    @Override
    public boolean isZRelative() {
        return this.z.isRelative();
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if (!($$0 instanceof WorldCoordinates)) {
            return false;
        }
        WorldCoordinates $$1 = (WorldCoordinates)$$0;
        if (!this.x.equals($$1.x)) {
            return false;
        }
        if (!this.y.equals($$1.y)) {
            return false;
        }
        return this.z.equals($$1.z);
    }

    public static WorldCoordinates parseInt(StringReader $$0) throws CommandSyntaxException {
        int $$1 = $$0.getCursor();
        WorldCoordinate $$2 = WorldCoordinate.parseInt($$0);
        if (!$$0.canRead() || $$0.peek() != ' ') {
            $$0.setCursor($$1);
            throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext((ImmutableStringReader)$$0);
        }
        $$0.skip();
        WorldCoordinate $$3 = WorldCoordinate.parseInt($$0);
        if (!$$0.canRead() || $$0.peek() != ' ') {
            $$0.setCursor($$1);
            throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext((ImmutableStringReader)$$0);
        }
        $$0.skip();
        WorldCoordinate $$4 = WorldCoordinate.parseInt($$0);
        return new WorldCoordinates($$2, $$3, $$4);
    }

    public static WorldCoordinates parseDouble(StringReader $$0, boolean $$1) throws CommandSyntaxException {
        int $$2 = $$0.getCursor();
        WorldCoordinate $$3 = WorldCoordinate.parseDouble($$0, $$1);
        if (!$$0.canRead() || $$0.peek() != ' ') {
            $$0.setCursor($$2);
            throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext((ImmutableStringReader)$$0);
        }
        $$0.skip();
        WorldCoordinate $$4 = WorldCoordinate.parseDouble($$0, false);
        if (!$$0.canRead() || $$0.peek() != ' ') {
            $$0.setCursor($$2);
            throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext((ImmutableStringReader)$$0);
        }
        $$0.skip();
        WorldCoordinate $$5 = WorldCoordinate.parseDouble($$0, $$1);
        return new WorldCoordinates($$3, $$4, $$5);
    }

    public static WorldCoordinates absolute(double $$0, double $$1, double $$2) {
        return new WorldCoordinates(new WorldCoordinate(false, $$0), new WorldCoordinate(false, $$1), new WorldCoordinate(false, $$2));
    }

    public static WorldCoordinates absolute(Vec2 $$0) {
        return new WorldCoordinates(new WorldCoordinate(false, $$0.x), new WorldCoordinate(false, $$0.y), new WorldCoordinate(true, 0.0));
    }

    public int hashCode() {
        int $$0 = this.x.hashCode();
        $$0 = 31 * $$0 + this.y.hashCode();
        $$0 = 31 * $$0 + this.z.hashCode();
        return $$0;
    }
}

