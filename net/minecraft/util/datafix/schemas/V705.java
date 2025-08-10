/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.types.templates.Hook$HookFunction
 *  com.mojang.datafixers.types.templates.TypeTemplate
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.util.datafix.schemas;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.Hook;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;
import net.minecraft.util.datafix.schemas.V704;
import net.minecraft.util.datafix.schemas.V99;

public class V705
extends NamespacedSchema {
    static final Map<String, String> ITEM_TO_ENTITY = ImmutableMap.builder().put("minecraft:armor_stand", "minecraft:armor_stand").put("minecraft:painting", "minecraft:painting").put("minecraft:armadillo_spawn_egg", "minecraft:armadillo").put("minecraft:allay_spawn_egg", "minecraft:allay").put("minecraft:axolotl_spawn_egg", "minecraft:axolotl").put("minecraft:bat_spawn_egg", "minecraft:bat").put("minecraft:bee_spawn_egg", "minecraft:bee").put("minecraft:blaze_spawn_egg", "minecraft:blaze").put("minecraft:bogged_spawn_egg", "minecraft:bogged").put("minecraft:breeze_spawn_egg", "minecraft:breeze").put("minecraft:cat_spawn_egg", "minecraft:cat").put("minecraft:camel_spawn_egg", "minecraft:camel").put("minecraft:cave_spider_spawn_egg", "minecraft:cave_spider").put("minecraft:chicken_spawn_egg", "minecraft:chicken").put("minecraft:cod_spawn_egg", "minecraft:cod").put("minecraft:cow_spawn_egg", "minecraft:cow").put("minecraft:creeper_spawn_egg", "minecraft:creeper").put("minecraft:dolphin_spawn_egg", "minecraft:dolphin").put("minecraft:donkey_spawn_egg", "minecraft:donkey").put("minecraft:drowned_spawn_egg", "minecraft:drowned").put("minecraft:elder_guardian_spawn_egg", "minecraft:elder_guardian").put("minecraft:ender_dragon_spawn_egg", "minecraft:ender_dragon").put("minecraft:enderman_spawn_egg", "minecraft:enderman").put("minecraft:endermite_spawn_egg", "minecraft:endermite").put("minecraft:evoker_spawn_egg", "minecraft:evoker").put("minecraft:fox_spawn_egg", "minecraft:fox").put("minecraft:frog_spawn_egg", "minecraft:frog").put("minecraft:ghast_spawn_egg", "minecraft:ghast").put("minecraft:glow_squid_spawn_egg", "minecraft:glow_squid").put("minecraft:goat_spawn_egg", "minecraft:goat").put("minecraft:guardian_spawn_egg", "minecraft:guardian").put("minecraft:hoglin_spawn_egg", "minecraft:hoglin").put("minecraft:horse_spawn_egg", "minecraft:horse").put("minecraft:husk_spawn_egg", "minecraft:husk").put("minecraft:iron_golem_spawn_egg", "minecraft:iron_golem").put("minecraft:llama_spawn_egg", "minecraft:llama").put("minecraft:magma_cube_spawn_egg", "minecraft:magma_cube").put("minecraft:mooshroom_spawn_egg", "minecraft:mooshroom").put("minecraft:mule_spawn_egg", "minecraft:mule").put("minecraft:ocelot_spawn_egg", "minecraft:ocelot").put("minecraft:panda_spawn_egg", "minecraft:panda").put("minecraft:parrot_spawn_egg", "minecraft:parrot").put("minecraft:phantom_spawn_egg", "minecraft:phantom").put("minecraft:pig_spawn_egg", "minecraft:pig").put("minecraft:piglin_spawn_egg", "minecraft:piglin").put("minecraft:piglin_brute_spawn_egg", "minecraft:piglin_brute").put("minecraft:pillager_spawn_egg", "minecraft:pillager").put("minecraft:polar_bear_spawn_egg", "minecraft:polar_bear").put("minecraft:pufferfish_spawn_egg", "minecraft:pufferfish").put("minecraft:rabbit_spawn_egg", "minecraft:rabbit").put("minecraft:ravager_spawn_egg", "minecraft:ravager").put("minecraft:salmon_spawn_egg", "minecraft:salmon").put("minecraft:sheep_spawn_egg", "minecraft:sheep").put("minecraft:shulker_spawn_egg", "minecraft:shulker").put("minecraft:silverfish_spawn_egg", "minecraft:silverfish").put("minecraft:skeleton_spawn_egg", "minecraft:skeleton").put("minecraft:skeleton_horse_spawn_egg", "minecraft:skeleton_horse").put("minecraft:slime_spawn_egg", "minecraft:slime").put("minecraft:sniffer_spawn_egg", "minecraft:sniffer").put("minecraft:snow_golem_spawn_egg", "minecraft:snow_golem").put("minecraft:spider_spawn_egg", "minecraft:spider").put("minecraft:squid_spawn_egg", "minecraft:squid").put("minecraft:stray_spawn_egg", "minecraft:stray").put("minecraft:strider_spawn_egg", "minecraft:strider").put("minecraft:tadpole_spawn_egg", "minecraft:tadpole").put("minecraft:trader_llama_spawn_egg", "minecraft:trader_llama").put("minecraft:tropical_fish_spawn_egg", "minecraft:tropical_fish").put("minecraft:turtle_spawn_egg", "minecraft:turtle").put("minecraft:vex_spawn_egg", "minecraft:vex").put("minecraft:villager_spawn_egg", "minecraft:villager").put("minecraft:vindicator_spawn_egg", "minecraft:vindicator").put("minecraft:wandering_trader_spawn_egg", "minecraft:wandering_trader").put("minecraft:warden_spawn_egg", "minecraft:warden").put("minecraft:witch_spawn_egg", "minecraft:witch").put("minecraft:wither_spawn_egg", "minecraft:wither").put("minecraft:wither_skeleton_spawn_egg", "minecraft:wither_skeleton").put("minecraft:wolf_spawn_egg", "minecraft:wolf").put("minecraft:zoglin_spawn_egg", "minecraft:zoglin").put("minecraft:zombie_spawn_egg", "minecraft:zombie").put("minecraft:zombie_horse_spawn_egg", "minecraft:zombie_horse").put("minecraft:zombie_villager_spawn_egg", "minecraft:zombie_villager").put("minecraft:zombified_piglin_spawn_egg", "minecraft:zombified_piglin").put("minecraft:item_frame", "minecraft:item_frame").put("minecraft:boat", "minecraft:oak_boat").put("minecraft:oak_boat", "minecraft:oak_boat").put("minecraft:oak_chest_boat", "minecraft:oak_chest_boat").put("minecraft:spruce_boat", "minecraft:spruce_boat").put("minecraft:spruce_chest_boat", "minecraft:spruce_chest_boat").put("minecraft:birch_boat", "minecraft:birch_boat").put("minecraft:birch_chest_boat", "minecraft:birch_chest_boat").put("minecraft:jungle_boat", "minecraft:jungle_boat").put("minecraft:jungle_chest_boat", "minecraft:jungle_chest_boat").put("minecraft:acacia_boat", "minecraft:acacia_boat").put("minecraft:acacia_chest_boat", "minecraft:acacia_chest_boat").put("minecraft:cherry_boat", "minecraft:cherry_boat").put("minecraft:cherry_chest_boat", "minecraft:cherry_chest_boat").put("minecraft:dark_oak_boat", "minecraft:dark_oak_boat").put("minecraft:dark_oak_chest_boat", "minecraft:dark_oak_chest_boat").put("minecraft:mangrove_boat", "minecraft:mangrove_boat").put("minecraft:mangrove_chest_boat", "minecraft:mangrove_chest_boat").put("minecraft:bamboo_raft", "minecraft:bamboo_raft").put("minecraft:bamboo_chest_raft", "minecraft:bamboo_chest_raft").put("minecraft:minecart", "minecraft:minecart").put("minecraft:chest_minecart", "minecraft:chest_minecart").put("minecraft:furnace_minecart", "minecraft:furnace_minecart").put("minecraft:tnt_minecart", "minecraft:tnt_minecart").put("minecraft:hopper_minecart", "minecraft:hopper_minecart").build();
    protected static final Hook.HookFunction ADD_NAMES = new Hook.HookFunction(){

        public <T> T apply(DynamicOps<T> $$0, T $$1) {
            return V99.addNames(new Dynamic($$0, $$1), V704.ITEM_TO_BLOCKENTITY, ITEM_TO_ENTITY);
        }
    };

    public V705(int $$0, Schema $$1) {
        super($$0, $$1);
    }

    protected static void registerMob(Schema $$0, Map<String, Supplier<TypeTemplate>> $$1, String $$2) {
        $$0.registerSimple($$1, $$2);
    }

    protected static void registerThrowableProjectile(Schema $$0, Map<String, Supplier<TypeTemplate>> $$1, String $$2) {
        $$0.register($$1, $$2, () -> DSL.optionalFields((String)"inTile", (TypeTemplate)References.BLOCK_NAME.in($$0)));
    }

    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema $$0) {
        HashMap<String, Supplier<TypeTemplate>> $$12 = Maps.newHashMap();
        $$0.register($$12, "minecraft:area_effect_cloud", $$1 -> DSL.optionalFields((String)"Particle", (TypeTemplate)References.PARTICLE.in($$0)));
        V705.registerMob($$0, $$12, "minecraft:armor_stand");
        $$0.register($$12, "minecraft:arrow", $$1 -> DSL.optionalFields((String)"inTile", (TypeTemplate)References.BLOCK_NAME.in($$0)));
        V705.registerMob($$0, $$12, "minecraft:bat");
        V705.registerMob($$0, $$12, "minecraft:blaze");
        $$0.registerSimple($$12, "minecraft:boat");
        V705.registerMob($$0, $$12, "minecraft:cave_spider");
        $$0.register($$12, "minecraft:chest_minecart", $$1 -> DSL.optionalFields((String)"DisplayTile", (TypeTemplate)References.BLOCK_NAME.in($$0), (String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in($$0))));
        V705.registerMob($$0, $$12, "minecraft:chicken");
        $$0.register($$12, "minecraft:commandblock_minecart", $$1 -> DSL.optionalFields((String)"DisplayTile", (TypeTemplate)References.BLOCK_NAME.in($$0), (String)"LastOutput", (TypeTemplate)References.TEXT_COMPONENT.in($$0)));
        V705.registerMob($$0, $$12, "minecraft:cow");
        V705.registerMob($$0, $$12, "minecraft:creeper");
        $$0.register($$12, "minecraft:donkey", $$1 -> DSL.optionalFields((String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in($$0)), (String)"SaddleItem", (TypeTemplate)References.ITEM_STACK.in($$0)));
        $$0.registerSimple($$12, "minecraft:dragon_fireball");
        V705.registerThrowableProjectile($$0, $$12, "minecraft:egg");
        V705.registerMob($$0, $$12, "minecraft:elder_guardian");
        $$0.registerSimple($$12, "minecraft:ender_crystal");
        V705.registerMob($$0, $$12, "minecraft:ender_dragon");
        $$0.register($$12, "minecraft:enderman", $$1 -> DSL.optionalFields((String)"carried", (TypeTemplate)References.BLOCK_NAME.in($$0)));
        V705.registerMob($$0, $$12, "minecraft:endermite");
        V705.registerThrowableProjectile($$0, $$12, "minecraft:ender_pearl");
        $$0.registerSimple($$12, "minecraft:eye_of_ender_signal");
        $$0.register($$12, "minecraft:falling_block", $$1 -> DSL.optionalFields((String)"Block", (TypeTemplate)References.BLOCK_NAME.in($$0), (String)"TileEntityData", (TypeTemplate)References.BLOCK_ENTITY.in($$0)));
        V705.registerThrowableProjectile($$0, $$12, "minecraft:fireball");
        $$0.register($$12, "minecraft:fireworks_rocket", $$1 -> DSL.optionalFields((String)"FireworksItem", (TypeTemplate)References.ITEM_STACK.in($$0)));
        $$0.register($$12, "minecraft:furnace_minecart", $$1 -> DSL.optionalFields((String)"DisplayTile", (TypeTemplate)References.BLOCK_NAME.in($$0)));
        V705.registerMob($$0, $$12, "minecraft:ghast");
        V705.registerMob($$0, $$12, "minecraft:giant");
        V705.registerMob($$0, $$12, "minecraft:guardian");
        $$0.register($$12, "minecraft:hopper_minecart", $$1 -> DSL.optionalFields((String)"DisplayTile", (TypeTemplate)References.BLOCK_NAME.in($$0), (String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in($$0))));
        $$0.register($$12, "minecraft:horse", $$1 -> DSL.optionalFields((String)"ArmorItem", (TypeTemplate)References.ITEM_STACK.in($$0), (String)"SaddleItem", (TypeTemplate)References.ITEM_STACK.in($$0)));
        V705.registerMob($$0, $$12, "minecraft:husk");
        $$0.register($$12, "minecraft:item", $$1 -> DSL.optionalFields((String)"Item", (TypeTemplate)References.ITEM_STACK.in($$0)));
        $$0.register($$12, "minecraft:item_frame", $$1 -> DSL.optionalFields((String)"Item", (TypeTemplate)References.ITEM_STACK.in($$0)));
        $$0.registerSimple($$12, "minecraft:leash_knot");
        V705.registerMob($$0, $$12, "minecraft:magma_cube");
        $$0.register($$12, "minecraft:minecart", $$1 -> DSL.optionalFields((String)"DisplayTile", (TypeTemplate)References.BLOCK_NAME.in($$0)));
        V705.registerMob($$0, $$12, "minecraft:mooshroom");
        $$0.register($$12, "minecraft:mule", $$1 -> DSL.optionalFields((String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in($$0)), (String)"SaddleItem", (TypeTemplate)References.ITEM_STACK.in($$0)));
        V705.registerMob($$0, $$12, "minecraft:ocelot");
        $$0.registerSimple($$12, "minecraft:painting");
        V705.registerMob($$0, $$12, "minecraft:parrot");
        V705.registerMob($$0, $$12, "minecraft:pig");
        V705.registerMob($$0, $$12, "minecraft:polar_bear");
        $$0.register($$12, "minecraft:potion", $$1 -> DSL.optionalFields((String)"Potion", (TypeTemplate)References.ITEM_STACK.in($$0), (String)"inTile", (TypeTemplate)References.BLOCK_NAME.in($$0)));
        V705.registerMob($$0, $$12, "minecraft:rabbit");
        V705.registerMob($$0, $$12, "minecraft:sheep");
        V705.registerMob($$0, $$12, "minecraft:shulker");
        $$0.registerSimple($$12, "minecraft:shulker_bullet");
        V705.registerMob($$0, $$12, "minecraft:silverfish");
        V705.registerMob($$0, $$12, "minecraft:skeleton");
        $$0.register($$12, "minecraft:skeleton_horse", $$1 -> DSL.optionalFields((String)"SaddleItem", (TypeTemplate)References.ITEM_STACK.in($$0)));
        V705.registerMob($$0, $$12, "minecraft:slime");
        V705.registerThrowableProjectile($$0, $$12, "minecraft:small_fireball");
        V705.registerThrowableProjectile($$0, $$12, "minecraft:snowball");
        V705.registerMob($$0, $$12, "minecraft:snowman");
        $$0.register($$12, "minecraft:spawner_minecart", $$1 -> DSL.optionalFields((String)"DisplayTile", (TypeTemplate)References.BLOCK_NAME.in($$0), (TypeTemplate)References.UNTAGGED_SPAWNER.in($$0)));
        $$0.register($$12, "minecraft:spectral_arrow", $$1 -> DSL.optionalFields((String)"inTile", (TypeTemplate)References.BLOCK_NAME.in($$0)));
        V705.registerMob($$0, $$12, "minecraft:spider");
        V705.registerMob($$0, $$12, "minecraft:squid");
        V705.registerMob($$0, $$12, "minecraft:stray");
        $$0.registerSimple($$12, "minecraft:tnt");
        $$0.register($$12, "minecraft:tnt_minecart", $$1 -> DSL.optionalFields((String)"DisplayTile", (TypeTemplate)References.BLOCK_NAME.in($$0)));
        $$0.register($$12, "minecraft:villager", $$1 -> DSL.optionalFields((String)"Inventory", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in($$0)), (String)"Offers", (TypeTemplate)DSL.optionalFields((String)"Recipes", (TypeTemplate)DSL.list((TypeTemplate)References.VILLAGER_TRADE.in($$0)))));
        V705.registerMob($$0, $$12, "minecraft:villager_golem");
        V705.registerMob($$0, $$12, "minecraft:witch");
        V705.registerMob($$0, $$12, "minecraft:wither");
        V705.registerMob($$0, $$12, "minecraft:wither_skeleton");
        V705.registerThrowableProjectile($$0, $$12, "minecraft:wither_skull");
        V705.registerMob($$0, $$12, "minecraft:wolf");
        V705.registerThrowableProjectile($$0, $$12, "minecraft:xp_bottle");
        $$0.registerSimple($$12, "minecraft:xp_orb");
        V705.registerMob($$0, $$12, "minecraft:zombie");
        $$0.register($$12, "minecraft:zombie_horse", $$1 -> DSL.optionalFields((String)"SaddleItem", (TypeTemplate)References.ITEM_STACK.in($$0)));
        V705.registerMob($$0, $$12, "minecraft:zombie_pigman");
        $$0.register($$12, "minecraft:zombie_villager", $$1 -> DSL.optionalFields((String)"Offers", (TypeTemplate)DSL.optionalFields((String)"Recipes", (TypeTemplate)DSL.list((TypeTemplate)References.VILLAGER_TRADE.in($$0)))));
        $$0.registerSimple($$12, "minecraft:evocation_fangs");
        V705.registerMob($$0, $$12, "minecraft:evocation_illager");
        V705.registerMob($$0, $$12, "minecraft:illusion_illager");
        $$0.register($$12, "minecraft:llama", $$1 -> DSL.optionalFields((String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in($$0)), (String)"SaddleItem", (TypeTemplate)References.ITEM_STACK.in($$0), (String)"DecorItem", (TypeTemplate)References.ITEM_STACK.in($$0)));
        $$0.registerSimple($$12, "minecraft:llama_spit");
        V705.registerMob($$0, $$12, "minecraft:vex");
        V705.registerMob($$0, $$12, "minecraft:vindication_illager");
        return $$12;
    }

    public void registerTypes(Schema $$0, Map<String, Supplier<TypeTemplate>> $$1, Map<String, Supplier<TypeTemplate>> $$2) {
        super.registerTypes($$0, $$1, $$2);
        $$0.registerType(true, References.ENTITY, () -> DSL.and((TypeTemplate)References.ENTITY_EQUIPMENT.in($$0), (TypeTemplate)DSL.optionalFields((String)"CustomName", (TypeTemplate)DSL.constType((Type)DSL.string()), (TypeTemplate)DSL.taggedChoiceLazy((String)"id", V705.namespacedString(), (Map)$$1))));
        $$0.registerType(true, References.ITEM_STACK, () -> DSL.hook((TypeTemplate)DSL.optionalFields((String)"id", (TypeTemplate)References.ITEM_NAME.in($$0), (String)"tag", (TypeTemplate)V99.itemStackTag($$0)), (Hook.HookFunction)ADD_NAMES, (Hook.HookFunction)Hook.HookFunction.IDENTITY));
    }
}

