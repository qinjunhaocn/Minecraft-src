/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  org.joml.Vector3f
 */
package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.SkullBlock;
import org.joml.Vector3f;

public class PlayerHeadSpecialRenderer
implements SpecialModelRenderer<PlayerHeadRenderInfo> {
    private final Map<ResolvableProfile, PlayerHeadRenderInfo> updatedResolvableProfiles = new HashMap<ResolvableProfile, PlayerHeadRenderInfo>();
    private final SkinManager skinManager;
    private final SkullModelBase modelBase;
    private final PlayerHeadRenderInfo defaultPlayerHeadRenderInfo;

    PlayerHeadSpecialRenderer(SkinManager $$0, SkullModelBase $$1, PlayerHeadRenderInfo $$2) {
        this.skinManager = $$0;
        this.modelBase = $$1;
        this.defaultPlayerHeadRenderInfo = $$2;
    }

    @Override
    public void render(@Nullable PlayerHeadRenderInfo $$0, ItemDisplayContext $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5, boolean $$6) {
        PlayerHeadRenderInfo $$7 = (PlayerHeadRenderInfo)((Object)Objects.requireNonNullElse((Object)((Object)$$0), (Object)((Object)this.defaultPlayerHeadRenderInfo)));
        RenderType $$8 = $$7.renderType();
        SkullBlockRenderer.renderSkull(null, 180.0f, 0.0f, $$2, $$3, $$4, this.modelBase, $$8);
    }

    @Override
    public void getExtents(Set<Vector3f> $$0) {
        PoseStack $$1 = new PoseStack();
        $$1.translate(0.5f, 0.0f, 0.5f);
        $$1.scale(-1.0f, -1.0f, 1.0f);
        this.modelBase.root().getExtentsForGui($$1, $$0);
    }

    @Override
    @Nullable
    public PlayerHeadRenderInfo extractArgument(ItemStack $$0) {
        ResolvableProfile $$1 = $$0.get(DataComponents.PROFILE);
        if ($$1 == null) {
            return null;
        }
        PlayerHeadRenderInfo $$2 = this.updatedResolvableProfiles.get((Object)$$1);
        if ($$2 != null) {
            return $$2;
        }
        ResolvableProfile $$3 = $$1.pollResolve();
        if ($$3 != null) {
            return this.createAndCacheIfTextureIsUnpacked($$3);
        }
        return null;
    }

    @Nullable
    private PlayerHeadRenderInfo createAndCacheIfTextureIsUnpacked(ResolvableProfile $$0) {
        PlayerSkin $$1 = this.skinManager.getInsecureSkin($$0.gameProfile(), null);
        if ($$1 != null) {
            PlayerHeadRenderInfo $$2 = PlayerHeadRenderInfo.create($$1);
            this.updatedResolvableProfiles.put($$0, $$2);
            return $$2;
        }
        return null;
    }

    @Override
    @Nullable
    public /* synthetic */ Object extractArgument(ItemStack itemStack) {
        return this.extractArgument(itemStack);
    }

    public record PlayerHeadRenderInfo(RenderType renderType) {
        static PlayerHeadRenderInfo create(PlayerSkin $$0) {
            return new PlayerHeadRenderInfo(SkullBlockRenderer.getPlayerSkinRenderType($$0.texture()));
        }
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked
    {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        @Nullable
        public SpecialModelRenderer<?> bake(EntityModelSet $$0) {
            SkullModelBase $$1 = SkullBlockRenderer.createModel($$0, SkullBlock.Types.PLAYER);
            if ($$1 == null) {
                return null;
            }
            return new PlayerHeadSpecialRenderer(Minecraft.getInstance().getSkinManager(), $$1, PlayerHeadRenderInfo.create(DefaultPlayerSkin.getDefaultSkin()));
        }
    }
}

