/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet
 */
package net.minecraft.world.level.block.entity.vault;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class VaultServerData {
    static final String TAG_NAME = "server_data";
    static Codec<VaultServerData> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)UUIDUtil.CODEC_LINKED_SET.lenientOptionalFieldOf("rewarded_players", (Object)Set.of()).forGetter($$0 -> $$0.rewardedPlayers), (App)Codec.LONG.lenientOptionalFieldOf("state_updating_resumes_at", (Object)0L).forGetter($$0 -> $$0.stateUpdatingResumesAt), (App)ItemStack.CODEC.listOf().lenientOptionalFieldOf("items_to_eject", (Object)List.of()).forGetter($$0 -> $$0.itemsToEject), (App)Codec.INT.lenientOptionalFieldOf("total_ejections_needed", (Object)0).forGetter($$0 -> $$0.totalEjectionsNeeded)).apply((Applicative)$$02, VaultServerData::new));
    private static final int MAX_REWARD_PLAYERS = 128;
    private final Set<UUID> rewardedPlayers = new ObjectLinkedOpenHashSet();
    private long stateUpdatingResumesAt;
    private final List<ItemStack> itemsToEject = new ObjectArrayList();
    private long lastInsertFailTimestamp;
    private int totalEjectionsNeeded;
    boolean isDirty;

    VaultServerData(Set<UUID> $$0, long $$1, List<ItemStack> $$2, int $$3) {
        this.rewardedPlayers.addAll($$0);
        this.stateUpdatingResumesAt = $$1;
        this.itemsToEject.addAll($$2);
        this.totalEjectionsNeeded = $$3;
    }

    VaultServerData() {
    }

    void setLastInsertFailTimestamp(long $$0) {
        this.lastInsertFailTimestamp = $$0;
    }

    long getLastInsertFailTimestamp() {
        return this.lastInsertFailTimestamp;
    }

    Set<UUID> getRewardedPlayers() {
        return this.rewardedPlayers;
    }

    boolean hasRewardedPlayer(Player $$0) {
        return this.rewardedPlayers.contains($$0.getUUID());
    }

    @VisibleForTesting
    public void addToRewardedPlayers(Player $$0) {
        Iterator<UUID> $$1;
        this.rewardedPlayers.add($$0.getUUID());
        if (this.rewardedPlayers.size() > 128 && ($$1 = this.rewardedPlayers.iterator()).hasNext()) {
            $$1.next();
            $$1.remove();
        }
        this.markChanged();
    }

    long stateUpdatingResumesAt() {
        return this.stateUpdatingResumesAt;
    }

    void pauseStateUpdatingUntil(long $$0) {
        this.stateUpdatingResumesAt = $$0;
        this.markChanged();
    }

    List<ItemStack> getItemsToEject() {
        return this.itemsToEject;
    }

    void markEjectionFinished() {
        this.totalEjectionsNeeded = 0;
        this.markChanged();
    }

    void setItemsToEject(List<ItemStack> $$0) {
        this.itemsToEject.clear();
        this.itemsToEject.addAll($$0);
        this.totalEjectionsNeeded = this.itemsToEject.size();
        this.markChanged();
    }

    ItemStack getNextItemToEject() {
        if (this.itemsToEject.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return (ItemStack)Objects.requireNonNullElse((Object)this.itemsToEject.get(this.itemsToEject.size() - 1), (Object)ItemStack.EMPTY);
    }

    ItemStack popNextItemToEject() {
        if (this.itemsToEject.isEmpty()) {
            return ItemStack.EMPTY;
        }
        this.markChanged();
        return (ItemStack)Objects.requireNonNullElse((Object)this.itemsToEject.remove(this.itemsToEject.size() - 1), (Object)ItemStack.EMPTY);
    }

    void set(VaultServerData $$0) {
        this.stateUpdatingResumesAt = $$0.stateUpdatingResumesAt();
        this.itemsToEject.clear();
        this.itemsToEject.addAll($$0.itemsToEject);
        this.rewardedPlayers.clear();
        this.rewardedPlayers.addAll($$0.rewardedPlayers);
    }

    private void markChanged() {
        this.isDirty = true;
    }

    public float ejectionProgress() {
        if (this.totalEjectionsNeeded == 1) {
            return 1.0f;
        }
        return 1.0f - Mth.inverseLerp(this.getItemsToEject().size(), 1.0f, this.totalEjectionsNeeded);
    }
}

