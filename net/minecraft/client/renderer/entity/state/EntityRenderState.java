/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity.state;

import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.CrashReportCategory;
import net.minecraft.client.renderer.entity.state.HitboxesRenderState;
import net.minecraft.client.renderer.entity.state.ServerHitboxesRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

public class EntityRenderState {
    public EntityType<?> entityType;
    public double x;
    public double y;
    public double z;
    public float ageInTicks;
    public float boundingBoxWidth;
    public float boundingBoxHeight;
    public float eyeHeight;
    public double distanceToCameraSq;
    public boolean isInvisible;
    public boolean isDiscrete;
    public boolean displayFireAnimation;
    @Nullable
    public Vec3 passengerOffset;
    @Nullable
    public Component nameTag;
    @Nullable
    public Vec3 nameTagAttachment;
    @Nullable
    public List<LeashState> leashStates;
    @Nullable
    public HitboxesRenderState hitboxesRenderState;
    @Nullable
    public ServerHitboxesRenderState serverHitboxesRenderState;

    public void fillCrashReportCategory(CrashReportCategory $$0) {
        $$0.setDetail("EntityRenderState", this.getClass().getCanonicalName());
        $$0.setDetail("Entity's Exact location", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", this.x, this.y, this.z));
    }

    public static class LeashState {
        public Vec3 offset = Vec3.ZERO;
        public Vec3 start = Vec3.ZERO;
        public Vec3 end = Vec3.ZERO;
        public int startBlockLight = 0;
        public int endBlockLight = 0;
        public int startSkyLight = 15;
        public int endSkyLight = 15;
        public boolean slack = true;
    }
}

