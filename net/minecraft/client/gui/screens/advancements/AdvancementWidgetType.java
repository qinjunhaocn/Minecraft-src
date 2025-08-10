/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 */
package net.minecraft.client.gui.screens.advancements;

import net.minecraft.advancements.AdvancementType;
import net.minecraft.resources.ResourceLocation;

public final class AdvancementWidgetType
extends Enum<AdvancementWidgetType> {
    public static final /* enum */ AdvancementWidgetType OBTAINED = new AdvancementWidgetType(ResourceLocation.withDefaultNamespace("advancements/box_obtained"), ResourceLocation.withDefaultNamespace("advancements/task_frame_obtained"), ResourceLocation.withDefaultNamespace("advancements/challenge_frame_obtained"), ResourceLocation.withDefaultNamespace("advancements/goal_frame_obtained"));
    public static final /* enum */ AdvancementWidgetType UNOBTAINED = new AdvancementWidgetType(ResourceLocation.withDefaultNamespace("advancements/box_unobtained"), ResourceLocation.withDefaultNamespace("advancements/task_frame_unobtained"), ResourceLocation.withDefaultNamespace("advancements/challenge_frame_unobtained"), ResourceLocation.withDefaultNamespace("advancements/goal_frame_unobtained"));
    private final ResourceLocation boxSprite;
    private final ResourceLocation taskFrameSprite;
    private final ResourceLocation challengeFrameSprite;
    private final ResourceLocation goalFrameSprite;
    private static final /* synthetic */ AdvancementWidgetType[] $VALUES;

    public static AdvancementWidgetType[] values() {
        return (AdvancementWidgetType[])$VALUES.clone();
    }

    public static AdvancementWidgetType valueOf(String $$0) {
        return Enum.valueOf(AdvancementWidgetType.class, $$0);
    }

    private AdvancementWidgetType(ResourceLocation $$0, ResourceLocation $$1, ResourceLocation $$2, ResourceLocation $$3) {
        this.boxSprite = $$0;
        this.taskFrameSprite = $$1;
        this.challengeFrameSprite = $$2;
        this.goalFrameSprite = $$3;
    }

    public ResourceLocation boxSprite() {
        return this.boxSprite;
    }

    public ResourceLocation frameSprite(AdvancementType $$0) {
        return switch ($$0) {
            default -> throw new MatchException(null, null);
            case AdvancementType.TASK -> this.taskFrameSprite;
            case AdvancementType.CHALLENGE -> this.challengeFrameSprite;
            case AdvancementType.GOAL -> this.goalFrameSprite;
        };
    }

    private static /* synthetic */ AdvancementWidgetType[] b() {
        return new AdvancementWidgetType[]{OBTAINED, UNOBTAINED};
    }

    static {
        $VALUES = AdvancementWidgetType.b();
    }
}

