/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class BreakingItemParticle
extends TextureSheetParticle {
    private final float uo;
    private final float vo;

    BreakingItemParticle(ClientLevel $$0, double $$1, double $$2, double $$3, double $$4, double $$5, double $$6, ItemStackRenderState $$7) {
        this($$0, $$1, $$2, $$3, $$7);
        this.xd *= (double)0.1f;
        this.yd *= (double)0.1f;
        this.zd *= (double)0.1f;
        this.xd += $$4;
        this.yd += $$5;
        this.zd += $$6;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.TERRAIN_SHEET;
    }

    protected BreakingItemParticle(ClientLevel $$0, double $$1, double $$2, double $$3, ItemStackRenderState $$4) {
        super($$0, $$1, $$2, $$3, 0.0, 0.0, 0.0);
        TextureAtlasSprite $$5 = $$4.pickParticleIcon(this.random);
        if ($$5 != null) {
            this.setSprite($$5);
        } else {
            this.setSprite(Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(MissingTextureAtlasSprite.getLocation()));
        }
        this.gravity = 1.0f;
        this.quadSize /= 2.0f;
        this.uo = this.random.nextFloat() * 3.0f;
        this.vo = this.random.nextFloat() * 3.0f;
    }

    @Override
    protected float getU0() {
        return this.sprite.getU((this.uo + 1.0f) / 4.0f);
    }

    @Override
    protected float getU1() {
        return this.sprite.getU(this.uo / 4.0f);
    }

    @Override
    protected float getV0() {
        return this.sprite.getV(this.vo / 4.0f);
    }

    @Override
    protected float getV1() {
        return this.sprite.getV((this.vo + 1.0f) / 4.0f);
    }

    public static class SnowballProvider
    extends ItemParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            return new BreakingItemParticle($$1, $$2, $$3, $$4, this.calculateState(new ItemStack(Items.SNOWBALL), $$1));
        }

        @Override
        public /* synthetic */ Particle createParticle(ParticleOptions particleOptions, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            return this.createParticle((SimpleParticleType)particleOptions, clientLevel, d, d2, d3, d4, d5, d6);
        }
    }

    public static class CobwebProvider
    extends ItemParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            return new BreakingItemParticle($$1, $$2, $$3, $$4, this.calculateState(new ItemStack(Items.COBWEB), $$1));
        }

        @Override
        public /* synthetic */ Particle createParticle(ParticleOptions particleOptions, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            return this.createParticle((SimpleParticleType)particleOptions, clientLevel, d, d2, d3, d4, d5, d6);
        }
    }

    public static class SlimeProvider
    extends ItemParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            return new BreakingItemParticle($$1, $$2, $$3, $$4, this.calculateState(new ItemStack(Items.SLIME_BALL), $$1));
        }

        @Override
        public /* synthetic */ Particle createParticle(ParticleOptions particleOptions, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            return this.createParticle((SimpleParticleType)particleOptions, clientLevel, d, d2, d3, d4, d5, d6);
        }
    }

    public static class Provider
    extends ItemParticleProvider<ItemParticleOption> {
        @Override
        public Particle createParticle(ItemParticleOption $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            return new BreakingItemParticle($$1, $$2, $$3, $$4, $$5, $$6, $$7, this.calculateState($$0.getItem(), $$1));
        }

        @Override
        public /* synthetic */ Particle createParticle(ParticleOptions particleOptions, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            return this.createParticle((ItemParticleOption)particleOptions, clientLevel, d, d2, d3, d4, d5, d6);
        }
    }

    public static abstract class ItemParticleProvider<T extends ParticleOptions>
    implements ParticleProvider<T> {
        private final ItemStackRenderState scratchRenderState = new ItemStackRenderState();

        protected ItemStackRenderState calculateState(ItemStack $$0, ClientLevel $$1) {
            Minecraft.getInstance().getItemModelResolver().updateForTopItem(this.scratchRenderState, $$0, ItemDisplayContext.GROUND, $$1, null, 0);
            return this.scratchRenderState;
        }
    }
}

