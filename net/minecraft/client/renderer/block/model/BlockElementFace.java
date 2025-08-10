/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 */
package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.math.Quadrant;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.util.GsonHelper;

public record BlockElementFace(@Nullable Direction cullForDirection, int tintIndex, String texture, @Nullable UVs uvs, Quadrant rotation) {
    public static final int NO_TINT = -1;

    public static float getU(UVs $$0, Quadrant $$1, int $$2) {
        return $$0.getVertexU($$1.rotateVertexIndex($$2)) / 16.0f;
    }

    public static float getV(UVs $$0, Quadrant $$1, int $$2) {
        return $$0.getVertexV($$1.rotateVertexIndex($$2)) / 16.0f;
    }

    @Nullable
    public Direction cullForDirection() {
        return this.cullForDirection;
    }

    @Nullable
    public UVs uvs() {
        return this.uvs;
    }

    public record UVs(float minU, float minV, float maxU, float maxV) {
        public float getVertexU(int $$0) {
            return $$0 == 0 || $$0 == 1 ? this.minU : this.maxU;
        }

        public float getVertexV(int $$0) {
            return $$0 == 0 || $$0 == 3 ? this.minV : this.maxV;
        }
    }

    protected static class Deserializer
    implements JsonDeserializer<BlockElementFace> {
        private static final int DEFAULT_TINT_INDEX = -1;
        private static final int DEFAULT_ROTATION = 0;

        protected Deserializer() {
        }

        public BlockElementFace deserialize(JsonElement $$0, Type $$1, JsonDeserializationContext $$2) throws JsonParseException {
            JsonObject $$3 = $$0.getAsJsonObject();
            Direction $$4 = Deserializer.getCullFacing($$3);
            int $$5 = Deserializer.getTintIndex($$3);
            String $$6 = Deserializer.getTexture($$3);
            UVs $$7 = Deserializer.getUVs($$3);
            Quadrant $$8 = Deserializer.getRotation($$3);
            return new BlockElementFace($$4, $$5, $$6, $$7, $$8);
        }

        private static int getTintIndex(JsonObject $$0) {
            return GsonHelper.getAsInt($$0, "tintindex", -1);
        }

        private static String getTexture(JsonObject $$0) {
            return GsonHelper.getAsString($$0, "texture");
        }

        @Nullable
        private static Direction getCullFacing(JsonObject $$0) {
            String $$1 = GsonHelper.getAsString($$0, "cullface", "");
            return Direction.byName($$1);
        }

        private static Quadrant getRotation(JsonObject $$0) {
            int $$1 = GsonHelper.getAsInt($$0, "rotation", 0);
            return Quadrant.parseJson($$1);
        }

        @Nullable
        private static UVs getUVs(JsonObject $$0) {
            if (!$$0.has("uv")) {
                return null;
            }
            JsonArray $$1 = GsonHelper.getAsJsonArray($$0, "uv");
            if ($$1.size() != 4) {
                throw new JsonParseException("Expected 4 uv values, found: " + $$1.size());
            }
            float $$2 = GsonHelper.convertToFloat($$1.get(0), "minU");
            float $$3 = GsonHelper.convertToFloat($$1.get(1), "minV");
            float $$4 = GsonHelper.convertToFloat($$1.get(2), "maxU");
            float $$5 = GsonHelper.convertToFloat($$1.get(3), "maxV");
            return new UVs($$2, $$3, $$4, $$5);
        }

        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }
}

