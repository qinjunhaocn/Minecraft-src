/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fStack
 */
package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

public abstract class RenderStateShard {
    public static final double MAX_ENCHANTMENT_GLINT_SPEED_MILLIS = 8.0;
    protected final String name;
    private final Runnable setupState;
    private final Runnable clearState;
    protected static final TextureStateShard BLOCK_SHEET_MIPPED = new TextureStateShard(TextureAtlas.LOCATION_BLOCKS, true);
    protected static final TextureStateShard BLOCK_SHEET = new TextureStateShard(TextureAtlas.LOCATION_BLOCKS, false);
    protected static final EmptyTextureStateShard NO_TEXTURE = new EmptyTextureStateShard();
    protected static final TexturingStateShard DEFAULT_TEXTURING = new TexturingStateShard("default_texturing", () -> {}, () -> {});
    protected static final TexturingStateShard GLINT_TEXTURING = new TexturingStateShard("glint_texturing", () -> RenderStateShard.setupGlintTexturing(8.0f), RenderSystem::resetTextureMatrix);
    protected static final TexturingStateShard ENTITY_GLINT_TEXTURING = new TexturingStateShard("entity_glint_texturing", () -> RenderStateShard.setupGlintTexturing(0.5f), RenderSystem::resetTextureMatrix);
    protected static final TexturingStateShard ARMOR_ENTITY_GLINT_TEXTURING = new TexturingStateShard("armor_entity_glint_texturing", () -> RenderStateShard.setupGlintTexturing(0.16f), RenderSystem::resetTextureMatrix);
    protected static final LightmapStateShard LIGHTMAP = new LightmapStateShard(true);
    protected static final LightmapStateShard NO_LIGHTMAP = new LightmapStateShard(false);
    protected static final OverlayStateShard OVERLAY = new OverlayStateShard(true);
    protected static final OverlayStateShard NO_OVERLAY = new OverlayStateShard(false);
    protected static final LayeringStateShard NO_LAYERING = new LayeringStateShard("no_layering", () -> {}, () -> {});
    protected static final LayeringStateShard VIEW_OFFSET_Z_LAYERING = new LayeringStateShard("view_offset_z_layering", () -> {
        Matrix4fStack $$0 = RenderSystem.getModelViewStack();
        $$0.pushMatrix();
        RenderSystem.getProjectionType().applyLayeringTransform((Matrix4f)$$0, 1.0f);
    }, () -> {
        Matrix4fStack $$0 = RenderSystem.getModelViewStack();
        $$0.popMatrix();
    });
    protected static final LayeringStateShard VIEW_OFFSET_Z_LAYERING_FORWARD = new LayeringStateShard("view_offset_z_layering_forward", () -> {
        Matrix4fStack $$0 = RenderSystem.getModelViewStack();
        $$0.pushMatrix();
        RenderSystem.getProjectionType().applyLayeringTransform((Matrix4f)$$0, -1.0f);
    }, () -> {
        Matrix4fStack $$0 = RenderSystem.getModelViewStack();
        $$0.popMatrix();
    });
    protected static final OutputStateShard MAIN_TARGET = new OutputStateShard("main_target", () -> Minecraft.getInstance().getMainRenderTarget());
    protected static final OutputStateShard OUTLINE_TARGET = new OutputStateShard("outline_target", () -> {
        RenderTarget $$0 = Minecraft.getInstance().levelRenderer.entityOutlineTarget();
        if ($$0 != null) {
            return $$0;
        }
        return Minecraft.getInstance().getMainRenderTarget();
    });
    protected static final OutputStateShard TRANSLUCENT_TARGET = new OutputStateShard("translucent_target", () -> {
        RenderTarget $$0 = Minecraft.getInstance().levelRenderer.getTranslucentTarget();
        if ($$0 != null) {
            return $$0;
        }
        return Minecraft.getInstance().getMainRenderTarget();
    });
    protected static final OutputStateShard PARTICLES_TARGET = new OutputStateShard("particles_target", () -> {
        RenderTarget $$0 = Minecraft.getInstance().levelRenderer.getParticlesTarget();
        if ($$0 != null) {
            return $$0;
        }
        return Minecraft.getInstance().getMainRenderTarget();
    });
    protected static final OutputStateShard WEATHER_TARGET = new OutputStateShard("weather_target", () -> {
        RenderTarget $$0 = Minecraft.getInstance().levelRenderer.getWeatherTarget();
        if ($$0 != null) {
            return $$0;
        }
        return Minecraft.getInstance().getMainRenderTarget();
    });
    protected static final OutputStateShard ITEM_ENTITY_TARGET = new OutputStateShard("item_entity_target", () -> {
        RenderTarget $$0 = Minecraft.getInstance().levelRenderer.getItemEntityTarget();
        if ($$0 != null) {
            return $$0;
        }
        return Minecraft.getInstance().getMainRenderTarget();
    });
    protected static final LineStateShard DEFAULT_LINE = new LineStateShard(OptionalDouble.of(1.0));

