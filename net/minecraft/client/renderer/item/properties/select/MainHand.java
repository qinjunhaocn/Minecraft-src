/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.client.renderer.item.properties.select;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public record MainHand() implements SelectItemModelProperty<HumanoidArm>
{
    public static final Codec<HumanoidArm> VALUE_CODEC = HumanoidArm.CODEC;
    public static final SelectItemModelProperty.Type<MainHand, HumanoidArm> TYPE = SelectItemModelProperty.Type.create(MapCodec.unit((Object)new MainHand()), VALUE_CODEC);

    @Override
    @Nullable
    public HumanoidArm get(ItemStack $$0, @Nullable ClientLevel $$1, @Nullable LivingEntity $$2, int $$3, ItemDisplayContext $$4) {
        return $$2 == null ? null : $$2.getMainArm();
    }

    @Override
    public SelectItemModelProperty.Type<MainHand, HumanoidArm> type() {
        return TYPE;
    }

    @Override
    public Codec<HumanoidArm> valueCodec() {
        return VALUE_CODEC;
    }

    @Override
    @Nullable
    public /* synthetic */ Object get(ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity, int n, ItemDisplayContext itemDisplayContext) {
        return this.get(itemStack, clientLevel, livingEntity, n, itemDisplayContext);
    }
}

