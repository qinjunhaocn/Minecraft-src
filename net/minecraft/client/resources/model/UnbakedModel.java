/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.resources.model;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.resources.model.UnbakedGeometry;
import net.minecraft.resources.ResourceLocation;

public interface UnbakedModel {
    public static final String PARTICLE_TEXTURE_REFERENCE = "particle";

    @Nullable
    default public Boolean ambientOcclusion() {
        return null;
    }

    @Nullable
    default public GuiLight guiLight() {
        return null;
    }

    @Nullable
    default public ItemTransforms transforms() {
        return null;
    }

    default public TextureSlots.Data textureSlots() {
        return TextureSlots.Data.EMPTY;
    }

    @Nullable
    default public UnbakedGeometry geometry() {
        return null;
    }

    @Nullable
    default public ResourceLocation parent() {
        return null;
    }

    public static final class GuiLight
    extends Enum<GuiLight> {
        public static final /* enum */ GuiLight FRONT = new GuiLight("front");
        public static final /* enum */ GuiLight SIDE = new GuiLight("side");
        private final String name;
        private static final /* synthetic */ GuiLight[] $VALUES;

        public static GuiLight[] values() {
            return (GuiLight[])$VALUES.clone();
        }

        public static GuiLight valueOf(String $$0) {
            return Enum.valueOf(GuiLight.class, $$0);
        }

        private GuiLight(String $$0) {
            this.name = $$0;
        }

        public static GuiLight getByName(String $$0) {
            for (GuiLight $$1 : GuiLight.values()) {
                if (!$$1.name.equals($$0)) continue;
                return $$1;
            }
            throw new IllegalArgumentException("Invalid gui light: " + $$0);
        }

        public boolean lightLikeBlock() {
            return this == SIDE;
        }

        private static /* synthetic */ GuiLight[] b() {
            return new GuiLight[]{FRONT, SIDE};
        }

        static {
            $VALUES = GuiLight.b();
        }
    }
}

