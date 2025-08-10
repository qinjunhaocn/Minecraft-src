/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 */
package net.minecraft.client.renderer;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec3;

public abstract class DimensionSpecialEffects {
    private static final Object2ObjectMap<ResourceLocation, DimensionSpecialEffects> EFFECTS = (Object2ObjectMap)Util.make(new Object2ObjectArrayMap(), $$0 -> {
        OverworldEffects $$1 = new OverworldEffects();
        $$0.defaultReturnValue((Object)$$1);
        $$0.put((Object)BuiltinDimensionTypes.OVERWORLD_EFFECTS, (Object)$$1);
        $$0.put((Object)BuiltinDimensionTypes.NETHER_EFFECTS, (Object)new NetherEffects());
        $$0.put((Object)BuiltinDimensionTypes.END_EFFECTS, (Object)new EndEffects());
    });
    private final SkyType skyType;
    private final boolean forceBrightLightmap;
    private final boolean constantAmbientLight;

    public DimensionSpecialEffects(SkyType $$0, boolean $$1, boolean $$2) {
        this.skyType = $$0;
        this.forceBrightLightmap = $$1;
        this.constantAmbientLight = $$2;
    }

    public static DimensionSpecialEffects forType(DimensionType $$0) {
        return (DimensionSpecialEffects)EFFECTS.get((Object)$$0.effectsLocation());
    }

    public boolean isSunriseOrSunset(float $$0) {
        return false;
    }

    public int getSunriseOrSunsetColor(float $$0) {
        return 0;
    }

    public abstract Vec3 getBrightnessDependentFogColor(Vec3 var1, float var2);

    public abstract boolean isFoggyAt(int var1, int var2);

    public SkyType skyType() {
        return this.skyType;
    }

    public boolean forceBrightLightmap() {
        return this.forceBrightLightmap;
    }

    public boolean constantAmbientLight() {
        return this.constantAmbientLight;
    }

    public static final class SkyType
    extends Enum<SkyType> {
        public static final /* enum */ SkyType NONE = new SkyType();
        public static final /* enum */ SkyType OVERWORLD = new SkyType();
        public static final /* enum */ SkyType END = new SkyType();
        private static final /* synthetic */ SkyType[] $VALUES;

        public static SkyType[] values() {
            return (SkyType[])$VALUES.clone();
        }

        public static SkyType valueOf(String $$0) {
            return Enum.valueOf(SkyType.class, $$0);
        }

        private static /* synthetic */ SkyType[] a() {
            return new SkyType[]{NONE, OVERWORLD, END};
        }

        static {
            $VALUES = SkyType.a();
        }
    }

    public static class OverworldEffects
    extends DimensionSpecialEffects {
        private static final float SUNRISE_AND_SUNSET_TIMESPAN = 0.4f;

        public OverworldEffects() {
            super(SkyType.OVERWORLD, false, false);
        }

        @Override
        public boolean isSunriseOrSunset(float $$0) {
            float $$1 = Mth.cos($$0 * ((float)Math.PI * 2));
            return $$1 >= -0.4f && $$1 <= 0.4f;
        }

        @Override
        public int getSunriseOrSunsetColor(float $$0) {
            float $$1 = Mth.cos($$0 * ((float)Math.PI * 2));
            float $$2 = $$1 / 0.4f * 0.5f + 0.5f;
            float $$3 = Mth.square(1.0f - (1.0f - Mth.sin($$2 * (float)Math.PI)) * 0.99f);
            return ARGB.colorFromFloat($$3, $$2 * 0.3f + 0.7f, $$2 * $$2 * 0.7f + 0.2f, 0.2f);
        }

        @Override
        public Vec3 getBrightnessDependentFogColor(Vec3 $$0, float $$1) {
            return $$0.multiply($$1 * 0.94f + 0.06f, $$1 * 0.94f + 0.06f, $$1 * 0.91f + 0.09f);
        }

        @Override
        public boolean isFoggyAt(int $$0, int $$1) {
            return false;
        }
    }

    public static class NetherEffects
    extends DimensionSpecialEffects {
        public NetherEffects() {
            super(SkyType.NONE, false, true);
        }

        @Override
        public Vec3 getBrightnessDependentFogColor(Vec3 $$0, float $$1) {
            return $$0;
        }

        @Override
        public boolean isFoggyAt(int $$0, int $$1) {
            return true;
        }
    }

    public static class EndEffects
    extends DimensionSpecialEffects {
        public EndEffects() {
            super(SkyType.END, true, false);
        }

        @Override
        public Vec3 getBrightnessDependentFogColor(Vec3 $$0, float $$1) {
            return $$0.scale(0.15f);
        }

        @Override
        public boolean isFoggyAt(int $$0, int $$1) {
            return false;
        }
    }
}

