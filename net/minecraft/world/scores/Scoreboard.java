/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMaps
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Reference2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.world.scores;

import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerScoreEntry;
import net.minecraft.world.scores.PlayerScores;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.slf4j.Logger;

public class Scoreboard {
    public static final String HIDDEN_SCORE_PREFIX = "#";
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Object2ObjectMap<String, Objective> objectivesByName = new Object2ObjectOpenHashMap(16, 0.5f);
    private final Reference2ObjectMap<ObjectiveCriteria, List<Objective>> objectivesByCriteria = new Reference2ObjectOpenHashMap();
    private final Map<String, PlayerScores> playerScores = new Object2ObjectOpenHashMap(16, 0.5f);
    private final Map<DisplaySlot, Objective> displayObjectives = new EnumMap<DisplaySlot, Objective>(DisplaySlot.class);
    private final Object2ObjectMap<String, PlayerTeam> teamsByName = new Object2ObjectOpenHashMap();
    private final Object2ObjectMap<String, PlayerTeam> teamsByPlayer = new Object2ObjectOpenHashMap();

    @Nullable
    public Objective getObjective(@Nullable String $$0) {
        return (Objective)this.objectivesByName.get((Object)$$0);
    }

    public Objective addObjective(String $$02, ObjectiveCriteria $$1, Component $$2, ObjectiveCriteria.RenderType $$3, boolean $$4, @Nullable NumberFormat $$5) {
        if (this.objectivesByName.containsKey((Object)$$02)) {
            throw new IllegalArgumentException("An objective with the name '" + $$02 + "' already exists!");
        }
        Objective $$6 = new Objective(this, $$02, $$1, $$2, $$3, $$4, $$5);
        ((List)this.objectivesByCriteria.computeIfAbsent((Object)$$1, $$0 -> Lists.newArrayList())).add($$6);
        this.objectivesByName.put((Object)$$02, (Object)$$6);
        this.onObjectiveAdded($$6);
        return $$6;
    }

    public final void forAllObjectives(ObjectiveCriteria $$0, ScoreHolder $$1, Consumer<ScoreAccess> $$22) {
        ((List)this.objectivesByCriteria.getOrDefault((Object)$$0, Collections.emptyList())).forEach($$2 -> $$22.accept(this.getOrCreatePlayerScore($$1, (Objective)$$2, true)));
    }

    private PlayerScores getOrCreatePlayerInfo(String $$02) {
        return this.playerScores.computeIfAbsent($$02, $$0 -> new PlayerScores());
    }

    public ScoreAccess getOrCreatePlayerScore(ScoreHolder $$0, Objective $$1) {
        return this.getOrCreatePlayerScore($$0, $$1, false);
    }

    public ScoreAccess getOrCreatePlayerScore(final ScoreHolder $$0, final Objective $$12, boolean $$2) {
        final boolean $$3 = $$2 || !$$12.getCriteria().isReadOnly();
        PlayerScores $$4 = this.getOrCreatePlayerInfo($$0.getScoreboardName());
        final MutableBoolean $$5 = new MutableBoolean();
        final Score $$6 = $$4.getOrCreate($$12, $$1 -> $$5.setTrue());
        return new ScoreAccess(){

            @Override
            public int get() {
                return $$6.value();
            }

            @Override
            public void set(int $$02) {
                Component $$2;
                if (!$$3) {
                    throw new IllegalStateException("Cannot modify read-only score");
                }
                boolean $$1 = $$5.isTrue();
                if ($$12.displayAutoUpdate() && ($$2 = $$0.getDisplayName()) != null && !$$2.equals($$6.display())) {
                    $$6.display($$2);
                    $$1 = true;
                }
                if ($$02 != $$6.value()) {
                    $$6.value($$02);
                    $$1 = true;
                }
                if ($$1) {
                    this.sendScoreToPlayers();
                }
            }

            @Override
            @Nullable
            public Component display() {
                return $$6.display();
            }

            @Override
            public void display(@Nullable Component $$02) {
                if ($$5.isTrue() || !Objects.equals($$02, $$6.display())) {
                    $$6.display($$02);
                    this.sendScoreToPlayers();
                }
            }

            @Override
            public void numberFormatOverride(@Nullable NumberFormat $$02) {
                $$6.numberFormat($$02);
                this.sendScoreToPlayers();
            }

            @Override
            public boolean locked() {
                return $$6.isLocked();
            }

            @Override
            public void unlock() {
                this.setLocked(false);
            }

            @Override
            public void lock() {
                this.setLocked(true);
            }

            private void setLocked(boolean $$02) {
                $$6.setLocked($$02);
                if ($$5.isTrue()) {
                    this.sendScoreToPlayers();
                }
                Scoreboard.this.onScoreLockChanged($$0, $$12);
            }

            private void sendScoreToPlayers() {
                Scoreboard.this.onScoreChanged($$0, $$12, $$6);
                $$5.setFalse();
            }
        };
    }

