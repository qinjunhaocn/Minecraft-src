/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.Object2ObjectFunction
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.client.resources.model;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectFunction;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.MissingBlockModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.QuadCollection;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.UnbakedGeometry;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class ModelDiscovery {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Object2ObjectMap<ResourceLocation, ModelWrapper> modelWrappers = new Object2ObjectOpenHashMap();
    private final ModelWrapper missingModel;
    private final Object2ObjectFunction<ResourceLocation, ModelWrapper> uncachedResolver;
    private final ResolvableModel.Resolver resolver;
    private final Queue<ModelWrapper> parentDiscoveryQueue = new ArrayDeque<ModelWrapper>();

    public ModelDiscovery(Map<ResourceLocation, UnbakedModel> $$0, UnbakedModel $$12) {
        this.missingModel = new ModelWrapper(MissingBlockModel.LOCATION, $$12, true);
        this.modelWrappers.put((Object)MissingBlockModel.LOCATION, (Object)this.missingModel);
        this.uncachedResolver = $$1 -> {
            ResourceLocation $$2 = (ResourceLocation)$$1;
            UnbakedModel $$3 = (UnbakedModel)$$0.get($$2);
            if ($$3 == null) {
                LOGGER.warn("Missing block model: {}", (Object)$$2);
                return this.missingModel;
            }
            return this.createAndQueueWrapper($$2, $$3);
        };
        this.resolver = this::getOrCreateModel;
    }

    private static boolean isRoot(UnbakedModel $$0) {
        return $$0.parent() == null;
    }

    private ModelWrapper getOrCreateModel(ResourceLocation $$0) {
        return (ModelWrapper)this.modelWrappers.computeIfAbsent((Object)$$0, this.uncachedResolver);
    }

    private ModelWrapper createAndQueueWrapper(ResourceLocation $$0, UnbakedModel $$1) {
        boolean $$2 = ModelDiscovery.isRoot($$1);
        ModelWrapper $$3 = new ModelWrapper($$0, $$1, $$2);
        if (!$$2) {
            this.parentDiscoveryQueue.add($$3);
        }
        return $$3;
    }

    public void addRoot(ResolvableModel $$0) {
        $$0.resolveDependencies(this.resolver);
    }

    public void addSpecialModel(ResourceLocation $$0, UnbakedModel $$1) {
        if (!ModelDiscovery.isRoot($$1)) {
            LOGGER.warn("Trying to add non-root special model {}, ignoring", (Object)$$0);
            return;
        }
        ModelWrapper $$2 = (ModelWrapper)this.modelWrappers.put((Object)$$0, (Object)this.createAndQueueWrapper($$0, $$1));
        if ($$2 != null) {
            LOGGER.warn("Duplicate special model {}", (Object)$$0);
        }
    }

    public ResolvedModel missingModel() {
        return this.missingModel;
    }

    public Map<ResourceLocation, ResolvedModel> resolve() {
        ArrayList<ModelWrapper> $$0 = new ArrayList<ModelWrapper>();
        this.discoverDependencies($$0);
        ModelDiscovery.propagateValidity($$0);
        ImmutableMap.Builder $$12 = ImmutableMap.builder();
        this.modelWrappers.forEach(($$1, $$2) -> {
            if ($$2.valid) {
                $$12.put($$1, $$2);
            } else {
                LOGGER.warn("Model {} ignored due to cyclic dependency", $$1);
            }
        });
        return $$12.build();
    }

    private void discoverDependencies(List<ModelWrapper> $$0) {
        ModelWrapper $$1;
        while (($$1 = this.parentDiscoveryQueue.poll()) != null) {
            ModelWrapper $$3;
            ResourceLocation $$2 = Objects.requireNonNull($$1.wrapped.parent());
            $$1.parent = $$3 = this.getOrCreateModel($$2);
            if ($$3.valid) {
                $$1.valid = true;
                continue;
            }
            $$0.add($$1);
        }
    }

    private static void propagateValidity(List<ModelWrapper> $$0) {
        boolean $$1 = true;
        while ($$1) {
            $$1 = false;
            Iterator<ModelWrapper> $$2 = $$0.iterator();
            while ($$2.hasNext()) {
                ModelWrapper $$3 = $$2.next();
                if (!Objects.requireNonNull($$3.parent).valid) continue;
                $$3.valid = true;
                $$2.remove();
                $$1 = true;
            }
        }
    }

    static class ModelWrapper
    implements ResolvedModel {
        private static final Slot<Boolean> KEY_AMBIENT_OCCLUSION = ModelWrapper.slot(0);
        private static final Slot<UnbakedModel.GuiLight> KEY_GUI_LIGHT = ModelWrapper.slot(1);
        private static final Slot<UnbakedGeometry> KEY_GEOMETRY = ModelWrapper.slot(2);
        private static final Slot<ItemTransforms> KEY_TRANSFORMS = ModelWrapper.slot(3);
        private static final Slot<TextureSlots> KEY_TEXTURE_SLOTS = ModelWrapper.slot(4);
        private static final Slot<TextureAtlasSprite> KEY_PARTICLE_SPRITE = ModelWrapper.slot(5);
        private static final Slot<QuadCollection> KEY_DEFAULT_GEOMETRY = ModelWrapper.slot(6);
        private static final int SLOT_COUNT = 7;
        private final ResourceLocation id;
        boolean valid;
        @Nullable
        ModelWrapper parent;
        final UnbakedModel wrapped;
        private final AtomicReferenceArray<Object> fixedSlots = new AtomicReferenceArray(7);
        private final Map<ModelState, QuadCollection> modelBakeCache = new ConcurrentHashMap<ModelState, QuadCollection>();

        private static <T> Slot<T> slot(int $$0) {
            Objects.checkIndex((int)$$0, (int)7);
            return new Slot($$0);
        }

        ModelWrapper(ResourceLocation $$0, UnbakedModel $$1, boolean $$2) {
            this.id = $$0;
            this.wrapped = $$1;
            this.valid = $$2;
        }

        @Override
        public UnbakedModel wrapped() {
            return this.wrapped;
        }

        @Override
        @Nullable
        public ResolvedModel parent() {
            return this.parent;
        }

        @Override
        public String debugName() {
            return this.id.toString();
        }

        @Nullable
        private <T> T getSlot(Slot<T> $$0) {
            return (T)this.fixedSlots.get($$0.index);
        }

        private <T> T updateSlot(Slot<T> $$0, T $$1) {
            Object $$2 = this.fixedSlots.compareAndExchange($$0.index, null, $$1);
            if ($$2 == null) {
                return $$1;
            }
            return (T)$$2;
        }

        private <T> T getSimpleProperty(Slot<T> $$0, Function<ResolvedModel, T> $$1) {
            T $$2 = this.getSlot($$0);
            if ($$2 != null) {
                return $$2;
            }
            return this.updateSlot($$0, $$1.apply(this));
        }

        @Override
        public boolean getTopAmbientOcclusion() {
            return this.getSimpleProperty(KEY_AMBIENT_OCCLUSION, ResolvedModel::findTopAmbientOcclusion);
        }

        @Override
        public UnbakedModel.GuiLight getTopGuiLight() {
            return this.getSimpleProperty(KEY_GUI_LIGHT, ResolvedModel::findTopGuiLight);
        }

        @Override
        public ItemTransforms getTopTransforms() {
            return this.getSimpleProperty(KEY_TRANSFORMS, ResolvedModel::findTopTransforms);
        }

        @Override
        public UnbakedGeometry getTopGeometry() {
            return this.getSimpleProperty(KEY_GEOMETRY, ResolvedModel::findTopGeometry);
        }

        @Override
        public TextureSlots getTopTextureSlots() {
            return this.getSimpleProperty(KEY_TEXTURE_SLOTS, ResolvedModel::findTopTextureSlots);
        }

        @Override
        public TextureAtlasSprite resolveParticleSprite(TextureSlots $$0, ModelBaker $$1) {
            TextureAtlasSprite $$2 = this.getSlot(KEY_PARTICLE_SPRITE);
            if ($$2 != null) {
                return $$2;
            }
            return this.updateSlot(KEY_PARTICLE_SPRITE, ResolvedModel.resolveParticleSprite($$0, $$1, this));
        }

        private QuadCollection bakeDefaultState(TextureSlots $$0, ModelBaker $$1, ModelState $$2) {
            QuadCollection $$3 = this.getSlot(KEY_DEFAULT_GEOMETRY);
            if ($$3 != null) {
                return $$3;
            }
            return this.updateSlot(KEY_DEFAULT_GEOMETRY, this.getTopGeometry().bake($$0, $$1, $$2, this));
        }

        @Override
        public QuadCollection bakeTopGeometry(TextureSlots $$0, ModelBaker $$1, ModelState $$22) {
            if ($$22 == BlockModelRotation.X0_Y0) {
                return this.bakeDefaultState($$0, $$1, $$22);
            }
            return this.modelBakeCache.computeIfAbsent($$22, $$2 -> {
                UnbakedGeometry $$3 = this.getTopGeometry();
                return $$3.bake($$0, $$1, (ModelState)$$2, this);
            });
        }
    }

    static final class Slot<T>
    extends Record {
        final int index;

        Slot(int $$0) {
            this.index = $$0;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Slot.class, "index", "index"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Slot.class, "index", "index"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Slot.class, "index", "index"}, this, $$0);
        }

        public int index() {
            return this.index;
        }
    }
}

