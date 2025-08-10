/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.storage.loot.providers.nbt;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.nbt.Tag;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.nbt.LootNbtProviderType;
import net.minecraft.world.level.storage.loot.providers.nbt.NbtProvider;
import net.minecraft.world.level.storage.loot.providers.nbt.NbtProviders;

public class ContextNbtProvider
implements NbtProvider {
    private static final String BLOCK_ENTITY_ID = "block_entity";
    private static final Getter BLOCK_ENTITY_PROVIDER = new Getter(){

        @Override
        public Tag get(LootContext $$0) {
            BlockEntity $$1 = $$0.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
            return $$1 != null ? $$1.saveWithFullMetadata($$1.getLevel().registryAccess()) : null;
        }

        @Override
        public String getId() {
            return ContextNbtProvider.BLOCK_ENTITY_ID;
        }

        @Override
        public Set<ContextKey<?>> getReferencedContextParams() {
            return Set.of(LootContextParams.BLOCK_ENTITY);
        }
    };
    public static final ContextNbtProvider BLOCK_ENTITY = new ContextNbtProvider(BLOCK_ENTITY_PROVIDER);
    private static final Codec<Getter> GETTER_CODEC = Codec.STRING.xmap($$0 -> {
        if ($$0.equals(BLOCK_ENTITY_ID)) {
            return BLOCK_ENTITY_PROVIDER;
        }
        LootContext.EntityTarget $$1 = LootContext.EntityTarget.getByName($$0);
        return ContextNbtProvider.forEntity($$1);
    }, Getter::getId);
    public static final MapCodec<ContextNbtProvider> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)GETTER_CODEC.fieldOf("target").forGetter($$0 -> $$0.getter)).apply((Applicative)$$02, ContextNbtProvider::new));
    public static final Codec<ContextNbtProvider> INLINE_CODEC = GETTER_CODEC.xmap(ContextNbtProvider::new, $$0 -> $$0.getter);
    private final Getter getter;

    private static Getter forEntity(final LootContext.EntityTarget $$0) {
        return new Getter(){

            @Override
            @Nullable
            public Tag get(LootContext $$02) {
                Entity $$1 = $$02.getOptionalParameter($$0.getParam());
                return $$1 != null ? NbtPredicate.getEntityTagToCompare($$1) : null;
            }

            @Override
            public String getId() {
                return $$0.name();
            }

            @Override
            public Set<ContextKey<?>> getReferencedContextParams() {
                return Set.of($$0.getParam());
            }
        };
    }

    private ContextNbtProvider(Getter $$0) {
        this.getter = $$0;
    }

    @Override
    public LootNbtProviderType getType() {
        return NbtProviders.CONTEXT;
    }

    @Override
    @Nullable
    public Tag get(LootContext $$0) {
        return this.getter.get($$0);
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams() {
        return this.getter.getReferencedContextParams();
    }

    public static NbtProvider forContextEntity(LootContext.EntityTarget $$0) {
        return new ContextNbtProvider(ContextNbtProvider.forEntity($$0));
    }

    static interface Getter {
        @Nullable
        public Tag get(LootContext var1);

        public String getId();

        public Set<ContextKey<?>> getReferencedContextParams();
    }
}

