/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.storage.loot;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;

public class LootParams {
    private final ServerLevel level;
    private final ContextMap params;
    private final Map<ResourceLocation, DynamicDrop> dynamicDrops;
    private final float luck;

    public LootParams(ServerLevel $$0, ContextMap $$1, Map<ResourceLocation, DynamicDrop> $$2, float $$3) {
        this.level = $$0;
        this.params = $$1;
        this.dynamicDrops = $$2;
        this.luck = $$3;
    }

    public ServerLevel getLevel() {
        return this.level;
    }

    public ContextMap contextMap() {
        return this.params;
    }

    public void addDynamicDrops(ResourceLocation $$0, Consumer<ItemStack> $$1) {
        DynamicDrop $$2 = this.dynamicDrops.get($$0);
        if ($$2 != null) {
            $$2.add($$1);
        }
    }

    public float getLuck() {
        return this.luck;
    }

    @FunctionalInterface
    public static interface DynamicDrop {
        public void add(Consumer<ItemStack> var1);
    }

    public static class Builder {
        private final ServerLevel level;
        private final ContextMap.Builder params = new ContextMap.Builder();
        private final Map<ResourceLocation, DynamicDrop> dynamicDrops = Maps.newHashMap();
        private float luck;

        public Builder(ServerLevel $$0) {
            this.level = $$0;
        }

        public ServerLevel getLevel() {
            return this.level;
        }

        public <T> Builder withParameter(ContextKey<T> $$0, T $$1) {
            this.params.withParameter($$0, $$1);
            return this;
        }

        public <T> Builder withOptionalParameter(ContextKey<T> $$0, @Nullable T $$1) {
            this.params.withOptionalParameter($$0, $$1);
            return this;
        }

        public <T> T getParameter(ContextKey<T> $$0) {
            return this.params.getParameter($$0);
        }

        @Nullable
        public <T> T getOptionalParameter(ContextKey<T> $$0) {
            return this.params.getOptionalParameter($$0);
        }

        public Builder withDynamicDrop(ResourceLocation $$0, DynamicDrop $$1) {
            DynamicDrop $$2 = this.dynamicDrops.put($$0, $$1);
            if ($$2 != null) {
                throw new IllegalStateException("Duplicated dynamic drop '" + String.valueOf(this.dynamicDrops) + "'");
            }
            return this;
        }

        public Builder withLuck(float $$0) {
            this.luck = $$0;
            return this;
        }

        public LootParams create(ContextKeySet $$0) {
            ContextMap $$1 = this.params.create($$0);
            return new LootParams(this.level, $$1, this.dynamicDrops, this.luck);
        }
    }
}

