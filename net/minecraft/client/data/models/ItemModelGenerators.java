/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.client.data.models;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;
import net.minecraft.client.color.item.Dye;
import net.minecraft.client.color.item.Firework;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.color.item.MapColor;
import net.minecraft.client.color.item.Potion;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.item.BundleSelectedItemSpecialRenderer;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.RangeSelectItemModel;
import net.minecraft.client.renderer.item.properties.conditional.Broken;
import net.minecraft.client.renderer.item.properties.conditional.BundleHasSelectedItem;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.client.renderer.item.properties.conditional.FishingRodCast;
import net.minecraft.client.renderer.item.properties.numeric.CompassAngle;
import net.minecraft.client.renderer.item.properties.numeric.CompassAngleState;
import net.minecraft.client.renderer.item.properties.numeric.CrossbowPull;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.client.renderer.item.properties.numeric.Time;
import net.minecraft.client.renderer.item.properties.numeric.UseCycle;
import net.minecraft.client.renderer.item.properties.numeric.UseDuration;
import net.minecraft.client.renderer.item.properties.select.Charge;
import net.minecraft.client.renderer.item.properties.select.DisplayContext;
import net.minecraft.client.renderer.item.properties.select.TrimMaterialProperty;
import net.minecraft.client.renderer.special.ShieldSpecialRenderer;
import net.minecraft.client.renderer.special.TridentSpecialRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.trim.MaterialAssetGroup;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimMaterials;

public class ItemModelGenerators {
    private static final ItemTintSource BLANK_LAYER = ItemModelUtils.constantTint(-1);
    public static final ResourceLocation TRIM_PREFIX_HELMET = ItemModelGenerators.prefixForSlotTrim("helmet");
    public static final ResourceLocation TRIM_PREFIX_CHESTPLATE = ItemModelGenerators.prefixForSlotTrim("chestplate");
    public static final ResourceLocation TRIM_PREFIX_LEGGINGS = ItemModelGenerators.prefixForSlotTrim("leggings");
    public static final ResourceLocation TRIM_PREFIX_BOOTS = ItemModelGenerators.prefixForSlotTrim("boots");
    public static final List<TrimMaterialData> TRIM_MATERIAL_MODELS = List.of((Object[])new TrimMaterialData[]{new TrimMaterialData(MaterialAssetGroup.QUARTZ, TrimMaterials.QUARTZ), new TrimMaterialData(MaterialAssetGroup.IRON, TrimMaterials.IRON), new TrimMaterialData(MaterialAssetGroup.NETHERITE, TrimMaterials.NETHERITE), new TrimMaterialData(MaterialAssetGroup.REDSTONE, TrimMaterials.REDSTONE), new TrimMaterialData(MaterialAssetGroup.COPPER, TrimMaterials.COPPER), new TrimMaterialData(MaterialAssetGroup.GOLD, TrimMaterials.GOLD), new TrimMaterialData(MaterialAssetGroup.EMERALD, TrimMaterials.EMERALD), new TrimMaterialData(MaterialAssetGroup.DIAMOND, TrimMaterials.DIAMOND), new TrimMaterialData(MaterialAssetGroup.LAPIS, TrimMaterials.LAPIS), new TrimMaterialData(MaterialAssetGroup.AMETHYST, TrimMaterials.AMETHYST), new TrimMaterialData(MaterialAssetGroup.RESIN, TrimMaterials.RESIN)});
    private final ItemModelOutput itemModelOutput;
    private final BiConsumer<ResourceLocation, ModelInstance> modelOutput;

    public static ResourceLocation prefixForSlotTrim(String $$0) {
        return ResourceLocation.withDefaultNamespace("trims/items/" + $$0 + "_trim");
    }

    public ItemModelGenerators(ItemModelOutput $$0, BiConsumer<ResourceLocation, ModelInstance> $$1) {
        this.itemModelOutput = $$0;
        this.modelOutput = $$1;
    }

