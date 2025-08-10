/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block.entity;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.DecoratedPotPattern;

public class DecoratedPotPatterns {
    public static final ResourceKey<DecoratedPotPattern> BLANK = DecoratedPotPatterns.create("blank");
    public static final ResourceKey<DecoratedPotPattern> ANGLER = DecoratedPotPatterns.create("angler");
    public static final ResourceKey<DecoratedPotPattern> ARCHER = DecoratedPotPatterns.create("archer");
    public static final ResourceKey<DecoratedPotPattern> ARMS_UP = DecoratedPotPatterns.create("arms_up");
    public static final ResourceKey<DecoratedPotPattern> BLADE = DecoratedPotPatterns.create("blade");
    public static final ResourceKey<DecoratedPotPattern> BREWER = DecoratedPotPatterns.create("brewer");
    public static final ResourceKey<DecoratedPotPattern> BURN = DecoratedPotPatterns.create("burn");
    public static final ResourceKey<DecoratedPotPattern> DANGER = DecoratedPotPatterns.create("danger");
    public static final ResourceKey<DecoratedPotPattern> EXPLORER = DecoratedPotPatterns.create("explorer");
    public static final ResourceKey<DecoratedPotPattern> FLOW = DecoratedPotPatterns.create("flow");
    public static final ResourceKey<DecoratedPotPattern> FRIEND = DecoratedPotPatterns.create("friend");
    public static final ResourceKey<DecoratedPotPattern> GUSTER = DecoratedPotPatterns.create("guster");
    public static final ResourceKey<DecoratedPotPattern> HEART = DecoratedPotPatterns.create("heart");
    public static final ResourceKey<DecoratedPotPattern> HEARTBREAK = DecoratedPotPatterns.create("heartbreak");
    public static final ResourceKey<DecoratedPotPattern> HOWL = DecoratedPotPatterns.create("howl");
    public static final ResourceKey<DecoratedPotPattern> MINER = DecoratedPotPatterns.create("miner");
    public static final ResourceKey<DecoratedPotPattern> MOURNER = DecoratedPotPatterns.create("mourner");
    public static final ResourceKey<DecoratedPotPattern> PLENTY = DecoratedPotPatterns.create("plenty");
    public static final ResourceKey<DecoratedPotPattern> PRIZE = DecoratedPotPatterns.create("prize");
    public static final ResourceKey<DecoratedPotPattern> SCRAPE = DecoratedPotPatterns.create("scrape");
    public static final ResourceKey<DecoratedPotPattern> SHEAF = DecoratedPotPatterns.create("sheaf");
    public static final ResourceKey<DecoratedPotPattern> SHELTER = DecoratedPotPatterns.create("shelter");
    public static final ResourceKey<DecoratedPotPattern> SKULL = DecoratedPotPatterns.create("skull");
    public static final ResourceKey<DecoratedPotPattern> SNORT = DecoratedPotPatterns.create("snort");
    private static final Map<Item, ResourceKey<DecoratedPotPattern>> ITEM_TO_POT_TEXTURE = Map.ofEntries((Map.Entry[])new Map.Entry[]{Map.entry((Object)Items.BRICK, BLANK), Map.entry((Object)Items.ANGLER_POTTERY_SHERD, ANGLER), Map.entry((Object)Items.ARCHER_POTTERY_SHERD, ARCHER), Map.entry((Object)Items.ARMS_UP_POTTERY_SHERD, ARMS_UP), Map.entry((Object)Items.BLADE_POTTERY_SHERD, BLADE), Map.entry((Object)Items.BREWER_POTTERY_SHERD, BREWER), Map.entry((Object)Items.BURN_POTTERY_SHERD, BURN), Map.entry((Object)Items.DANGER_POTTERY_SHERD, DANGER), Map.entry((Object)Items.EXPLORER_POTTERY_SHERD, EXPLORER), Map.entry((Object)Items.FLOW_POTTERY_SHERD, FLOW), Map.entry((Object)Items.FRIEND_POTTERY_SHERD, FRIEND), Map.entry((Object)Items.GUSTER_POTTERY_SHERD, GUSTER), Map.entry((Object)Items.HEART_POTTERY_SHERD, HEART), Map.entry((Object)Items.HEARTBREAK_POTTERY_SHERD, HEARTBREAK), Map.entry((Object)Items.HOWL_POTTERY_SHERD, HOWL), Map.entry((Object)Items.MINER_POTTERY_SHERD, MINER), Map.entry((Object)Items.MOURNER_POTTERY_SHERD, MOURNER), Map.entry((Object)Items.PLENTY_POTTERY_SHERD, PLENTY), Map.entry((Object)Items.PRIZE_POTTERY_SHERD, PRIZE), Map.entry((Object)Items.SCRAPE_POTTERY_SHERD, SCRAPE), Map.entry((Object)Items.SHEAF_POTTERY_SHERD, SHEAF), Map.entry((Object)Items.SHELTER_POTTERY_SHERD, SHELTER), Map.entry((Object)Items.SKULL_POTTERY_SHERD, SKULL), Map.entry((Object)Items.SNORT_POTTERY_SHERD, SNORT)});

