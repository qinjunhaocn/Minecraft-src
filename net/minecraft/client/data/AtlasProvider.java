/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.renderer.MaterialMapper;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BellRenderer;
import net.minecraft.client.renderer.blockentity.ConduitRenderer;
import net.minecraft.client.renderer.blockentity.EnchantTableRenderer;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import net.minecraft.client.renderer.texture.atlas.sources.PalettedPermutations;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.client.resources.model.AtlasIds;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.equipment.trim.MaterialAssetGroup;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import net.minecraft.world.item.equipment.trim.TrimPatterns;

public class AtlasProvider
implements DataProvider {
    private static final ResourceLocation TRIM_PALETTE_KEY = ResourceLocation.withDefaultNamespace("trims/color_palettes/trim_palette");
    private static final Map<String, ResourceLocation> TRIM_PALETTE_VALUES = AtlasProvider.extractAllMaterialAssets().collect(Collectors.toMap(MaterialAssetGroup.AssetInfo::suffix, $$0 -> ResourceLocation.withDefaultNamespace("trims/color_palettes/" + $$0.suffix())));
    private static final List<ResourceKey<TrimPattern>> VANILLA_PATTERNS = List.of((Object[])new ResourceKey[]{TrimPatterns.SENTRY, TrimPatterns.DUNE, TrimPatterns.COAST, TrimPatterns.WILD, TrimPatterns.WARD, TrimPatterns.EYE, TrimPatterns.VEX, TrimPatterns.TIDE, TrimPatterns.SNOUT, TrimPatterns.RIB, TrimPatterns.SPIRE, TrimPatterns.WAYFINDER, TrimPatterns.SHAPER, TrimPatterns.SILENCE, TrimPatterns.RAISER, TrimPatterns.HOST, TrimPatterns.FLOW, TrimPatterns.BOLT});
    private static final List<EquipmentClientInfo.LayerType> HUMANOID_LAYERS = List.of((Object)EquipmentClientInfo.LayerType.HUMANOID, (Object)EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS);
    private final PackOutput.PathProvider pathProvider;

    public AtlasProvider(PackOutput $$0) {
        this.pathProvider = $$0.createPathProvider(PackOutput.Target.RESOURCE_PACK, "atlases");
    }

    private static List<ResourceLocation> patternTextures() {
        ArrayList<ResourceLocation> $$0 = new ArrayList<ResourceLocation>(VANILLA_PATTERNS.size() * HUMANOID_LAYERS.size());
        for (ResourceKey<TrimPattern> $$12 : VANILLA_PATTERNS) {
            ResourceLocation $$2 = TrimPatterns.defaultAssetId($$12);
            for (EquipmentClientInfo.LayerType $$3 : HUMANOID_LAYERS) {
                $$0.add($$2.withPath($$1 -> $$3.trimAssetPrefix() + "/" + $$1));
            }
        }
        return $$0;
    }

    private static SpriteSource forMaterial(Material $$0) {
        return new SingleFile($$0.texture());
    }

    private static SpriteSource forMapper(MaterialMapper $$0) {
        return new DirectoryLister($$0.prefix(), $$0.prefix() + "/");
    }

    private static List<SpriteSource> simpleMapper(MaterialMapper $$0) {
        return List.of((Object)AtlasProvider.forMapper($$0));
    }

    private static List<SpriteSource> noPrefixMapper(String $$0) {
        return List.of((Object)new DirectoryLister($$0, ""));
    }

    private static Stream<MaterialAssetGroup.AssetInfo> extractAllMaterialAssets() {
        return ItemModelGenerators.TRIM_MATERIAL_MODELS.stream().map(ItemModelGenerators.TrimMaterialData::assets).flatMap($$0 -> Stream.concat(Stream.of($$0.base()), $$0.overrides().values().stream())).sorted(Comparator.comparing(MaterialAssetGroup.AssetInfo::suffix));
    }

    private static List<SpriteSource> armorTrims() {
        return List.of((Object)new PalettedPermutations(AtlasProvider.patternTextures(), TRIM_PALETTE_KEY, TRIM_PALETTE_VALUES));
    }

    private static List<SpriteSource> blocksList() {
        return List.of((Object)AtlasProvider.forMapper(Sheets.BLOCKS_MAPPER), (Object)AtlasProvider.forMapper(Sheets.ITEMS_MAPPER), (Object)AtlasProvider.forMapper(ConduitRenderer.MAPPER), (Object)AtlasProvider.forMaterial(BellRenderer.BELL_RESOURCE_LOCATION), (Object)AtlasProvider.forMaterial(Sheets.DECORATED_POT_SIDE), (Object)AtlasProvider.forMaterial(EnchantTableRenderer.BOOK_LOCATION), (Object)new PalettedPermutations(List.of((Object)ItemModelGenerators.TRIM_PREFIX_HELMET, (Object)ItemModelGenerators.TRIM_PREFIX_CHESTPLATE, (Object)ItemModelGenerators.TRIM_PREFIX_LEGGINGS, (Object)ItemModelGenerators.TRIM_PREFIX_BOOTS), TRIM_PALETTE_KEY, TRIM_PALETTE_VALUES));
    }

    private static List<SpriteSource> bannerPatterns() {
        return List.of((Object)AtlasProvider.forMaterial(ModelBakery.BANNER_BASE), (Object)AtlasProvider.forMapper(Sheets.BANNER_MAPPER));
    }

    private static List<SpriteSource> shieldPatterns() {
        return List.of((Object)AtlasProvider.forMaterial(ModelBakery.SHIELD_BASE), (Object)AtlasProvider.forMaterial(ModelBakery.NO_PATTERN_SHIELD), (Object)AtlasProvider.forMapper(Sheets.SHIELD_MAPPER));
    }

    private static List<SpriteSource> guiSprites() {
        return List.of((Object)new DirectoryLister("gui/sprites", ""), (Object)new DirectoryLister("mob_effect", "mob_effect/"));
    }

    @Override
    public CompletableFuture<?> run(CachedOutput $$0) {
        return CompletableFuture.allOf(this.storeAtlas($$0, AtlasIds.ARMOR_TRIMS, AtlasProvider.armorTrims()), this.storeAtlas($$0, AtlasIds.BANNER_PATTERNS, AtlasProvider.bannerPatterns()), this.storeAtlas($$0, AtlasIds.BEDS, AtlasProvider.simpleMapper(Sheets.BED_MAPPER)), this.storeAtlas($$0, AtlasIds.BLOCKS, AtlasProvider.blocksList()), this.storeAtlas($$0, AtlasIds.CHESTS, AtlasProvider.simpleMapper(Sheets.CHEST_MAPPER)), this.storeAtlas($$0, AtlasIds.DECORATED_POT, AtlasProvider.simpleMapper(Sheets.DECORATED_POT_MAPPER)), this.storeAtlas($$0, AtlasIds.GUI, AtlasProvider.guiSprites()), this.storeAtlas($$0, AtlasIds.MAP_DECORATIONS, AtlasProvider.noPrefixMapper("map/decorations")), this.storeAtlas($$0, AtlasIds.PAINTINGS, AtlasProvider.noPrefixMapper("painting")), this.storeAtlas($$0, AtlasIds.PARTICLES, AtlasProvider.noPrefixMapper("particle")), this.storeAtlas($$0, AtlasIds.SHIELD_PATTERNS, AtlasProvider.shieldPatterns()), this.storeAtlas($$0, AtlasIds.SHULKER_BOXES, AtlasProvider.simpleMapper(Sheets.SHULKER_MAPPER)), this.storeAtlas($$0, AtlasIds.SIGNS, AtlasProvider.simpleMapper(Sheets.SIGN_MAPPER)));
    }

    private CompletableFuture<?> storeAtlas(CachedOutput $$0, ResourceLocation $$1, List<SpriteSource> $$2) {
        return DataProvider.saveStable($$0, SpriteSources.FILE_CODEC, $$2, this.pathProvider.json($$1));
    }

    @Override
    public String getName() {
        return "Atlas Definitions";
    }
}

