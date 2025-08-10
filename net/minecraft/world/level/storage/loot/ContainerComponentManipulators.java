/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 */
package net.minecraft.world.level.storage.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.storage.loot.ContainerComponentManipulator;

public interface ContainerComponentManipulators {
    public static final ContainerComponentManipulator<ItemContainerContents> CONTAINER = new ContainerComponentManipulator<ItemContainerContents>(){

        @Override
        public DataComponentType<ItemContainerContents> type() {
            return DataComponents.CONTAINER;
        }

        @Override
        public Stream<ItemStack> getContents(ItemContainerContents $$0) {
            return $$0.stream();
        }

        @Override
        public ItemContainerContents empty() {
            return ItemContainerContents.EMPTY;
        }

        @Override
        public ItemContainerContents setContents(ItemContainerContents $$0, Stream<ItemStack> $$1) {
            return ItemContainerContents.fromItems($$1.toList());
        }

        @Override
        public /* synthetic */ Object empty() {
            return this.empty();
        }
    };
    public static final ContainerComponentManipulator<BundleContents> BUNDLE_CONTENTS = new ContainerComponentManipulator<BundleContents>(){

        @Override
        public DataComponentType<BundleContents> type() {
            return DataComponents.BUNDLE_CONTENTS;
        }

        @Override
        public BundleContents empty() {
            return BundleContents.EMPTY;
        }

        @Override
        public Stream<ItemStack> getContents(BundleContents $$0) {
            return $$0.itemCopyStream();
        }

        @Override
        public BundleContents setContents(BundleContents $$0, Stream<ItemStack> $$1) {
            BundleContents.Mutable $$2 = new BundleContents.Mutable($$0).clearItems();
            $$1.forEach($$2::tryInsert);
            return $$2.toImmutable();
        }

        @Override
        public /* synthetic */ Object empty() {
            return this.empty();
        }
    };
    public static final ContainerComponentManipulator<ChargedProjectiles> CHARGED_PROJECTILES = new ContainerComponentManipulator<ChargedProjectiles>(){

        @Override
        public DataComponentType<ChargedProjectiles> type() {
            return DataComponents.CHARGED_PROJECTILES;
        }

        @Override
        public ChargedProjectiles empty() {
            return ChargedProjectiles.EMPTY;
        }

        @Override
        public Stream<ItemStack> getContents(ChargedProjectiles $$0) {
            return $$0.getItems().stream();
        }

        @Override
        public ChargedProjectiles setContents(ChargedProjectiles $$0, Stream<ItemStack> $$1) {
            return ChargedProjectiles.of($$1.toList());
        }

        @Override
        public /* synthetic */ Object empty() {
            return this.empty();
        }
    };
    public static final Map<DataComponentType<?>, ContainerComponentManipulator<?>> ALL_MANIPULATORS = Stream.of(CONTAINER, BUNDLE_CONTENTS, CHARGED_PROJECTILES).collect(Collectors.toMap(ContainerComponentManipulator::type, $$0 -> $$0));
    public static final Codec<ContainerComponentManipulator<?>> CODEC = BuiltInRegistries.DATA_COMPONENT_TYPE.byNameCodec().comapFlatMap($$0 -> {
        ContainerComponentManipulator<?> $$1 = ALL_MANIPULATORS.get($$0);
        return $$1 != null ? DataResult.success($$1) : DataResult.error(() -> "No items in component");
    }, ContainerComponentManipulator::type);
}

