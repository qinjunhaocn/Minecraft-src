/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.data.models.model;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class TextureMapping {
    private final Map<TextureSlot, ResourceLocation> slots = Maps.newHashMap();
    private final Set<TextureSlot> forcedSlots = Sets.newHashSet();

    public TextureMapping put(TextureSlot $$0, ResourceLocation $$1) {
        this.slots.put($$0, $$1);
        return this;
    }

    public TextureMapping putForced(TextureSlot $$0, ResourceLocation $$1) {
        this.slots.put($$0, $$1);
        this.forcedSlots.add($$0);
        return this;
    }

    public Stream<TextureSlot> getForced() {
        return this.forcedSlots.stream();
    }

    public TextureMapping copySlot(TextureSlot $$0, TextureSlot $$1) {
        this.slots.put($$1, this.slots.get($$0));
        return this;
    }

    public TextureMapping copyForced(TextureSlot $$0, TextureSlot $$1) {
        this.slots.put($$1, this.slots.get($$0));
        this.forcedSlots.add($$1);
        return this;
    }

    public ResourceLocation get(TextureSlot $$0) {
        for (TextureSlot $$1 = $$0; $$1 != null; $$1 = $$1.getParent()) {
            ResourceLocation $$2 = this.slots.get($$1);
            if ($$2 == null) continue;
            return $$2;
        }
        throw new IllegalStateException("Can't find texture for slot " + String.valueOf($$0));
    }

    public TextureMapping copyAndUpdate(TextureSlot $$0, ResourceLocation $$1) {
        TextureMapping $$2 = new TextureMapping();
        $$2.slots.putAll(this.slots);
        $$2.forcedSlots.addAll(this.forcedSlots);
        $$2.put($$0, $$1);
        return $$2;
    }

    public static TextureMapping cube(Block $$0) {
        ResourceLocation $$1 = TextureMapping.getBlockTexture($$0);
        return TextureMapping.cube($$1);
    }

    public static TextureMapping defaultTexture(Block $$0) {
        ResourceLocation $$1 = TextureMapping.getBlockTexture($$0);
        return TextureMapping.defaultTexture($$1);
    }

    public static TextureMapping defaultTexture(ResourceLocation $$0) {
        return new TextureMapping().put(TextureSlot.TEXTURE, $$0);
    }

    public static TextureMapping cube(ResourceLocation $$0) {
        return new TextureMapping().put(TextureSlot.ALL, $$0);
    }

    public static TextureMapping cross(Block $$0) {
        return TextureMapping.singleSlot(TextureSlot.CROSS, TextureMapping.getBlockTexture($$0));
    }

    public static TextureMapping side(Block $$0) {
        return TextureMapping.singleSlot(TextureSlot.SIDE, TextureMapping.getBlockTexture($$0));
    }

    public static TextureMapping crossEmissive(Block $$0) {
        return new TextureMapping().put(TextureSlot.CROSS, TextureMapping.getBlockTexture($$0)).put(TextureSlot.CROSS_EMISSIVE, TextureMapping.getBlockTexture($$0, "_emissive"));
    }

    public static TextureMapping cross(ResourceLocation $$0) {
        return TextureMapping.singleSlot(TextureSlot.CROSS, $$0);
    }

    public static TextureMapping plant(Block $$0) {
        return TextureMapping.singleSlot(TextureSlot.PLANT, TextureMapping.getBlockTexture($$0));
    }

    public static TextureMapping plantEmissive(Block $$0) {
        return new TextureMapping().put(TextureSlot.PLANT, TextureMapping.getBlockTexture($$0)).put(TextureSlot.CROSS_EMISSIVE, TextureMapping.getBlockTexture($$0, "_emissive"));
    }

    public static TextureMapping plant(ResourceLocation $$0) {
        return TextureMapping.singleSlot(TextureSlot.PLANT, $$0);
    }

    public static TextureMapping rail(Block $$0) {
        return TextureMapping.singleSlot(TextureSlot.RAIL, TextureMapping.getBlockTexture($$0));
    }

    public static TextureMapping rail(ResourceLocation $$0) {
        return TextureMapping.singleSlot(TextureSlot.RAIL, $$0);
    }

    public static TextureMapping wool(Block $$0) {
        return TextureMapping.singleSlot(TextureSlot.WOOL, TextureMapping.getBlockTexture($$0));
    }

    public static TextureMapping flowerbed(Block $$0) {
        return new TextureMapping().put(TextureSlot.FLOWERBED, TextureMapping.getBlockTexture($$0)).put(TextureSlot.STEM, TextureMapping.getBlockTexture($$0, "_stem"));
    }

    public static TextureMapping wool(ResourceLocation $$0) {
        return TextureMapping.singleSlot(TextureSlot.WOOL, $$0);
    }

    public static TextureMapping stem(Block $$0) {
        return TextureMapping.singleSlot(TextureSlot.STEM, TextureMapping.getBlockTexture($$0));
    }

    public static TextureMapping attachedStem(Block $$0, Block $$1) {
        return new TextureMapping().put(TextureSlot.STEM, TextureMapping.getBlockTexture($$0)).put(TextureSlot.UPPER_STEM, TextureMapping.getBlockTexture($$1));
    }

    public static TextureMapping pattern(Block $$0) {
        return TextureMapping.singleSlot(TextureSlot.PATTERN, TextureMapping.getBlockTexture($$0));
    }

    public static TextureMapping fan(Block $$0) {
        return TextureMapping.singleSlot(TextureSlot.FAN, TextureMapping.getBlockTexture($$0));
    }

    public static TextureMapping crop(ResourceLocation $$0) {
        return TextureMapping.singleSlot(TextureSlot.CROP, $$0);
    }

    public static TextureMapping pane(Block $$0, Block $$1) {
        return new TextureMapping().put(TextureSlot.PANE, TextureMapping.getBlockTexture($$0)).put(TextureSlot.EDGE, TextureMapping.getBlockTexture($$1, "_top"));
    }

    public static TextureMapping singleSlot(TextureSlot $$0, ResourceLocation $$1) {
        return new TextureMapping().put($$0, $$1);
    }

    public static TextureMapping column(Block $$0) {
        return new TextureMapping().put(TextureSlot.SIDE, TextureMapping.getBlockTexture($$0, "_side")).put(TextureSlot.END, TextureMapping.getBlockTexture($$0, "_top"));
    }

    public static TextureMapping cubeTop(Block $$0) {
        return new TextureMapping().put(TextureSlot.SIDE, TextureMapping.getBlockTexture($$0, "_side")).put(TextureSlot.TOP, TextureMapping.getBlockTexture($$0, "_top"));
    }

    public static TextureMapping pottedAzalea(Block $$0) {
        return new TextureMapping().put(TextureSlot.PLANT, TextureMapping.getBlockTexture($$0, "_plant")).put(TextureSlot.SIDE, TextureMapping.getBlockTexture($$0, "_side")).put(TextureSlot.TOP, TextureMapping.getBlockTexture($$0, "_top"));
    }

    public static TextureMapping logColumn(Block $$0) {
        return new TextureMapping().put(TextureSlot.SIDE, TextureMapping.getBlockTexture($$0)).put(TextureSlot.END, TextureMapping.getBlockTexture($$0, "_top")).put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture($$0));
    }

    public static TextureMapping column(ResourceLocation $$0, ResourceLocation $$1) {
        return new TextureMapping().put(TextureSlot.SIDE, $$0).put(TextureSlot.END, $$1);
    }

    public static TextureMapping fence(Block $$0) {
        return new TextureMapping().put(TextureSlot.TEXTURE, TextureMapping.getBlockTexture($$0)).put(TextureSlot.SIDE, TextureMapping.getBlockTexture($$0, "_side")).put(TextureSlot.TOP, TextureMapping.getBlockTexture($$0, "_top"));
    }

    public static TextureMapping customParticle(Block $$0) {
        return new TextureMapping().put(TextureSlot.TEXTURE, TextureMapping.getBlockTexture($$0)).put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture($$0, "_particle"));
    }

    public static TextureMapping cubeBottomTop(Block $$0) {
        return new TextureMapping().put(TextureSlot.SIDE, TextureMapping.getBlockTexture($$0, "_side")).put(TextureSlot.TOP, TextureMapping.getBlockTexture($$0, "_top")).put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture($$0, "_bottom"));
    }

    public static TextureMapping cubeBottomTopWithWall(Block $$0) {
        ResourceLocation $$1 = TextureMapping.getBlockTexture($$0);
        return new TextureMapping().put(TextureSlot.WALL, $$1).put(TextureSlot.SIDE, $$1).put(TextureSlot.TOP, TextureMapping.getBlockTexture($$0, "_top")).put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture($$0, "_bottom"));
    }

    public static TextureMapping columnWithWall(Block $$0) {
        ResourceLocation $$1 = TextureMapping.getBlockTexture($$0);
        return new TextureMapping().put(TextureSlot.TEXTURE, $$1).put(TextureSlot.WALL, $$1).put(TextureSlot.SIDE, $$1).put(TextureSlot.END, TextureMapping.getBlockTexture($$0, "_top"));
    }

    public static TextureMapping door(ResourceLocation $$0, ResourceLocation $$1) {
        return new TextureMapping().put(TextureSlot.TOP, $$0).put(TextureSlot.BOTTOM, $$1);
    }

    public static TextureMapping door(Block $$0) {
        return new TextureMapping().put(TextureSlot.TOP, TextureMapping.getBlockTexture($$0, "_top")).put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture($$0, "_bottom"));
    }

    public static TextureMapping particle(Block $$0) {
        return new TextureMapping().put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture($$0));
    }

    public static TextureMapping particle(ResourceLocation $$0) {
        return new TextureMapping().put(TextureSlot.PARTICLE, $$0);
    }

    public static TextureMapping fire0(Block $$0) {
        return new TextureMapping().put(TextureSlot.FIRE, TextureMapping.getBlockTexture($$0, "_0"));
    }

    public static TextureMapping fire1(Block $$0) {
        return new TextureMapping().put(TextureSlot.FIRE, TextureMapping.getBlockTexture($$0, "_1"));
    }

    public static TextureMapping lantern(Block $$0) {
        return new TextureMapping().put(TextureSlot.LANTERN, TextureMapping.getBlockTexture($$0));
    }

    public static TextureMapping torch(Block $$0) {
        return new TextureMapping().put(TextureSlot.TORCH, TextureMapping.getBlockTexture($$0));
    }

    public static TextureMapping torch(ResourceLocation $$0) {
        return new TextureMapping().put(TextureSlot.TORCH, $$0);
    }

    public static TextureMapping trialSpawner(Block $$0, String $$1, String $$2) {
        return new TextureMapping().put(TextureSlot.SIDE, TextureMapping.getBlockTexture($$0, $$1)).put(TextureSlot.TOP, TextureMapping.getBlockTexture($$0, $$2)).put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture($$0, "_bottom"));
    }

    public static TextureMapping vault(Block $$0, String $$1, String $$2, String $$3, String $$4) {
        return new TextureMapping().put(TextureSlot.FRONT, TextureMapping.getBlockTexture($$0, $$1)).put(TextureSlot.SIDE, TextureMapping.getBlockTexture($$0, $$2)).put(TextureSlot.TOP, TextureMapping.getBlockTexture($$0, $$3)).put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture($$0, $$4));
    }

    public static TextureMapping particleFromItem(Item $$0) {
        return new TextureMapping().put(TextureSlot.PARTICLE, TextureMapping.getItemTexture($$0));
    }

    public static TextureMapping commandBlock(Block $$0) {
        return new TextureMapping().put(TextureSlot.SIDE, TextureMapping.getBlockTexture($$0, "_side")).put(TextureSlot.FRONT, TextureMapping.getBlockTexture($$0, "_front")).put(TextureSlot.BACK, TextureMapping.getBlockTexture($$0, "_back"));
    }

    public static TextureMapping orientableCube(Block $$0) {
        return new TextureMapping().put(TextureSlot.SIDE, TextureMapping.getBlockTexture($$0, "_side")).put(TextureSlot.FRONT, TextureMapping.getBlockTexture($$0, "_front")).put(TextureSlot.TOP, TextureMapping.getBlockTexture($$0, "_top")).put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture($$0, "_bottom"));
    }

    public static TextureMapping orientableCubeOnlyTop(Block $$0) {
        return new TextureMapping().put(TextureSlot.SIDE, TextureMapping.getBlockTexture($$0, "_side")).put(TextureSlot.FRONT, TextureMapping.getBlockTexture($$0, "_front")).put(TextureSlot.TOP, TextureMapping.getBlockTexture($$0, "_top"));
    }

    public static TextureMapping orientableCubeSameEnds(Block $$0) {
        return new TextureMapping().put(TextureSlot.SIDE, TextureMapping.getBlockTexture($$0, "_side")).put(TextureSlot.FRONT, TextureMapping.getBlockTexture($$0, "_front")).put(TextureSlot.END, TextureMapping.getBlockTexture($$0, "_end"));
    }

    public static TextureMapping top(Block $$0) {
        return new TextureMapping().put(TextureSlot.TOP, TextureMapping.getBlockTexture($$0, "_top"));
    }

    public static TextureMapping craftingTable(Block $$0, Block $$1) {
        return new TextureMapping().put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture($$0, "_front")).put(TextureSlot.DOWN, TextureMapping.getBlockTexture($$1)).put(TextureSlot.UP, TextureMapping.getBlockTexture($$0, "_top")).put(TextureSlot.NORTH, TextureMapping.getBlockTexture($$0, "_front")).put(TextureSlot.EAST, TextureMapping.getBlockTexture($$0, "_side")).put(TextureSlot.SOUTH, TextureMapping.getBlockTexture($$0, "_side")).put(TextureSlot.WEST, TextureMapping.getBlockTexture($$0, "_front"));
    }

    public static TextureMapping fletchingTable(Block $$0, Block $$1) {
        return new TextureMapping().put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture($$0, "_front")).put(TextureSlot.DOWN, TextureMapping.getBlockTexture($$1)).put(TextureSlot.UP, TextureMapping.getBlockTexture($$0, "_top")).put(TextureSlot.NORTH, TextureMapping.getBlockTexture($$0, "_front")).put(TextureSlot.SOUTH, TextureMapping.getBlockTexture($$0, "_front")).put(TextureSlot.EAST, TextureMapping.getBlockTexture($$0, "_side")).put(TextureSlot.WEST, TextureMapping.getBlockTexture($$0, "_side"));
    }

    public static TextureMapping snifferEgg(String $$0) {
        return new TextureMapping().put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(Blocks.SNIFFER_EGG, $$0 + "_north")).put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(Blocks.SNIFFER_EGG, $$0 + "_bottom")).put(TextureSlot.TOP, TextureMapping.getBlockTexture(Blocks.SNIFFER_EGG, $$0 + "_top")).put(TextureSlot.NORTH, TextureMapping.getBlockTexture(Blocks.SNIFFER_EGG, $$0 + "_north")).put(TextureSlot.SOUTH, TextureMapping.getBlockTexture(Blocks.SNIFFER_EGG, $$0 + "_south")).put(TextureSlot.EAST, TextureMapping.getBlockTexture(Blocks.SNIFFER_EGG, $$0 + "_east")).put(TextureSlot.WEST, TextureMapping.getBlockTexture(Blocks.SNIFFER_EGG, $$0 + "_west"));
    }

    public static TextureMapping driedGhast(String $$0) {
        return new TextureMapping().put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(Blocks.DRIED_GHAST, $$0 + "_north")).put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(Blocks.DRIED_GHAST, $$0 + "_bottom")).put(TextureSlot.TOP, TextureMapping.getBlockTexture(Blocks.DRIED_GHAST, $$0 + "_top")).put(TextureSlot.NORTH, TextureMapping.getBlockTexture(Blocks.DRIED_GHAST, $$0 + "_north")).put(TextureSlot.SOUTH, TextureMapping.getBlockTexture(Blocks.DRIED_GHAST, $$0 + "_south")).put(TextureSlot.EAST, TextureMapping.getBlockTexture(Blocks.DRIED_GHAST, $$0 + "_east")).put(TextureSlot.WEST, TextureMapping.getBlockTexture(Blocks.DRIED_GHAST, $$0 + "_west")).put(TextureSlot.TENTACLES, TextureMapping.getBlockTexture(Blocks.DRIED_GHAST, $$0 + "_tentacles"));
    }

    public static TextureMapping campfire(Block $$0) {
        return new TextureMapping().put(TextureSlot.LIT_LOG, TextureMapping.getBlockTexture($$0, "_log_lit")).put(TextureSlot.FIRE, TextureMapping.getBlockTexture($$0, "_fire"));
    }

    public static TextureMapping candleCake(Block $$0, boolean $$1) {
        return new TextureMapping().put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(Blocks.CAKE, "_side")).put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(Blocks.CAKE, "_bottom")).put(TextureSlot.TOP, TextureMapping.getBlockTexture(Blocks.CAKE, "_top")).put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.CAKE, "_side")).put(TextureSlot.CANDLE, TextureMapping.getBlockTexture($$0, $$1 ? "_lit" : ""));
    }

    public static TextureMapping cauldron(ResourceLocation $$0) {
        return new TextureMapping().put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(Blocks.CAULDRON, "_side")).put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.CAULDRON, "_side")).put(TextureSlot.TOP, TextureMapping.getBlockTexture(Blocks.CAULDRON, "_top")).put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(Blocks.CAULDRON, "_bottom")).put(TextureSlot.INSIDE, TextureMapping.getBlockTexture(Blocks.CAULDRON, "_inner")).put(TextureSlot.CONTENT, $$0);
    }

    public static TextureMapping sculkShrieker(boolean $$0) {
        String $$1 = $$0 ? "_can_summon" : "";
        return new TextureMapping().put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(Blocks.SCULK_SHRIEKER, "_bottom")).put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.SCULK_SHRIEKER, "_side")).put(TextureSlot.TOP, TextureMapping.getBlockTexture(Blocks.SCULK_SHRIEKER, "_top")).put(TextureSlot.INNER_TOP, TextureMapping.getBlockTexture(Blocks.SCULK_SHRIEKER, $$1 + "_inner_top")).put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(Blocks.SCULK_SHRIEKER, "_bottom"));
    }

    public static TextureMapping layer0(Item $$0) {
        return new TextureMapping().put(TextureSlot.LAYER0, TextureMapping.getItemTexture($$0));
    }

    public static TextureMapping layer0(Block $$0) {
        return new TextureMapping().put(TextureSlot.LAYER0, TextureMapping.getBlockTexture($$0));
    }

    public static TextureMapping layer0(ResourceLocation $$0) {
        return new TextureMapping().put(TextureSlot.LAYER0, $$0);
    }

    public static TextureMapping layered(ResourceLocation $$0, ResourceLocation $$1) {
        return new TextureMapping().put(TextureSlot.LAYER0, $$0).put(TextureSlot.LAYER1, $$1);
    }

    public static TextureMapping layered(ResourceLocation $$0, ResourceLocation $$1, ResourceLocation $$2) {
        return new TextureMapping().put(TextureSlot.LAYER0, $$0).put(TextureSlot.LAYER1, $$1).put(TextureSlot.LAYER2, $$2);
    }

    public static ResourceLocation getBlockTexture(Block $$0) {
        ResourceLocation $$1 = BuiltInRegistries.BLOCK.getKey($$0);
        return $$1.withPrefix("block/");
    }

    public static ResourceLocation getBlockTexture(Block $$0, String $$12) {
        ResourceLocation $$2 = BuiltInRegistries.BLOCK.getKey($$0);
        return $$2.withPath($$1 -> "block/" + $$1 + $$12);
    }

    public static ResourceLocation getItemTexture(Item $$0) {
        ResourceLocation $$1 = BuiltInRegistries.ITEM.getKey($$0);
        return $$1.withPrefix("item/");
    }

    public static ResourceLocation getItemTexture(Item $$0, String $$12) {
        ResourceLocation $$2 = BuiltInRegistries.ITEM.getKey($$0);
        return $$2.withPath($$1 -> "item/" + $$1 + $$12);
    }
}

