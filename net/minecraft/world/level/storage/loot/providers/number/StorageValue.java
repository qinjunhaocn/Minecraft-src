/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.storage.loot.providers.number;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public record StorageValue(ResourceLocation storage, NbtPathArgument.NbtPath path) implements NumberProvider
{
    public static final MapCodec<StorageValue> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)ResourceLocation.CODEC.fieldOf("storage").forGetter(StorageValue::storage), (App)NbtPathArgument.NbtPath.CODEC.fieldOf("path").forGetter(StorageValue::path)).apply((Applicative)$$0, StorageValue::new));

    @Override
    public LootNumberProviderType getType() {
        return NumberProviders.STORAGE;
    }

    private Number getNumericTag(LootContext $$0, Number $$1) {
        CompoundTag $$2 = $$0.getLevel().getServer().getCommandStorage().get(this.storage);
        try {
            Object object;
            List<Tag> $$3 = this.path.get($$2);
            if ($$3.size() == 1 && (object = $$3.getFirst()) instanceof NumericTag) {
                NumericTag $$4 = (NumericTag)object;
                return $$4.box();
            }
        } catch (CommandSyntaxException commandSyntaxException) {
            // empty catch block
        }
        return $$1;
    }

    @Override
    public float getFloat(LootContext $$0) {
        return this.getNumericTag($$0, Float.valueOf(0.0f)).floatValue();
    }

    @Override
    public int getInt(LootContext $$0) {
        return this.getNumericTag($$0, 0).intValue();
    }
}

