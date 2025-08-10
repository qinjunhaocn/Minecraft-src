/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.data;

import java.nio.file.Path;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class PackOutput {
    private final Path outputFolder;

    public PackOutput(Path $$0) {
        this.outputFolder = $$0;
    }

    public Path getOutputFolder() {
        return this.outputFolder;
    }

    public Path getOutputFolder(Target $$0) {
        return this.getOutputFolder().resolve($$0.directory);
    }

    public PathProvider createPathProvider(Target $$0, String $$1) {
        return new PathProvider(this, $$0, $$1);
    }

    public PathProvider createRegistryElementsPathProvider(ResourceKey<? extends Registry<?>> $$0) {
        return this.createPathProvider(Target.DATA_PACK, Registries.elementsDirPath($$0));
    }

    public PathProvider createRegistryTagsPathProvider(ResourceKey<? extends Registry<?>> $$0) {
        return this.createPathProvider(Target.DATA_PACK, Registries.tagsDirPath($$0));
    }

    public static final class Target
    extends Enum<Target> {
        public static final /* enum */ Target DATA_PACK = new Target("data");
        public static final /* enum */ Target RESOURCE_PACK = new Target("assets");
        public static final /* enum */ Target REPORTS = new Target("reports");
        final String directory;
        private static final /* synthetic */ Target[] $VALUES;

        public static Target[] values() {
            return (Target[])$VALUES.clone();
        }

        public static Target valueOf(String $$0) {
            return Enum.valueOf(Target.class, $$0);
        }

        private Target(String $$0) {
            this.directory = $$0;
        }

        private static /* synthetic */ Target[] a() {
            return new Target[]{DATA_PACK, RESOURCE_PACK, REPORTS};
        }

        static {
            $VALUES = Target.a();
        }
    }

    public static class PathProvider {
        private final Path root;
        private final String kind;

        PathProvider(PackOutput $$0, Target $$1, String $$2) {
            this.root = $$0.getOutputFolder($$1);
            this.kind = $$2;
        }

        public Path file(ResourceLocation $$0, String $$1) {
            return this.root.resolve($$0.getNamespace()).resolve(this.kind).resolve($$0.getPath() + "." + $$1);
        }

        public Path json(ResourceLocation $$0) {
            return this.root.resolve($$0.getNamespace()).resolve(this.kind).resolve($$0.getPath() + ".json");
        }

        public Path json(ResourceKey<?> $$0) {
            return this.root.resolve($$0.location().getNamespace()).resolve(this.kind).resolve($$0.location().getPath() + ".json");
        }
    }
}