    private void declareCustomModelItem(Item $$0) {
        this.itemModelOutput.accept($$0, ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation($$0)));
    }

    private ResourceLocation createFlatItemModel(Item $$0, ModelTemplate $$1) {
        return $$1.create(ModelLocationUtils.getModelLocation($$0), TextureMapping.layer0($$0), this.modelOutput);
    }

    private void generateFlatItem(Item $$0, ModelTemplate $$1) {
        this.itemModelOutput.accept($$0, ItemModelUtils.plainModel(this.createFlatItemModel($$0, $$1)));
    }

    private ResourceLocation createFlatItemModel(Item $$0, String $$1, ModelTemplate $$2) {
        return $$2.create(ModelLocationUtils.getModelLocation($$0, $$1), TextureMapping.layer0(TextureMapping.getItemTexture($$0, $$1)), this.modelOutput);
    }

    private ResourceLocation createFlatItemModel(Item $$0, Item $$1, ModelTemplate $$2) {
        return $$2.create(ModelLocationUtils.getModelLocation($$0), TextureMapping.layer0($$1), this.modelOutput);
    }

    private void generateFlatItem(Item $$0, Item $$1, ModelTemplate $$2) {
        this.itemModelOutput.accept($$0, ItemModelUtils.plainModel(this.createFlatItemModel($$0, $$1, $$2)));
    }

    private void generateItemWithTintedOverlay(Item $$0, ItemTintSource $$1) {
        this.generateItemWithTintedOverlay($$0, "_overlay", $$1);
    }

    private void generateItemWithTintedOverlay(Item $$0, String $$1, ItemTintSource $$2) {
        ResourceLocation $$3 = this.generateLayeredItem($$0, TextureMapping.getItemTexture($$0), TextureMapping.getItemTexture($$0, $$1));
        this.itemModelOutput.accept($$0, ItemModelUtils.a($$3, BLANK_LAYER, $$2));
    }

    private List<RangeSelectItemModel.Entry> createCompassModels(Item $$0) {
        ArrayList<RangeSelectItemModel.Entry> $$1 = new ArrayList<RangeSelectItemModel.Entry>();
        ItemModel.Unbaked $$2 = ItemModelUtils.plainModel(this.createFlatItemModel($$0, "_16", ModelTemplates.FLAT_ITEM));
        $$1.add(ItemModelUtils.override($$2, 0.0f));
        for (int $$3 = 1; $$3 < 32; ++$$3) {
            int $$4 = Mth.positiveModulo($$3 - 16, 32);
            ItemModel.Unbaked $$5 = ItemModelUtils.plainModel(this.createFlatItemModel($$0, String.format(Locale.ROOT, "_%02d", $$4), ModelTemplates.FLAT_ITEM));
            $$1.add(ItemModelUtils.override($$5, (float)$$3 - 0.5f));
        }
        $$1.add(ItemModelUtils.override($$2, 31.5f));
        return $$1;
    }

    private void generateStandardCompassItem(Item $$0) {
        List<RangeSelectItemModel.Entry> $$1 = this.createCompassModels($$0);
        this.itemModelOutput.accept($$0, ItemModelUtils.conditional(ItemModelUtils.hasComponent(DataComponents.LODESTONE_TRACKER), ItemModelUtils.rangeSelect((RangeSelectItemModelProperty)new CompassAngle(true, CompassAngleState.CompassTarget.LODESTONE), 32.0f, $$1), ItemModelUtils.inOverworld(ItemModelUtils.rangeSelect((RangeSelectItemModelProperty)new CompassAngle(true, CompassAngleState.CompassTarget.SPAWN), 32.0f, $$1), ItemModelUtils.rangeSelect((RangeSelectItemModelProperty)new CompassAngle(true, CompassAngleState.CompassTarget.NONE), 32.0f, $$1))));
    }

    private void generateRecoveryCompassItem(Item $$0) {
        this.itemModelOutput.accept($$0, ItemModelUtils.rangeSelect((RangeSelectItemModelProperty)new CompassAngle(true, CompassAngleState.CompassTarget.RECOVERY), 32.0f, this.createCompassModels($$0)));
    }

    private void generateClockItem(Item $$0) {
        ArrayList<RangeSelectItemModel.Entry> $$1 = new ArrayList<RangeSelectItemModel.Entry>();
        ItemModel.Unbaked $$2 = ItemModelUtils.plainModel(this.createFlatItemModel($$0, "_00", ModelTemplates.FLAT_ITEM));
        $$1.add(ItemModelUtils.override($$2, 0.0f));
        for (int $$3 = 1; $$3 < 64; ++$$3) {
            ItemModel.Unbaked $$4 = ItemModelUtils.plainModel(this.createFlatItemModel($$0, String.format(Locale.ROOT, "_%02d", $$3), ModelTemplates.FLAT_ITEM));
            $$1.add(ItemModelUtils.override($$4, (float)$$3 - 0.5f));
        }
        $$1.add(ItemModelUtils.override($$2, 63.5f));
        this.itemModelOutput.accept($$0, ItemModelUtils.inOverworld(ItemModelUtils.rangeSelect((RangeSelectItemModelProperty)new Time(true, Time.TimeSource.DAYTIME), 64.0f, $$1), ItemModelUtils.rangeSelect((RangeSelectItemModelProperty)new Time(true, Time.TimeSource.RANDOM), 64.0f, $$1)));
    }

    private ResourceLocation generateLayeredItem(Item $$0, ResourceLocation $$1, ResourceLocation $$2) {
        return ModelTemplates.TWO_LAYERED_ITEM.create($$0, TextureMapping.layered($$1, $$2), this.modelOutput);
    }

    private ResourceLocation generateLayeredItem(ResourceLocation $$0, ResourceLocation $$1, ResourceLocation $$2) {
        return ModelTemplates.TWO_LAYERED_ITEM.create($$0, TextureMapping.layered($$1, $$2), this.modelOutput);
    }

    private void generateLayeredItem(ResourceLocation $$0, ResourceLocation $$1, ResourceLocation $$2, ResourceLocation $$3) {
        ModelTemplates.THREE_LAYERED_ITEM.create($$0, TextureMapping.layered($$1, $$2, $$3), this.modelOutput);
    }

    private void generateTrimmableItem(Item $$0, ResourceKey<EquipmentAsset> $$1, ResourceLocation $$2, boolean $$3) {
        ItemModel.Unbaked $$14;
        ResourceLocation $$4 = ModelLocationUtils.getModelLocation($$0);
        ResourceLocation $$5 = TextureMapping.getItemTexture($$0);
        ResourceLocation $$6 = TextureMapping.getItemTexture($$0, "_overlay");
        ArrayList $$7 = new ArrayList(TRIM_MATERIAL_MODELS.size());
        for (TrimMaterialData $$8 : TRIM_MATERIAL_MODELS) {
            ItemModel.Unbaked $$12;
            ResourceLocation $$9 = $$4.withSuffix("_" + $$8.assets().base().suffix() + "_trim");
            ResourceLocation $$10 = $$2.withSuffix("_" + $$8.assets().assetId($$1).suffix());
            if ($$3) {
                this.generateLayeredItem($$9, $$5, $$6, $$10);
                ItemModel.Unbaked $$11 = ItemModelUtils.a($$9, new Dye(-6265536));
            } else {
                this.generateLayeredItem($$9, $$5, $$10);
                $$12 = ItemModelUtils.plainModel($$9);
            }
            $$7.add(ItemModelUtils.when($$8.materialKey, $$12));
        }
        if ($$3) {
            ModelTemplates.TWO_LAYERED_ITEM.create($$4, TextureMapping.layered($$5, $$6), this.modelOutput);
            ItemModel.Unbaked $$13 = ItemModelUtils.a($$4, new Dye(-6265536));
        } else {
            ModelTemplates.FLAT_ITEM.create($$4, TextureMapping.layer0($$5), this.modelOutput);
            $$14 = ItemModelUtils.plainModel($$4);
        }
        this.itemModelOutput.accept($$0, ItemModelUtils.select(new TrimMaterialProperty(), $$14, $$7));
    }

    private void generateBundleModels(Item $$0) {
        ItemModel.Unbaked $$1 = ItemModelUtils.plainModel(this.createFlatItemModel($$0, ModelTemplates.FLAT_ITEM));
        ResourceLocation $$2 = this.generateBundleCoverModel($$0, ModelTemplates.BUNDLE_OPEN_BACK_INVENTORY, "_open_back");
        ResourceLocation $$3 = this.generateBundleCoverModel($$0, ModelTemplates.BUNDLE_OPEN_FRONT_INVENTORY, "_open_front");
        ItemModel.Unbaked $$4 = ItemModelUtils.a(ItemModelUtils.plainModel($$2), new BundleSelectedItemSpecialRenderer.Unbaked(), ItemModelUtils.plainModel($$3));
        ItemModel.Unbaked $$5 = ItemModelUtils.conditional(new BundleHasSelectedItem(), $$4, $$1);
        this.itemModelOutput.accept($$0, ItemModelUtils.a(new DisplayContext(), $$1, ItemModelUtils.when(ItemDisplayContext.GUI, $$5)));
    }

    private ResourceLocation generateBundleCoverModel(Item $$0, ModelTemplate $$1, String $$2) {
        ResourceLocation $$3 = TextureMapping.getItemTexture($$0, $$2);
        return $$1.create($$0, TextureMapping.layer0($$3), this.modelOutput);
    }

    private void generateBow(Item $$0) {
        ItemModel.Unbaked $$1 = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation($$0));
        ItemModel.Unbaked $$2 = ItemModelUtils.plainModel(this.createFlatItemModel($$0, "_pulling_0", ModelTemplates.BOW));
        ItemModel.Unbaked $$3 = ItemModelUtils.plainModel(this.createFlatItemModel($$0, "_pulling_1", ModelTemplates.BOW));
        ItemModel.Unbaked $$4 = ItemModelUtils.plainModel(this.createFlatItemModel($$0, "_pulling_2", ModelTemplates.BOW));
        this.itemModelOutput.accept($$0, ItemModelUtils.conditional(ItemModelUtils.isUsingItem(), ItemModelUtils.a((RangeSelectItemModelProperty)new UseDuration(false), 0.05f, $$2, ItemModelUtils.override($$3, 0.65f), ItemModelUtils.override($$4, 0.9f)), $$1));
    }

    private void generateCrossbow(Item $$0) {
        ItemModel.Unbaked $$1 = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation($$0));
        ItemModel.Unbaked $$2 = ItemModelUtils.plainModel(this.createFlatItemModel($$0, "_pulling_0", ModelTemplates.CROSSBOW));
        ItemModel.Unbaked $$3 = ItemModelUtils.plainModel(this.createFlatItemModel($$0, "_pulling_1", ModelTemplates.CROSSBOW));
        ItemModel.Unbaked $$4 = ItemModelUtils.plainModel(this.createFlatItemModel($$0, "_pulling_2", ModelTemplates.CROSSBOW));
        ItemModel.Unbaked $$5 = ItemModelUtils.plainModel(this.createFlatItemModel($$0, "_arrow", ModelTemplates.CROSSBOW));
        ItemModel.Unbaked $$6 = ItemModelUtils.plainModel(this.createFlatItemModel($$0, "_firework", ModelTemplates.CROSSBOW));
        this.itemModelOutput.accept($$0, ItemModelUtils.a(new Charge(), ItemModelUtils.conditional(ItemModelUtils.isUsingItem(), ItemModelUtils.a((RangeSelectItemModelProperty)new CrossbowPull(), $$2, ItemModelUtils.override($$3, 0.58f), ItemModelUtils.override($$4, 1.0f)), $$1), ItemModelUtils.when(CrossbowItem.ChargeType.ARROW, $$5), ItemModelUtils.when(CrossbowItem.ChargeType.ROCKET, $$6)));
    }

    private void generateBooleanDispatch(Item $$0, ConditionalItemModelProperty $$1, ItemModel.Unbaked $$2, ItemModel.Unbaked $$3) {
        this.itemModelOutput.accept($$0, ItemModelUtils.conditional($$1, $$2, $$3));
    }

    private void generateElytra(Item $$0) {
        ItemModel.Unbaked $$1 = ItemModelUtils.plainModel(this.createFlatItemModel($$0, ModelTemplates.FLAT_ITEM));
        ItemModel.Unbaked $$2 = ItemModelUtils.plainModel(this.createFlatItemModel($$0, "_broken", ModelTemplates.FLAT_ITEM));
        this.generateBooleanDispatch($$0, new Broken(), $$2, $$1);
    }

    private void generateBrush(Item $$0) {
        ItemModel.Unbaked $$1 = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation($$0));
        ItemModel.Unbaked $$2 = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation($$0, "_brushing_0"));
        ItemModel.Unbaked $$3 = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation($$0, "_brushing_1"));
        ItemModel.Unbaked $$4 = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation($$0, "_brushing_2"));
        this.itemModelOutput.accept($$0, ItemModelUtils.a((RangeSelectItemModelProperty)new UseCycle(10.0f), 0.1f, $$1, ItemModelUtils.override($$2, 0.25f), ItemModelUtils.override($$3, 0.5f), ItemModelUtils.override($$4, 0.75f)));
    }

    private void generateFishingRod(Item $$0) {
        ItemModel.Unbaked $$1 = ItemModelUtils.plainModel(this.createFlatItemModel($$0, ModelTemplates.FLAT_HANDHELD_ROD_ITEM));
        ItemModel.Unbaked $$2 = ItemModelUtils.plainModel(this.createFlatItemModel($$0, "_cast", ModelTemplates.FLAT_HANDHELD_ROD_ITEM));
        this.generateBooleanDispatch($$0, new FishingRodCast(), $$2, $$1);
    }

    private void generateGoatHorn(Item $$0) {
        ItemModel.Unbaked $$1 = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation($$0));
        ItemModel.Unbaked $$2 = ItemModelUtils.plainModel(ModelLocationUtils.decorateItemModelLocation("tooting_goat_horn"));
        this.generateBooleanDispatch($$0, ItemModelUtils.isUsingItem(), $$2, $$1);
    }

    private void generateShield(Item $$0) {
        ItemModel.Unbaked $$1 = ItemModelUtils.specialModel(ModelLocationUtils.getModelLocation($$0), new ShieldSpecialRenderer.Unbaked());
        ItemModel.Unbaked $$2 = ItemModelUtils.specialModel(ModelLocationUtils.getModelLocation($$0, "_blocking"), new ShieldSpecialRenderer.Unbaked());
        this.generateBooleanDispatch($$0, ItemModelUtils.isUsingItem(), $$2, $$1);
    }

    private static ItemModel.Unbaked createFlatModelDispatch(ItemModel.Unbaked $$0, ItemModel.Unbaked $$1) {
        return ItemModelUtils.a(new DisplayContext(), $$1, ItemModelUtils.when(List.of((Object)ItemDisplayContext.GUI, (Object)ItemDisplayContext.GROUND, (Object)ItemDisplayContext.FIXED), $$0));
    }

    private void generateSpyglass(Item $$0) {
        ItemModel.Unbaked $$1 = ItemModelUtils.plainModel(this.createFlatItemModel($$0, ModelTemplates.FLAT_ITEM));
        ItemModel.Unbaked $$2 = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation($$0, "_in_hand"));
        this.itemModelOutput.accept($$0, ItemModelGenerators.createFlatModelDispatch($$1, $$2));
    }

    private void generateTrident(Item $$0) {
        ItemModel.Unbaked $$1 = ItemModelUtils.plainModel(this.createFlatItemModel($$0, ModelTemplates.FLAT_ITEM));
        ItemModel.Unbaked $$2 = ItemModelUtils.specialModel(ModelLocationUtils.getModelLocation($$0, "_in_hand"), new TridentSpecialRenderer.Unbaked());
        ItemModel.Unbaked $$3 = ItemModelUtils.specialModel(ModelLocationUtils.getModelLocation($$0, "_throwing"), new TridentSpecialRenderer.Unbaked());
        ItemModel.Unbaked $$4 = ItemModelUtils.conditional(ItemModelUtils.isUsingItem(), $$3, $$2);
        this.itemModelOutput.accept($$0, ItemModelGenerators.createFlatModelDispatch($$1, $$4));
    }

    private void addPotionTint(Item $$0, ResourceLocation $$1) {
        this.itemModelOutput.accept($$0, ItemModelUtils.a($$1, new Potion()));
    }

    private void generatePotion(Item $$0) {
        ResourceLocation $$1 = this.generateLayeredItem($$0, ModelLocationUtils.decorateItemModelLocation("potion_overlay"), ModelLocationUtils.getModelLocation($$0));
        this.addPotionTint($$0, $$1);
    }

    private void generateTippedArrow(Item $$0) {
        ResourceLocation $$1 = this.generateLayeredItem($$0, ModelLocationUtils.getModelLocation($$0, "_head"), ModelLocationUtils.getModelLocation($$0, "_base"));
        this.addPotionTint($$0, $$1);
    }

    private void generateDyedItem(Item $$0, int $$1) {
        ResourceLocation $$2 = this.createFlatItemModel($$0, ModelTemplates.FLAT_ITEM);
        this.itemModelOutput.accept($$0, ItemModelUtils.a($$2, new Dye($$1)));
    }

    private void generateTwoLayerDyedItem(Item $$0) {
        ResourceLocation $$1 = TextureMapping.getItemTexture($$0);
        ResourceLocation $$2 = TextureMapping.getItemTexture($$0, "_overlay");
        ResourceLocation $$3 = ModelTemplates.FLAT_ITEM.create($$0, TextureMapping.layer0($$1), this.modelOutput);
        ResourceLocation $$4 = ModelLocationUtils.getModelLocation($$0, "_dyed");
        ModelTemplates.TWO_LAYERED_ITEM.create($$4, TextureMapping.layered($$1, $$2), this.modelOutput);
        this.itemModelOutput.accept($$0, ItemModelUtils.conditional(ItemModelUtils.hasComponent(DataComponents.DYED_COLOR), ItemModelUtils.a($$4, BLANK_LAYER, new Dye(0)), ItemModelUtils.plainModel($$3)));
    }

    public void run() {
        this.generateFlatItem(Items.ACACIA_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.CHERRY_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.ACACIA_CHEST_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.CHERRY_CHEST_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.AMETHYST_SHARD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.APPLE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.ARMADILLO_SCUTE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.ARMOR_STAND, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.ARROW, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BAKED_POTATO, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BAMBOO, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.BEEF, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BEETROOT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BEETROOT_SOUP, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BIRCH_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BIRCH_CHEST_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BLACK_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BLAZE_POWDER, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BLAZE_ROD, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.BLUE_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BONE_MEAL, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BORDURE_INDENTED_BANNER_PATTERN, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BOOK, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BOWL, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BREAD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BRICK, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BREEZE_ROD, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.BROWN_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BUCKET, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.CARROT_ON_A_STICK, ModelTemplates.FLAT_HANDHELD_ROD_ITEM);
        this.generateFlatItem(Items.WARPED_FUNGUS_ON_A_STICK, ModelTemplates.FLAT_HANDHELD_ROD_ITEM);
        this.generateFlatItem(Items.CHARCOAL, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.CHEST_MINECART, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.CHICKEN, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.CHORUS_FRUIT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.CLAY_BALL, ModelTemplates.FLAT_ITEM);
        this.generateClockItem(Items.CLOCK);
        this.generateFlatItem(Items.COAL, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.COD_BUCKET, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.COMMAND_BLOCK_MINECART, ModelTemplates.FLAT_ITEM);
        this.generateStandardCompassItem(Items.COMPASS);
        this.generateRecoveryCompassItem(Items.RECOVERY_COMPASS);
        this.generateFlatItem(Items.COOKED_BEEF, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.COOKED_CHICKEN, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.COOKED_COD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.COOKED_MUTTON, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.COOKED_PORKCHOP, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.COOKED_RABBIT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.COOKED_SALMON, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.COOKIE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.RAW_COPPER, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.COPPER_INGOT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.CREEPER_BANNER_PATTERN, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.CYAN_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.DARK_OAK_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.DARK_OAK_CHEST_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.DIAMOND, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.DIAMOND_AXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.DIAMOND_HOE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.DIAMOND_HORSE_ARMOR, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.DIAMOND_PICKAXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.DIAMOND_SHOVEL, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.DIAMOND_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.DRAGON_BREATH, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.DRIED_KELP, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BLUE_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BROWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.EMERALD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.ENCHANTED_BOOK, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.ENDER_EYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.ENDER_PEARL, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.END_CRYSTAL, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.EXPERIENCE_BOTTLE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.FERMENTED_SPIDER_EYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.FIELD_MASONED_BANNER_PATTERN, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.FIREWORK_ROCKET, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.FIRE_CHARGE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.FLINT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.FLINT_AND_STEEL, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.FLOW_BANNER_PATTERN, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.FLOWER_BANNER_PATTERN, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.FURNACE_MINECART, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GHAST_TEAR, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GLASS_BOTTLE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GLISTERING_MELON_SLICE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GLOBE_BANNER_PATTERN, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GLOW_BERRIES, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GLOWSTONE_DUST, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GLOW_INK_SAC, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GLOW_ITEM_FRAME, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.RAW_GOLD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GOLDEN_APPLE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GOLDEN_AXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.GOLDEN_CARROT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GOLDEN_HOE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.GOLDEN_HORSE_ARMOR, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GOLDEN_PICKAXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.GOLDEN_SHOVEL, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.GOLDEN_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.GOLD_INGOT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GOLD_NUGGET, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GRAY_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GREEN_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GUNPOWDER, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GUSTER_BANNER_PATTERN, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.HEART_OF_THE_SEA, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.HONEYCOMB, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.HONEY_BOTTLE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.HOPPER_MINECART, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.INK_SAC, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.RAW_IRON, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.IRON_AXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.IRON_HOE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.IRON_HORSE_ARMOR, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.IRON_INGOT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.IRON_NUGGET, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.IRON_PICKAXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.IRON_SHOVEL, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.IRON_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.ITEM_FRAME, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.JUNGLE_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.JUNGLE_CHEST_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.KNOWLEDGE_BOOK, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.LAPIS_LAZULI, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.LAVA_BUCKET, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.LEATHER, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.LIGHT_BLUE_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.LIGHT_GRAY_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.LIME_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MAGENTA_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MAGMA_CREAM, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MANGROVE_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MANGROVE_CHEST_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BAMBOO_RAFT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BAMBOO_CHEST_RAFT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MAP, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MELON_SLICE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MILK_BUCKET, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MINECART, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MOJANG_BANNER_PATTERN, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MUSHROOM_STEW, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.DISC_FRAGMENT_5, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MUSIC_DISC_11, ModelTemplates.MUSIC_DISC);
        this.generateFlatItem(Items.MUSIC_DISC_13, ModelTemplates.MUSIC_DISC);
        this.generateFlatItem(Items.MUSIC_DISC_BLOCKS, ModelTemplates.MUSIC_DISC);
        this.generateFlatItem(Items.MUSIC_DISC_CAT, ModelTemplates.MUSIC_DISC);
        this.generateFlatItem(Items.MUSIC_DISC_CHIRP, ModelTemplates.MUSIC_DISC);
        this.generateFlatItem(Items.MUSIC_DISC_CREATOR, ModelTemplates.MUSIC_DISC);
        this.generateFlatItem(Items.MUSIC_DISC_CREATOR_MUSIC_BOX, ModelTemplates.MUSIC_DISC);
        this.generateFlatItem(Items.MUSIC_DISC_FAR, ModelTemplates.MUSIC_DISC);
        this.generateFlatItem(Items.MUSIC_DISC_LAVA_CHICKEN, ModelTemplates.MUSIC_DISC);
        this.generateFlatItem(Items.MUSIC_DISC_MALL, ModelTemplates.MUSIC_DISC);
        this.generateFlatItem(Items.MUSIC_DISC_MELLOHI, ModelTemplates.MUSIC_DISC);
        this.generateFlatItem(Items.MUSIC_DISC_PIGSTEP, ModelTemplates.MUSIC_DISC);
        this.generateFlatItem(Items.MUSIC_DISC_PRECIPICE, ModelTemplates.MUSIC_DISC);
        this.generateFlatItem(Items.MUSIC_DISC_STAL, ModelTemplates.MUSIC_DISC);
        this.generateFlatItem(Items.MUSIC_DISC_STRAD, ModelTemplates.MUSIC_DISC);
        this.generateFlatItem(Items.MUSIC_DISC_WAIT, ModelTemplates.MUSIC_DISC);
        this.generateFlatItem(Items.MUSIC_DISC_WARD, ModelTemplates.MUSIC_DISC);
        this.generateFlatItem(Items.MUSIC_DISC_OTHERSIDE, ModelTemplates.MUSIC_DISC);
        this.generateFlatItem(Items.MUSIC_DISC_RELIC, ModelTemplates.MUSIC_DISC);
        this.generateFlatItem(Items.MUSIC_DISC_5, ModelTemplates.MUSIC_DISC);
        this.generateFlatItem(Items.MUSIC_DISC_TEARS, ModelTemplates.MUSIC_DISC);
        this.generateFlatItem(Items.MUTTON, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.NAME_TAG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.NAUTILUS_SHELL, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.NETHERITE_AXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.NETHERITE_HOE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.NETHERITE_INGOT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.NETHERITE_PICKAXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.NETHERITE_SCRAP, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.NETHERITE_SHOVEL, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.NETHERITE_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.NETHER_BRICK, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.RESIN_BRICK, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.NETHER_STAR, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.OAK_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.OAK_CHEST_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.ORANGE_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PAINTING, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PALE_OAK_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PALE_OAK_CHEST_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PAPER, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PHANTOM_MEMBRANE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PIGLIN_BANNER_PATTERN, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PINK_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.POISONOUS_POTATO, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.POPPED_CHORUS_FRUIT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PORKCHOP, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.POWDER_SNOW_BUCKET, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PRISMARINE_CRYSTALS, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PRISMARINE_SHARD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PUFFERFISH, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PUFFERFISH_BUCKET, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PUMPKIN_PIE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PURPLE_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.QUARTZ, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.RABBIT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.RABBIT_FOOT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.RABBIT_HIDE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.RABBIT_STEW, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.RED_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.ROTTEN_FLESH, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SADDLE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SALMON, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SALMON_BUCKET, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.TURTLE_SCUTE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SHEARS, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SHULKER_SHELL, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SKULL_BANNER_PATTERN, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SLIME_BALL, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SNOWBALL, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.ECHO_SHARD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SPECTRAL_ARROW, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SPIDER_EYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SPRUCE_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SPRUCE_CHEST_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.STICK, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.STONE_AXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.STONE_HOE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.STONE_PICKAXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.STONE_SHOVEL, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.STONE_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.SUGAR, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SUSPICIOUS_STEW, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.TNT_MINECART, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.TOTEM_OF_UNDYING, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.TROPICAL_FISH, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.TROPICAL_FISH_BUCKET, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.AXOLOTL_BUCKET, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.TADPOLE_BUCKET, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.WATER_BUCKET, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.WHEAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.WHITE_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.WIND_CHARGE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MACE, ModelTemplates.FLAT_HANDHELD_MACE_ITEM);
        this.generateFlatItem(Items.WOODEN_AXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.WOODEN_HOE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.WOODEN_PICKAXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.WOODEN_SHOVEL, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.WOODEN_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.WRITABLE_BOOK, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.WRITTEN_BOOK, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.YELLOW_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.HOST_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.DEBUG_STICK, Items.STICK, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.ENCHANTED_GOLDEN_APPLE, Items.GOLDEN_APPLE, ModelTemplates.FLAT_ITEM);
        this.generateTrimmableItem(Items.TURTLE_HELMET, EquipmentAssets.TURTLE_SCUTE, TRIM_PREFIX_HELMET, false);
        this.generateTrimmableItem(Items.LEATHER_HELMET, EquipmentAssets.LEATHER, TRIM_PREFIX_HELMET, true);
        this.generateTrimmableItem(Items.LEATHER_CHESTPLATE, EquipmentAssets.LEATHER, TRIM_PREFIX_CHESTPLATE, true);
        this.generateTrimmableItem(Items.LEATHER_LEGGINGS, EquipmentAssets.LEATHER, TRIM_PREFIX_LEGGINGS, true);
        this.generateTrimmableItem(Items.LEATHER_BOOTS, EquipmentAssets.LEATHER, TRIM_PREFIX_BOOTS, true);
        this.generateTrimmableItem(Items.CHAINMAIL_HELMET, EquipmentAssets.CHAINMAIL, TRIM_PREFIX_HELMET, false);
        this.generateTrimmableItem(Items.CHAINMAIL_CHESTPLATE, EquipmentAssets.CHAINMAIL, TRIM_PREFIX_CHESTPLATE, false);
        this.generateTrimmableItem(Items.CHAINMAIL_LEGGINGS, EquipmentAssets.CHAINMAIL, TRIM_PREFIX_LEGGINGS, false);
        this.generateTrimmableItem(Items.CHAINMAIL_BOOTS, EquipmentAssets.CHAINMAIL, TRIM_PREFIX_BOOTS, false);
        this.generateTrimmableItem(Items.IRON_HELMET, EquipmentAssets.IRON, TRIM_PREFIX_HELMET, false);
        this.generateTrimmableItem(Items.IRON_CHESTPLATE, EquipmentAssets.IRON, TRIM_PREFIX_CHESTPLATE, false);
        this.generateTrimmableItem(Items.IRON_LEGGINGS, EquipmentAssets.IRON, TRIM_PREFIX_LEGGINGS, false);
        this.generateTrimmableItem(Items.IRON_BOOTS, EquipmentAssets.IRON, TRIM_PREFIX_BOOTS, false);
        this.generateTrimmableItem(Items.DIAMOND_HELMET, EquipmentAssets.DIAMOND, TRIM_PREFIX_HELMET, false);
        this.generateTrimmableItem(Items.DIAMOND_CHESTPLATE, EquipmentAssets.DIAMOND, TRIM_PREFIX_CHESTPLATE, false);
        this.generateTrimmableItem(Items.DIAMOND_LEGGINGS, EquipmentAssets.DIAMOND, TRIM_PREFIX_LEGGINGS, false);
        this.generateTrimmableItem(Items.DIAMOND_BOOTS, EquipmentAssets.DIAMOND, TRIM_PREFIX_BOOTS, false);
        this.generateTrimmableItem(Items.GOLDEN_HELMET, EquipmentAssets.GOLD, TRIM_PREFIX_HELMET, false);
        this.generateTrimmableItem(Items.GOLDEN_CHESTPLATE, EquipmentAssets.GOLD, TRIM_PREFIX_CHESTPLATE, false);
        this.generateTrimmableItem(Items.GOLDEN_LEGGINGS, EquipmentAssets.GOLD, TRIM_PREFIX_LEGGINGS, false);
        this.generateTrimmableItem(Items.GOLDEN_BOOTS, EquipmentAssets.GOLD, TRIM_PREFIX_BOOTS, false);
        this.generateTrimmableItem(Items.NETHERITE_HELMET, EquipmentAssets.NETHERITE, TRIM_PREFIX_HELMET, false);
        this.generateTrimmableItem(Items.NETHERITE_CHESTPLATE, EquipmentAssets.NETHERITE, TRIM_PREFIX_CHESTPLATE, false);
        this.generateTrimmableItem(Items.NETHERITE_LEGGINGS, EquipmentAssets.NETHERITE, TRIM_PREFIX_LEGGINGS, false);
        this.generateTrimmableItem(Items.NETHERITE_BOOTS, EquipmentAssets.NETHERITE, TRIM_PREFIX_BOOTS, false);
        this.generateDyedItem(Items.LEATHER_HORSE_ARMOR, -6265536);
        this.generateFlatItem(Items.ANGLER_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.ARCHER_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.ARMS_UP_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BLADE_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BREWER_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BURN_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.DANGER_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.EXPLORER_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.FLOW_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.FRIEND_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GUSTER_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.HEART_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.HEARTBREAK_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.HOWL_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MINER_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MOURNER_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PLENTY_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PRIZE_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SCRAPE_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SHEAF_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SHELTER_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SKULL_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SNORT_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.TRIAL_KEY, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.OMINOUS_TRIAL_KEY, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.OMINOUS_BOTTLE, ModelTemplates.FLAT_ITEM);
        this.generateItemWithTintedOverlay(Items.FIREWORK_STAR, new Firework());
        this.generateItemWithTintedOverlay(Items.FILLED_MAP, "_markings", new MapColor());
        this.generateBundleModels(Items.BUNDLE);
        this.generateBundleModels(Items.BLACK_BUNDLE);
        this.generateBundleModels(Items.WHITE_BUNDLE);
        this.generateBundleModels(Items.GRAY_BUNDLE);
        this.generateBundleModels(Items.LIGHT_GRAY_BUNDLE);
        this.generateBundleModels(Items.LIGHT_BLUE_BUNDLE);
        this.generateBundleModels(Items.BLUE_BUNDLE);
        this.generateBundleModels(Items.CYAN_BUNDLE);
        this.generateBundleModels(Items.YELLOW_BUNDLE);
        this.generateBundleModels(Items.RED_BUNDLE);
        this.generateBundleModels(Items.PURPLE_BUNDLE);
        this.generateBundleModels(Items.MAGENTA_BUNDLE);
        this.generateBundleModels(Items.PINK_BUNDLE);
        this.generateBundleModels(Items.GREEN_BUNDLE);
        this.generateBundleModels(Items.LIME_BUNDLE);
        this.generateBundleModels(Items.BROWN_BUNDLE);
        this.generateBundleModels(Items.ORANGE_BUNDLE);
        this.generateSpyglass(Items.SPYGLASS);
        this.generateTrident(Items.TRIDENT);
        this.generateTwoLayerDyedItem(Items.WOLF_ARMOR);
        this.generateFlatItem(Items.WHITE_HARNESS, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.ORANGE_HARNESS, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MAGENTA_HARNESS, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.LIGHT_BLUE_HARNESS, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.YELLOW_HARNESS, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.LIME_HARNESS, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PINK_HARNESS, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GRAY_HARNESS, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.LIGHT_GRAY_HARNESS, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.CYAN_HARNESS, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PURPLE_HARNESS, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BLUE_HARNESS, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BROWN_HARNESS, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GREEN_HARNESS, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.RED_HARNESS, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BLACK_HARNESS, ModelTemplates.FLAT_ITEM);
        this.generateBow(Items.BOW);
        this.generateCrossbow(Items.CROSSBOW);
        this.generateElytra(Items.ELYTRA);
        this.generateBrush(Items.BRUSH);
        this.generateFishingRod(Items.FISHING_ROD);
        this.generateGoatHorn(Items.GOAT_HORN);
        this.generateShield(Items.SHIELD);
        this.generateTippedArrow(Items.TIPPED_ARROW);
        this.generatePotion(Items.POTION);
        this.generatePotion(Items.SPLASH_POTION);
        this.generatePotion(Items.LINGERING_POTION);
        this.generateFlatItem(Items.ARMADILLO_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.ALLAY_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.AXOLOTL_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BAT_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BEE_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BLAZE_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BOGGED_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BREEZE_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.CAT_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.CAMEL_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.CAVE_SPIDER_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.CHICKEN_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.COD_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.COW_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.CREEPER_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.DOLPHIN_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.DONKEY_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.DROWNED_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.ELDER_GUARDIAN_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.ENDER_DRAGON_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.ENDERMAN_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.ENDERMITE_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.EVOKER_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.FOX_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.FROG_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GHAST_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GLOW_SQUID_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GOAT_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GUARDIAN_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.HAPPY_GHAST_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.HOGLIN_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.HORSE_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.HUSK_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.IRON_GOLEM_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.LLAMA_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MAGMA_CUBE_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MOOSHROOM_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MULE_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.OCELOT_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PANDA_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PARROT_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PHANTOM_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PIG_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PIGLIN_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PIGLIN_BRUTE_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PILLAGER_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.POLAR_BEAR_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PUFFERFISH_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.RABBIT_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.RAVAGER_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SALMON_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SHEEP_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SHULKER_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SILVERFISH_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SKELETON_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SKELETON_HORSE_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SLIME_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SNIFFER_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SNOW_GOLEM_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SPIDER_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SQUID_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.STRAY_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.STRIDER_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.TADPOLE_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.TRADER_LLAMA_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.TROPICAL_FISH_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.TURTLE_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.VEX_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.VILLAGER_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.VINDICATOR_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.WANDERING_TRADER_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.WARDEN_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.WITCH_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.WITHER_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.WITHER_SKELETON_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.WOLF_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.ZOGLIN_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.CREAKING_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.ZOMBIE_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.ZOMBIE_HORSE_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.ZOMBIE_VILLAGER_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.ZOMBIFIED_PIGLIN_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        this.declareCustomModelItem(Items.AIR);
        this.declareCustomModelItem(Items.AMETHYST_CLUSTER);
        this.declareCustomModelItem(Items.SMALL_AMETHYST_BUD);
        this.declareCustomModelItem(Items.MEDIUM_AMETHYST_BUD);
        this.declareCustomModelItem(Items.LARGE_AMETHYST_BUD);
        this.declareCustomModelItem(Items.SMALL_DRIPLEAF);
        this.declareCustomModelItem(Items.BIG_DRIPLEAF);
        this.declareCustomModelItem(Items.HANGING_ROOTS);
        this.declareCustomModelItem(Items.POINTED_DRIPSTONE);
        this.declareCustomModelItem(Items.BONE);
        this.declareCustomModelItem(Items.COD);
        this.declareCustomModelItem(Items.FEATHER);
        this.declareCustomModelItem(Items.LEAD);
    }

    public static final class TrimMaterialData
    extends Record {
        private final MaterialAssetGroup assets;
        final ResourceKey<TrimMaterial> materialKey;

        public TrimMaterialData(MaterialAssetGroup $$0, ResourceKey<TrimMaterial> $$1) {
            this.assets = $$0;
            this.materialKey = $$1;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{TrimMaterialData.class, "assets;materialKey", "assets", "materialKey"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{TrimMaterialData.class, "assets;materialKey", "assets", "materialKey"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{TrimMaterialData.class, "assets;materialKey", "assets", "materialKey"}, this, $$0);
        }

        public MaterialAssetGroup assets() {
            return this.assets;
        }

        public ResourceKey<TrimMaterial> materialKey() {
            return this.materialKey;
        }
    }
}

