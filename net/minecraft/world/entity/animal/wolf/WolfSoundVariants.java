/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.animal.wolf;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.animal.wolf.WolfSoundVariant;

public class WolfSoundVariants {
    public static final ResourceKey<WolfSoundVariant> CLASSIC = WolfSoundVariants.createKey(SoundSet.CLASSIC);
    public static final ResourceKey<WolfSoundVariant> PUGLIN = WolfSoundVariants.createKey(SoundSet.PUGLIN);
    public static final ResourceKey<WolfSoundVariant> SAD = WolfSoundVariants.createKey(SoundSet.SAD);
    public static final ResourceKey<WolfSoundVariant> ANGRY = WolfSoundVariants.createKey(SoundSet.ANGRY);
    public static final ResourceKey<WolfSoundVariant> GRUMPY = WolfSoundVariants.createKey(SoundSet.GRUMPY);
    public static final ResourceKey<WolfSoundVariant> BIG = WolfSoundVariants.createKey(SoundSet.BIG);
    public static final ResourceKey<WolfSoundVariant> CUTE = WolfSoundVariants.createKey(SoundSet.CUTE);

    private static ResourceKey<WolfSoundVariant> createKey(SoundSet $$0) {
        return ResourceKey.create(Registries.WOLF_SOUND_VARIANT, ResourceLocation.withDefaultNamespace($$0.getIdentifier()));
    }

    public static void bootstrap(BootstrapContext<WolfSoundVariant> $$0) {
        WolfSoundVariants.register($$0, CLASSIC, SoundSet.CLASSIC);
        WolfSoundVariants.register($$0, PUGLIN, SoundSet.PUGLIN);
        WolfSoundVariants.register($$0, SAD, SoundSet.SAD);
        WolfSoundVariants.register($$0, ANGRY, SoundSet.ANGRY);
        WolfSoundVariants.register($$0, GRUMPY, SoundSet.GRUMPY);
        WolfSoundVariants.register($$0, BIG, SoundSet.BIG);
        WolfSoundVariants.register($$0, CUTE, SoundSet.CUTE);
    }

    private static void register(BootstrapContext<WolfSoundVariant> $$0, ResourceKey<WolfSoundVariant> $$1, SoundSet $$2) {
        $$0.register($$1, SoundEvents.WOLF_SOUNDS.get((Object)$$2));
    }

    public static Holder<WolfSoundVariant> pickRandomSoundVariant(RegistryAccess $$0, RandomSource $$1) {
        return (Holder)$$0.lookupOrThrow(Registries.WOLF_SOUND_VARIANT).getRandom($$1).orElseThrow();
    }

    public static final class SoundSet
    extends Enum<SoundSet> {
        public static final /* enum */ SoundSet CLASSIC = new SoundSet("classic", "");
        public static final /* enum */ SoundSet PUGLIN = new SoundSet("puglin", "_puglin");
        public static final /* enum */ SoundSet SAD = new SoundSet("sad", "_sad");
        public static final /* enum */ SoundSet ANGRY = new SoundSet("angry", "_angry");
        public static final /* enum */ SoundSet GRUMPY = new SoundSet("grumpy", "_grumpy");
        public static final /* enum */ SoundSet BIG = new SoundSet("big", "_big");
        public static final /* enum */ SoundSet CUTE = new SoundSet("cute", "_cute");
        private final String identifier;
        private final String soundEventSuffix;
        private static final /* synthetic */ SoundSet[] $VALUES;

        public static SoundSet[] values() {
            return (SoundSet[])$VALUES.clone();
        }

        public static SoundSet valueOf(String $$0) {
            return Enum.valueOf(SoundSet.class, $$0);
        }

        private SoundSet(String $$0, String $$1) {
            this.identifier = $$0;
            this.soundEventSuffix = $$1;
        }

        public String getIdentifier() {
            return this.identifier;
        }

        public String getSoundEventSuffix() {
            return this.soundEventSuffix;
        }

        private static /* synthetic */ SoundSet[] c() {
            return new SoundSet[]{CLASSIC, PUGLIN, SAD, ANGRY, GRUMPY, BIG, CUTE};
        }

        static {
            $VALUES = SoundSet.c();
        }
    }
}

