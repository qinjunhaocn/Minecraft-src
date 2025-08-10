/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 */
package net.minecraft.client.gui.screens.inventory;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.FittingMultiLineTextWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestInstance;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ServerboundTestInstanceBlockActionPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.TestInstanceBlockEntity;

public class TestInstanceBlockEditScreen
extends Screen {
    private static final Component ID_LABEL = Component.translatable("test_instance_block.test_id");
    private static final Component SIZE_LABEL = Component.translatable("test_instance_block.size");
    private static final Component INCLUDE_ENTITIES_LABEL = Component.translatable("test_instance_block.entities");
    private static final Component ROTATION_LABEL = Component.translatable("test_instance_block.rotation");
    private static final int BUTTON_PADDING = 8;
    private static final int WIDTH = 316;
    private static final int COLOR_SILVER = -4144960;
    private final TestInstanceBlockEntity blockEntity;
    @Nullable
    private EditBox idEdit;
    @Nullable
    private EditBox sizeXEdit;
    @Nullable
    private EditBox sizeYEdit;
    @Nullable
    private EditBox sizeZEdit;
    @Nullable
    private FittingMultiLineTextWidget infoWidget;
    @Nullable
    private Button saveButton;
    @Nullable
    private Button exportButton;
    @Nullable
    private CycleButton<Boolean> includeEntitiesButton;
    @Nullable
    private CycleButton<Rotation> rotationButton;

    public TestInstanceBlockEditScreen(TestInstanceBlockEntity $$0) {
        super($$0.getBlockState().getBlock().getName());
        this.blockEntity = $$0;
    }

    @Override
    protected void init() {
        int $$02 = this.width / 2 - 158;
        boolean $$12 = SharedConstants.IS_RUNNING_IN_IDE;
        int $$2 = $$12 ? 3 : 2;
        int $$3 = TestInstanceBlockEditScreen.widgetSize($$2);
        this.idEdit = new EditBox(this.font, $$02, 40, 316, 20, Component.translatable("test_instance_block.test_id"));
        this.idEdit.setMaxLength(128);
        Optional<ResourceKey<GameTestInstance>> $$4 = this.blockEntity.test();
        if ($$4.isPresent()) {
            this.idEdit.setValue($$4.get().location().toString());
        }
        this.idEdit.setResponder($$0 -> this.updateTestInfo(false));
        this.addRenderableWidget(this.idEdit);
        this.infoWidget = new FittingMultiLineTextWidget($$02, 70, 316, 8 * this.font.lineHeight, Component.literal(""), this.font);
        this.addRenderableWidget(this.infoWidget);
        Vec3i $$5 = this.blockEntity.getSize();
        int $$6 = 0;
        this.sizeXEdit = new EditBox(this.font, this.widgetX($$6++, 5), 160, TestInstanceBlockEditScreen.widgetSize(5), 20, Component.translatable("structure_block.size.x"));
        this.sizeXEdit.setMaxLength(15);
        this.addRenderableWidget(this.sizeXEdit);
        this.sizeYEdit = new EditBox(this.font, this.widgetX($$6++, 5), 160, TestInstanceBlockEditScreen.widgetSize(5), 20, Component.translatable("structure_block.size.y"));
        this.sizeYEdit.setMaxLength(15);
        this.addRenderableWidget(this.sizeYEdit);
        this.sizeZEdit = new EditBox(this.font, this.widgetX($$6++, 5), 160, TestInstanceBlockEditScreen.widgetSize(5), 20, Component.translatable("structure_block.size.z"));
        this.sizeZEdit.setMaxLength(15);
        this.addRenderableWidget(this.sizeZEdit);
        this.setSize($$5);
        this.rotationButton = this.addRenderableWidget(CycleButton.builder(TestInstanceBlockEditScreen::rotationDisplay).a((Rotation[])Rotation.values()).withInitialValue(this.blockEntity.getRotation()).displayOnlyValue().create(this.widgetX($$6++, 5), 160, TestInstanceBlockEditScreen.widgetSize(5), 20, ROTATION_LABEL, ($$0, $$1) -> this.updateSaveState()));
        this.includeEntitiesButton = this.addRenderableWidget(CycleButton.onOffBuilder(!this.blockEntity.ignoreEntities()).displayOnlyValue().create(this.widgetX($$6++, 5), 160, TestInstanceBlockEditScreen.widgetSize(5), 20, INCLUDE_ENTITIES_LABEL));
        $$6 = 0;
        this.addRenderableWidget(Button.builder(Component.translatable("test_instance.action.reset"), $$0 -> {
            this.sendToServer(ServerboundTestInstanceBlockActionPacket.Action.RESET);
            this.minecraft.setScreen(null);
        }).bounds(this.widgetX($$6++, $$2), 185, $$3, 20).build());
        this.saveButton = this.addRenderableWidget(Button.builder(Component.translatable("test_instance.action.save"), $$0 -> {
            this.sendToServer(ServerboundTestInstanceBlockActionPacket.Action.SAVE);
            this.minecraft.setScreen(null);
        }).bounds(this.widgetX($$6++, $$2), 185, $$3, 20).build());
        if ($$12) {
            this.exportButton = this.addRenderableWidget(Button.builder(Component.literal("Export Structure"), $$0 -> {
                this.sendToServer(ServerboundTestInstanceBlockActionPacket.Action.EXPORT);
                this.minecraft.setScreen(null);
            }).bounds(this.widgetX($$6++, $$2), 185, $$3, 20).build());
        }
        this.addRenderableWidget(Button.builder(Component.translatable("test_instance.action.run"), $$0 -> {
            this.sendToServer(ServerboundTestInstanceBlockActionPacket.Action.RUN);
            this.minecraft.setScreen(null);
        }).bounds(this.widgetX(0, 3), 210, TestInstanceBlockEditScreen.widgetSize(3), 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, $$0 -> this.onDone()).bounds(this.widgetX(1, 3), 210, TestInstanceBlockEditScreen.widgetSize(3), 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, $$0 -> this.onCancel()).bounds(this.widgetX(2, 3), 210, TestInstanceBlockEditScreen.widgetSize(3), 20).build());
        this.updateTestInfo(true);
    }

    private void updateSaveState() {
        boolean $$0;
        this.saveButton.active = $$0 = this.rotationButton.getValue() == Rotation.NONE && ResourceLocation.tryParse(this.idEdit.getValue()) != null;
        if (this.exportButton != null) {
            this.exportButton.active = $$0;
        }
    }

    private static Component rotationDisplay(Rotation $$0) {
        return Component.literal(switch ($$0) {
            default -> throw new MatchException(null, null);
            case Rotation.NONE -> "0";
            case Rotation.CLOCKWISE_90 -> "90";
            case Rotation.CLOCKWISE_180 -> "180";
            case Rotation.COUNTERCLOCKWISE_90 -> "270";
        });
    }

    private void setSize(Vec3i $$0) {
        this.sizeXEdit.setValue(Integer.toString($$0.getX()));
        this.sizeYEdit.setValue(Integer.toString($$0.getY()));
        this.sizeZEdit.setValue(Integer.toString($$0.getZ()));
    }

    private int widgetX(int $$0, int $$1) {
        int $$2 = this.width / 2 - 158;
        float $$3 = TestInstanceBlockEditScreen.exactWidgetSize($$1);
        return (int)((float)$$2 + (float)$$0 * (8.0f + $$3));
    }

    private static int widgetSize(int $$0) {
        return (int)TestInstanceBlockEditScreen.exactWidgetSize($$0);
    }

    private static float exactWidgetSize(int $$0) {
        return (float)(316 - ($$0 - 1) * 8) / (float)$$0;
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        int $$4 = this.width / 2 - 158;
        $$0.drawCenteredString(this.font, this.title, this.width / 2, 10, -1);
        $$0.drawString(this.font, ID_LABEL, $$4, 30, -4144960);
        $$0.drawString(this.font, SIZE_LABEL, $$4, 150, -4144960);
        $$0.drawString(this.font, ROTATION_LABEL, this.rotationButton.getX(), 150, -4144960);
        $$0.drawString(this.font, INCLUDE_ENTITIES_LABEL, this.includeEntitiesButton.getX(), 150, -4144960);
    }

    private void updateTestInfo(boolean $$0) {
        boolean $$1 = this.sendToServer($$0 ? ServerboundTestInstanceBlockActionPacket.Action.INIT : ServerboundTestInstanceBlockActionPacket.Action.QUERY);
        if (!$$1) {
            this.infoWidget.setMessage(Component.translatable("test_instance.description.invalid_id").withStyle(ChatFormatting.RED));
        }
        this.updateSaveState();
    }

    private void onDone() {
        this.sendToServer(ServerboundTestInstanceBlockActionPacket.Action.SET);
        this.onClose();
    }

    private boolean sendToServer(ServerboundTestInstanceBlockActionPacket.Action $$02) {
        Optional<ResourceLocation> $$1 = Optional.ofNullable(ResourceLocation.tryParse(this.idEdit.getValue()));
        Optional<ResourceKey<GameTestInstance>> $$2 = $$1.map($$0 -> ResourceKey.create(Registries.TEST_INSTANCE, $$0));
        Vec3i $$3 = new Vec3i(TestInstanceBlockEditScreen.parseSize(this.sizeXEdit.getValue()), TestInstanceBlockEditScreen.parseSize(this.sizeYEdit.getValue()), TestInstanceBlockEditScreen.parseSize(this.sizeZEdit.getValue()));
        boolean $$4 = this.includeEntitiesButton.getValue() == false;
        this.minecraft.getConnection().send(new ServerboundTestInstanceBlockActionPacket(this.blockEntity.getBlockPos(), $$02, $$2, $$3, this.rotationButton.getValue(), $$4));
        return $$1.isPresent();
    }

    public void setStatus(Component $$0, Optional<Vec3i> $$12) {
        MutableComponent $$2 = Component.empty();
        this.blockEntity.errorMessage().ifPresent($$1 -> $$2.append(Component.a("test_instance.description.failed", Component.empty().withStyle(ChatFormatting.RED).append((Component)$$1))).append("\n\n"));
        $$2.append($$0);
        this.infoWidget.setMessage($$2);
        $$12.ifPresent(this::setSize);
    }

    private void onCancel() {
        this.onClose();
    }

    private static int parseSize(String $$0) {
        try {
            return Mth.clamp(Integer.parseInt($$0), 1, 48);
        } catch (NumberFormatException $$1) {
            return 1;
        }
    }

    @Override
    public void renderBackground(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        this.renderTransparentBackground($$0);
    }
}

