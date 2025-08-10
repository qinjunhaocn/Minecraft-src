/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 */
package net.minecraft.world.level.block.entity.vault;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.entity.vault.VaultConfig;
import net.minecraft.world.level.block.entity.vault.VaultServerData;
import net.minecraft.world.level.block.entity.vault.VaultSharedData;
import net.minecraft.world.phys.Vec3;

public sealed class VaultState
extends Enum<VaultState>
implements StringRepresentable {
    public static final /* enum */ VaultState INACTIVE = new VaultState("inactive", LightLevel.HALF_LIT){

        @Override
        protected void onEnter(ServerLevel $$0, BlockPos $$1, VaultConfig $$2, VaultSharedData $$3, boolean $$4) {
            $$3.setDisplayItem(ItemStack.EMPTY);
            $$0.levelEvent(3016, $$1, $$4 ? 1 : 0);
        }
    };
    public static final /* enum */ VaultState ACTIVE = new VaultState("active", LightLevel.LIT){

        @Override
        protected void onEnter(ServerLevel $$0, BlockPos $$1, VaultConfig $$2, VaultSharedData $$3, boolean $$4) {
            if (!$$3.hasDisplayItem()) {
                VaultBlockEntity.Server.cycleDisplayItemFromLootTable($$0, this, $$2, $$3, $$1);
            }
            $$0.levelEvent(3015, $$1, $$4 ? 1 : 0);
        }
    };
    public static final /* enum */ VaultState UNLOCKING = new VaultState("unlocking", LightLevel.LIT){

        @Override
        protected void onEnter(ServerLevel $$0, BlockPos $$1, VaultConfig $$2, VaultSharedData $$3, boolean $$4) {
            $$0.playSound(null, $$1, SoundEvents.VAULT_INSERT_ITEM, SoundSource.BLOCKS);
        }
    };
    public static final /* enum */ VaultState EJECTING = new VaultState("ejecting", LightLevel.LIT){

        @Override
        protected void onEnter(ServerLevel $$0, BlockPos $$1, VaultConfig $$2, VaultSharedData $$3, boolean $$4) {
            $$0.playSound(null, $$1, SoundEvents.VAULT_OPEN_SHUTTER, SoundSource.BLOCKS);
        }

        @Override
        protected void onExit(ServerLevel $$0, BlockPos $$1, VaultConfig $$2, VaultSharedData $$3) {
            $$0.playSound(null, $$1, SoundEvents.VAULT_CLOSE_SHUTTER, SoundSource.BLOCKS);
        }
    };
    private static final int UPDATE_CONNECTED_PLAYERS_TICK_RATE = 20;
    private static final int DELAY_BETWEEN_EJECTIONS_TICKS = 20;
    private static final int DELAY_AFTER_LAST_EJECTION_TICKS = 20;
    private static final int DELAY_BEFORE_FIRST_EJECTION_TICKS = 20;
    private final String stateName;
    private final LightLevel lightLevel;
    private static final /* synthetic */ VaultState[] $VALUES;

    public static VaultState[] values() {
        return (VaultState[])$VALUES.clone();
    }

    public static VaultState valueOf(String $$0) {
        return Enum.valueOf(VaultState.class, $$0);
    }

    VaultState(String $$0, LightLevel $$1) {
        this.stateName = $$0;
        this.lightLevel = $$1;
    }

    @Override
    public String getSerializedName() {
        return this.stateName;
    }

    public int lightLevel() {
        return this.lightLevel.value;
    }

    public VaultState tickAndGetNext(ServerLevel $$0, BlockPos $$1, VaultConfig $$2, VaultServerData $$3, VaultSharedData $$4) {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> VaultState.updateStateForConnectedPlayers($$0, $$1, $$2, $$3, $$4, $$2.activationRange());
            case 1 -> VaultState.updateStateForConnectedPlayers($$0, $$1, $$2, $$3, $$4, $$2.deactivationRange());
            case 2 -> {
                $$3.pauseStateUpdatingUntil($$0.getGameTime() + 20L);
                yield EJECTING;
            }
            case 3 -> {
                if ($$3.getItemsToEject().isEmpty()) {
                    $$3.markEjectionFinished();
                    yield VaultState.updateStateForConnectedPlayers($$0, $$1, $$2, $$3, $$4, $$2.deactivationRange());
                }
                float $$5 = $$3.ejectionProgress();
                this.ejectResultItem($$0, $$1, $$3.popNextItemToEject(), $$5);
                $$4.setDisplayItem($$3.getNextItemToEject());
                boolean $$6 = $$3.getItemsToEject().isEmpty();
                int $$7 = $$6 ? 20 : 20;
                $$3.pauseStateUpdatingUntil($$0.getGameTime() + (long)$$7);
                yield EJECTING;
            }
        };
    }

    private static VaultState updateStateForConnectedPlayers(ServerLevel $$0, BlockPos $$1, VaultConfig $$2, VaultServerData $$3, VaultSharedData $$4, double $$5) {
        $$4.updateConnectedPlayersWithinRange($$0, $$1, $$3, $$2, $$5);
        $$3.pauseStateUpdatingUntil($$0.getGameTime() + 20L);
        return $$4.hasConnectedPlayers() ? ACTIVE : INACTIVE;
    }

    public void onTransition(ServerLevel $$0, BlockPos $$1, VaultState $$2, VaultConfig $$3, VaultSharedData $$4, boolean $$5) {
        this.onExit($$0, $$1, $$3, $$4);
        $$2.onEnter($$0, $$1, $$3, $$4, $$5);
    }

    protected void onEnter(ServerLevel $$0, BlockPos $$1, VaultConfig $$2, VaultSharedData $$3, boolean $$4) {
    }

    protected void onExit(ServerLevel $$0, BlockPos $$1, VaultConfig $$2, VaultSharedData $$3) {
    }

    private void ejectResultItem(ServerLevel $$0, BlockPos $$1, ItemStack $$2, float $$3) {
        DefaultDispenseItemBehavior.spawnItem($$0, $$2, 2, Direction.UP, Vec3.atBottomCenterOf($$1).relative(Direction.UP, 1.2));
        $$0.levelEvent(3017, $$1, 0);
        $$0.playSound(null, $$1, SoundEvents.VAULT_EJECT_ITEM, SoundSource.BLOCKS, 1.0f, 0.8f + 0.4f * $$3);
    }

    private static /* synthetic */ VaultState[] b() {
        return new VaultState[]{INACTIVE, ACTIVE, UNLOCKING, EJECTING};
    }

    static {
        $VALUES = VaultState.b();
    }

    static final class LightLevel
    extends Enum<LightLevel> {
        public static final /* enum */ LightLevel HALF_LIT = new LightLevel(6);
        public static final /* enum */ LightLevel LIT = new LightLevel(12);
        final int value;
        private static final /* synthetic */ LightLevel[] $VALUES;

        public static LightLevel[] values() {
            return (LightLevel[])$VALUES.clone();
        }

        public static LightLevel valueOf(String $$0) {
            return Enum.valueOf(LightLevel.class, $$0);
        }

        private LightLevel(int $$0) {
            this.value = $$0;
        }

        private static /* synthetic */ LightLevel[] a() {
            return new LightLevel[]{HALF_LIT, LIT};
        }

        static {
            $VALUES = LightLevel.a();
        }
    }
}

