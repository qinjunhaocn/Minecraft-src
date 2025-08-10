/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 *  org.joml.Matrix3x2f
 *  org.joml.Vector2f
 */
package net.minecraft.client.gui.navigation;

import javax.annotation.Nullable;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.util.Mth;
import org.joml.Matrix3x2f;
import org.joml.Vector2f;

public record ScreenRectangle(ScreenPosition position, int width, int height) {
    private static final ScreenRectangle EMPTY = new ScreenRectangle(0, 0, 0, 0);

    public ScreenRectangle(int $$0, int $$1, int $$2, int $$3) {
        this(new ScreenPosition($$0, $$1), $$2, $$3);
    }

    public static ScreenRectangle empty() {
        return EMPTY;
    }

    public static ScreenRectangle of(ScreenAxis $$0, int $$1, int $$2, int $$3, int $$4) {
        return switch ($$0) {
            default -> throw new MatchException(null, null);
            case ScreenAxis.HORIZONTAL -> new ScreenRectangle($$1, $$2, $$3, $$4);
            case ScreenAxis.VERTICAL -> new ScreenRectangle($$2, $$1, $$4, $$3);
        };
    }

    public ScreenRectangle step(ScreenDirection $$0) {
        return new ScreenRectangle(this.position.step($$0), this.width, this.height);
    }

    public int getLength(ScreenAxis $$0) {
        return switch ($$0) {
            default -> throw new MatchException(null, null);
            case ScreenAxis.HORIZONTAL -> this.width;
            case ScreenAxis.VERTICAL -> this.height;
        };
    }

    public int getBoundInDirection(ScreenDirection $$0) {
        ScreenAxis $$1 = $$0.getAxis();
        if ($$0.isPositive()) {
            return this.position.getCoordinate($$1) + this.getLength($$1) - 1;
        }
        return this.position.getCoordinate($$1);
    }

    public ScreenRectangle getBorder(ScreenDirection $$0) {
        int $$1 = this.getBoundInDirection($$0);
        ScreenAxis $$2 = $$0.getAxis().orthogonal();
        int $$3 = this.getBoundInDirection($$2.getNegative());
        int $$4 = this.getLength($$2);
        return ScreenRectangle.of($$0.getAxis(), $$1, $$3, 1, $$4).step($$0);
    }

    public boolean overlaps(ScreenRectangle $$0) {
        return this.overlapsInAxis($$0, ScreenAxis.HORIZONTAL) && this.overlapsInAxis($$0, ScreenAxis.VERTICAL);
    }

    public boolean overlapsInAxis(ScreenRectangle $$0, ScreenAxis $$1) {
        int $$2 = this.getBoundInDirection($$1.getNegative());
        int $$3 = $$0.getBoundInDirection($$1.getNegative());
        int $$4 = this.getBoundInDirection($$1.getPositive());
        int $$5 = $$0.getBoundInDirection($$1.getPositive());
        return Math.max($$2, $$3) <= Math.min($$4, $$5);
    }

    public int getCenterInAxis(ScreenAxis $$0) {
        return (this.getBoundInDirection($$0.getPositive()) + this.getBoundInDirection($$0.getNegative())) / 2;
    }

    @Nullable
    public ScreenRectangle intersection(ScreenRectangle $$0) {
        int $$1 = Math.max(this.left(), $$0.left());
        int $$2 = Math.max(this.top(), $$0.top());
        int $$3 = Math.min(this.right(), $$0.right());
        int $$4 = Math.min(this.bottom(), $$0.bottom());
        if ($$1 >= $$3 || $$2 >= $$4) {
            return null;
        }
        return new ScreenRectangle($$1, $$2, $$3 - $$1, $$4 - $$2);
    }

    public boolean intersects(ScreenRectangle $$0) {
        return this.left() < $$0.right() && this.right() > $$0.left() && this.top() < $$0.bottom() && this.bottom() > $$0.top();
    }

    public boolean encompasses(ScreenRectangle $$0) {
        return $$0.left() >= this.left() && $$0.top() >= this.top() && $$0.right() <= this.right() && $$0.bottom() <= this.bottom();
    }

    public int top() {
        return this.position.y();
    }

    public int bottom() {
        return this.position.y() + this.height;
    }

    public int left() {
        return this.position.x();
    }

    public int right() {
        return this.position.x() + this.width;
    }

    public boolean containsPoint(int $$0, int $$1) {
        return $$0 >= this.left() && $$0 < this.right() && $$1 >= this.top() && $$1 < this.bottom();
    }

    public ScreenRectangle transformAxisAligned(Matrix3x2f $$0) {
        Vector2f $$1 = $$0.transformPosition((float)this.left(), (float)this.top(), new Vector2f());
        Vector2f $$2 = $$0.transformPosition((float)this.right(), (float)this.bottom(), new Vector2f());
        return new ScreenRectangle(Mth.floor($$1.x), Mth.floor($$1.y), Mth.floor($$2.x - $$1.x), Mth.floor($$2.y - $$1.y));
    }

    public ScreenRectangle transformMaxBounds(Matrix3x2f $$0) {
        Vector2f $$1 = $$0.transformPosition((float)this.left(), (float)this.top(), new Vector2f());
        Vector2f $$2 = $$0.transformPosition((float)this.right(), (float)this.top(), new Vector2f());
        Vector2f $$3 = $$0.transformPosition((float)this.left(), (float)this.bottom(), new Vector2f());
        Vector2f $$4 = $$0.transformPosition((float)this.right(), (float)this.bottom(), new Vector2f());
        float $$5 = Math.min(Math.min($$1.x(), $$3.x()), Math.min($$2.x(), $$4.x()));
        float $$6 = Math.max(Math.max($$1.x(), $$3.x()), Math.max($$2.x(), $$4.x()));
        float $$7 = Math.min(Math.min($$1.y(), $$3.y()), Math.min($$2.y(), $$4.y()));
        float $$8 = Math.max(Math.max($$1.y(), $$3.y()), Math.max($$2.y(), $$4.y()));
        return new ScreenRectangle(Mth.floor($$5), Mth.floor($$7), Mth.ceil($$6 - $$5), Mth.ceil($$8 - $$7));
    }
}

