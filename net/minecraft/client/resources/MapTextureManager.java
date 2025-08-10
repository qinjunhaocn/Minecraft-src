/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 */
package net.minecraft.client.resources;

import com.mojang.blaze3d.platform.NativeImage;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class MapTextureManager
implements AutoCloseable {
    private final Int2ObjectMap<MapInstance> maps = new Int2ObjectOpenHashMap();
    final TextureManager textureManager;

    public MapTextureManager(TextureManager $$0) {
        this.textureManager = $$0;
    }

    public void update(MapId $$0, MapItemSavedData $$1) {
        this.getOrCreateMapInstance($$0, $$1).forceUpload();
    }

    public ResourceLocation prepareMapTexture(MapId $$0, MapItemSavedData $$1) {
        MapInstance $$2 = this.getOrCreateMapInstance($$0, $$1);
        $$2.updateTextureIfNeeded();
        return $$2.location;
    }

    public void resetData() {
        for (MapInstance $$0 : this.maps.values()) {
            $$0.close();
        }
        this.maps.clear();
    }

    private MapInstance getOrCreateMapInstance(MapId $$0, MapItemSavedData $$12) {
        return (MapInstance)this.maps.compute($$0.id(), ($$1, $$2) -> {
            if ($$2 == null) {
                return new MapInstance(this, (int)$$1, $$12);
            }
            $$2.replaceMapData($$12);
            return $$2;
        });
    }

    @Override
    public void close() {
        this.resetData();
    }

    class MapInstance
    implements AutoCloseable {
        private MapItemSavedData data;
        private final DynamicTexture texture;
        private boolean requiresUpload = true;
        final ResourceLocation location;

        MapInstance(MapTextureManager mapTextureManager, int $$0, MapItemSavedData $$1) {
            this.data = $$1;
            this.texture = new DynamicTexture(() -> "Map " + $$0, 128, 128, true);
            this.location = ResourceLocation.withDefaultNamespace("map/" + $$0);
            mapTextureManager.textureManager.register(this.location, this.texture);
        }

        void replaceMapData(MapItemSavedData $$0) {
            boolean $$1 = this.data != $$0;
            this.data = $$0;
            this.requiresUpload |= $$1;
        }

        public void forceUpload() {
            this.requiresUpload = true;
        }

        void updateTextureIfNeeded() {
            if (this.requiresUpload) {
                NativeImage $$0 = this.texture.getPixels();
                if ($$0 != null) {
                    for (int $$1 = 0; $$1 < 128; ++$$1) {
                        for (int $$2 = 0; $$2 < 128; ++$$2) {
                            int $$3 = $$2 + $$1 * 128;
                            $$0.setPixel($$2, $$1, MapColor.getColorFromPackedId(this.data.colors[$$3]));
                        }
                    }
                }
                this.texture.upload();
                this.requiresUpload = false;
            }
        }

        @Override
        public void close() {
            this.texture.close();
        }
    }
}

