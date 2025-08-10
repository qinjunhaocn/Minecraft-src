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
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.vertex.PoseStack;
import java.lang.reflect.Type;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public record ItemTransform(Vector3fc rotation, Vector3fc translation, Vector3fc scale) {
    public static final ItemTransform NO_TRANSFORM = new ItemTransform((Vector3fc)new Vector3f(), (Vector3fc)new Vector3f(), (Vector3fc)new Vector3f(1.0f, 1.0f, 1.0f));

    public void apply(boolean $$0, PoseStack.Pose $$1) {
        float $$7;
        float $$6;
        float $$5;
        if (this == NO_TRANSFORM) {
            $$1.translate(-0.5f, -0.5f, -0.5f);
            return;
        }
        if ($$0) {
            float $$2 = -this.translation.x();
            float $$3 = -this.rotation.y();
            float $$4 = -this.rotation.z();
        } else {
            $$5 = this.translation.x();
            $$6 = this.rotation.y();
            $$7 = this.rotation.z();
        }
        $$1.translate($$5, this.translation.y(), this.translation.z());
        $$1.rotate((Quaternionfc)new Quaternionf().rotationXYZ(this.rotation.x() * ((float)Math.PI / 180), $$6 * ((float)Math.PI / 180), $$7 * ((float)Math.PI / 180)));
        $$1.scale(this.scale.x(), this.scale.y(), this.scale.z());
        $$1.translate(-0.5f, -0.5f, -0.5f);
    }

    protected static class Deserializer
    implements JsonDeserializer<ItemTransform> {
        private static final Vector3f DEFAULT_ROTATION = new Vector3f(0.0f, 0.0f, 0.0f);
        private static final Vector3f DEFAULT_TRANSLATION = new Vector3f(0.0f, 0.0f, 0.0f);
        private static final Vector3f DEFAULT_SCALE = new Vector3f(1.0f, 1.0f, 1.0f);
        public static final float MAX_TRANSLATION = 5.0f;
        public static final float MAX_SCALE = 4.0f;

        protected Deserializer() {
        }

        public ItemTransform deserialize(JsonElement $$0, Type $$1, JsonDeserializationContext $$2) throws JsonParseException {
            JsonObject $$3 = $$0.getAsJsonObject();
            Vector3f $$4 = this.getVector3f($$3, "rotation", DEFAULT_ROTATION);
            Vector3f $$5 = this.getVector3f($$3, "translation", DEFAULT_TRANSLATION);
            $$5.mul(0.0625f);
            $$5.set(Mth.clamp($$5.x, -5.0f, 5.0f), Mth.clamp($$5.y, -5.0f, 5.0f), Mth.clamp($$5.z, -5.0f, 5.0f));
            Vector3f $$6 = this.getVector3f($$3, "scale", DEFAULT_SCALE);
            $$6.set(Mth.clamp($$6.x, -4.0f, 4.0f), Mth.clamp($$6.y, -4.0f, 4.0f), Mth.clamp($$6.z, -4.0f, 4.0f));
            return new ItemTransform((Vector3fc)$$4, (Vector3fc)$$5, (Vector3fc)$$6);
        }

        private Vector3f getVector3f(JsonObject $$0, String $$1, Vector3f $$2) {
            if (!$$0.has($$1)) {
                return $$2;
            }
            JsonArray $$3 = GsonHelper.getAsJsonArray($$0, $$1);
            if ($$3.size() != 3) {
                throw new JsonParseException("Expected 3 " + $$1 + " values, found: " + $$3.size());
            }
            float[] $$4 = new float[3];
            for (int $$5 = 0; $$5 < $$4.length; ++$$5) {
                $$4[$$5] = GsonHelper.convertToFloat($$3.get($$5), $$1 + "[" + $$5 + "]");
            }
            return new Vector3f($$4[0], $$4[1], $$4[2]);
        }

        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }
}

