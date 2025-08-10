/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;

public class ViewArea {
    protected final LevelRenderer levelRenderer;
    protected final Level level;
    protected int sectionGridSizeY;
    protected int sectionGridSizeX;
    protected int sectionGridSizeZ;
    private int viewDistance;
    private SectionPos cameraSectionPos;
    public SectionRenderDispatcher.RenderSection[] sections;

    public ViewArea(SectionRenderDispatcher $$0, Level $$1, int $$2, LevelRenderer $$3) {
        this.levelRenderer = $$3;
        this.level = $$1;
        this.setViewDistance($$2);
        this.createSections($$0);
        this.cameraSectionPos = SectionPos.of(this.viewDistance + 1, 0, this.viewDistance + 1);
    }

    protected void createSections(SectionRenderDispatcher $$0) {
        if (!Minecraft.getInstance().isSameThread()) {
            throw new IllegalStateException("createSections called from wrong thread: " + Thread.currentThread().getName());
        }
        int $$1 = this.sectionGridSizeX * this.sectionGridSizeY * this.sectionGridSizeZ;
        this.sections = new SectionRenderDispatcher.RenderSection[$$1];
        for (int $$2 = 0; $$2 < this.sectionGridSizeX; ++$$2) {
            for (int $$3 = 0; $$3 < this.sectionGridSizeY; ++$$3) {
                for (int $$4 = 0; $$4 < this.sectionGridSizeZ; ++$$4) {
                    int $$5 = this.getSectionIndex($$2, $$3, $$4);
                    SectionRenderDispatcher sectionRenderDispatcher = $$0;
                    Objects.requireNonNull(sectionRenderDispatcher);
                    this.sections[$$5] = new SectionRenderDispatcher.RenderSection(sectionRenderDispatcher, $$5, SectionPos.asLong($$2, $$3 + this.level.getMinSectionY(), $$4));
                }
            }
        }
    }

    public void releaseAllBuffers() {
        for (SectionRenderDispatcher.RenderSection $$0 : this.sections) {
            $$0.reset();
        }
    }

    private int getSectionIndex(int $$0, int $$1, int $$2) {
        return ($$2 * this.sectionGridSizeY + $$1) * this.sectionGridSizeX + $$0;
    }

    protected void setViewDistance(int $$0) {
        int $$1;
        this.sectionGridSizeX = $$1 = $$0 * 2 + 1;
        this.sectionGridSizeY = this.level.getSectionsCount();
        this.sectionGridSizeZ = $$1;
        this.viewDistance = $$0;
    }

    public int getViewDistance() {
        return this.viewDistance;
    }

    public LevelHeightAccessor getLevelHeightAccessor() {
        return this.level;
    }

    public void repositionCamera(SectionPos $$0) {
        for (int $$1 = 0; $$1 < this.sectionGridSizeX; ++$$1) {
            int $$2 = $$0.x() - this.viewDistance;
            int $$3 = $$2 + Math.floorMod($$1 - $$2, this.sectionGridSizeX);
            for (int $$4 = 0; $$4 < this.sectionGridSizeZ; ++$$4) {
                int $$5 = $$0.z() - this.viewDistance;
                int $$6 = $$5 + Math.floorMod($$4 - $$5, this.sectionGridSizeZ);
                for (int $$7 = 0; $$7 < this.sectionGridSizeY; ++$$7) {
                    int $$8 = this.level.getMinSectionY() + $$7;
                    SectionRenderDispatcher.RenderSection $$9 = this.sections[this.getSectionIndex($$1, $$7, $$4)];
                    long $$10 = $$9.getSectionNode();
                    if ($$10 == SectionPos.asLong($$3, $$8, $$6)) continue;
                    $$9.setSectionNode(SectionPos.asLong($$3, $$8, $$6));
                }
            }
        }
        this.cameraSectionPos = $$0;
        this.levelRenderer.getSectionOcclusionGraph().invalidate();
    }

    public SectionPos getCameraSectionPos() {
        return this.cameraSectionPos;
    }

    public void setDirty(int $$0, int $$1, int $$2, boolean $$3) {
        SectionRenderDispatcher.RenderSection $$4 = this.getRenderSection($$0, $$1, $$2);
        if ($$4 != null) {
            $$4.setDirty($$3);
        }
    }

    @Nullable
    protected SectionRenderDispatcher.RenderSection getRenderSectionAt(BlockPos $$0) {
        return this.getRenderSection(SectionPos.asLong($$0));
    }

    @Nullable
    protected SectionRenderDispatcher.RenderSection getRenderSection(long $$0) {
        int $$1 = SectionPos.x($$0);
        int $$2 = SectionPos.y($$0);
        int $$3 = SectionPos.z($$0);
        return this.getRenderSection($$1, $$2, $$3);
    }

    @Nullable
    private SectionRenderDispatcher.RenderSection getRenderSection(int $$0, int $$1, int $$2) {
        if (!this.containsSection($$0, $$1, $$2)) {
            return null;
        }
        int $$3 = $$1 - this.level.getMinSectionY();
        int $$4 = Math.floorMod($$0, this.sectionGridSizeX);
        int $$5 = Math.floorMod($$2, this.sectionGridSizeZ);
        return this.sections[this.getSectionIndex($$4, $$3, $$5)];
    }

    private boolean containsSection(int $$0, int $$1, int $$2) {
        if ($$1 < this.level.getMinSectionY() || $$1 > this.level.getMaxSectionY()) {
            return false;
        }
        if ($$0 < this.cameraSectionPos.x() - this.viewDistance || $$0 > this.cameraSectionPos.x() + this.viewDistance) {
            return false;
        }
        return $$2 >= this.cameraSectionPos.z() - this.viewDistance && $$2 <= this.cameraSectionPos.z() + this.viewDistance;
    }
}

