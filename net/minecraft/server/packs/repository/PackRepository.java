/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.server.packs.repository;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.world.flag.FeatureFlagSet;

public class PackRepository {
    private final Set<RepositorySource> sources;
    private Map<String, Pack> available = ImmutableMap.of();
    private List<Pack> selected = ImmutableList.of();

    public PackRepository(RepositorySource ... $$0) {
        this.sources = ImmutableSet.copyOf($$0);
    }

    public static String displayPackList(Collection<Pack> $$02) {
        return $$02.stream().map($$0 -> $$0.getId() + ($$0.getCompatibility().isCompatible() ? "" : " (incompatible)")).collect(Collectors.joining(", "));
    }

    public void reload() {
        List $$0 = this.selected.stream().map(Pack::getId).collect(ImmutableList.toImmutableList());
        this.available = this.discoverAvailable();
        this.selected = this.rebuildSelected($$0);
    }

    private Map<String, Pack> discoverAvailable() {
        TreeMap $$0 = Maps.newTreeMap();
        for (RepositorySource $$12 : this.sources) {
            $$12.loadPacks($$1 -> $$0.put($$1.getId(), $$1));
        }
        return ImmutableMap.copyOf($$0);
    }

    public boolean isAbleToClearAnyPack() {
        List<Pack> $$0 = this.rebuildSelected(List.of());
        return !this.selected.equals($$0);
    }

    public void setSelected(Collection<String> $$0) {
        this.selected = this.rebuildSelected($$0);
    }

    public boolean addPack(String $$0) {
        Pack $$1 = this.available.get($$0);
        if ($$1 != null && !this.selected.contains($$1)) {
            ArrayList<Pack> $$2 = Lists.newArrayList(this.selected);
            $$2.add($$1);
            this.selected = $$2;
            return true;
        }
        return false;
    }

    public boolean removePack(String $$0) {
        Pack $$1 = this.available.get($$0);
        if ($$1 != null && this.selected.contains($$1)) {
            ArrayList<Pack> $$2 = Lists.newArrayList(this.selected);
            $$2.remove($$1);
            this.selected = $$2;
            return true;
        }
        return false;
    }

    private List<Pack> rebuildSelected(Collection<String> $$0) {
        List $$1 = this.getAvailablePacks($$0).collect(Util.toMutableList());
        for (Pack $$2 : this.available.values()) {
            if (!$$2.isRequired() || $$1.contains($$2)) continue;
            $$2.getDefaultPosition().insert($$1, $$2, Pack::selectionConfig, false);
        }
        return ImmutableList.copyOf($$1);
    }

    private Stream<Pack> getAvailablePacks(Collection<String> $$0) {
        return $$0.stream().map(this.available::get).filter(Objects::nonNull);
    }

    public Collection<String> getAvailableIds() {
        return this.available.keySet();
    }

    public Collection<Pack> getAvailablePacks() {
        return this.available.values();
    }

    public Collection<String> getSelectedIds() {
        return this.selected.stream().map(Pack::getId).collect(ImmutableSet.toImmutableSet());
    }

    public FeatureFlagSet getRequestedFeatureFlags() {
        return this.getSelectedPacks().stream().map(Pack::getRequestedFeatures).reduce(FeatureFlagSet::join).orElse(FeatureFlagSet.of());
    }

    public Collection<Pack> getSelectedPacks() {
        return this.selected;
    }

    @Nullable
    public Pack getPack(String $$0) {
        return this.available.get($$0);
    }

    public boolean isAvailable(String $$0) {
        return this.available.containsKey($$0);
    }

    public List<PackResources> openAllSelected() {
        return this.selected.stream().map(Pack::open).collect(ImmutableList.toImmutableList());
    }
}

