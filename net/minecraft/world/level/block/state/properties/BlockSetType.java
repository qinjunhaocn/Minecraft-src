/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 */
package net.minecraft.world.level.block.state.properties;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;

public record BlockSetType(String name, boolean canOpenByHand, boolean canOpenByWindCharge, boolean canButtonBeActivatedByArrows, PressurePlateSensitivity pressurePlateSensitivity, SoundType soundType, SoundEvent doorClose, SoundEvent doorOpen, SoundEvent trapdoorClose, SoundEvent trapdoorOpen, SoundEvent pressurePlateClickOff, SoundEvent pressurePlateClickOn, SoundEvent buttonClickOff, SoundEvent buttonClickOn) {
    private static final Map<String, BlockSetType> TYPES = new Object2ObjectArrayMap();
    public static final Codec<BlockSetType> CODEC = Codec.stringResolver(BlockSetType::name, TYPES::get);
    public static final BlockSetType IRON = BlockSetType.register(new BlockSetType("iron", false, false, false, PressurePlateSensitivity.EVERYTHING, SoundType.IRON, SoundEvents.IRON_DOOR_CLOSE, SoundEvents.IRON_DOOR_OPEN, SoundEvents.IRON_TRAPDOOR_CLOSE, SoundEvents.IRON_TRAPDOOR_OPEN, SoundEvents.METAL_PRESSURE_PLATE_CLICK_OFF, SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON, SoundEvents.STONE_BUTTON_CLICK_OFF, SoundEvents.STONE_BUTTON_CLICK_ON));
    public static final BlockSetType COPPER = BlockSetType.register(new BlockSetType("copper", true, true, false, PressurePlateSensitivity.EVERYTHING, SoundType.COPPER, SoundEvents.COPPER_DOOR_CLOSE, SoundEvents.COPPER_DOOR_OPEN, SoundEvents.COPPER_TRAPDOOR_CLOSE, SoundEvents.COPPER_TRAPDOOR_OPEN, SoundEvents.METAL_PRESSURE_PLATE_CLICK_OFF, SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON, SoundEvents.STONE_BUTTON_CLICK_OFF, SoundEvents.STONE_BUTTON_CLICK_ON));
    public static final BlockSetType GOLD = BlockSetType.register(new BlockSetType("gold", false, true, false, PressurePlateSensitivity.EVERYTHING, SoundType.METAL, SoundEvents.IRON_DOOR_CLOSE, SoundEvents.IRON_DOOR_OPEN, SoundEvents.IRON_TRAPDOOR_CLOSE, SoundEvents.IRON_TRAPDOOR_OPEN, SoundEvents.METAL_PRESSURE_PLATE_CLICK_OFF, SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON, SoundEvents.STONE_BUTTON_CLICK_OFF, SoundEvents.STONE_BUTTON_CLICK_ON));
    public static final BlockSetType STONE = BlockSetType.register(new BlockSetType("stone", true, true, false, PressurePlateSensitivity.MOBS, SoundType.STONE, SoundEvents.IRON_DOOR_CLOSE, SoundEvents.IRON_DOOR_OPEN, SoundEvents.IRON_TRAPDOOR_CLOSE, SoundEvents.IRON_TRAPDOOR_OPEN, SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF, SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON, SoundEvents.STONE_BUTTON_CLICK_OFF, SoundEvents.STONE_BUTTON_CLICK_ON));
    public static final BlockSetType POLISHED_BLACKSTONE = BlockSetType.register(new BlockSetType("polished_blackstone", true, true, false, PressurePlateSensitivity.MOBS, SoundType.STONE, SoundEvents.IRON_DOOR_CLOSE, SoundEvents.IRON_DOOR_OPEN, SoundEvents.IRON_TRAPDOOR_CLOSE, SoundEvents.IRON_TRAPDOOR_OPEN, SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF, SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON, SoundEvents.STONE_BUTTON_CLICK_OFF, SoundEvents.STONE_BUTTON_CLICK_ON));
    public static final BlockSetType OAK = BlockSetType.register(new BlockSetType("oak"));
    public static final BlockSetType SPRUCE = BlockSetType.register(new BlockSetType("spruce"));
    public static final BlockSetType BIRCH = BlockSetType.register(new BlockSetType("birch"));
    public static final BlockSetType ACACIA = BlockSetType.register(new BlockSetType("acacia"));
    public static final BlockSetType CHERRY = BlockSetType.register(new BlockSetType("cherry", true, true, true, PressurePlateSensitivity.EVERYTHING, SoundType.CHERRY_WOOD, SoundEvents.CHERRY_WOOD_DOOR_CLOSE, SoundEvents.CHERRY_WOOD_DOOR_OPEN, SoundEvents.CHERRY_WOOD_TRAPDOOR_CLOSE, SoundEvents.CHERRY_WOOD_TRAPDOOR_OPEN, SoundEvents.CHERRY_WOOD_PRESSURE_PLATE_CLICK_OFF, SoundEvents.CHERRY_WOOD_PRESSURE_PLATE_CLICK_ON, SoundEvents.CHERRY_WOOD_BUTTON_CLICK_OFF, SoundEvents.CHERRY_WOOD_BUTTON_CLICK_ON));
    public static final BlockSetType JUNGLE = BlockSetType.register(new BlockSetType("jungle"));
    public static final BlockSetType DARK_OAK = BlockSetType.register(new BlockSetType("dark_oak"));
    public static final BlockSetType PALE_OAK = BlockSetType.register(new BlockSetType("pale_oak"));
    public static final BlockSetType CRIMSON = BlockSetType.register(new BlockSetType("crimson", true, true, true, PressurePlateSensitivity.EVERYTHING, SoundType.NETHER_WOOD, SoundEvents.NETHER_WOOD_DOOR_CLOSE, SoundEvents.NETHER_WOOD_DOOR_OPEN, SoundEvents.NETHER_WOOD_TRAPDOOR_CLOSE, SoundEvents.NETHER_WOOD_TRAPDOOR_OPEN, SoundEvents.NETHER_WOOD_PRESSURE_PLATE_CLICK_OFF, SoundEvents.NETHER_WOOD_PRESSURE_PLATE_CLICK_ON, SoundEvents.NETHER_WOOD_BUTTON_CLICK_OFF, SoundEvents.NETHER_WOOD_BUTTON_CLICK_ON));
    public static final BlockSetType WARPED = BlockSetType.register(new BlockSetType("warped", true, true, true, PressurePlateSensitivity.EVERYTHING, SoundType.NETHER_WOOD, SoundEvents.NETHER_WOOD_DOOR_CLOSE, SoundEvents.NETHER_WOOD_DOOR_OPEN, SoundEvents.NETHER_WOOD_TRAPDOOR_CLOSE, SoundEvents.NETHER_WOOD_TRAPDOOR_OPEN, SoundEvents.NETHER_WOOD_PRESSURE_PLATE_CLICK_OFF, SoundEvents.NETHER_WOOD_PRESSURE_PLATE_CLICK_ON, SoundEvents.NETHER_WOOD_BUTTON_CLICK_OFF, SoundEvents.NETHER_WOOD_BUTTON_CLICK_ON));
    public static final BlockSetType MANGROVE = BlockSetType.register(new BlockSetType("mangrove"));
    public static final BlockSetType BAMBOO = BlockSetType.register(new BlockSetType("bamboo", true, true, true, PressurePlateSensitivity.EVERYTHING, SoundType.BAMBOO_WOOD, SoundEvents.BAMBOO_WOOD_DOOR_CLOSE, SoundEvents.BAMBOO_WOOD_DOOR_OPEN, SoundEvents.BAMBOO_WOOD_TRAPDOOR_CLOSE, SoundEvents.BAMBOO_WOOD_TRAPDOOR_OPEN, SoundEvents.BAMBOO_WOOD_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BAMBOO_WOOD_PRESSURE_PLATE_CLICK_ON, SoundEvents.BAMBOO_WOOD_BUTTON_CLICK_OFF, SoundEvents.BAMBOO_WOOD_BUTTON_CLICK_ON));