    @Nullable
    public ReadOnlyScoreInfo getPlayerScoreInfo(ScoreHolder $$0, Objective $$1) {
        PlayerScores $$2 = this.playerScores.get($$0.getScoreboardName());
        if ($$2 != null) {
            return $$2.get($$1);
        }
        return null;
    }

    public Collection<PlayerScoreEntry> listPlayerScores(Objective $$0) {
        ArrayList<PlayerScoreEntry> $$1 = new ArrayList<PlayerScoreEntry>();
        this.playerScores.forEach(($$2, $$3) -> {
            Score $$4 = $$3.get($$0);
            if ($$4 != null) {
                $$1.add(new PlayerScoreEntry((String)$$2, $$4.value(), $$4.display(), $$4.numberFormat()));
            }
        });
        return $$1;
    }

    public Collection<Objective> getObjectives() {
        return this.objectivesByName.values();
    }

    public Collection<String> getObjectiveNames() {
        return this.objectivesByName.keySet();
    }

    public Collection<ScoreHolder> getTrackedPlayers() {
        return this.playerScores.keySet().stream().map(ScoreHolder::forNameOnly).toList();
    }

    public void resetAllPlayerScores(ScoreHolder $$0) {
        PlayerScores $$1 = this.playerScores.remove($$0.getScoreboardName());
        if ($$1 != null) {
            this.onPlayerRemoved($$0);
        }
    }

    public void resetSinglePlayerScore(ScoreHolder $$0, Objective $$1) {
        PlayerScores $$2 = this.playerScores.get($$0.getScoreboardName());
        if ($$2 != null) {
            boolean $$3 = $$2.remove($$1);
            if (!$$2.hasScores()) {
                PlayerScores $$4 = this.playerScores.remove($$0.getScoreboardName());
                if ($$4 != null) {
                    this.onPlayerRemoved($$0);
                }
            } else if ($$3) {
                this.onPlayerScoreRemoved($$0, $$1);
            }
        }
    }

    public Object2IntMap<Objective> listPlayerScores(ScoreHolder $$0) {
        PlayerScores $$1 = this.playerScores.get($$0.getScoreboardName());
        return $$1 != null ? $$1.listScores() : Object2IntMaps.emptyMap();
    }

    public void removeObjective(Objective $$0) {
        this.objectivesByName.remove((Object)$$0.getName());
        for (DisplaySlot $$1 : DisplaySlot.values()) {
            if (this.getDisplayObjective($$1) != $$0) continue;
            this.setDisplayObjective($$1, null);
        }
        List $$2 = (List)this.objectivesByCriteria.get((Object)$$0.getCriteria());
        if ($$2 != null) {
            $$2.remove($$0);
        }
        for (PlayerScores $$3 : this.playerScores.values()) {
            $$3.remove($$0);
        }
        this.onObjectiveRemoved($$0);
    }

    public void setDisplayObjective(DisplaySlot $$0, @Nullable Objective $$1) {
        this.displayObjectives.put($$0, $$1);
    }

    @Nullable
    public Objective getDisplayObjective(DisplaySlot $$0) {
        return this.displayObjectives.get($$0);
    }

    @Nullable
    public PlayerTeam getPlayerTeam(String $$0) {
        return (PlayerTeam)this.teamsByName.get((Object)$$0);
    }

    public PlayerTeam addPlayerTeam(String $$0) {
        PlayerTeam $$1 = this.getPlayerTeam($$0);
        if ($$1 != null) {
            LOGGER.warn("Requested creation of existing team '{}'", (Object)$$0);
            return $$1;
        }
        $$1 = new PlayerTeam(this, $$0);
        this.teamsByName.put((Object)$$0, (Object)$$1);
        this.onTeamAdded($$1);
        return $$1;
    }

    public void removePlayerTeam(PlayerTeam $$0) {
        this.teamsByName.remove((Object)$$0.getName());
        for (String $$1 : $$0.getPlayers()) {
            this.teamsByPlayer.remove((Object)$$1);
        }
        this.onTeamRemoved($$0);
    }

    public boolean addPlayerToTeam(String $$0, PlayerTeam $$1) {
        if (this.getPlayersTeam($$0) != null) {
            this.removePlayerFromTeam($$0);
        }
        this.teamsByPlayer.put((Object)$$0, (Object)$$1);
        return $$1.getPlayers().add($$0);
    }

    public boolean removePlayerFromTeam(String $$0) {
        PlayerTeam $$1 = this.getPlayersTeam($$0);
        if ($$1 != null) {
            this.removePlayerFromTeam($$0, $$1);
            return true;
        }
        return false;
    }

