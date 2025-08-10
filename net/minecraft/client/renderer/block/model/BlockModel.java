/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 */
package net.minecraft.client.renderer.block.model;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.SimpleUnbakedGeometry;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.UnbakedGeometry;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public record BlockModel(@Nullable UnbakedGeometry geometry, @Nullable UnbakedModel.GuiLight guiLight, @Nullable Boolean ambientOcclusion, @Nullable ItemTransforms transforms, TextureSlots.Data textureSlots, @Nullable ResourceLocation parent) implements UnbakedModel
{
    @VisibleForTesting
    static final Gson GSON = new GsonBuilder().registerTypeAdapter(BlockModel.class, (Object)new Deserializer()).registerTypeAdapter(BlockElement.class, (Object)new BlockElement.Deserializer()).registerTypeAdapter(BlockElementFace.class, (Object)new BlockElementFace.Deserializer()).registerTypeAdapter(ItemTransform.class, (Object)new ItemTransform.Deserializer()).registerTypeAdapter(ItemTransforms.class, (Object)new ItemTransforms.Deserializer()).create();

    public static BlockModel fromStream(Reader $$0) {
        return GsonHelper.fromJson(GSON, $$0, BlockModel.class);
    }

    @Override
    @Nullable
    public UnbakedGeometry geometry() {
        return this.geometry;
    }

    @Override
    @Nullable
    public UnbakedModel.GuiLight guiLight() {
        return this.guiLight;
    }

    @Override
    @Nullable
    public Boolean ambientOcclusion() {
        return this.ambientOcclusion;
    }

    @Override
    @Nullable
    public ItemTransforms transforms() {
        return this.transforms;
    }

    @Override
    @Nullable
    public ResourceLocation parent() {
        return this.parent;
    }

    public static class Deserializer
    implements JsonDeserializer<BlockModel> {
        public BlockModel deserialize(JsonElement $$0, Type $$1, JsonDeserializationContext $$2) throws JsonParseException {
            JsonObject $$3 = $$0.getAsJsonObject();
            UnbakedGeometry $$4 = this.getElements($$2, $$3);
            String $$5 = this.getParentName($$3);
            TextureSlots.Data $$6 = this.getTextureMap($$3);
            Boolean $$7 = this.getAmbientOcclusion($$3);
            ItemTransforms $$8 = null;
            if ($$3.has("display")) {
                JsonObject $$9 = GsonHelper.getAsJsonObject($$3, "display");
                $$8 = (ItemTransforms)((Object)$$2.deserialize((JsonElement)$$9, ItemTransforms.class));
            }
            UnbakedModel.GuiLight $$10 = null;
            if ($$3.has("gui_light")) {
                $$10 = UnbakedModel.GuiLight.getByName(GsonHelper.getAsString($$3, "gui_light"));
            }
            ResourceLocation $$11 = $$5.isEmpty() ? null : ResourceLocation.parse($$5);
            return new BlockModel($$4, $$10, $$7, $$8, $$6, $$11);
        }

        private TextureSlots.Data getTextureMap(JsonObject $$0) {
            if ($$0.has("textures")) {
                JsonObject $$1 = GsonHelper.getAsJsonObject($$0, "textures");
                return TextureSlots.parseTextureMap($$1, TextureAtlas.LOCATION_BLOCKS);
            }
            return TextureSlots.Data.EMPTY;
        }

        private String getParentName(JsonObject $$0) {
            return GsonHelper.getAsString($$0, "parent", "");
        }

        @Nullable
        protected Boolean getAmbientOcclusion(JsonObject $$0) {
            if ($$0.has("ambientocclusion")) {
                return GsonHelper.getAsBoolean($$0, "ambientocclusion");
            }
            return null;
        }

        @Nullable
        protected UnbakedGeometry getElements(JsonDeserializationContext $$0, JsonObject $$1) {
            if ($$1.has("elements")) {
                ArrayList<BlockElement> $$2 = new ArrayList<BlockElement>();
                for (JsonElement $$3 : GsonHelper.getAsJsonArray($$1, "elements")) {
                    $$2.add((BlockElement)((Object)$$0.deserialize($$3, BlockElement.class)));
                }
                return new SimpleUnbakedGeometry($$2);
            }
            return null;
        }

        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }
}

