/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.gameevent.vibrations;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.world.level.gameevent.vibrations.VibrationInfo;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import org.apache.commons.lang3.tuple.Pair;

public class VibrationSelector {
    public static final Codec<VibrationSelector> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)VibrationInfo.CODEC.lenientOptionalFieldOf("event").forGetter($$0 -> $$0.currentVibrationData.map(Pair::getLeft)), (App)Codec.LONG.fieldOf("tick").forGetter($$0 -> $$0.currentVibrationData.map(Pair::getRight).orElse(-1L))).apply((Applicative)$$02, VibrationSelector::new));
    private Optional<Pair<VibrationInfo, Long>> currentVibrationData;

    public VibrationSelector(Optional<VibrationInfo> $$0, long $$12) {
        this.currentVibrationData = $$0.map($$1 -> Pair.of($$1, $$12));
    }

    public VibrationSelector() {
        this.currentVibrationData = Optional.empty();
    }

    public void addCandidate(VibrationInfo $$0, long $$1) {
        if (this.shouldReplaceVibration($$0, $$1)) {
            this.currentVibrationData = Optional.of(Pair.of($$0, $$1));
        }
    }

    private boolean shouldReplaceVibration(VibrationInfo $$0, long $$1) {
        if (this.currentVibrationData.isEmpty()) {
            return true;
        }
        Pair<VibrationInfo, Long> $$2 = this.currentVibrationData.get();
        long $$3 = $$2.getRight();
        if ($$1 != $$3) {
            return false;
        }
        VibrationInfo $$4 = $$2.getLeft();
        if ($$0.distance() < $$4.distance()) {
            return true;
        }
        if ($$0.distance() > $$4.distance()) {
            return false;
        }
        return VibrationSystem.getGameEventFrequency($$0.gameEvent()) > VibrationSystem.getGameEventFrequency($$4.gameEvent());
    }

    public Optional<VibrationInfo> chosenCandidate(long $$0) {
        if (this.currentVibrationData.isEmpty()) {
            return Optional.empty();
        }
        if (this.currentVibrationData.get().getRight() < $$0) {
            return Optional.of(this.currentVibrationData.get().getLeft());
        }
        return Optional.empty();
    }

    public void startOver() {
        this.currentVibrationData = Optional.empty();
    }
}

