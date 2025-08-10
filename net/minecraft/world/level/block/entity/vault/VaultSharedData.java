/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet
 */
package net.minecraft.world.level.block.entity.vault;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.vault.VaultConfig;
import net.minecraft.world.level.block.entity.vault.VaultServerData;

public class VaultSharedData {
    static final String TAG_NAME = "shared_data";
    static Codec<VaultSharedData> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)ItemStack.lenientOptionalFieldOf("display_item").forGetter($$0 -> $$0.displayItem), (App)UUIDUtil.CODEC_LINKED_SET.lenientOptionalFieldOf("connected_players", (Object)Set.of()).forGetter($$0 -> $$0.connectedPlayers), (App)Codec.DOUBLE.lenientOptionalFieldOf("connected_particles_range", (Object)VaultConfig.DEFAULT.deactivationRange()).forGetter($$0 -> $$0.connectedParticlesRange)).apply((Applicative)$$02, VaultSharedData::new));
    private ItemStack displayItem = ItemStack.EMPTY;
    private Set<UUID> connectedPlayers = new ObjectLinkedOpenHashSet();
    private double connectedParticlesRange = VaultConfig.DEFAULT.deactivationRange();
    boolean isDirty;

    VaultSharedData(ItemStack $$0, Set<UUID> $$1, double $$2) {
        this.displayItem = $$0;
        this.connectedPlayers.addAll($$1);
        this.connectedParticlesRange = $$2;
    }

    VaultSharedData() {
    }

    public ItemStack getDisplayItem() {
        return this.displayItem;
    }

    public boolean hasDisplayItem() {
        return !this.displayItem.isEmpty();
    }

    public void setDisplayItem(ItemStack $$0) {
        if (ItemStack.matches(this.displayItem, $$0)) {
            return;
        }
        this.displayItem = $$0.copy();
        this.markDirty();
    }

    boolean hasConnectedPlayers() {
        return !this.connectedPlayers.isEmpty();
    }

    Set<UUID> getConnectedPlayers() {
        return this.connectedPlayers;
    }

    double connectedParticlesRange() {
        return this.connectedParticlesRange;
    }

    void updateConnectedPlayersWithinRange(ServerLevel $$0, BlockPos $$12, VaultServerData $$2, VaultConfig $$3, double $$4) {
        Set $$5 = $$3.playerDetector().detect($$0, $$3.entitySelector(), $$12, $$4, false).stream().filter($$1 -> !$$2.getRewardedPlayers().contains($$1)).collect(Collectors.toSet());
        if (!this.connectedPlayers.equals($$5)) {
            this.connectedPlayers = $$5;
            this.markDirty();
        }
    }

    private void markDirty() {
        this.isDirty = true;
    }

    void set(VaultSharedData $$0) {
        this.displayItem = $$0.displayItem;
        this.connectedPlayers = $$0.connectedPlayers;
        this.connectedParticlesRange = $$0.connectedParticlesRange;
    }
}

