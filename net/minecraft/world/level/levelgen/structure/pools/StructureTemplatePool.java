/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 */
package net.minecraft.world.level.levelgen.structure.pools;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.pools.EmptyPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.GravityProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.apache.commons.lang3.mutable.MutableObject;

public class StructureTemplatePool {
    private static final int SIZE_UNSET = Integer.MIN_VALUE;
    private static final MutableObject<Codec<Holder<StructureTemplatePool>>> CODEC_REFERENCE = new MutableObject();
    public static final Codec<StructureTemplatePool> DIRECT_CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)Codec.lazyInitialized(CODEC_REFERENCE::getValue).fieldOf("fallback").forGetter(StructureTemplatePool::getFallback), (App)Codec.mapPair((MapCodec)StructurePoolElement.CODEC.fieldOf("element"), (MapCodec)Codec.intRange((int)1, (int)150).fieldOf("weight")).codec().listOf().fieldOf("elements").forGetter($$0 -> $$0.rawTemplates)).apply((Applicative)$$02, StructureTemplatePool::new));
    public static final Codec<Holder<StructureTemplatePool>> CODEC = Util.make(RegistryFileCodec.create(Registries.TEMPLATE_POOL, DIRECT_CODEC), CODEC_REFERENCE::setValue);
    private final List<Pair<StructurePoolElement, Integer>> rawTemplates;
    private final ObjectArrayList<StructurePoolElement> templates;
    private final Holder<StructureTemplatePool> fallback;
    private int maxSize = Integer.MIN_VALUE;

    public StructureTemplatePool(Holder<StructureTemplatePool> $$0, List<Pair<StructurePoolElement, Integer>> $$1) {
        this.rawTemplates = $$1;
        this.templates = new ObjectArrayList();
        for (Pair<StructurePoolElement, Integer> $$2 : $$1) {
            StructurePoolElement $$3 = (StructurePoolElement)$$2.getFirst();
            for (int $$4 = 0; $$4 < (Integer)$$2.getSecond(); ++$$4) {
                this.templates.add((Object)$$3);
            }
        }
        this.fallback = $$0;
    }

    public StructureTemplatePool(Holder<StructureTemplatePool> $$0, List<Pair<Function<Projection, ? extends StructurePoolElement>, Integer>> $$1, Projection $$2) {
        this.rawTemplates = Lists.newArrayList();
        this.templates = new ObjectArrayList();
        for (Pair<Function<Projection, ? extends StructurePoolElement>, Integer> $$3 : $$1) {
            StructurePoolElement $$4 = (StructurePoolElement)((Function)$$3.getFirst()).apply($$2);
            this.rawTemplates.add((Pair<StructurePoolElement, Integer>)Pair.of((Object)$$4, (Object)((Integer)$$3.getSecond())));
            for (int $$5 = 0; $$5 < (Integer)$$3.getSecond(); ++$$5) {
                this.templates.add((Object)$$4);
            }
        }
        this.fallback = $$0;
    }

    public int getMaxSize(StructureTemplateManager $$02) {
        if (this.maxSize == Integer.MIN_VALUE) {
            this.maxSize = this.templates.stream().filter($$0 -> $$0 != EmptyPoolElement.INSTANCE).mapToInt($$1 -> $$1.getBoundingBox($$02, BlockPos.ZERO, Rotation.NONE).getYSpan()).max().orElse(0);
        }
        return this.maxSize;
    }

    @VisibleForTesting
    public List<Pair<StructurePoolElement, Integer>> getTemplates() {
        return this.rawTemplates;
    }

    public Holder<StructureTemplatePool> getFallback() {
        return this.fallback;
    }

    public StructurePoolElement getRandomTemplate(RandomSource $$0) {
        if (this.templates.isEmpty()) {
            return EmptyPoolElement.INSTANCE;
        }
        return (StructurePoolElement)this.templates.get($$0.nextInt(this.templates.size()));
    }

    public List<StructurePoolElement> getShuffledTemplates(RandomSource $$0) {
        return Util.shuffledCopy(this.templates, $$0);
    }

    public int size() {
        return this.templates.size();
    }

    public static final class Projection
    extends Enum<Projection>
    implements StringRepresentable {
        public static final /* enum */ Projection TERRAIN_MATCHING = new Projection("terrain_matching", ImmutableList.of(new GravityProcessor(Heightmap.Types.WORLD_SURFACE_WG, -1)));
        public static final /* enum */ Projection RIGID = new Projection("rigid", ImmutableList.of());
        public static final StringRepresentable.EnumCodec<Projection> CODEC;
        private final String name;
        private final ImmutableList<StructureProcessor> processors;
        private static final /* synthetic */ Projection[] $VALUES;

        public static Projection[] values() {
            return (Projection[])$VALUES.clone();
        }

        public static Projection valueOf(String $$0) {
            return Enum.valueOf(Projection.class, $$0);
        }

        private Projection(String $$0, ImmutableList<StructureProcessor> $$1) {
            this.name = $$0;
            this.processors = $$1;
        }

        public String getName() {
            return this.name;
        }

        public static Projection byName(String $$0) {
            return CODEC.byName($$0);
        }

        public ImmutableList<StructureProcessor> getProcessors() {
            return this.processors;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        private static /* synthetic */ Projection[] d() {
            return new Projection[]{TERRAIN_MATCHING, RIGID};
        }

        static {
            $VALUES = Projection.d();
            CODEC = StringRepresentable.fromEnum(Projection::values);
        }
    }
}

