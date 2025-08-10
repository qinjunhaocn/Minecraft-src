/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.item;

import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multisets;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.MapPostProcessing;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class MapItem
extends Item {
    public static final int IMAGE_WIDTH = 128;
    public static final int IMAGE_HEIGHT = 128;

    public MapItem(Item.Properties $$0) {
        super($$0);
    }

    public static ItemStack create(ServerLevel $$0, int $$1, int $$2, byte $$3, boolean $$4, boolean $$5) {
        ItemStack $$6 = new ItemStack(Items.FILLED_MAP);
        MapId $$7 = MapItem.createNewSavedData($$0, $$1, $$2, $$3, $$4, $$5, $$0.dimension());
        $$6.set(DataComponents.MAP_ID, $$7);
        return $$6;
    }

    @Nullable
    public static MapItemSavedData getSavedData(@Nullable MapId $$0, Level $$1) {
        return $$0 == null ? null : $$1.getMapData($$0);
    }

    @Nullable
    public static MapItemSavedData getSavedData(ItemStack $$0, Level $$1) {
        MapId $$2 = $$0.get(DataComponents.MAP_ID);
        return MapItem.getSavedData($$2, $$1);
    }

    private static MapId createNewSavedData(ServerLevel $$0, int $$1, int $$2, int $$3, boolean $$4, boolean $$5, ResourceKey<Level> $$6) {
        MapItemSavedData $$7 = MapItemSavedData.createFresh($$1, $$2, (byte)$$3, $$4, $$5, $$6);
        MapId $$8 = $$0.getFreeMapId();
        $$0.setMapData($$8, $$7);
        return $$8;
    }

    public void update(Level $$0, Entity $$1, MapItemSavedData $$2) {
        if ($$0.dimension() != $$2.dimension || !($$1 instanceof Player)) {
            return;
        }
        int $$3 = 1 << $$2.scale;
        int $$4 = $$2.centerX;
        int $$5 = $$2.centerZ;
        int $$6 = Mth.floor($$1.getX() - (double)$$4) / $$3 + 64;
        int $$7 = Mth.floor($$1.getZ() - (double)$$5) / $$3 + 64;
        int $$8 = 128 / $$3;
        if ($$0.dimensionType().hasCeiling()) {
            $$8 /= 2;
        }
        MapItemSavedData.HoldingPlayer $$9 = $$2.getHoldingPlayer((Player)$$1);
        ++$$9.step;
        BlockPos.MutableBlockPos $$10 = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos $$11 = new BlockPos.MutableBlockPos();
        boolean $$12 = false;
        for (int $$13 = $$6 - $$8 + 1; $$13 < $$6 + $$8; ++$$13) {
            if (($$13 & 0xF) != ($$9.step & 0xF) && !$$12) continue;
            $$12 = false;
            double $$14 = 0.0;
            for (int $$15 = $$7 - $$8 - 1; $$15 < $$7 + $$8; ++$$15) {
                MapColor.Brightness $$40;
                if ($$13 < 0 || $$15 < -1 || $$13 >= 128 || $$15 >= 128) continue;
                int $$16 = Mth.square($$13 - $$6) + Mth.square($$15 - $$7);
                boolean $$17 = $$16 > ($$8 - 2) * ($$8 - 2);
                int $$18 = ($$4 / $$3 + $$13 - 64) * $$3;
                int $$19 = ($$5 / $$3 + $$15 - 64) * $$3;
                LinkedHashMultiset<MapColor> $$20 = LinkedHashMultiset.create();
                LevelChunk $$21 = $$0.getChunk(SectionPos.blockToSectionCoord($$18), SectionPos.blockToSectionCoord($$19));
                if ($$21.isEmpty()) continue;
                int $$22 = 0;
                double $$23 = 0.0;
                if ($$0.dimensionType().hasCeiling()) {
                    int $$24 = $$18 + $$19 * 231871;
                    if ((($$24 = $$24 * $$24 * 31287121 + $$24 * 11) >> 20 & 1) == 0) {
                        $$20.add(Blocks.DIRT.defaultBlockState().getMapColor($$0, BlockPos.ZERO), 10);
                    } else {
                        $$20.add(Blocks.STONE.defaultBlockState().getMapColor($$0, BlockPos.ZERO), 100);
                    }
                    $$23 = 100.0;
                } else {
                    for (int $$25 = 0; $$25 < $$3; ++$$25) {
                        for (int $$26 = 0; $$26 < $$3; ++$$26) {
                            BlockState $$31;
                            $$10.set($$18 + $$25, 0, $$19 + $$26);
                            int $$27 = $$21.getHeight(Heightmap.Types.WORLD_SURFACE, $$10.getX(), $$10.getZ()) + 1;
                            if ($$27 > $$0.getMinY()) {
                                BlockState $$28;
                                do {
                                    $$10.setY(--$$27);
                                } while (($$28 = $$21.getBlockState($$10)).getMapColor($$0, $$10) == MapColor.NONE && $$27 > $$0.getMinY());
                                if ($$27 > $$0.getMinY() && !$$28.getFluidState().isEmpty()) {
                                    BlockState $$30;
                                    int $$29 = $$27 - 1;
                                    $$11.set($$10);
                                    do {
                                        $$11.setY($$29--);
                                        $$30 = $$21.getBlockState($$11);
                                        ++$$22;
                                    } while ($$29 > $$0.getMinY() && !$$30.getFluidState().isEmpty());
                                    $$28 = this.getCorrectStateForFluidBlock($$0, $$28, $$10);
                                }
                            } else {
                                $$31 = Blocks.BEDROCK.defaultBlockState();
                            }
                            $$2.checkBanners($$0, $$10.getX(), $$10.getZ());
                            $$23 += (double)$$27 / (double)($$3 * $$3);
                            $$20.add($$31.getMapColor($$0, $$10));
                        }
                    }
                }
                $$22 /= $$3 * $$3;
                MapColor $$32 = Iterables.getFirst(Multisets.copyHighestCountFirst($$20), MapColor.NONE);
                if ($$32 == MapColor.WATER) {
                    double $$33 = (double)$$22 * 0.1 + (double)($$13 + $$15 & 1) * 0.2;
                    if ($$33 < 0.5) {
                        MapColor.Brightness $$34 = MapColor.Brightness.HIGH;
                    } else if ($$33 > 0.9) {
                        MapColor.Brightness $$35 = MapColor.Brightness.LOW;
                    } else {
                        MapColor.Brightness $$36 = MapColor.Brightness.NORMAL;
                    }
                } else {
                    double $$37 = ($$23 - $$14) * 4.0 / (double)($$3 + 4) + ((double)($$13 + $$15 & 1) - 0.5) * 0.4;
                    if ($$37 > 0.6) {
                        MapColor.Brightness $$38 = MapColor.Brightness.HIGH;
                    } else if ($$37 < -0.6) {
                        MapColor.Brightness $$39 = MapColor.Brightness.LOW;
                    } else {
                        $$40 = MapColor.Brightness.NORMAL;
                    }
                }
                $$14 = $$23;
                if ($$15 < 0 || $$16 >= $$8 * $$8 || $$17 && ($$13 + $$15 & 1) == 0) continue;
                $$12 |= $$2.updateColor($$13, $$15, $$32.getPackedId($$40));
            }
        }
    }

    private BlockState getCorrectStateForFluidBlock(Level $$0, BlockState $$1, BlockPos $$2) {
        FluidState $$3 = $$1.getFluidState();
        if (!$$3.isEmpty() && !$$1.isFaceSturdy($$0, $$2, Direction.UP)) {
            return $$3.createLegacyBlock();
        }
        return $$1;
    }

    private static boolean a(boolean[] $$0, int $$1, int $$2) {
        return $$0[$$2 * 128 + $$1];
    }

    public static void renderBiomePreviewMap(ServerLevel $$0, ItemStack $$1) {
        MapItemSavedData $$2 = MapItem.getSavedData($$1, (Level)$$0);
        if ($$2 == null) {
            return;
        }
        if ($$0.dimension() != $$2.dimension) {
            return;
        }
        int $$3 = 1 << $$2.scale;
        int $$4 = $$2.centerX;
        int $$5 = $$2.centerZ;
        boolean[] $$6 = new boolean[16384];
        int $$7 = $$4 / $$3 - 64;
        int $$8 = $$5 / $$3 - 64;
        BlockPos.MutableBlockPos $$9 = new BlockPos.MutableBlockPos();
        for (int $$10 = 0; $$10 < 128; ++$$10) {
            for (int $$11 = 0; $$11 < 128; ++$$11) {
                Holder<Biome> $$12 = $$0.getBiome($$9.set(($$7 + $$11) * $$3, 0, ($$8 + $$10) * $$3));
                $$6[$$10 * 128 + $$11] = $$12.is(BiomeTags.WATER_ON_MAP_OUTLINES);
            }
        }
        for (int $$13 = 1; $$13 < 127; ++$$13) {
            for (int $$14 = 1; $$14 < 127; ++$$14) {
                int $$15 = 0;
                for (int $$16 = -1; $$16 < 2; ++$$16) {
                    for (int $$17 = -1; $$17 < 2; ++$$17) {
                        if ($$16 == 0 && $$17 == 0 || !MapItem.a($$6, $$13 + $$16, $$14 + $$17)) continue;
                        ++$$15;
                    }
                }
                MapColor.Brightness $$18 = MapColor.Brightness.LOWEST;
                MapColor $$19 = MapColor.NONE;
                if (MapItem.a($$6, $$13, $$14)) {
                    $$19 = MapColor.COLOR_ORANGE;
                    if ($$15 > 7 && $$14 % 2 == 0) {
                        switch (($$13 + (int)(Mth.sin((float)$$14 + 0.0f) * 7.0f)) / 8 % 5) {
                            case 0: 
                            case 4: {
                                $$18 = MapColor.Brightness.LOW;
                                break;
                            }
                            case 1: 
                            case 3: {
                                $$18 = MapColor.Brightness.NORMAL;
                                break;
                            }
                            case 2: {
                                $$18 = MapColor.Brightness.HIGH;
                            }
                        }
                    } else if ($$15 > 7) {
                        $$19 = MapColor.NONE;
                    } else if ($$15 > 5) {
                        $$18 = MapColor.Brightness.NORMAL;
                    } else if ($$15 > 3) {
                        $$18 = MapColor.Brightness.LOW;
                    } else if ($$15 > 1) {
                        $$18 = MapColor.Brightness.LOW;
                    }
                } else if ($$15 > 0) {
                    $$19 = MapColor.COLOR_BROWN;
                    $$18 = $$15 > 3 ? MapColor.Brightness.NORMAL : MapColor.Brightness.LOWEST;
                }
                if ($$19 == MapColor.NONE) continue;
                $$2.setColor($$13, $$14, $$19.getPackedId($$18));
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack $$0, ServerLevel $$1, Entity $$2, @Nullable EquipmentSlot $$3) {
        MapItemSavedData $$4 = MapItem.getSavedData($$0, (Level)$$1);
        if ($$4 == null) {
            return;
        }
        if ($$2 instanceof Player) {
            Player $$5 = (Player)$$2;
            $$4.tickCarriedBy($$5, $$0);
        }
        if (!$$4.locked && $$3 != null && $$3.getType() == EquipmentSlot.Type.HAND) {
            this.update($$1, $$2, $$4);
        }
    }

    @Override
    public void onCraftedPostProcess(ItemStack $$0, Level $$1) {
        MapPostProcessing $$2 = $$0.remove(DataComponents.MAP_POST_PROCESSING);
        if ($$2 == null) {
            return;
        }
        if ($$1 instanceof ServerLevel) {
            ServerLevel $$3 = (ServerLevel)$$1;
            switch ($$2) {
                case LOCK: {
                    MapItem.lockMap($$0, $$3);
                    break;
                }
                case SCALE: {
                    MapItem.scaleMap($$0, $$3);
                }
            }
        }
    }

    private static void scaleMap(ItemStack $$0, ServerLevel $$1) {
        MapItemSavedData $$2 = MapItem.getSavedData($$0, (Level)$$1);
        if ($$2 != null) {
            MapId $$3 = $$1.getFreeMapId();
            $$1.setMapData($$3, $$2.scaled());
            $$0.set(DataComponents.MAP_ID, $$3);
        }
    }

    private static void lockMap(ItemStack $$0, ServerLevel $$1) {
        MapItemSavedData $$2 = MapItem.getSavedData($$0, (Level)$$1);
        if ($$2 != null) {
            MapId $$3 = $$1.getFreeMapId();
            MapItemSavedData $$4 = $$2.locked();
            $$1.setMapData($$3, $$4);
            $$0.set(DataComponents.MAP_ID, $$3);
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext $$0) {
        BlockState $$1 = $$0.getLevel().getBlockState($$0.getClickedPos());
        if ($$1.is(BlockTags.BANNERS)) {
            MapItemSavedData $$2;
            if (!$$0.getLevel().isClientSide && ($$2 = MapItem.getSavedData($$0.getItemInHand(), $$0.getLevel())) != null && !$$2.toggleBanner($$0.getLevel(), $$0.getClickedPos())) {
                return InteractionResult.FAIL;
            }
            return InteractionResult.SUCCESS;
        }
        return super.useOn($$0);
    }
}

