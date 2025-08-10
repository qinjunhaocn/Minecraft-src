/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.storage.loot;

import com.google.common.collect.Sets;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootContext {
    private final LootParams params;
    private final RandomSource random;
    private final HolderGetter.Provider lootDataResolver;
    private final Set<VisitedEntry<?>> visitedElements = Sets.newLinkedHashSet();

    LootContext(LootParams $$0, RandomSource $$1, HolderGetter.Provider $$2) {
        this.params = $$0;
        this.random = $$1;
        this.lootDataResolver = $$2;
    }

    public boolean hasParameter(ContextKey<?> $$0) {
        return this.params.contextMap().has($$0);
    }

    public <T> T getParameter(ContextKey<T> $$0) {
        return this.params.contextMap().getOrThrow($$0);
    }

    @Nullable
    public <T> T getOptionalParameter(ContextKey<T> $$0) {
        return this.params.contextMap().getOptional($$0);
    }

    public void addDynamicDrops(ResourceLocation $$0, Consumer<ItemStack> $$1) {
        this.params.addDynamicDrops($$0, $$1);
    }

    public boolean hasVisitedElement(VisitedEntry<?> $$0) {
        return this.visitedElements.contains($$0);
    }

    public boolean pushVisitedElement(VisitedEntry<?> $$0) {
        return this.visitedElements.add($$0);
    }

    public void popVisitedElement(VisitedEntry<?> $$0) {
        this.visitedElements.remove($$0);
    }

    public HolderGetter.Provider getResolver() {
        return this.lootDataResolver;
    }

    public RandomSource getRandom() {
        return this.random;
    }

    public float getLuck() {
        return this.params.getLuck();
    }

    public ServerLevel getLevel() {
        return this.params.getLevel();
    }

    public static VisitedEntry<LootTable> createVisitedEntry(LootTable $$0) {
        return new VisitedEntry<LootTable>(LootDataType.TABLE, $$0);
    }

    public static VisitedEntry<LootItemCondition> createVisitedEntry(LootItemCondition $$0) {
        return new VisitedEntry<LootItemCondition>(LootDataType.PREDICATE, $$0);
    }

    public static VisitedEntry<LootItemFunction> createVisitedEntry(LootItemFunction $$0) {
        return new VisitedEntry<LootItemFunction>(LootDataType.MODIFIER, $$0);
    }

    public record VisitedEntry<T>(LootDataType<T> type, T value) {
    }

    public static final class EntityTarget
    extends Enum<EntityTarget>
    implements StringRepresentable {
        public static final /* enum */ EntityTarget THIS = new EntityTarget("this", LootContextParams.THIS_ENTITY);
        public static final /* enum */ EntityTarget ATTACKER = new EntityTarget("attacker", LootContextParams.ATTACKING_ENTITY);
        public static final /* enum */ EntityTarget DIRECT_ATTACKER = new EntityTarget("direct_attacker", LootContextParams.DIRECT_ATTACKING_ENTITY);
        public static final /* enum */ EntityTarget ATTACKING_PLAYER = new EntityTarget("attacking_player", LootContextParams.LAST_DAMAGE_PLAYER);
        public static final StringRepresentable.EnumCodec<EntityTarget> CODEC;
        private final String name;
        private final ContextKey<? extends Entity> param;
        private static final /* synthetic */ EntityTarget[] $VALUES;

        public static EntityTarget[] values() {
            return (EntityTarget[])$VALUES.clone();
        }

        public static EntityTarget valueOf(String $$0) {
            return Enum.valueOf(EntityTarget.class, $$0);
        }

        private EntityTarget(String $$0, ContextKey<? extends Entity> $$1) {
            this.name = $$0;
            this.param = $$1;
        }

        public ContextKey<? extends Entity> getParam() {
            return this.param;
        }

        public static EntityTarget getByName(String $$0) {
            EntityTarget $$1 = CODEC.byName($$0);
            if ($$1 != null) {
                return $$1;
            }
            throw new IllegalArgumentException("Invalid entity target " + $$0);
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        private static /* synthetic */ EntityTarget[] b() {
            return new EntityTarget[]{THIS, ATTACKER, DIRECT_ATTACKER, ATTACKING_PLAYER};
        }

        static {
            $VALUES = EntityTarget.b();
            CODEC = StringRepresentable.fromEnum(EntityTarget::values);
        }
    }

    public static class Builder {
        private final LootParams params;
        @Nullable
        private RandomSource random;

        public Builder(LootParams $$0) {
            this.params = $$0;
        }

        public Builder withOptionalRandomSeed(long $$0) {
            if ($$0 != 0L) {
                this.random = RandomSource.create($$0);
            }
            return this;
        }

        public Builder withOptionalRandomSource(RandomSource $$0) {
            this.random = $$0;
            return this;
        }

        public ServerLevel getLevel() {
            return this.params.getLevel();
        }

        public LootContext create(Optional<ResourceLocation> $$0) {
            ServerLevel $$1 = this.getLevel();
            MinecraftServer $$2 = $$1.getServer();
            RandomSource $$3 = Optional.ofNullable(this.random).or(() -> $$0.map($$1::getRandomSequence)).orElseGet($$1::getRandom);
            return new LootContext(this.params, $$3, $$2.reloadableRegistries().lookup());
        }
    }
}