    @Nullable
    public static ResourceKey<DecoratedPotPattern> getPatternFromItem(Item $$0) {
        return ITEM_TO_POT_TEXTURE.get($$0);
    }

    private static ResourceKey<DecoratedPotPattern> create(String $$0) {
        return ResourceKey.create(Registries.DECORATED_POT_PATTERN, ResourceLocation.withDefaultNamespace($$0));
    }

    public static DecoratedPotPattern bootstrap(Registry<DecoratedPotPattern> $$0) {
        DecoratedPotPatterns.register($$0, ANGLER, "angler_pottery_pattern");
        DecoratedPotPatterns.register($$0, ARCHER, "archer_pottery_pattern");
        DecoratedPotPatterns.register($$0, ARMS_UP, "arms_up_pottery_pattern");
        DecoratedPotPatterns.register($$0, BLADE, "blade_pottery_pattern");
        DecoratedPotPatterns.register($$0, BREWER, "brewer_pottery_pattern");
        DecoratedPotPatterns.register($$0, BURN, "burn_pottery_pattern");
        DecoratedPotPatterns.register($$0, DANGER, "danger_pottery_pattern");
        DecoratedPotPatterns.register($$0, EXPLORER, "explorer_pottery_pattern");
        DecoratedPotPatterns.register($$0, FLOW, "flow_pottery_pattern");
        DecoratedPotPatterns.register($$0, FRIEND, "friend_pottery_pattern");
        DecoratedPotPatterns.register($$0, GUSTER, "guster_pottery_pattern");
        DecoratedPotPatterns.register($$0, HEART, "heart_pottery_pattern");
        DecoratedPotPatterns.register($$0, HEARTBREAK, "heartbreak_pottery_pattern");
        DecoratedPotPatterns.register($$0, HOWL, "howl_pottery_pattern");
        DecoratedPotPatterns.register($$0, MINER, "miner_pottery_pattern");
        DecoratedPotPatterns.register($$0, MOURNER, "mourner_pottery_pattern");
        DecoratedPotPatterns.register($$0, PLENTY, "plenty_pottery_pattern");
        DecoratedPotPatterns.register($$0, PRIZE, "prize_pottery_pattern");
        DecoratedPotPatterns.register($$0, SCRAPE, "scrape_pottery_pattern");
        DecoratedPotPatterns.register($$0, SHEAF, "sheaf_pottery_pattern");
        DecoratedPotPatterns.register($$0, SHELTER, "shelter_pottery_pattern");
        DecoratedPotPatterns.register($$0, SKULL, "skull_pottery_pattern");
        DecoratedPotPatterns.register($$0, SNORT, "snort_pottery_pattern");
        return DecoratedPotPatterns.register($$0, BLANK, "decorated_pot_side");
    }

    private static DecoratedPotPattern register(Registry<DecoratedPotPattern> $$0, ResourceKey<DecoratedPotPattern> $$1, String $$2) {
        return Registry.register($$0, $$1, new DecoratedPotPattern(ResourceLocation.withDefaultNamespace($$2)));
    }
}

