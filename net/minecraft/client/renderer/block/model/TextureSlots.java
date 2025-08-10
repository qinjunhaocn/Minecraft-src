/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMaps
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  java.lang.MatchException
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 *  java.lang.runtime.SwitchBootstraps
 */
package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.lang.runtime.SwitchBootstraps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class TextureSlots {
    public static final TextureSlots EMPTY = new TextureSlots(Map.of());
    private static final char REFERENCE_CHAR = '#';
    private final Map<String, Material> resolvedValues;

    TextureSlots(Map<String, Material> $$0) {
        this.resolvedValues = $$0;
    }

    @Nullable
    public Material getMaterial(String $$0) {
        if (TextureSlots.isTextureReference($$0)) {
            $$0 = $$0.substring(1);
        }
        return this.resolvedValues.get($$0);
    }

    private static boolean isTextureReference(String $$0) {
        return $$0.charAt(0) == '#';
    }

    public static Data parseTextureMap(JsonObject $$0, ResourceLocation $$1) {
        Data.Builder $$2 = new Data.Builder();
        for (Map.Entry $$3 : $$0.entrySet()) {
            TextureSlots.parseEntry($$1, (String)$$3.getKey(), ((JsonElement)$$3.getValue()).getAsString(), $$2);
        }
        return $$2.build();
    }

    private static void parseEntry(ResourceLocation $$0, String $$1, String $$2, Data.Builder $$3) {
        if (TextureSlots.isTextureReference($$2)) {
            $$3.addReference($$1, $$2.substring(1));
        } else {
            ResourceLocation $$4 = ResourceLocation.tryParse($$2);
            if ($$4 == null) {
                throw new JsonParseException($$2 + " is not valid resource location");
            }
            $$3.addTexture($$1, new Material($$0, $$4));
        }
    }

    public static final class Data
    extends Record {
        final Map<String, SlotContents> values;
        public static final Data EMPTY = new Data(Map.of());

        public Data(Map<String, SlotContents> $$0) {
            this.values = $$0;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Data.class, "values", "values"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Data.class, "values", "values"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Data.class, "values", "values"}, this, $$0);
        }

        public Map<String, SlotContents> values() {
            return this.values;
        }

        public static class Builder {
            private final Map<String, SlotContents> textureMap = new HashMap<String, SlotContents>();

            public Builder addReference(String $$0, String $$1) {
                this.textureMap.put($$0, new Reference($$1));
                return this;
            }

            public Builder addTexture(String $$0, Material $$1) {
                this.textureMap.put($$0, new Value($$1));
                return this;
            }

            public Data build() {
                if (this.textureMap.isEmpty()) {
                    return EMPTY;
                }
                return new Data(Map.copyOf(this.textureMap));
            }
        }
    }

    public static class Resolver {
        private static final Logger LOGGER = LogUtils.getLogger();
        private final List<Data> entries = new ArrayList<Data>();

        public Resolver addLast(Data $$0) {
            this.entries.addLast((Object)$$0);
            return this;
        }

        public Resolver addFirst(Data $$0) {
            this.entries.addFirst((Object)$$0);
            return this;
        }

        public TextureSlots resolve(ModelDebugName $$02) {
            if (this.entries.isEmpty()) {
                return EMPTY;
            }
            Object2ObjectArrayMap $$1 = new Object2ObjectArrayMap();
            Object2ObjectArrayMap $$2 = new Object2ObjectArrayMap();
            for (Data $$3 : Lists.reverse(this.entries)) {
                $$3.values.forEach((arg_0, arg_1) -> Resolver.lambda$resolve$0((Object2ObjectMap)$$2, (Object2ObjectMap)$$1, arg_0, arg_1));
            }
            if ($$2.isEmpty()) {
                return new TextureSlots((Map<String, Material>)$$1);
            }
            boolean $$4 = true;
            while ($$4) {
                $$4 = false;
                ObjectIterator $$5 = Object2ObjectMaps.fastIterator((Object2ObjectMap)$$2);
                while ($$5.hasNext()) {
                    Object2ObjectMap.Entry $$6 = (Object2ObjectMap.Entry)$$5.next();
                    Material $$7 = (Material)$$1.get((Object)((Reference)$$6.getValue()).target);
                    if ($$7 == null) continue;
                    $$1.put((Object)((String)$$6.getKey()), (Object)$$7);
                    $$5.remove();
                    $$4 = true;
                }
            }
            if (!$$2.isEmpty()) {
                LOGGER.warn("Unresolved texture references in {}:\n{}", (Object)$$02.debugName(), (Object)$$2.entrySet().stream().map($$0 -> "\t#" + (String)$$0.getKey() + "-> #" + ((Reference)$$0.getValue()).target + "\n").collect(Collectors.joining()));
            }
            return new TextureSlots((Map<String, Material>)$$1);
        }

        private static /* synthetic */ void lambda$resolve$0(Object2ObjectMap $$0, Object2ObjectMap $$1, String $$2, SlotContents $$3) {
            SlotContents slotContents = $$3;
            Objects.requireNonNull(slotContents);
            SlotContents $$4 = slotContents;
            int $$5 = 0;
            switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{Value.class, Reference.class}, (Object)$$4, (int)$$5)) {
                default: {
                    throw new MatchException(null, null);
                }
                case 0: {
                    Value $$6 = (Value)$$4;
                    $$0.remove((Object)$$2);
                    $$1.put((Object)$$2, (Object)$$6.material());
                    break;
                }
                case 1: {
                    Reference $$7 = (Reference)$$4;
                    $$1.remove((Object)$$2);
                    $$0.put((Object)$$2, (Object)$$7);
                }
            }
        }
    }

    static final class Reference
    extends Record
    implements SlotContents {
        final String target;

        Reference(String $$0) {
            this.target = $$0;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Reference.class, "target", "target"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Reference.class, "target", "target"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Reference.class, "target", "target"}, this, $$0);
        }

        public String target() {
            return this.target;
        }
    }

    record Value(Material material) implements SlotContents
    {
    }

    public static sealed interface SlotContents
    permits Value, Reference {
    }
}