    public void removePlayerFromTeam(String $$0, PlayerTeam $$1) {
        if (this.getPlayersTeam($$0) != $$1) {
            throw new IllegalStateException("Player is either on another team or not on any team. Cannot remove from team '" + $$1.getName() + "'.");
        }
        this.teamsByPlayer.remove((Object)$$0);
        $$1.getPlayers().remove($$0);
    }

    public Collection<String> getTeamNames() {
        return this.teamsByName.keySet();
    }

    public Collection<PlayerTeam> getPlayerTeams() {
        return this.teamsByName.values();
    }

    @Nullable
    public PlayerTeam getPlayersTeam(String $$0) {
        return (PlayerTeam)this.teamsByPlayer.get((Object)$$0);
    }

    public void onObjectiveAdded(Objective $$0) {
    }

    public void onObjectiveChanged(Objective $$0) {
    }

    public void onObjectiveRemoved(Objective $$0) {
    }

    protected void onScoreChanged(ScoreHolder $$0, Objective $$1, Score $$2) {
    }

    protected void onScoreLockChanged(ScoreHolder $$0, Objective $$1) {
    }

    public void onPlayerRemoved(ScoreHolder $$0) {
    }

    public void onPlayerScoreRemoved(ScoreHolder $$0, Objective $$1) {
    }

    public void onTeamAdded(PlayerTeam $$0) {
    }

    public void onTeamChanged(PlayerTeam $$0) {
    }

    public void onTeamRemoved(PlayerTeam $$0) {
    }

    public void entityRemoved(Entity $$0) {
        if ($$0 instanceof Player || $$0.isAlive()) {
            return;
        }
        this.resetAllPlayerScores($$0);
        this.removePlayerFromTeam($$0.getScoreboardName());
    }

    protected List<PackedScore> packPlayerScores() {
        return this.playerScores.entrySet().stream().flatMap($$0 -> {
            String $$12 = (String)$$0.getKey();
            return ((PlayerScores)$$0.getValue()).listRawScores().entrySet().stream().map($$1 -> new PackedScore($$12, ((Objective)$$1.getKey()).getName(), (Score)$$1.getValue()));
        }).toList();
    }

    protected void loadPlayerScore(PackedScore $$0) {
        Objective $$1 = this.getObjective($$0.objective);
        if ($$1 == null) {
            LOGGER.error("Unknown objective {} for name {}, ignoring", (Object)$$0.objective, (Object)$$0.owner);
            return;
        }
        this.getOrCreatePlayerInfo($$0.owner).setScore($$1, $$0.score);
    }

    protected void loadPlayerTeam(PlayerTeam.Packed $$0) {
        PlayerTeam $$1 = this.addPlayerTeam($$0.name());
        $$0.displayName().ifPresent($$1::setDisplayName);
        $$0.color().ifPresent($$1::setColor);
        $$1.setAllowFriendlyFire($$0.allowFriendlyFire());
        $$1.setSeeFriendlyInvisibles($$0.seeFriendlyInvisibles());
        $$1.setPlayerPrefix($$0.memberNamePrefix());
        $$1.setPlayerSuffix($$0.memberNameSuffix());
        $$1.setNameTagVisibility($$0.nameTagVisibility());
        $$1.setDeathMessageVisibility($$0.deathMessageVisibility());
        $$1.setCollisionRule($$0.collisionRule());
        for (String $$2 : $$0.players()) {
            this.addPlayerToTeam($$2, $$1);
        }
    }

    protected void loadObjective(Objective.Packed $$0) {
        this.addObjective($$0.name(), $$0.criteria(), $$0.displayName(), $$0.renderType(), $$0.displayAutoUpdate(), $$0.numberFormat().orElse(null));
    }

    public static final class PackedScore
    extends Record {
        final String owner;
        final String objective;
        final Score score;
        public static final Codec<PackedScore> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)Codec.STRING.fieldOf("Name").forGetter(PackedScore::owner), (App)Codec.STRING.fieldOf("Objective").forGetter(PackedScore::objective), (App)Score.MAP_CODEC.forGetter(PackedScore::score)).apply((Applicative)$$0, PackedScore::new));

        public PackedScore(String $$0, String $$1, Score $$2) {
            this.owner = $$0;
            this.objective = $$1;
            this.score = $$2;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{PackedScore.class, "owner;objective;score", "owner", "objective", "score"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{PackedScore.class, "owner;objective;score", "owner", "objective", "score"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{PackedScore.class, "owner;objective;score", "owner", "objective", "score"}, this, $$0);
        }

        public String owner() {
            return this.owner;
        }

        public String objective() {
            return this.objective;
        }

        public Score score() {
            return this.score;
        }
    }
}

