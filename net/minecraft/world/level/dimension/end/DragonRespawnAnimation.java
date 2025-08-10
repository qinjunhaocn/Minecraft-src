/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.dimension.end;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.SpikeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.SpikeConfiguration;

public abstract sealed class DragonRespawnAnimation
extends Enum<DragonRespawnAnimation> {
    public static final /* enum */ DragonRespawnAnimation START = new DragonRespawnAnimation(){

        @Override
        public void tick(ServerLevel $$0, EndDragonFight $$1, List<EndCrystal> $$2, int $$3, BlockPos $$4) {
            BlockPos $$5 = new BlockPos(0, 128, 0);
            for (EndCrystal $$6 : $$2) {
                $$6.setBeamTarget($$5);
            }
            $$1.setRespawnStage(PREPARING_TO_SUMMON_PILLARS);
        }
    };
    public static final /* enum */ DragonRespawnAnimation PREPARING_TO_SUMMON_PILLARS = new DragonRespawnAnimation(){

        @Override
        public void tick(ServerLevel $$0, EndDragonFight $$1, List<EndCrystal> $$2, int $$3, BlockPos $$4) {
            if ($$3 < 100) {
                if ($$3 == 0 || $$3 == 50 || $$3 == 51 || $$3 == 52 || $$3 >= 95) {
                    $$0.levelEvent(3001, new BlockPos(0, 128, 0), 0);
                }
            } else {
                $$1.setRespawnStage(SUMMONING_PILLARS);
            }
        }
    };
    public static final /* enum */ DragonRespawnAnimation SUMMONING_PILLARS = new DragonRespawnAnimation(){

        @Override
        public void tick(ServerLevel $$0, EndDragonFight $$1, List<EndCrystal> $$2, int $$3, BlockPos $$4) {
            boolean $$7;
            int $$5 = 40;
            boolean $$6 = $$3 % 40 == 0;
            boolean bl = $$7 = $$3 % 40 == 39;
            if ($$6 || $$7) {
                int $$9 = $$3 / 40;
                List<SpikeFeature.EndSpike> $$8 = SpikeFeature.getSpikesForLevel($$0);
                if ($$9 < $$8.size()) {
                    SpikeFeature.EndSpike $$10 = $$8.get($$9);
                    if ($$6) {
                        for (EndCrystal $$11 : $$2) {
                            $$11.setBeamTarget(new BlockPos($$10.getCenterX(), $$10.getHeight() + 1, $$10.getCenterZ()));
                        }
                    } else {
                        int $$12 = 10;
                        for (BlockPos $$13 : BlockPos.betweenClosed(new BlockPos($$10.getCenterX() - 10, $$10.getHeight() - 10, $$10.getCenterZ() - 10), new BlockPos($$10.getCenterX() + 10, $$10.getHeight() + 10, $$10.getCenterZ() + 10))) {
                            $$0.removeBlock($$13, false);
                        }
                        $$0.explode(null, (float)$$10.getCenterX() + 0.5f, $$10.getHeight(), (float)$$10.getCenterZ() + 0.5f, 5.0f, Level.ExplosionInteraction.BLOCK);
                        SpikeConfiguration $$14 = new SpikeConfiguration(true, ImmutableList.of($$10), new BlockPos(0, 128, 0));
                        Feature.END_SPIKE.place($$14, $$0, $$0.getChunkSource().getGenerator(), RandomSource.create(), new BlockPos($$10.getCenterX(), 45, $$10.getCenterZ()));
                    }
                } else if ($$6) {
                    $$1.setRespawnStage(SUMMONING_DRAGON);
                }
            }
        }
    };
    public static final /* enum */ DragonRespawnAnimation SUMMONING_DRAGON = new DragonRespawnAnimation(){

        @Override
        public void tick(ServerLevel $$0, EndDragonFight $$1, List<EndCrystal> $$2, int $$3, BlockPos $$4) {
            if ($$3 >= 100) {
                $$1.setRespawnStage(END);
                $$1.resetSpikeCrystals();
                for (EndCrystal $$5 : $$2) {
                    $$5.setBeamTarget(null);
                    $$0.explode($$5, $$5.getX(), $$5.getY(), $$5.getZ(), 6.0f, Level.ExplosionInteraction.NONE);
                    $$5.discard();
                }
            } else if ($$3 >= 80) {
                $$0.levelEvent(3001, new BlockPos(0, 128, 0), 0);
            } else if ($$3 == 0) {
                for (EndCrystal $$6 : $$2) {
                    $$6.setBeamTarget(new BlockPos(0, 128, 0));
                }
            } else if ($$3 < 5) {
                $$0.levelEvent(3001, new BlockPos(0, 128, 0), 0);
            }
        }
    };
    public static final /* enum */ DragonRespawnAnimation END = new DragonRespawnAnimation(){

        @Override
        public void tick(ServerLevel $$0, EndDragonFight $$1, List<EndCrystal> $$2, int $$3, BlockPos $$4) {
        }
    };
    private static final /* synthetic */ DragonRespawnAnimation[] $VALUES;

    public static DragonRespawnAnimation[] values() {
        return (DragonRespawnAnimation[])$VALUES.clone();
    }

    public static DragonRespawnAnimation valueOf(String $$0) {
        return Enum.valueOf(DragonRespawnAnimation.class, $$0);
    }

    public abstract void tick(ServerLevel var1, EndDragonFight var2, List<EndCrystal> var3, int var4, BlockPos var5);

    private static /* synthetic */ DragonRespawnAnimation[] a() {
        return new DragonRespawnAnimation[]{START, PREPARING_TO_SUMMON_PILLARS, SUMMONING_PILLARS, SUMMONING_DRAGON, END};
    }

    static {
        $VALUES = DragonRespawnAnimation.a();
    }
}

