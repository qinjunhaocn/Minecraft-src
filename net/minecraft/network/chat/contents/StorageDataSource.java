/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.network.chat.contents;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.contents.DataSource;
import net.minecraft.resources.ResourceLocation;

public record StorageDataSource(ResourceLocation id) implements DataSource
{
    public static final MapCodec<StorageDataSource> SUB_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)ResourceLocation.CODEC.fieldOf("storage").forGetter(StorageDataSource::id)).apply((Applicative)$$0, StorageDataSource::new));
    public static final DataSource.Type<StorageDataSource> TYPE = new DataSource.Type<StorageDataSource>(SUB_CODEC, "storage");

    @Override
    public Stream<CompoundTag> getData(CommandSourceStack $$0) {
        CompoundTag $$1 = $$0.getServer().getCommandStorage().get(this.id);
        return Stream.of($$1);
    }

    @Override
    public DataSource.Type<?> type() {
        return TYPE;
    }

    public String toString() {
        return "storage=" + String.valueOf(this.id);
    }
}

