/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.debug;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Set;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;

public class VillageSectionsDebugRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private static final int MAX_RENDER_DIST_FOR_VILLAGE_SECTIONS = 60;
    private final Set<SectionPos> villageSections = Sets.newHashSet();

    VillageSectionsDebugRenderer() {
    }

    @Override
    public void clear() {
        this.villageSections.clear();
    }

    public void setVillageSection(SectionPos $$0) {
        this.villageSections.add($$0);
    }

    public void setNotVillageSection(SectionPos $$0) {
        this.villageSections.remove($$0);
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, double $$2, double $$32, double $$4) {
        BlockPos $$5 = BlockPos.containing($$2, $$32, $$4);
        this.villageSections.forEach($$3 -> {
            if ($$5.closerThan($$3.center(), 60.0)) {
                VillageSectionsDebugRenderer.highlightVillageSection($$0, $$1, $$3);
            }
        });
    }

    private static void highlightVillageSection(PoseStack $$0, MultiBufferSource $$1, SectionPos $$2) {
        DebugRenderer.renderFilledUnitCube($$0, $$1, $$2.center(), 0.2f, 1.0f, 0.2f, 0.15f);
    }
}