    public BlockSetType(String $$0) {
        this($$0, true, true, true, PressurePlateSensitivity.EVERYTHING, SoundType.WOOD, SoundEvents.WOODEN_DOOR_CLOSE, SoundEvents.WOODEN_DOOR_OPEN, SoundEvents.WOODEN_TRAPDOOR_CLOSE, SoundEvents.WOODEN_TRAPDOOR_OPEN, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_OFF, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_ON, SoundEvents.WOODEN_BUTTON_CLICK_OFF, SoundEvents.WOODEN_BUTTON_CLICK_ON);
    }

    private static BlockSetType register(BlockSetType $$0) {
        TYPES.put($$0.name, $$0);
        return $$0;
    }

    public static Stream<BlockSetType> values() {
        return TYPES.values().stream();
    }

    public static final class PressurePlateSensitivity
    extends Enum<PressurePlateSensitivity> {
        public static final /* enum */ PressurePlateSensitivity EVERYTHING = new PressurePlateSensitivity();
        public static final /* enum */ PressurePlateSensitivity MOBS = new PressurePlateSensitivity();
        private static final /* synthetic */ PressurePlateSensitivity[] $VALUES;

        public static PressurePlateSensitivity[] values() {
            return (PressurePlateSensitivity[])$VALUES.clone();
        }

        public static PressurePlateSensitivity valueOf(String $$0) {
            return Enum.valueOf(PressurePlateSensitivity.class, $$0);
        }

        private static /* synthetic */ PressurePlateSensitivity[] a() {
            return new PressurePlateSensitivity[]{EVERYTHING, MOBS};
        }

        static {
            $VALUES = PressurePlateSensitivity.a();
        }
    }
}

