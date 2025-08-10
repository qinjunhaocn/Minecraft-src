/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 */
package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.world.item.ItemDisplayContext;

public record ItemTransforms(ItemTransform thirdPersonLeftHand, ItemTransform thirdPersonRightHand, ItemTransform firstPersonLeftHand, ItemTransform firstPersonRightHand, ItemTransform head, ItemTransform gui, ItemTransform ground, ItemTransform fixed) {
    public static final ItemTransforms NO_TRANSFORMS = new ItemTransforms(ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM);

    public ItemTransform getTransform(ItemDisplayContext $$0) {
        return switch ($$0) {
            case ItemDisplayContext.THIRD_PERSON_LEFT_HAND -> this.thirdPersonLeftHand;
            case ItemDisplayContext.THIRD_PERSON_RIGHT_HAND -> this.thirdPersonRightHand;
            case ItemDisplayContext.FIRST_PERSON_LEFT_HAND -> this.firstPersonLeftHand;
            case ItemDisplayContext.FIRST_PERSON_RIGHT_HAND -> this.firstPersonRightHand;
            case ItemDisplayContext.HEAD -> this.head;
            case ItemDisplayContext.GUI -> this.gui;
            case ItemDisplayContext.GROUND -> this.ground;
            case ItemDisplayContext.FIXED -> this.fixed;
            default -> ItemTransform.NO_TRANSFORM;
        };
    }

    protected static class Deserializer
    implements JsonDeserializer<ItemTransforms> {
        protected Deserializer() {
        }

        public ItemTransforms deserialize(JsonElement $$0, Type $$1, JsonDeserializationContext $$2) throws JsonParseException {
            JsonObject $$3 = $$0.getAsJsonObject();
            ItemTransform $$4 = this.getTransform($$2, $$3, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND);
            ItemTransform $$5 = this.getTransform($$2, $$3, ItemDisplayContext.THIRD_PERSON_LEFT_HAND);
            if ($$5 == ItemTransform.NO_TRANSFORM) {
                $$5 = $$4;
            }
            ItemTransform $$6 = this.getTransform($$2, $$3, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND);
            ItemTransform $$7 = this.getTransform($$2, $$3, ItemDisplayContext.FIRST_PERSON_LEFT_HAND);
            if ($$7 == ItemTransform.NO_TRANSFORM) {
                $$7 = $$6;
            }
            ItemTransform $$8 = this.getTransform($$2, $$3, ItemDisplayContext.HEAD);
            ItemTransform $$9 = this.getTransform($$2, $$3, ItemDisplayContext.GUI);
            ItemTransform $$10 = this.getTransform($$2, $$3, ItemDisplayContext.GROUND);
            ItemTransform $$11 = this.getTransform($$2, $$3, ItemDisplayContext.FIXED);
            return new ItemTransforms($$5, $$4, $$7, $$6, $$8, $$9, $$10, $$11);
        }

        private ItemTransform getTransform(JsonDeserializationContext $$0, JsonObject $$1, ItemDisplayContext $$2) {
            String $$3 = $$2.getSerializedName();
            if ($$1.has($$3)) {
                return (ItemTransform)((Object)$$0.deserialize($$1.get($$3), ItemTransform.class));
            }
            return ItemTransform.NO_TRANSFORM;
        }

        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }
}

