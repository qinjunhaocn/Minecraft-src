/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package com.mojang.realmsclient.gui.screens.configuration;

import com.mojang.realmsclient.dto.RealmsRegion;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RegionSelectionPreference;
import com.mojang.realmsclient.dto.RegionSelectionPreferenceDto;
import com.mojang.realmsclient.dto.ServiceQuality;
import com.mojang.realmsclient.gui.screens.RealmsPopups;
import com.mojang.realmsclient.gui.screens.configuration.RealmsConfigurationTab;
import com.mojang.realmsclient.gui.screens.configuration.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.screens.configuration.RealmsPreferredRegionSelectionScreen;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.client.gui.layouts.EqualSpacingLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class RealmsSettingsTab
extends GridLayoutTab
implements RealmsConfigurationTab {
    private static final int COMPONENT_WIDTH = 212;
    private static final int EXTRA_SPACING = 2;
    private static final int DEFAULT_SPACING = 6;
    static final Component TITLE = Component.translatable("mco.configure.world.settings.title");
    private static final Component NAME_LABEL = Component.translatable("mco.configure.world.name");
    private static final Component DESCRIPTION_LABEL = Component.translatable("mco.configure.world.description");
    private static final Component REGION_PREFERENCE_LABEL = Component.translatable("mco.configure.world.region_preference");
    private final RealmsConfigureWorldScreen configurationScreen;
    private final Minecraft minecraft;
    private RealmsServer serverData;
    private final Map<RealmsRegion, ServiceQuality> regionServiceQuality;
    final Button closeOpenButton;
    private EditBox descEdit;
    private EditBox nameEdit;
    private final StringWidget selectedRegionStringWidget;
    private final ImageWidget selectedRegionImageWidget;
    private RegionSelection preferredRegionSelection;

    RealmsSettingsTab(RealmsConfigureWorldScreen $$02, Minecraft $$1, RealmsServer $$2, Map<RealmsRegion, ServiceQuality> $$32) {
        super(TITLE);
        this.configurationScreen = $$02;
        this.minecraft = $$1;
        this.serverData = $$2;
        this.regionServiceQuality = $$32;
        GridLayout.RowHelper $$4 = this.layout.rowSpacing(6).createRowHelper(1);
        $$4.addChild(new StringWidget(NAME_LABEL, $$02.getFont()));
        this.nameEdit = new EditBox($$1.font, 0, 0, 212, 20, Component.translatable("mco.configure.world.name"));
        this.nameEdit.setMaxLength(32);
        $$4.addChild(this.nameEdit);
        $$4.addChild(SpacerElement.height(2));
        $$4.addChild(new StringWidget(DESCRIPTION_LABEL, $$02.getFont()));
        this.descEdit = new EditBox($$1.font, 0, 0, 212, 20, Component.translatable("mco.configure.world.description"));
        this.descEdit.setMaxLength(32);
        $$4.addChild(this.descEdit);
        $$4.addChild(SpacerElement.height(2));
        $$4.addChild(new StringWidget(REGION_PREFERENCE_LABEL, $$02.getFont()));
        EqualSpacingLayout $$5 = new EqualSpacingLayout(0, 0, 212, $$02.getFont().lineHeight, EqualSpacingLayout.Orientation.HORIZONTAL);
        this.selectedRegionStringWidget = $$5.addChild(new StringWidget(192, $$02.getFont().lineHeight, Component.empty(), $$02.getFont()).alignLeft());
        this.selectedRegionImageWidget = $$5.addChild(ImageWidget.sprite(10, 8, ServiceQuality.UNKNOWN.getIcon()));
        $$4.addChild($$5);
        $$4.addChild(Button.builder(Component.translatable("mco.configure.world.buttons.region_preference"), $$0 -> this.openPreferenceSelector()).bounds(0, 0, 212, 20).build());
        $$4.addChild(SpacerElement.height(2));
        this.closeOpenButton = $$4.addChild(Button.builder(Component.empty(), $$3 -> {
            if ($$0.state == RealmsServer.State.OPEN) {
                $$1.setScreen(RealmsPopups.customPopupScreen($$02, Component.translatable("mco.configure.world.close.question.title"), Component.translatable("mco.configure.world.close.question.line1"), $$1 -> {
                    this.save();
                    $$02.closeTheWorld();
                }));
            } else {
                this.save();
                $$02.openTheWorld(false);
            }
        }).bounds(0, 0, 212, 20).build());
        this.closeOpenButton.active = false;
        this.updateData($$2);
    }

    private static MutableComponent getTranslatableFromPreference(RegionSelection $$0) {
        return ($$0.preference().equals((Object)RegionSelectionPreference.MANUAL) && $$0.region() != null ? Component.translatable($$0.region().translationKey) : Component.translatable($$0.preference().translationKey)).withStyle(ChatFormatting.GRAY);
    }

    private static ResourceLocation getServiceQualityIcon(RegionSelection $$0, Map<RealmsRegion, ServiceQuality> $$1) {
        if ($$0.region() != null && $$1.containsKey((Object)$$0.region())) {
            ServiceQuality $$2 = $$1.getOrDefault((Object)$$0.region(), ServiceQuality.UNKNOWN);
            return $$2.getIcon();
        }
        return ServiceQuality.UNKNOWN.getIcon();
    }

    private void openPreferenceSelector() {
        this.minecraft.setScreen(new RealmsPreferredRegionSelectionScreen(this.configurationScreen, this::applyRegionPreferenceSelection, this.regionServiceQuality, this.preferredRegionSelection));
    }

    private void applyRegionPreferenceSelection(RegionSelectionPreference $$0, RealmsRegion $$1) {
        this.preferredRegionSelection = new RegionSelection($$0, $$1);
        this.updateRegionPreferenceValues();
    }

    private void updateRegionPreferenceValues() {
        this.selectedRegionStringWidget.setMessage(RealmsSettingsTab.getTranslatableFromPreference(this.preferredRegionSelection));
        this.selectedRegionImageWidget.updateResource(RealmsSettingsTab.getServiceQualityIcon(this.preferredRegionSelection, this.regionServiceQuality));
        this.selectedRegionImageWidget.visible = this.preferredRegionSelection.preference == RegionSelectionPreference.MANUAL;
    }

    @Override
    public void onSelected(RealmsServer $$0) {
        this.updateData($$0);
    }

    @Override
    public void updateData(RealmsServer $$0) {
        this.serverData = $$0;
        if ($$0.regionSelectionPreference == null) {
            $$0.regionSelectionPreference = RegionSelectionPreferenceDto.DEFAULT;
        }
        if ($$0.regionSelectionPreference.regionSelectionPreference == RegionSelectionPreference.MANUAL && $$0.regionSelectionPreference.preferredRegion == null) {
            Optional $$12 = this.regionServiceQuality.keySet().stream().findFirst();
            $$12.ifPresent($$1 -> {
                $$0.regionSelectionPreference.preferredRegion = $$1;
            });
        }
        String $$2 = $$0.state == RealmsServer.State.OPEN ? "mco.configure.world.buttons.close" : "mco.configure.world.buttons.open";
        this.closeOpenButton.setMessage(Component.translatable($$2));
        this.closeOpenButton.active = true;
        this.preferredRegionSelection = new RegionSelection($$0.regionSelectionPreference.regionSelectionPreference, $$0.regionSelectionPreference.preferredRegion);
        this.nameEdit.setValue((String)Objects.requireNonNullElse((Object)$$0.getName(), (Object)""));
        this.descEdit.setValue($$0.getDescription());
        this.updateRegionPreferenceValues();
    }

    @Override
    public void onDeselected(RealmsServer $$0) {
        this.save();
    }

    public void save() {
        if (this.serverData.regionSelectionPreference != null && Objects.equals(this.nameEdit.getValue(), this.serverData.name) && Objects.equals(this.descEdit.getValue(), this.serverData.motd) && this.preferredRegionSelection.preference() == this.serverData.regionSelectionPreference.regionSelectionPreference && this.preferredRegionSelection.region() == this.serverData.regionSelectionPreference.preferredRegion) {
            return;
        }
        this.configurationScreen.saveSettings(this.nameEdit.getValue(), this.descEdit.getValue(), this.preferredRegionSelection.preference(), this.preferredRegionSelection.region());
    }

    public static final class RegionSelection
    extends Record {
        final RegionSelectionPreference preference;
        @Nullable
        private final RealmsRegion region;

        public RegionSelection(RegionSelectionPreference $$0, @Nullable RealmsRegion $$1) {
            this.preference = $$0;
            this.region = $$1;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{RegionSelection.class, "preference;region", "preference", "region"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{RegionSelection.class, "preference;region", "preference", "region"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{RegionSelection.class, "preference;region", "preference", "region"}, this, $$0);
        }

        public RegionSelectionPreference preference() {
            return this.preference;
        }

        @Nullable
        public RealmsRegion region() {
            return this.region;
        }
    }
}

