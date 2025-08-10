/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.item;

import com.mojang.serialization.Codec;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EitherHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public record JukeboxPlayable(EitherHolder<JukeboxSong> song) implements TooltipProvider
{
    public static final Codec<JukeboxPlayable> CODEC = EitherHolder.codec(Registries.JUKEBOX_SONG, JukeboxSong.CODEC).xmap(JukeboxPlayable::new, JukeboxPlayable::song);
    public static final StreamCodec<RegistryFriendlyByteBuf, JukeboxPlayable> STREAM_CODEC = StreamCodec.composite(EitherHolder.streamCodec(Registries.JUKEBOX_SONG, JukeboxSong.STREAM_CODEC), JukeboxPlayable::song, JukeboxPlayable::new);

    @Override
    public void addToTooltip(Item.TooltipContext $$0, Consumer<Component> $$12, TooltipFlag $$2, DataComponentGetter $$3) {
        HolderLookup.Provider $$4 = $$0.registries();
        if ($$4 != null) {
            this.song.unwrap($$4).ifPresent($$1 -> {
                MutableComponent $$2 = ((JukeboxSong)((Object)((Object)$$1.value()))).description().copy();
                ComponentUtils.mergeStyles($$2, Style.EMPTY.withColor(ChatFormatting.GRAY));
                $$12.accept($$2);
            });
        }
    }

    public static InteractionResult tryInsertIntoJukebox(Level $$0, BlockPos $$1, ItemStack $$2, Player $$3) {
        JukeboxPlayable $$4 = $$2.get(DataComponents.JUKEBOX_PLAYABLE);
        if ($$4 == null) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        BlockState $$5 = $$0.getBlockState($$1);
        if (!$$5.is(Blocks.JUKEBOX) || $$5.getValue(JukeboxBlock.HAS_RECORD).booleanValue()) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        if (!$$0.isClientSide) {
            ItemStack $$6 = $$2.consumeAndReturn(1, $$3);
            BlockEntity blockEntity = $$0.getBlockEntity($$1);
            if (blockEntity instanceof JukeboxBlockEntity) {
                JukeboxBlockEntity $$7 = (JukeboxBlockEntity)blockEntity;
                $$7.setTheItem($$6);
                $$0.gameEvent(GameEvent.BLOCK_CHANGE, $$1, GameEvent.Context.of($$3, $$5));
            }
            $$3.awardStat(Stats.PLAY_RECORD);
        }
        return InteractionResult.SUCCESS;
    }
}

