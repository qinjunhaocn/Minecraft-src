/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.monster.warden;

import java.util.Arrays;
import net.minecraft.Util;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public final class AngerLevel
extends Enum<AngerLevel> {
    public static final /* enum */ AngerLevel CALM = new AngerLevel(0, SoundEvents.WARDEN_AMBIENT, SoundEvents.WARDEN_LISTENING);
    public static final /* enum */ AngerLevel AGITATED = new AngerLevel(40, SoundEvents.WARDEN_AGITATED, SoundEvents.WARDEN_LISTENING_ANGRY);
    public static final /* enum */ AngerLevel ANGRY = new AngerLevel(80, SoundEvents.WARDEN_ANGRY, SoundEvents.WARDEN_LISTENING_ANGRY);
    private static final AngerLevel[] SORTED_LEVELS;
    private final int minimumAnger;
    private final SoundEvent ambientSound;
    private final SoundEvent listeningSound;
    private static final /* synthetic */ AngerLevel[] $VALUES;

    public static AngerLevel[] values() {
        return (AngerLevel[])$VALUES.clone();
    }

    public static AngerLevel valueOf(String $$0) {
        return Enum.valueOf(AngerLevel.class, $$0);
    }

    private AngerLevel(int $$0, SoundEvent $$1, SoundEvent $$2) {
        this.minimumAnger = $$0;
        this.ambientSound = $$1;
        this.listeningSound = $$2;
    }

    public int getMinimumAnger() {
        return this.minimumAnger;
    }

    public SoundEvent getAmbientSound() {
        return this.ambientSound;
    }

    public SoundEvent getListeningSound() {
        return this.listeningSound;
    }

    public static AngerLevel byAnger(int $$0) {
        for (AngerLevel $$1 : SORTED_LEVELS) {
            if ($$0 < $$1.minimumAnger) continue;
            return $$1;
        }
        return CALM;
    }

    public boolean isAngry() {
        return this == ANGRY;
    }

    private static /* synthetic */ AngerLevel[] e() {
        return new AngerLevel[]{CALM, AGITATED, ANGRY};
    }

    static {
        $VALUES = AngerLevel.e();
        SORTED_LEVELS = Util.make(AngerLevel.values(), $$02 -> Arrays.sort($$02, ($$0, $$1) -> Integer.compare($$1.minimumAnger, $$0.minimumAnger)));
    }
}

