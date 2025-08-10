/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.resources;

import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public record PlayerSkin(ResourceLocation texture, @Nullable String textureUrl, @Nullable ResourceLocation capeTexture, @Nullable ResourceLocation elytraTexture, Model model, boolean secure) {
    @Nullable
    public String textureUrl() {
        return this.textureUrl;
    }

    @Nullable
    public ResourceLocation capeTexture() {
        return this.capeTexture;
    }

    @Nullable
    public ResourceLocation elytraTexture() {
        return this.elytraTexture;
    }

    public static final class Model
    extends Enum<Model> {
        public static final /* enum */ Model SLIM = new Model("slim");
        public static final /* enum */ Model WIDE = new Model("default");
        private final String id;
        private static final /* synthetic */ Model[] $VALUES;

        public static Model[] values() {
            return (Model[])$VALUES.clone();
        }

        public static Model valueOf(String $$0) {
            return Enum.valueOf(Model.class, $$0);
        }

        private Model(String $$0) {
            this.id = $$0;
        }

        public static Model byName(@Nullable String $$0) {
            if ($$0 == null) {
                return WIDE;
            }
            return switch ($$0) {
                case "slim" -> SLIM;
                default -> WIDE;
            };
        }

        public String id() {
            return this.id;
        }

        private static /* synthetic */ Model[] b() {
            return new Model[]{SLIM, WIDE};
        }

        static {
            $VALUES = Model.b();
        }
    }
}

