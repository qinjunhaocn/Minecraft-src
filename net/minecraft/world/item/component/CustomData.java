/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.MapDecoder
 *  com.mojang.serialization.MapEncoder
 *  com.mojang.serialization.MapLike
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.world.item.component;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapEncoder;
import com.mojang.serialization.MapLike;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import org.slf4j.Logger;

public final class CustomData {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final CustomData EMPTY = new CustomData(new CompoundTag());
    private static final String TYPE_TAG = "id";
    public static final Codec<CustomData> CODEC = Codec.withAlternative(CompoundTag.CODEC, TagParser.FLATTENED_CODEC).xmap(CustomData::new, $$0 -> $$0.tag);
    public static final Codec<CustomData> CODEC_WITH_ID = CODEC.validate($$0 -> $$0.getUnsafe().getString(TYPE_TAG).isPresent() ? DataResult.success((Object)$$0) : DataResult.error(() -> "Missing id for entity in: " + String.valueOf($$0)));
    @Deprecated
    public static final StreamCodec<ByteBuf, CustomData> STREAM_CODEC = ByteBufCodecs.COMPOUND_TAG.map(CustomData::new, $$0 -> $$0.tag);
    private final CompoundTag tag;

    private CustomData(CompoundTag $$0) {
        this.tag = $$0;
    }

    public static CustomData of(CompoundTag $$0) {
        return new CustomData($$0.copy());
    }

    public boolean matchedBy(CompoundTag $$0) {
        return NbtUtils.compareNbt($$0, this.tag, true);
    }

    public static void update(DataComponentType<CustomData> $$0, ItemStack $$1, Consumer<CompoundTag> $$2) {
        CustomData $$3 = $$1.getOrDefault($$0, EMPTY).update($$2);
        if ($$3.tag.isEmpty()) {
            $$1.remove($$0);
        } else {
            $$1.set($$0, $$3);
        }
    }

    public static void set(DataComponentType<CustomData> $$0, ItemStack $$1, CompoundTag $$2) {
        if (!$$2.isEmpty()) {
            $$1.set($$0, CustomData.of($$2));
        } else {
            $$1.remove($$0);
        }
    }

    public CustomData update(Consumer<CompoundTag> $$0) {
        CompoundTag $$1 = this.tag.copy();
        $$0.accept($$1);
        return new CustomData($$1);
    }

    @Nullable
    public ResourceLocation parseEntityId() {
        return this.tag.read(TYPE_TAG, ResourceLocation.CODEC).orElse(null);
    }

    @Nullable
    public <T> T parseEntityType(HolderLookup.Provider $$0, ResourceKey<? extends Registry<T>> $$1) {
        ResourceLocation $$22 = this.parseEntityId();
        if ($$22 == null) {
            return null;
        }
        return $$0.lookup($$1).flatMap($$2 -> $$2.get(ResourceKey.create($$1, $$22))).map(Holder::value).orElse(null);
    }

    public void loadInto(Entity $$0) {
        try (ProblemReporter.ScopedCollector $$1 = new ProblemReporter.ScopedCollector($$0.problemPath(), LOGGER);){
            TagValueOutput $$2 = TagValueOutput.createWithContext($$1, $$0.registryAccess());
            $$0.saveWithoutId($$2);
            CompoundTag $$3 = $$2.buildResult();
            UUID $$4 = $$0.getUUID();
            $$3.merge(this.tag);
            $$0.load(TagValueInput.create((ProblemReporter)$$1, (HolderLookup.Provider)$$0.registryAccess(), $$3));
            $$0.setUUID($$4);
        }
    }

    /*
     * Exception decompiling
     */
    public boolean loadInto(BlockEntity $$0, HolderLookup.Provider $$1) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [5[CATCHBLOCK]], but top level block is 2[TRYBLOCK]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:538)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         *     at async.DecompilerRunnable.cfrDecompilation(DecompilerRunnable.java:348)
         *     at async.DecompilerRunnable.call(DecompilerRunnable.java:309)
         *     at async.DecompilerRunnable.call(DecompilerRunnable.java:31)
         *     at java.util.concurrent.FutureTask.run(FutureTask.java:266)
         *     at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
         *     at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
         *     at java.lang.Thread.run(Thread.java:750)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    public <T> DataResult<CustomData> update(DynamicOps<Tag> $$02, MapEncoder<T> $$1, T $$2) {
        return $$1.encode($$2, $$02, $$02.mapBuilder()).build((Object)this.tag).map($$0 -> new CustomData((CompoundTag)$$0));
    }

    public <T> DataResult<T> read(MapDecoder<T> $$0) {
        return this.read(NbtOps.INSTANCE, $$0);
    }

    public <T> DataResult<T> read(DynamicOps<Tag> $$0, MapDecoder<T> $$1) {
        MapLike $$2 = (MapLike)$$0.getMap((Object)this.tag).getOrThrow();
        return $$1.decode($$0, $$2);
    }

    public int size() {
        return this.tag.size();
    }

    public boolean isEmpty() {
        return this.tag.isEmpty();
    }

    public CompoundTag copyTag() {
        return this.tag.copy();
    }

    public boolean contains(String $$0) {
        return this.tag.contains($$0);
    }

    public boolean equals(Object $$0) {
        if ($$0 == this) {
            return true;
        }
        if ($$0 instanceof CustomData) {
            CustomData $$1 = (CustomData)$$0;
            return this.tag.equals($$1.tag);
        }
        return false;
    }

    public int hashCode() {
        return this.tag.hashCode();
    }

    public String toString() {
        return this.tag.toString();
    }

    @Deprecated
    public CompoundTag getUnsafe() {
        return this.tag;
    }

    private static /* synthetic */ String lambda$loadInto$5() {
        return "(rollback)";
    }
}