    public RenderStateShard(String $$0, Runnable $$1, Runnable $$2) {
        this.name = $$0;
        this.setupState = $$1;
        this.clearState = $$2;
    }

    public void setupRenderState() {
        this.setupState.run();
    }

    public void clearRenderState() {
        this.clearState.run();
    }

    public String toString() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }

    private static void setupGlintTexturing(float $$0) {
        long $$1 = (long)((double)Util.getMillis() * Minecraft.getInstance().options.glintSpeed().get() * 8.0);
        float $$2 = (float)($$1 % 110000L) / 110000.0f;
        float $$3 = (float)($$1 % 30000L) / 30000.0f;
        Matrix4f $$4 = new Matrix4f().translation(-$$2, $$3, 0.0f);
        $$4.rotateZ(0.17453292f).scale($$0);
        RenderSystem.setTextureMatrix($$4);
    }

    protected static class TextureStateShard
    extends EmptyTextureStateShard {
        private final Optional<ResourceLocation> texture;
        private final boolean mipmap;

        public TextureStateShard(ResourceLocation $$0, boolean $$1) {
            super(() -> {
                TextureManager $$2 = Minecraft.getInstance().getTextureManager();
                AbstractTexture $$3 = $$2.getTexture($$0);
                $$3.setUseMipmaps($$1);
                RenderSystem.setShaderTexture(0, $$3.getTextureView());
            }, () -> {});
            this.texture = Optional.of($$0);
            this.mipmap = $$1;
        }

        @Override
        public String toString() {
            return this.name + "[" + String.valueOf(this.texture) + "(mipmap=" + this.mipmap + ")]";
        }

        @Override
        protected Optional<ResourceLocation> cutoutTexture() {
            return this.texture;
        }
    }

    protected static class EmptyTextureStateShard
    extends RenderStateShard {
        public EmptyTextureStateShard(Runnable $$0, Runnable $$1) {
            super("texture", $$0, $$1);
        }

        EmptyTextureStateShard() {
            super("texture", () -> {}, () -> {});
        }

        protected Optional<ResourceLocation> cutoutTexture() {
            return Optional.empty();
        }
    }

    protected static class TexturingStateShard
    extends RenderStateShard {
        public TexturingStateShard(String $$0, Runnable $$1, Runnable $$2) {
            super($$0, $$1, $$2);
        }
    }

    protected static class LightmapStateShard
    extends BooleanStateShard {
        public LightmapStateShard(boolean $$0) {
            super("lightmap", () -> {
                if ($$0) {
                    Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
                }
            }, () -> {
                if ($$0) {
                    Minecraft.getInstance().gameRenderer.lightTexture().turnOffLightLayer();
                }
            }, $$0);
        }
    }

    protected static class OverlayStateShard
    extends BooleanStateShard {
        public OverlayStateShard(boolean $$0) {
            super("overlay", () -> {
                if ($$0) {
                    Minecraft.getInstance().gameRenderer.overlayTexture().setupOverlayColor();
                }
            }, () -> {
                if ($$0) {
                    Minecraft.getInstance().gameRenderer.overlayTexture().teardownOverlayColor();
                }
            }, $$0);
        }
    }

    protected static class LayeringStateShard
    extends RenderStateShard {
        public LayeringStateShard(String $$0, Runnable $$1, Runnable $$2) {
            super($$0, $$1, $$2);
        }
    }

    protected static class OutputStateShard
    extends RenderStateShard {
        private final Supplier<RenderTarget> renderTargetSupplier;

        public OutputStateShard(String $$0, Supplier<RenderTarget> $$1) {
            super($$0, () -> {}, () -> {});
            this.renderTargetSupplier = $$1;
        }

        public RenderTarget getRenderTarget() {
            return this.renderTargetSupplier.get();
        }
    }

    protected static class LineStateShard
    extends RenderStateShard {
        private final OptionalDouble width;

        public LineStateShard(OptionalDouble $$0) {
            super("line_width", () -> {
                if (!Objects.equals($$0, OptionalDouble.of(1.0))) {
                    if ($$0.isPresent()) {
                        RenderSystem.lineWidth((float)$$0.getAsDouble());
                    } else {
                        RenderSystem.lineWidth(Math.max(2.5f, (float)Minecraft.getInstance().getWindow().getWidth() / 1920.0f * 2.5f));
                    }
                }
            }, () -> {
                if (!Objects.equals($$0, OptionalDouble.of(1.0))) {
                    RenderSystem.lineWidth(1.0f);
                }
            });
            this.width = $$0;
        }

        @Override
        public String toString() {
            return this.name + "[" + String.valueOf(this.width.isPresent() ? Double.valueOf(this.width.getAsDouble()) : "window_scale") + "]";
        }
    }

    static class BooleanStateShard
    extends RenderStateShard {
        private final boolean enabled;

        public BooleanStateShard(String $$0, Runnable $$1, Runnable $$2, boolean $$3) {
            super($$0, $$1, $$2);
            this.enabled = $$3;
        }

        @Override
        public String toString() {
            return this.name + "[" + this.enabled + "]";
        }
    }

    protected static final class OffsetTexturingStateShard
    extends TexturingStateShard {
        public OffsetTexturingStateShard(float $$0, float $$1) {
            super("offset_texturing", () -> RenderSystem.setTextureMatrix(new Matrix4f().translation($$0, $$1, 0.0f)), () -> RenderSystem.resetTextureMatrix());
        }
    }

    protected static class MultiTextureStateShard
    extends EmptyTextureStateShard {
        private final Optional<ResourceLocation> cutoutTexture;

        MultiTextureStateShard(List<Entry> $$0) {
            super(() -> {
                for (int $$1 = 0; $$1 < $$0.size(); ++$$1) {
                    Entry $$2 = (Entry)((Object)((Object)$$0.get($$1)));
                    TextureManager $$3 = Minecraft.getInstance().getTextureManager();
                    AbstractTexture $$4 = $$3.getTexture($$2.id);
                    $$4.setUseMipmaps($$2.mipmap);
                    RenderSystem.setShaderTexture($$1, $$4.getTextureView());
                }
            }, () -> {});
            this.cutoutTexture = $$0.isEmpty() ? Optional.empty() : Optional.of(((Entry)((Object)$$0.getFirst())).id);
        }

        @Override
        protected Optional<ResourceLocation> cutoutTexture() {
            return this.cutoutTexture;
        }

        public static Builder builder() {
            return new Builder();
        }

        static final class Entry
        extends Record {
            final ResourceLocation id;
            final boolean mipmap;

            Entry(ResourceLocation $$0, boolean $$1) {
                this.id = $$0;
                this.mipmap = $$1;
            }

            public final String toString() {
                return ObjectMethods.bootstrap("toString", new MethodHandle[]{Entry.class, "id;mipmap", "id", "mipmap"}, this);
            }

            public final int hashCode() {
                return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Entry.class, "id;mipmap", "id", "mipmap"}, this);
            }

            public final boolean equals(Object $$0) {
                return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Entry.class, "id;mipmap", "id", "mipmap"}, this, $$0);
            }

            public ResourceLocation id() {
                return this.id;
            }

            public boolean mipmap() {
                return this.mipmap;
            }
        }

        public static final class Builder {
            private final ImmutableList.Builder<Entry> builder = new ImmutableList.Builder();

            public Builder add(ResourceLocation $$0, boolean $$1) {
                this.builder.add((Object)new Entry($$0, $$1));
                return this;
            }

            public MultiTextureStateShard build() {
                return new MultiTextureStateShard((List<Entry>)((Object)this.builder.build()));
            }
        }
    }
}

