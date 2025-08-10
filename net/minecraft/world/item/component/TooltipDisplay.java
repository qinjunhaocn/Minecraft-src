/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.objects.ReferenceLinkedOpenHashSet
 *  it.unimi.dsi.fastutil.objects.ReferenceSortedSets
 *  java.util.SequencedSet
 */
package net.minecraft.world.item.component;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ReferenceLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceSortedSets;
import java.lang.invoke.LambdaMetafactory;
import java.util.List;
import java.util.SequencedSet;
import java.util.function.Function;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record TooltipDisplay(boolean hideTooltip, SequencedSet<DataComponentType<?>> hiddenComponents) {
    private static final Codec<SequencedSet<DataComponentType<?>>> COMPONENT_SET_CODEC = DataComponentType.CODEC.listOf().xmap(ReferenceLinkedOpenHashSet::new, (Function<SequencedSet, List>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Ljava/lang/Object;, copyOf(java.util.Collection ), (Ljava/util/SequencedSet;)Ljava/util/List;)());
    public static final Codec<TooltipDisplay> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)Codec.BOOL.optionalFieldOf("hide_tooltip", (Object)false).forGetter(TooltipDisplay::hideTooltip), (App)COMPONENT_SET_CODEC.optionalFieldOf("hidden_components", (Object)ReferenceSortedSets.emptySet()).forGetter(TooltipDisplay::hiddenComponents)).apply((Applicative)$$0, TooltipDisplay::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, TooltipDisplay> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, TooltipDisplay::hideTooltip, DataComponentType.STREAM_CODEC.apply(ByteBufCodecs.collection(ReferenceLinkedOpenHashSet::new)), TooltipDisplay::hiddenComponents, TooltipDisplay::new);
    public static final TooltipDisplay DEFAULT = new TooltipDisplay(false, (SequencedSet<DataComponentType<?>>)ReferenceSortedSets.emptySet());

    public TooltipDisplay withHidden(DataComponentType<?> $$0, boolean $$1) {
        if (this.hiddenComponents.contains($$0) == $$1) {
            return this;
        }
        ReferenceLinkedOpenHashSet $$2 = new ReferenceLinkedOpenHashSet(this.hiddenComponents);
        if ($$1) {
            $$2.add($$0);
        } else {
            $$2.remove($$0);
        }
        return new TooltipDisplay(this.hideTooltip, (SequencedSet<DataComponentType<?>>)$$2);
    }

    public boolean shows(DataComponentType<?> $$0) {
        return !this.hideTooltip && !this.hiddenComponents.contains($$0);
    }
}

