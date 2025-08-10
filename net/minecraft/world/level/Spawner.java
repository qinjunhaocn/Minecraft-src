/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.component.CustomData;

public interface Spawner {
    public void setEntityId(EntityType<?> var1, RandomSource var2);

    public static void appendHoverText(CustomData $$0, Consumer<Component> $$1, String $$2) {
        Component $$3 = Spawner.getSpawnEntityDisplayName($$0, $$2);
        if ($$3 != null) {
            $$1.accept($$3);
        } else {
            $$1.accept(CommonComponents.EMPTY);
            $$1.accept(Component.translatable("block.minecraft.spawner.desc1").withStyle(ChatFormatting.GRAY));
            $$1.accept(CommonComponents.space().append(Component.translatable("block.minecraft.spawner.desc2").withStyle(ChatFormatting.BLUE)));
        }
    }

    @Nullable
    public static Component getSpawnEntityDisplayName(CustomData $$02, String $$1) {
        return $$02.getUnsafe().getCompound($$1).flatMap($$0 -> $$0.getCompound("entity")).flatMap($$0 -> $$0.read("id", EntityType.CODEC)).map($$0 -> Component.translatable($$0.getDescriptionId()).withStyle(ChatFormatting.GRAY)).orElse(null);
    }
}

