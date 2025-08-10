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
package net.minecraft.world.level.storage.loot.functions;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class CopyNameFunction
extends LootItemConditionalFunction {
    public static final MapCodec<CopyNameFunction> CODEC = RecordCodecBuilder.mapCodec($$02 -> CopyNameFunction.commonFields($$02).and((App)NameSource.CODEC.fieldOf("source").forGetter($$0 -> $$0.source)).apply((Applicative)$$02, CopyNameFunction::new));
    private final NameSource source;

    private CopyNameFunction(List<LootItemCondition> $$0, NameSource $$1) {
        super($$0);
        this.source = $$1;
    }

    public LootItemFunctionType<CopyNameFunction> getType() {
        return LootItemFunctions.COPY_NAME;
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams() {
        return Set.of(this.source.param);
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$1) {
        Object $$2 = $$1.getOptionalParameter(this.source.param);
        if ($$2 instanceof Nameable) {
            Nameable $$3 = (Nameable)$$2;
            $$0.set(DataComponents.CUSTOM_NAME, $$3.getCustomName());
        }
        return $$0;
    }

    public static LootItemConditionalFunction.Builder<?> copyName(NameSource $$0) {
        return CopyNameFunction.simpleBuilder($$1 -> new CopyNameFunction((List<LootItemCondition>)$$1, $$0));
    }

    public static final class NameSource
    extends Enum<NameSource>
    implements StringRepresentable {
        public static final /* enum */ NameSource THIS = new NameSource("this", LootContextParams.THIS_ENTITY);
        public static final /* enum */ NameSource ATTACKING_ENTITY = new NameSource("attacking_entity", LootContextParams.ATTACKING_ENTITY);
        public static final /* enum */ NameSource LAST_DAMAGE_PLAYER = new NameSource("last_damage_player", LootContextParams.LAST_DAMAGE_PLAYER);
        public static final /* enum */ NameSource BLOCK_ENTITY = new NameSource("block_entity", LootContextParams.BLOCK_ENTITY);
        public static final Codec<NameSource> CODEC;
        private final String name;
        final ContextKey<?> param;
        private static final /* synthetic */ NameSource[] $VALUES;

        public static NameSource[] values() {
            return (NameSource[])$VALUES.clone();
        }

        public static NameSource valueOf(String $$0) {
            return Enum.valueOf(NameSource.class, $$0);
        }

        private NameSource(String $$0, ContextKey<?> $$1) {
            this.name = $$0;
            this.param = $$1;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        private static /* synthetic */ NameSource[] a() {
            return new NameSource[]{THIS, ATTACKING_ENTITY, LAST_DAMAGE_PLAYER, BLOCK_ENTITY};
        }

        static {
            $VALUES = NameSource.a();
            CODEC = StringRepresentable.fromEnum(NameSource::values);
        }
    }
}

