/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 */
package net.minecraft.tags;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.lang.invoke.LambdaMetafactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.RegistryLayer;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagLoader;

public class TagNetworkSerialization {
    public static Map<ResourceKey<? extends Registry<?>>, NetworkPayload> serializeTagsToNetwork(LayeredRegistryAccess<RegistryLayer> $$02) {
        return RegistrySynchronization.networkSafeRegistries($$02).map($$0 -> Pair.of($$0.key(), (Object)TagNetworkSerialization.serializeToNetwork($$0.value()))).filter($$0 -> !((NetworkPayload)$$0.getSecond()).isEmpty()).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }

    private static <T> NetworkPayload serializeToNetwork(Registry<T> $$0) {
        HashMap<ResourceLocation, IntList> $$1 = new HashMap<ResourceLocation, IntList>();
        $$0.getTags().forEach($$2 -> {
            IntArrayList $$3 = new IntArrayList($$2.size());
            for (Holder $$4 : $$2) {
                if ($$4.kind() != Holder.Kind.REFERENCE) {
                    throw new IllegalStateException("Can't serialize unregistered value " + String.valueOf($$4));
                }
                $$3.add($$0.getId($$4.value()));
            }
            $$1.put($$2.key().location(), (IntList)$$3);
        });
        return new NetworkPayload($$1);
    }

    static <T> TagLoader.LoadResult<T> deserializeTagsFromNetwork(Registry<T> $$0, NetworkPayload $$1) {
        ResourceKey $$2 = $$0.key();
        HashMap $$32 = new HashMap();
        $$1.tags.forEach(($$3, $$4) -> {
            TagKey $$5 = TagKey.create($$2, $$3);
            List $$6 = (List)$$4.intStream().mapToObj($$0::get).flatMap((Function<Optional, Stream>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Ljava/lang/Object;, stream(), (Ljava/util/Optional;)Ljava/util/stream/Stream;)()).collect(Collectors.toUnmodifiableList());
            $$32.put($$5, $$6);
        });
        return new TagLoader.LoadResult<T>($$2, $$32);
    }

    public static final class NetworkPayload {
        public static final NetworkPayload EMPTY = new NetworkPayload(Map.of());
        final Map<ResourceLocation, IntList> tags;

        NetworkPayload(Map<ResourceLocation, IntList> $$0) {
            this.tags = $$0;
        }

        public void write(FriendlyByteBuf $$0) {
            $$0.writeMap(this.tags, FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::writeIntIdList);
        }

        public static NetworkPayload read(FriendlyByteBuf $$0) {
            return new NetworkPayload($$0.readMap(FriendlyByteBuf::readResourceLocation, FriendlyByteBuf::readIntIdList));
        }

        public boolean isEmpty() {
            return this.tags.isEmpty();
        }

        public int size() {
            return this.tags.size();
        }

        public <T> TagLoader.LoadResult<T> resolve(Registry<T> $$0) {
            return TagNetworkSerialization.deserializeTagsFromNetwork($$0, this);
        }
    }
}

