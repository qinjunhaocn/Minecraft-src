/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.advancements;

import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;

public final class AdvancementType
extends Enum<AdvancementType>
implements StringRepresentable {
    public static final /* enum */ AdvancementType TASK = new AdvancementType("task", ChatFormatting.GREEN);
    public static final /* enum */ AdvancementType CHALLENGE = new AdvancementType("challenge", ChatFormatting.DARK_PURPLE);
    public static final /* enum */ AdvancementType GOAL = new AdvancementType("goal", ChatFormatting.GREEN);
    public static final Codec<AdvancementType> CODEC;
    private final String name;
    private final ChatFormatting chatColor;
    private final Component displayName;
    private static final /* synthetic */ AdvancementType[] $VALUES;

    public static AdvancementType[] values() {
        return (AdvancementType[])$VALUES.clone();
    }

    public static AdvancementType valueOf(String $$0) {
        return Enum.valueOf(AdvancementType.class, $$0);
    }

    private AdvancementType(String $$0, ChatFormatting $$1) {
        this.name = $$0;
        this.chatColor = $$1;
        this.displayName = Component.translatable("advancements.toast." + $$0);
    }

    public ChatFormatting getChatColor() {
        return this.chatColor;
    }

    public Component getDisplayName() {
        return this.displayName;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public MutableComponent createAnnouncement(AdvancementHolder $$0, ServerPlayer $$1) {
        return Component.a("chat.type.advancement." + this.name, $$1.getDisplayName(), Advancement.name($$0));
    }

    private static /* synthetic */ AdvancementType[] d() {
        return new AdvancementType[]{TASK, CHALLENGE, GOAL};
    }

    static {
        $VALUES = AdvancementType.d();
        CODEC = StringRepresentable.fromEnum(AdvancementType::values);
    }
}

