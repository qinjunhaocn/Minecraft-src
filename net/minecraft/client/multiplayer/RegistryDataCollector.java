/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.multiplayer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.multiplayer.ClientRegistryLayer;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.tags.TagLoader;
import net.minecraft.tags.TagNetworkSerialization;

public class RegistryDataCollector {
    @Nullable
    private ContentsCollector contentsCollector;
    @Nullable
    private TagCollector tagCollector;

    public void appendContents(ResourceKey<? extends Registry<?>> $$0, List<RegistrySynchronization.PackedRegistryEntry> $$1) {
        if (this.contentsCollector == null) {
            this.contentsCollector = new ContentsCollector();
        }
        this.contentsCollector.append($$0, $$1);
    }

    public void appendTags(Map<ResourceKey<? extends Registry<?>>, TagNetworkSerialization.NetworkPayload> $$0) {
        if (this.tagCollector == null) {
            this.tagCollector = new TagCollector();
        }
        $$0.forEach(this.tagCollector::append);
    }

    private static <T> Registry.PendingTags<T> resolveRegistryTags(RegistryAccess.Frozen $$0, ResourceKey<? extends Registry<? extends T>> $$1, TagNetworkSerialization.NetworkPayload $$2) {
        HolderLookup.RegistryLookup $$3 = $$0.lookupOrThrow((ResourceKey)$$1);
        return $$3.prepareTagReload($$2.resolve($$3));
    }

    /*
     * WARNING - void declaration
     */
    private RegistryAccess loadNewElementsAndTags(ResourceProvider $$0, ContentsCollector $$12, boolean $$22) {
        void $$11;
        LayeredRegistryAccess<ClientRegistryLayer> $$3 = ClientRegistryLayer.createRegistryAccess();
        RegistryAccess.Frozen $$42 = $$3.getAccessForLoading(ClientRegistryLayer.REMOTE);
        HashMap $$52 = new HashMap();
        $$12.elements.forEach(($$1, $$2) -> $$52.put((ResourceKey<? extends Registry<?>>)$$1, new RegistryDataLoader.NetworkedRegistryData((List<RegistrySynchronization.PackedRegistryEntry>)$$2, TagNetworkSerialization.NetworkPayload.EMPTY)));
        ArrayList $$6 = new ArrayList();
        if (this.tagCollector != null) {
            this.tagCollector.forEach(($$4, $$5) -> {
                if ($$5.isEmpty()) {
                    return;
                }
                if (RegistrySynchronization.isNetworkable($$4)) {
                    $$52.compute((ResourceKey<? extends Registry<?>>)$$4, ($$1, $$2) -> {
                        RegistryAccess.Frozen $$42 = $$2 != null ? $$2.elements() : List.of();
                        return new RegistryDataLoader.NetworkedRegistryData((List<RegistrySynchronization.PackedRegistryEntry>)((Object)$$42), (TagNetworkSerialization.NetworkPayload)$$5);
                    });
                } else if (!$$22) {
                    $$6.add(RegistryDataCollector.resolveRegistryTags($$42, $$4, $$5));
                }
            });
        }
        List<HolderLookup.RegistryLookup<?>> $$7 = TagLoader.buildUpdatedLookups($$42, $$6);
        try {
            RegistryAccess.Frozen $$8 = RegistryDataLoader.load($$52, $$0, $$7, RegistryDataLoader.SYNCHRONIZED_REGISTRIES).freeze();
        } catch (Exception $$9) {
            CrashReport $$10 = CrashReport.forThrowable($$9, "Network Registry Load");
            RegistryDataCollector.addCrashDetails($$10, $$52, $$6);
            throw new ReportedException($$10);
        }
        RegistryAccess.Frozen $$122 = $$3.a(ClientRegistryLayer.REMOTE, new RegistryAccess.Frozen[]{$$11}).compositeAccess();
        $$6.forEach(Registry.PendingTags::apply);
        return $$122;
    }

    private static void addCrashDetails(CrashReport $$0, Map<ResourceKey<? extends Registry<?>>, RegistryDataLoader.NetworkedRegistryData> $$1, List<Registry.PendingTags<?>> $$2) {
        CrashReportCategory $$3 = $$0.addCategory("Received Elements and Tags");
        $$3.setDetail("Dynamic Registries", () -> $$1.entrySet().stream().sorted(Comparator.comparing($$0 -> ((ResourceKey)$$0.getKey()).location())).map($$0 -> String.format(Locale.ROOT, "\n\t\t%s: elements=%d tags=%d", ((ResourceKey)$$0.getKey()).location(), ((RegistryDataLoader.NetworkedRegistryData)((Object)((Object)((Object)$$0.getValue())))).elements().size(), ((RegistryDataLoader.NetworkedRegistryData)((Object)((Object)((Object)$$0.getValue())))).tags().size())).collect(Collectors.joining()));
        $$3.setDetail("Static Registries", () -> $$2.stream().sorted(Comparator.comparing($$0 -> $$0.key().location())).map($$0 -> String.format(Locale.ROOT, "\n\t\t%s: tags=%d", $$0.key().location(), $$0.size())).collect(Collectors.joining()));
    }

    private void loadOnlyTags(TagCollector $$0, RegistryAccess.Frozen $$1, boolean $$22) {
        $$0.forEach(($$2, $$3) -> {
            if ($$22 || RegistrySynchronization.isNetworkable($$2)) {
                RegistryDataCollector.resolveRegistryTags($$1, $$2, $$3).apply();
            }
        });
    }

    public RegistryAccess.Frozen collectGameRegistries(ResourceProvider $$0, RegistryAccess.Frozen $$1, boolean $$2) {
        RegistryAccess.Frozen $$4;
        if (this.contentsCollector != null) {
            RegistryAccess $$3 = this.loadNewElementsAndTags($$0, this.contentsCollector, $$2);
        } else {
            if (this.tagCollector != null) {
                this.loadOnlyTags(this.tagCollector, $$1, !$$2);
            }
            $$4 = $$1;
        }
        return $$4.freeze();
    }

    static class ContentsCollector {
        final Map<ResourceKey<? extends Registry<?>>, List<RegistrySynchronization.PackedRegistryEntry>> elements = new HashMap();

        ContentsCollector() {
        }

        public void append(ResourceKey<? extends Registry<?>> $$02, List<RegistrySynchronization.PackedRegistryEntry> $$1) {
            this.elements.computeIfAbsent($$02, $$0 -> new ArrayList()).addAll($$1);
        }
    }

    static class TagCollector {
        private final Map<ResourceKey<? extends Registry<?>>, TagNetworkSerialization.NetworkPayload> tags = new HashMap();

        TagCollector() {
        }

        public void append(ResourceKey<? extends Registry<?>> $$0, TagNetworkSerialization.NetworkPayload $$1) {
            this.tags.put($$0, $$1);
        }

        public void forEach(BiConsumer<? super ResourceKey<? extends Registry<?>>, ? super TagNetworkSerialization.NetworkPayload> $$0) {
            this.tags.forEach($$0);
        }
    }
}

