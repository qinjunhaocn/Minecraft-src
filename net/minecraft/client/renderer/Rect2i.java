/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer;

public class Rect2i {
    private int xPos;
    private int yPos;
    private int width;
    private int height;

    public Rect2i(int $$0, int $$1, int $$2, int $$3) {
        this.xPos = $$0;
        this.yPos = $$1;
        this.width = $$2;
        this.height = $$3;
    }

    public Rect2i intersect(Rect2i $$0) {
        int $$1 = this.xPos;
        int $$2 = this.yPos;
        int $$3 = this.xPos + this.width;
        int $$4 = this.yPos + this.height;
        int $$5 = $$0.getX();
        int $$6 = $$0.getY();
        int $$7 = $$5 + $$0.getWidth();
        int $$8 = $$6 + $$0.getHeight();
        this.xPos = Math.max($$1, $$5);
        this.yPos = Math.max($$2, $$6);
        this.width = Math.max(0, Math.min($$3, $$7) - this.xPos);
        this.height = Math.max(0, Math.min($$4, $$8) - this.yPos);
        return this;
    }

    public int getX() {
        return this.xPos;
    }

    public int getY() {
        return this.yPos;
    }

    public void setX(int $$0) {
        this.xPos = $$0;
    }

    public void setY(int $$0) {
        this.yPos = $$0;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setWidth(int $$0) {
        this.width = $$0;
    }

    public void setHeight(int $$0) {
        this.height = $$0;
    }

    public void setPosition(int $$0, int $$1) {
        this.xPos = $$0;
        this.yPos = $$1;
    }

    public boolean contains(int $$0, int $$1) {
        return $$0 >= this.xPos && $$0 <= this.xPos + this.width && $$1 >= this.yPos && $$1 <= this.yPos + this.height;
    }
}

