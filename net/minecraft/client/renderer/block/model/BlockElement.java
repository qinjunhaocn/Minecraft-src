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
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockElementRotation;
import net.minecraft.core.Direction;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public record BlockElement(Vector3fc from, Vector3fc to, Map<Direction, BlockElementFace> faces, @Nullable BlockElementRotation rotation, boolean shade, int lightEmission) {
    private static final boolean DEFAULT_RESCALE = false;
    private static final float MIN_EXTENT = -16.0f;
    private static final float MAX_EXTENT = 32.0f;

    public BlockElement(Vector3fc $$0, Vector3fc $$1, Map<Direction, BlockElementFace> $$2) {
        this($$0, $$1, $$2, null, true, 0);
    }

    @Nullable
    public BlockElementRotation rotation() {
        return this.rotation;
    }

    protected static class Deserializer
    implements JsonDeserializer<BlockElement> {
        private static final boolean DEFAULT_SHADE = true;
        private static final int DEFAULT_LIGHT_EMISSION = 0;

        protected Deserializer() {
        }

        public BlockElement deserialize(JsonElement $$0, Type $$1, JsonDeserializationContext $$2) throws JsonParseException {
            JsonObject $$3 = $$0.getAsJsonObject();
            Vector3f $$4 = this.getFrom($$3);
            Vector3f $$5 = this.getTo($$3);
            BlockElementRotation $$6 = this.getRotation($$3);
            Map<Direction, BlockElementFace> $$7 = this.getFaces($$2, $$3);
            if ($$3.has("shade") && !GsonHelper.isBooleanValue($$3, "shade")) {
                throw new JsonParseException("Expected shade to be a Boolean");
            }
            boolean $$8 = GsonHelper.getAsBoolean($$3, "shade", true);
            int $$9 = 0;
            if ($$3.has("light_emission")) {
                boolean $$10 = GsonHelper.isNumberValue($$3, "light_emission");
                if ($$10) {
                    $$9 = GsonHelper.getAsInt($$3, "light_emission");
                }
                if (!$$10 || $$9 < 0 || $$9 > 15) {
                    throw new JsonParseException("Expected light_emission to be an Integer between (inclusive) 0 and 15");
                }
            }
            return new BlockElement((Vector3fc)$$4, (Vector3fc)$$5, $$7, $$6, $$8, $$9);
        }

        @Nullable
        private BlockElementRotation getRotation(JsonObject $$0) {
            BlockElementRotation $$1 = null;
            if ($$0.has("rotation")) {
                JsonObject $$2 = GsonHelper.getAsJsonObject($$0, "rotation");
                Vector3f $$3 = this.getVector3f($$2, "origin");
                $$3.mul(0.0625f);
                Direction.Axis $$4 = this.getAxis($$2);
                float $$5 = this.getAngle($$2);
                boolean $$6 = GsonHelper.getAsBoolean($$2, "rescale", false);
                $$1 = new BlockElementRotation($$3, $$4, $$5, $$6);
            }
            return $$1;
        }

        private float getAngle(JsonObject $$0) {
            float $$1 = GsonHelper.getAsFloat($$0, "angle");
            if (Mth.abs($$1) > 45.0f) {
                throw new JsonParseException("Invalid rotation " + $$1 + " found, only values in [-45,45] range allowed");
            }
            return $$1;
        }

        private Direction.Axis getAxis(JsonObject $$0) {
            String $$1 = GsonHelper.getAsString($$0, "axis");
            Direction.Axis $$2 = Direction.Axis.byName($$1.toLowerCase(Locale.ROOT));
            if ($$2 == null) {
                throw new JsonParseException("Invalid rotation axis: " + $$1);
            }
            return $$2;
        }

        private Map<Direction, BlockElementFace> getFaces(JsonDeserializationContext $$0, JsonObject $$1) {
            Map<Direction, BlockElementFace> $$2 = this.filterNullFromFaces($$0, $$1);
            if ($$2.isEmpty()) {
                throw new JsonParseException("Expected between 1 and 6 unique faces, got 0");
            }
            return $$2;
        }

        private Map<Direction, BlockElementFace> filterNullFromFaces(JsonDeserializationContext $$0, JsonObject $$1) {
            EnumMap<Direction, BlockElementFace> $$2 = Maps.newEnumMap(Direction.class);
            JsonObject $$3 = GsonHelper.getAsJsonObject($$1, "faces");
            for (Map.Entry $$4 : $$3.entrySet()) {
                Direction $$5 = this.getFacing((String)$$4.getKey());
                $$2.put($$5, (BlockElementFace)((Object)$$0.deserialize((JsonElement)$$4.getValue(), BlockElementFace.class)));
            }
            return $$2;
        }

        private Direction getFacing(String $$0) {
            Direction $$1 = Direction.byName($$0);
            if ($$1 == null) {
                throw new JsonParseException("Unknown facing: " + $$0);
            }
            return $$1;
        }

        private Vector3f getTo(JsonObject $$0) {
            Vector3f $$1 = this.getVector3f($$0, "to");
            if ($$1.x() < -16.0f || $$1.y() < -16.0f || $$1.z() < -16.0f || $$1.x() > 32.0f || $$1.y() > 32.0f || $$1.z() > 32.0f) {
                throw new JsonParseException("'to' specifier exceeds the allowed boundaries: " + String.valueOf($$1));
            }
            return $$1;
        }

        private Vector3f getFrom(JsonObject $$0) {
            Vector3f $$1 = this.getVector3f($$0, "from");
            if ($$1.x() < -16.0f || $$1.y() < -16.0f || $$1.z() < -16.0f || $$1.x() > 32.0f || $$1.y() > 32.0f || $$1.z() > 32.0f) {
                throw new JsonParseException("'from' specifier exceeds the allowed boundaries: " + String.valueOf($$1));
            }
            return $$1;
        }

        private Vector3f getVector3f(JsonObject $$0, String $$1) {
            JsonArray $$2 = GsonHelper.getAsJsonArray($$0, $$1);
            if ($$2.size() != 3) {
                throw new JsonParseException("Expected 3 " + $$1 + " values, found: " + $$2.size());
            }
            float[] $$3 = new float[3];
            for (int $$4 = 0; $$4 < $$3.length; ++$$4) {
                $$3[$$4] = GsonHelper.convertToFloat($$2.get($$4), $$1 + "[" + $$4 + "]");
            }
            return new Vector3f($$3[0], $$3[1], $$3[2]);
        }

        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }
}

