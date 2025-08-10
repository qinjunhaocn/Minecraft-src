/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.scores;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

public class ScoreboardSaveData
extends SavedData {
    public static final String FILE_ID = "scoreboard";
    private final Scoreboard scoreboard;

    public ScoreboardSaveData(Scoreboard $$0) {
        this.scoreboard = $$0;
    }

    public void loadFrom(Packed $$02) {
        $$02.objectives().forEach(this.scoreboard::loadObjective);
        $$02.scores().forEach(this.scoreboard::loadPlayerScore);
        $$02.displaySlots().forEach(($$0, $$1) -> {
            Objective $$2 = this.scoreboard.getObjective((String)$$1);
            this.scoreboard.setDisplayObjective((DisplaySlot)$$0, $$2);
        });
        $$02.teams().forEach(this.scoreboard::loadPlayerTeam);
    }

    public Packed pack() {
        EnumMap<DisplaySlot, String> $$0 = new EnumMap<DisplaySlot, String>(DisplaySlot.class);
        for (DisplaySlot $$1 : DisplaySlot.values()) {
            Objective $$2 = this.scoreboard.getDisplayObjective($$1);
            if ($$2 == null) continue;
            $$0.put($$1, $$2.getName());
        }
        return new Packed(this.scoreboard.getObjectives().stream().map(Objective::pack).toList(), this.scoreboard.packPlayerScores(), $$0, this.scoreboard.getPlayerTeams().stream().map(PlayerTeam::pack).toList());
    }

    public record Packed(List<Objective.Packed> objectives, List<Scoreboard.PackedScore> scores, Map<DisplaySlot, String> displaySlots, List<PlayerTeam.Packed> teams) {
        public static final Codec<Packed> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)Objective.Packed.CODEC.listOf().optionalFieldOf("Objectives", (Object)List.of()).forGetter(Packed::objectives), (App)Scoreboard.PackedScore.CODEC.listOf().optionalFieldOf("PlayerScores", (Object)List.of()).forGetter(Packed::scores), (App)Codec.unboundedMap(DisplaySlot.CODEC, (Codec)Codec.STRING).optionalFieldOf("DisplaySlots", (Object)Map.of()).forGetter(Packed::displaySlots), (App)PlayerTeam.Packed.CODEC.listOf().optionalFieldOf("Teams", (Object)List.of()).forGetter(Packed::teams)).apply((Applicative)$$0, Packed::new));
    }
}

