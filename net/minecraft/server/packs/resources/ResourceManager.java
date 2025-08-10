/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.server.packs.resources;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;

public interface ResourceManager
extends ResourceProvider {
    public Set<String> getNamespaces();

    public List<Resource> getResourceStack(ResourceLocation var1);

    public Map<ResourceLocation, Resource> listResources(String var1, Predicate<ResourceLocation> var2);

    public Map<ResourceLocation, List<Resource>> listResourceStacks(String var1, Predicate<ResourceLocation> var2);

    public Stream<PackResources> listPacks();

    public static final class Empty
    extends Enum<Empty>
    implements ResourceManager {
        public static final /* enum */ Empty INSTANCE = new Empty();
        private static final /* synthetic */ Empty[] $VALUES;

        public static Empty[] values() {
            return (Empty[])$VALUES.clone();
        }

        public static Empty valueOf(String $$0) {
            return Enum.valueOf(Empty.class, $$0);
        }

        @Override
        public Set<String> getNamespaces() {
            return Set.of();
        }

        @Override
        public Optional<Resource> getResource(ResourceLocation $$0) {
            return Optional.empty();
        }

        @Override
        public List<Resource> getResourceStack(ResourceLocation $$0) {
            return List.of();
        }

        @Override
        public Map<ResourceLocation, Resource> listResources(String $$0, Predicate<ResourceLocation> $$1) {
            return Map.of();
        }

        @Override
        public Map<ResourceLocation, List<Resource>> listResourceStacks(String $$0, Predicate<ResourceLocation> $$1) {
            return Map.of();
        }

        @Override
        public Stream<PackResources> listPacks() {
            return Stream.of(new PackResources[0]);
        }

        private static /* synthetic */ Empty[] c() {
            return new Empty[]{INSTANCE};
        }

        static {
            $VALUES = Empty.c();
        }
    }
}

