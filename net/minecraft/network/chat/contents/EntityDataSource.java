/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.network.chat.contents;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.contents.DataSource;
import net.minecraft.world.entity.Entity;

public record EntityDataSource(String selectorPattern, @Nullable EntitySelector compiledSelector) implements DataSource
{
    public static final MapCodec<EntityDataSource> SUB_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)Codec.STRING.fieldOf("entity").forGetter(EntityDataSource::selectorPattern)).apply((Applicative)$$0, EntityDataSource::new));
    public static final DataSource.Type<EntityDataSource> TYPE = new DataSource.Type<EntityDataSource>(SUB_CODEC, "entity");

    public EntityDataSource(String $$0) {
        this($$0, EntityDataSource.compileSelector($$0));
    }

    @Nullable
    private static EntitySelector compileSelector(String $$0) {
        try {
            EntitySelectorParser $$1 = new EntitySelectorParser(new StringReader($$0), true);
            return $$1.parse();
        } catch (CommandSyntaxException $$2) {
            return null;
        }
    }

    @Override
    public Stream<CompoundTag> getData(CommandSourceStack $$0) throws CommandSyntaxException {
        if (this.compiledSelector != null) {
            List<? extends Entity> $$1 = this.compiledSelector.findEntities($$0);
            return $$1.stream().map(NbtPredicate::getEntityTagToCompare);
        }
        return Stream.empty();
    }

    @Override
    public DataSource.Type<?> type() {
        return TYPE;
    }

    public String toString() {
        return "entity=" + this.selectorPattern;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if (!($$0 instanceof EntityDataSource)) return false;
        EntityDataSource $$1 = (EntityDataSource)$$0;
        if (!this.selectorPattern.equals($$1.selectorPattern)) return false;
        return true;
    }

    public int hashCode() {
        return this.selectorPattern.hashCode();
    }

    @Nullable
    public EntitySelector compiledSelector() {
        return this.compiledSelector;
    }
}

