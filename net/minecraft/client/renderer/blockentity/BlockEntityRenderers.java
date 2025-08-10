/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.blockentity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BedRenderer;
import net.minecraft.client.renderer.blockentity.BellRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityWithBoundingBoxRenderer;
import net.minecraft.client.renderer.blockentity.BrushableBlockRenderer;
import net.minecraft.client.renderer.blockentity.CampfireRenderer;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.ConduitRenderer;
import net.minecraft.client.renderer.blockentity.DecoratedPotRenderer;
import net.minecraft.client.renderer.blockentity.EnchantTableRenderer;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.LecternRenderer;
import net.minecraft.client.renderer.blockentity.PistonHeadRenderer;
import net.minecraft.client.renderer.blockentity.ShulkerBoxRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.blockentity.SpawnerRenderer;
import net.minecraft.client.renderer.blockentity.TestInstanceRenderer;
import net.minecraft.client.renderer.blockentity.TheEndGatewayRenderer;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.client.renderer.blockentity.TrialSpawnerRenderer;
import net.minecraft.client.renderer.blockentity.VaultRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BlockEntityRenderers {
    private static final Map<BlockEntityType<?>, BlockEntityRendererProvider<?>> PROVIDERS = Maps.newHashMap();

    private static <T extends BlockEntity> void register(BlockEntityType<? extends T> $$0, BlockEntityRendererProvider<T> $$1) {
        PROVIDERS.put($$0, $$1);
    }

    public static Map<BlockEntityType<?>, BlockEntityRenderer<?>> createEntityRenderers(BlockEntityRendererProvider.Context $$0) {
        ImmutableMap.Builder $$1 = ImmutableMap.builder();
        PROVIDERS.forEach(($$2, $$3) -> {
            try {
                $$1.put($$2, $$3.create($$0));
            } catch (Exception $$4) {
                throw new IllegalStateException("Failed to create model for " + String.valueOf(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey((BlockEntityType<?>)$$2)), $$4);
            }
        });
        return $$1.build();
    }

    static {
        BlockEntityRenderers.register(BlockEntityType.SIGN, SignRenderer::new);
        BlockEntityRenderers.register(BlockEntityType.HANGING_SIGN, HangingSignRenderer::new);
        BlockEntityRenderers.register(BlockEntityType.MOB_SPAWNER, SpawnerRenderer::new);
        BlockEntityRenderers.register(BlockEntityType.PISTON, PistonHeadRenderer::new);
        BlockEntityRenderers.register(BlockEntityType.CHEST, ChestRenderer::new);
        BlockEntityRenderers.register(BlockEntityType.ENDER_CHEST, ChestRenderer::new);
        BlockEntityRenderers.register(BlockEntityType.TRAPPED_CHEST, ChestRenderer::new);
        BlockEntityRenderers.register(BlockEntityType.ENCHANTING_TABLE, EnchantTableRenderer::new);
        BlockEntityRenderers.register(BlockEntityType.LECTERN, LecternRenderer::new);
        BlockEntityRenderers.register(BlockEntityType.END_PORTAL, TheEndPortalRenderer::new);
        BlockEntityRenderers.register(BlockEntityType.END_GATEWAY, TheEndGatewayRenderer::new);
        BlockEntityRenderers.register(BlockEntityType.BEACON, BeaconRenderer::new);
        BlockEntityRenderers.register(BlockEntityType.SKULL, SkullBlockRenderer::new);
        BlockEntityRenderers.register(BlockEntityType.BANNER, BannerRenderer::new);
        BlockEntityRenderers.register(BlockEntityType.STRUCTURE_BLOCK, BlockEntityWithBoundingBoxRenderer::new);
        BlockEntityRenderers.register(BlockEntityType.TEST_INSTANCE_BLOCK, TestInstanceRenderer::new);
        BlockEntityRenderers.register(BlockEntityType.SHULKER_BOX, ShulkerBoxRenderer::new);
        BlockEntityRenderers.register(BlockEntityType.BED, BedRenderer::new);
        BlockEntityRenderers.register(BlockEntityType.CONDUIT, ConduitRenderer::new);
        BlockEntityRenderers.register(BlockEntityType.BELL, BellRenderer::new);
        BlockEntityRenderers.register(BlockEntityType.CAMPFIRE, CampfireRenderer::new);
        BlockEntityRenderers.register(BlockEntityType.BRUSHABLE_BLOCK, BrushableBlockRenderer::new);
        BlockEntityRenderers.register(BlockEntityType.DECORATED_POT, DecoratedPotRenderer::new);
        BlockEntityRenderers.register(BlockEntityType.TRIAL_SPAWNER, TrialSpawnerRenderer::new);
        BlockEntityRenderers.register(BlockEntityType.VAULT, VaultRenderer::new);
    }
}

