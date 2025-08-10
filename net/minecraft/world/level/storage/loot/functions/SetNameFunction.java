/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.MatchException
 */
package net.minecraft.world.level.storage.loot.functions;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.slf4j.Logger;

public class SetNameFunction
extends LootItemConditionalFunction {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final MapCodec<SetNameFunction> CODEC = RecordCodecBuilder.mapCodec($$02 -> SetNameFunction.commonFields($$02).and($$02.group((App)ComponentSerialization.CODEC.optionalFieldOf("name").forGetter($$0 -> $$0.name), (App)LootContext.EntityTarget.CODEC.optionalFieldOf("entity").forGetter($$0 -> $$0.resolutionContext), (App)Target.CODEC.optionalFieldOf("target", (Object)Target.CUSTOM_NAME).forGetter($$0 -> $$0.target))).apply((Applicative)$$02, SetNameFunction::new));
    private final Optional<Component> name;
    private final Optional<LootContext.EntityTarget> resolutionContext;
    private final Target target;

    private SetNameFunction(List<LootItemCondition> $$0, Optional<Component> $$1, Optional<LootContext.EntityTarget> $$2, Target $$3) {
        super($$0);
        this.name = $$1;
        this.resolutionContext = $$2;
        this.target = $$3;
    }

    public LootItemFunctionType<SetNameFunction> getType() {
        return LootItemFunctions.SET_NAME;
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams() {
        return this.resolutionContext.map($$0 -> Set.of($$0.getParam())).orElse(Set.of());
    }

    public static UnaryOperator<Component> createResolver(LootContext $$02, @Nullable LootContext.EntityTarget $$1) {
        Entity $$22;
        if ($$1 != null && ($$22 = $$02.getOptionalParameter($$1.getParam())) != null) {
            CommandSourceStack $$3 = $$22.createCommandSourceStackForNameResolution($$02.getLevel()).withPermission(2);
            return $$2 -> {
                try {
                    return ComponentUtils.updateForEntity($$3, $$2, $$22, 0);
                } catch (CommandSyntaxException $$3) {
                    LOGGER.warn("Failed to resolve text component", $$3);
                    return $$2;
                }
            };
        }
        return $$0 -> $$0;
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$1) {
        this.name.ifPresent($$2 -> $$0.set(this.target.component(), (Component)SetNameFunction.createResolver($$1, this.resolutionContext.orElse(null)).apply((Component)$$2)));
        return $$0;
    }

    public static LootItemConditionalFunction.Builder<?> setName(Component $$0, Target $$1) {
        return SetNameFunction.simpleBuilder($$2 -> new SetNameFunction((List<LootItemCondition>)$$2, Optional.of($$0), Optional.empty(), $$1));
    }

    public static LootItemConditionalFunction.Builder<?> setName(Component $$0, Target $$1, LootContext.EntityTarget $$2) {
        return SetNameFunction.simpleBuilder($$3 -> new SetNameFunction((List<LootItemCondition>)$$3, Optional.of($$0), Optional.of($$2), $$1));
    }

    public static final class Target
    extends Enum<Target>
    implements StringRepresentable {
        public static final /* enum */ Target CUSTOM_NAME = new Target("custom_name");
        public static final /* enum */ Target ITEM_NAME = new Target("item_name");
        public static final Codec<Target> CODEC;
        private final String name;
        private static final /* synthetic */ Target[] $VALUES;

        public static Target[] values() {
            return (Target[])$VALUES.clone();
        }

        public static Target valueOf(String $$0) {
            return Enum.valueOf(Target.class, $$0);
        }

        private Target(String $$0) {
            this.name = $$0;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public DataComponentType<Component> component() {
            return switch (this.ordinal()) {
                default -> throw new MatchException(null, null);
                case 1 -> DataComponents.ITEM_NAME;
                case 0 -> DataComponents.CUSTOM_NAME;
            };
        }

        private static /* synthetic */ Target[] b() {
            return new Target[]{CUSTOM_NAME, ITEM_NAME};
        }

        static {
            $VALUES = Target.b();
            CODEC = StringRepresentable.fromEnum(Target::values);
        }
    }
}

