/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntList
 */
package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.component.FireworkExplosion;

public class FireworkParticles {

    public static class SparkProvider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public SparkProvider(SpriteSet $$0) {
            this.sprites = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            SparkParticle $$8 = new SparkParticle($$1, $$2, $$3, $$4, $$5, $$6, $$7, Minecraft.getInstance().particleEngine, this.sprites);
            $$8.setAlpha(0.99f);
            return $$8;
        }

        @Override
        public /* synthetic */ Particle createParticle(ParticleOptions particleOptions, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            return this.createParticle((SimpleParticleType)particleOptions, clientLevel, d, d2, d3, d4, d5, d6);
        }
    }

    public static class FlashProvider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public FlashProvider(SpriteSet $$0) {
            this.sprite = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            OverlayParticle $$8 = new OverlayParticle($$1, $$2, $$3, $$4);
            $$8.pickSprite(this.sprite);
            return $$8;
        }

        @Override
        public /* synthetic */ Particle createParticle(ParticleOptions particleOptions, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            return this.createParticle((SimpleParticleType)particleOptions, clientLevel, d, d2, d3, d4, d5, d6);
        }
    }

    public static class OverlayParticle
    extends TextureSheetParticle {
        OverlayParticle(ClientLevel $$0, double $$1, double $$2, double $$3) {
            super($$0, $$1, $$2, $$3);
            this.lifetime = 4;
        }

        @Override
        public ParticleRenderType getRenderType() {
            return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
        }

        @Override
        public void render(VertexConsumer $$0, Camera $$1, float $$2) {
            this.setAlpha(0.6f - ((float)this.age + $$2 - 1.0f) * 0.25f * 0.5f);
            super.render($$0, $$1, $$2);
        }

        @Override
        public float getQuadSize(float $$0) {
            return 7.1f * Mth.sin(((float)this.age + $$0 - 1.0f) * 0.25f * (float)Math.PI);
        }
    }

    static class SparkParticle
    extends SimpleAnimatedParticle {
        private boolean trail;
        private boolean twinkle;
        private final ParticleEngine engine;
        private float fadeR;
        private float fadeG;
        private float fadeB;
        private boolean hasFade;

        SparkParticle(ClientLevel $$0, double $$1, double $$2, double $$3, double $$4, double $$5, double $$6, ParticleEngine $$7, SpriteSet $$8) {
            super($$0, $$1, $$2, $$3, $$8, 0.1f);
            this.xd = $$4;
            this.yd = $$5;
            this.zd = $$6;
            this.engine = $$7;
            this.quadSize *= 0.75f;
            this.lifetime = 48 + this.random.nextInt(12);
            this.setSpriteFromAge($$8);
        }

        public void setTrail(boolean $$0) {
            this.trail = $$0;
        }

        public void setTwinkle(boolean $$0) {
            this.twinkle = $$0;
        }

        @Override
        public void render(VertexConsumer $$0, Camera $$1, float $$2) {
            if (!this.twinkle || this.age < this.lifetime / 3 || (this.age + this.lifetime) / 3 % 2 == 0) {
                super.render($$0, $$1, $$2);
            }
        }

        @Override
        public void tick() {
            super.tick();
            if (this.trail && this.age < this.lifetime / 2 && (this.age + this.lifetime) % 2 == 0) {
                SparkParticle $$0 = new SparkParticle(this.level, this.x, this.y, this.z, 0.0, 0.0, 0.0, this.engine, this.sprites);
                $$0.setAlpha(0.99f);
                $$0.setColor(this.rCol, this.gCol, this.bCol);
                $$0.age = $$0.lifetime / 2;
                if (this.hasFade) {
                    $$0.hasFade = true;
                    $$0.fadeR = this.fadeR;
                    $$0.fadeG = this.fadeG;
                    $$0.fadeB = this.fadeB;
                }
                $$0.twinkle = this.twinkle;
                this.engine.add($$0);
            }
        }
    }

    public static class Starter
    extends NoRenderParticle {
        private static final double[][] CREEPER_PARTICLE_COORDS = new double[][]{{0.0, 0.2}, {0.2, 0.2}, {0.2, 0.6}, {0.6, 0.6}, {0.6, 0.2}, {0.2, 0.2}, {0.2, 0.0}, {0.4, 0.0}, {0.4, -0.6}, {0.2, -0.6}, {0.2, -0.4}, {0.0, -0.4}};
        private static final double[][] STAR_PARTICLE_COORDS = new double[][]{{0.0, 1.0}, {0.3455, 0.309}, {0.9511, 0.309}, {0.3795918367346939, -0.12653061224489795}, {0.6122448979591837, -0.8040816326530612}, {0.0, -0.35918367346938773}};
        private int life;
        private final ParticleEngine engine;
        private final List<FireworkExplosion> explosions;
        private boolean twinkleDelay;

        public Starter(ClientLevel $$0, double $$1, double $$2, double $$3, double $$4, double $$5, double $$6, ParticleEngine $$7, List<FireworkExplosion> $$8) {
            super($$0, $$1, $$2, $$3);
            this.xd = $$4;
            this.yd = $$5;
            this.zd = $$6;
            this.engine = $$7;
            if ($$8.isEmpty()) {
                throw new IllegalArgumentException("Cannot create firework starter with no explosions");
            }
            this.explosions = $$8;
            this.lifetime = $$8.size() * 2 - 1;
            for (FireworkExplosion $$9 : $$8) {
                if (!$$9.hasTwinkle()) continue;
                this.twinkleDelay = true;
                this.lifetime += 15;
                break;
            }
        }

        @Override
        public void tick() {
            if (this.life == 0) {
                SoundEvent $$4;
                boolean $$0 = this.isFarAwayFromCamera();
                boolean $$1 = false;
                if (this.explosions.size() >= 3) {
                    $$1 = true;
                } else {
                    for (FireworkExplosion $$2 : this.explosions) {
                        if ($$2.shape() != FireworkExplosion.Shape.LARGE_BALL) continue;
                        $$1 = true;
                        break;
                    }
                }
                if ($$1) {
                    SoundEvent $$3 = $$0 ? SoundEvents.FIREWORK_ROCKET_LARGE_BLAST_FAR : SoundEvents.FIREWORK_ROCKET_LARGE_BLAST;
                } else {
                    $$4 = $$0 ? SoundEvents.FIREWORK_ROCKET_BLAST_FAR : SoundEvents.FIREWORK_ROCKET_BLAST;
                }
                this.level.playLocalSound(this.x, this.y, this.z, $$4, SoundSource.AMBIENT, 20.0f, 0.95f + this.random.nextFloat() * 0.1f, true);
            }
            if (this.life % 2 == 0 && this.life / 2 < this.explosions.size()) {
                int $$5 = this.life / 2;
                FireworkExplosion $$6 = this.explosions.get($$5);
                boolean $$7 = $$6.hasTrail();
                boolean $$8 = $$6.hasTwinkle();
                IntList $$9 = $$6.colors();
                IntList $$10 = $$6.fadeColors();
                if ($$9.isEmpty()) {
                    $$9 = IntList.of((int)DyeColor.BLACK.getFireworkColor());
                }
                switch ($$6.shape()) {
                    case SMALL_BALL: {
                        this.createParticleBall(0.25, 2, $$9, $$10, $$7, $$8);
                        break;
                    }
                    case LARGE_BALL: {
                        this.createParticleBall(0.5, 4, $$9, $$10, $$7, $$8);
                        break;
                    }
                    case STAR: {
                        this.a(0.5, STAR_PARTICLE_COORDS, $$9, $$10, $$7, $$8, false);
                        break;
                    }
                    case CREEPER: {
                        this.a(0.5, CREEPER_PARTICLE_COORDS, $$9, $$10, $$7, $$8, true);
                        break;
                    }
                    case BURST: {
                        this.createParticleBurst($$9, $$10, $$7, $$8);
                    }
                }
                int $$11 = $$9.getInt(0);
                Particle $$12 = this.engine.createParticle(ParticleTypes.FLASH, this.x, this.y, this.z, 0.0, 0.0, 0.0);
                $$12.setColor((float)ARGB.red($$11) / 255.0f, (float)ARGB.green($$11) / 255.0f, (float)ARGB.blue($$11) / 255.0f);
            }
            ++this.life;
            if (this.life > this.lifetime) {
                if (this.twinkleDelay) {
                    boolean $$13 = this.isFarAwayFromCamera();
                    SoundEvent $$14 = $$13 ? SoundEvents.FIREWORK_ROCKET_TWINKLE_FAR : SoundEvents.FIREWORK_ROCKET_TWINKLE;
                    this.level.playLocalSound(this.x, this.y, this.z, $$14, SoundSource.AMBIENT, 20.0f, 0.9f + this.random.nextFloat() * 0.15f, true);
                }
                this.remove();
            }
        }

        private boolean isFarAwayFromCamera() {
            Minecraft $$0 = Minecraft.getInstance();
            return $$0.gameRenderer.getMainCamera().getPosition().distanceToSqr(this.x, this.y, this.z) >= 256.0;
        }

        private void createParticle(double $$0, double $$1, double $$2, double $$3, double $$4, double $$5, IntList $$6, IntList $$7, boolean $$8, boolean $$9) {
            SparkParticle $$10 = (SparkParticle)this.engine.createParticle(ParticleTypes.FIREWORK, $$0, $$1, $$2, $$3, $$4, $$5);
            $$10.setTrail($$8);
            $$10.setTwinkle($$9);
            $$10.setAlpha(0.99f);
            $$10.setColor((Integer)Util.getRandom($$6, this.random));
            if (!$$7.isEmpty()) {
                $$10.setFadeColor((Integer)Util.getRandom($$7, this.random));
            }
        }

        private void createParticleBall(double $$0, int $$1, IntList $$2, IntList $$3, boolean $$4, boolean $$5) {
            double $$6 = this.x;
            double $$7 = this.y;
            double $$8 = this.z;
            for (int $$9 = -$$1; $$9 <= $$1; ++$$9) {
                for (int $$10 = -$$1; $$10 <= $$1; ++$$10) {
                    for (int $$11 = -$$1; $$11 <= $$1; ++$$11) {
                        double $$12 = (double)$$10 + (this.random.nextDouble() - this.random.nextDouble()) * 0.5;
                        double $$13 = (double)$$9 + (this.random.nextDouble() - this.random.nextDouble()) * 0.5;
                        double $$14 = (double)$$11 + (this.random.nextDouble() - this.random.nextDouble()) * 0.5;
                        double $$15 = Math.sqrt($$12 * $$12 + $$13 * $$13 + $$14 * $$14) / $$0 + this.random.nextGaussian() * 0.05;
                        this.createParticle($$6, $$7, $$8, $$12 / $$15, $$13 / $$15, $$14 / $$15, $$2, $$3, $$4, $$5);
                        if ($$9 == -$$1 || $$9 == $$1 || $$10 == -$$1 || $$10 == $$1) continue;
                        $$11 += $$1 * 2 - 1;
                    }
                }
            }
        }

        private void a(double $$0, double[][] $$1, IntList $$2, IntList $$3, boolean $$4, boolean $$5, boolean $$6) {
            double $$7 = $$1[0][0];
            double $$8 = $$1[0][1];
            this.createParticle(this.x, this.y, this.z, $$7 * $$0, $$8 * $$0, 0.0, $$2, $$3, $$4, $$5);
            float $$9 = this.random.nextFloat() * (float)Math.PI;
            double $$10 = $$6 ? 0.034 : 0.34;
            for (int $$11 = 0; $$11 < 3; ++$$11) {
                double $$12 = (double)$$9 + (double)((float)$$11 * (float)Math.PI) * $$10;
                double $$13 = $$7;
                double $$14 = $$8;
                for (int $$15 = 1; $$15 < $$1.length; ++$$15) {
                    double $$16 = $$1[$$15][0];
                    double $$17 = $$1[$$15][1];
                    for (double $$18 = 0.25; $$18 <= 1.0; $$18 += 0.25) {
                        double $$19 = Mth.lerp($$18, $$13, $$16) * $$0;
                        double $$20 = Mth.lerp($$18, $$14, $$17) * $$0;
                        double $$21 = $$19 * Math.sin($$12);
                        $$19 *= Math.cos($$12);
                        for (double $$22 = -1.0; $$22 <= 1.0; $$22 += 2.0) {
                            this.createParticle(this.x, this.y, this.z, $$19 * $$22, $$20, $$21 * $$22, $$2, $$3, $$4, $$5);
                        }
                    }
                    $$13 = $$16;
                    $$14 = $$17;
                }
            }
        }

        private void createParticleBurst(IntList $$0, IntList $$1, boolean $$2, boolean $$3) {
            double $$4 = this.random.nextGaussian() * 0.05;
            double $$5 = this.random.nextGaussian() * 0.05;
            for (int $$6 = 0; $$6 < 70; ++$$6) {
                double $$7 = this.xd * 0.5 + this.random.nextGaussian() * 0.15 + $$4;
                double $$8 = this.zd * 0.5 + this.random.nextGaussian() * 0.15 + $$5;
                double $$9 = this.yd * 0.5 + this.random.nextDouble() * 0.5;
                this.createParticle(this.x, this.y, this.z, $$7, $$9, $$8, $$0, $$1, $$2, $$3);
            }
        }
    }
}

