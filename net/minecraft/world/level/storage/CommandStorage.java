/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.storage;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.DimensionDataStorage;

public class CommandStorage {
    private static final String ID_PREFIX = "command_storage_";
    private final Map<String, Container> namespaces = new HashMap<String, Container>();
    private final DimensionDataStorage storage;

    public CommandStorage(DimensionDataStorage $$0) {
        this.storage = $$0;
    }

    public CompoundTag get(ResourceLocation $$0) {
        Container $$1 = this.getContainer($$0.getNamespace());
        if ($$1 != null) {
            return $$1.get($$0.getPath());
        }
        return new CompoundTag();
    }

    @Nullable
    private Container getContainer(String $$0) {
        Container $$1 = this.namespaces.get($$0);
        if ($$1 != null) {
            return $$1;
        }
        Container $$2 = this.storage.get(Container.type($$0));
        if ($$2 != null) {
            this.namespaces.put($$0, $$2);
        }
        return $$2;
    }

    private Container getOrCreateContainer(String $$0) {
        Container $$1 = this.namespaces.get($$0);
        if ($$1 != null) {
            return $$1;
        }
        Container $$2 = this.storage.computeIfAbsent(Container.type($$0));
        this.namespaces.put($$0, $$2);
        return $$2;
    }

    public void set(ResourceLocation $$0, CompoundTag $$1) {
        this.getOrCreateContainer($$0.getNamespace()).put($$0.getPath(), $$1);
    }

    public Stream<ResourceLocation> keys() {
        return this.namespaces.entrySet().stream().flatMap($$0 -> ((Container)$$0.getValue()).getKeys((String)$$0.getKey()));
    }

    static String createId(String $$0) {
        return ID_PREFIX + $$0;
    }

    static class Container
    extends SavedData {
        public static final Codec<Container> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)Codec.unboundedMap(ExtraCodecs.RESOURCE_PATH_CODEC, CompoundTag.CODEC).fieldOf("contents").forGetter($$0 -> $$0.storage)).apply((Applicative)$$02, Container::new));
        private final Map<String, CompoundTag> storage;

        private Container(Map<String, CompoundTag> $$0) {
            this.storage = new HashMap<String, CompoundTag>($$0);
        }

        private Container() {
            this(new HashMap<String, CompoundTag>());
        }

        public static SavedDataType<Container> type(String $$0) {
            return new SavedDataType<Container>(CommandStorage.createId($$0), Container::new, CODEC, DataFixTypes.SAVED_DATA_COMMAND_STORAGE);
        }

        public CompoundTag get(String $$0) {
            CompoundTag $$1 = this.storage.get($$0);
            return $$1 != null ? $$1 : new CompoundTag();
        }

        public void put(String $$0, CompoundTag $$1) {
            if ($$1.isEmpty()) {
                this.storage.remove($$0);
            } else {
                this.storage.put($$0, $$1);
            }
            this.setDirty();
        }

        public Stream<ResourceLocation> getKeys(String $$0) {
            return this.storage.keySet().stream().map($$1 -> ResourceLocation.fromNamespaceAndPath($$0, $$1));
        }
    }
}

