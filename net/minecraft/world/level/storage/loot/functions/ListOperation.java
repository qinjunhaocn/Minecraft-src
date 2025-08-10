/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import org.slf4j.Logger;

public interface ListOperation {
    public static final MapCodec<ListOperation> UNLIMITED_CODEC = ListOperation.codec(Integer.MAX_VALUE);

    public static MapCodec<ListOperation> codec(int $$02) {
        return Type.CODEC.dispatchMap("mode", ListOperation::mode, $$0 -> $$0.mapCodec).validate($$1 -> {
            int $$3;
            ReplaceSection $$2;
            if ($$1 instanceof ReplaceSection && ($$2 = (ReplaceSection)$$1).size().isPresent() && ($$3 = $$2.size().get().intValue()) > $$02) {
                return DataResult.error(() -> "Size value too large: " + $$3 + ", max size is " + $$02);
            }
            return DataResult.success((Object)$$1);
        });
    }

    public Type mode();

    default public <T> List<T> apply(List<T> $$0, List<T> $$1) {
        return this.apply($$0, $$1, Integer.MAX_VALUE);
    }

    public <T> List<T> apply(List<T> var1, List<T> var2, int var3);

    public static final class Type
    extends Enum<Type>
    implements StringRepresentable {
        public static final /* enum */ Type REPLACE_ALL = new Type("replace_all", ReplaceAll.MAP_CODEC);
        public static final /* enum */ Type REPLACE_SECTION = new Type("replace_section", ReplaceSection.MAP_CODEC);
        public static final /* enum */ Type INSERT = new Type("insert", Insert.MAP_CODEC);
        public static final /* enum */ Type APPEND = new Type("append", Append.MAP_CODEC);
        public static final Codec<Type> CODEC;
        private final String id;
        final MapCodec<? extends ListOperation> mapCodec;
        private static final /* synthetic */ Type[] $VALUES;

        public static Type[] values() {
            return (Type[])$VALUES.clone();
        }

        public static Type valueOf(String $$0) {
            return Enum.valueOf(Type.class, $$0);
        }

        private Type(String $$0, MapCodec<? extends ListOperation> $$1) {
            this.id = $$0;
            this.mapCodec = $$1;
        }

        public MapCodec<? extends ListOperation> mapCodec() {
            return this.mapCodec;
        }

        @Override
        public String getSerializedName() {
            return this.id;
        }

        private static /* synthetic */ Type[] b() {
            return new Type[]{REPLACE_ALL, REPLACE_SECTION, INSERT, APPEND};
        }

        static {
            $VALUES = Type.b();
            CODEC = StringRepresentable.fromEnum(Type::values);
        }
    }

    public record ReplaceSection(int offset, Optional<Integer> size) implements ListOperation
    {
        private static final Logger LOGGER = LogUtils.getLogger();
        public static final MapCodec<ReplaceSection> MAP_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("offset", (Object)0).forGetter(ReplaceSection::offset), (App)ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("size").forGetter(ReplaceSection::size)).apply((Applicative)$$0, ReplaceSection::new));

        public ReplaceSection(int $$0) {
            this($$0, Optional.empty());
        }

        @Override
        public Type mode() {
            return Type.REPLACE_SECTION;
        }

        @Override
        public <T> List<T> apply(List<T> $$0, List<T> $$1, int $$2) {
            ImmutableCollection $$6;
            int $$3 = $$0.size();
            if (this.offset > $$3) {
                LOGGER.error("Cannot replace when offset is out of bounds");
                return $$0;
            }
            ImmutableList.Builder $$4 = ImmutableList.builder();
            $$4.addAll($$0.subList(0, this.offset));
            $$4.addAll($$1);
            int $$5 = this.offset + this.size.orElse($$1.size());
            if ($$5 < $$3) {
                $$4.addAll($$0.subList($$5, $$3));
            }
            if (($$6 = $$4.build()).size() > $$2) {
                LOGGER.error("Contents overflow in section replacement");
                return $$0;
            }
            return $$6;
        }
    }

    public record StandAlone<T>(List<T> value, ListOperation operation) {
        public static <T> Codec<StandAlone<T>> codec(Codec<T> $$0, int $$1) {
            return RecordCodecBuilder.create($$2 -> $$2.group((App)$$0.sizeLimitedListOf($$1).fieldOf("values").forGetter($$0 -> $$0.value), (App)ListOperation.codec($$1).forGetter($$0 -> $$0.operation)).apply((Applicative)$$2, StandAlone::new));
        }

        public List<T> apply(List<T> $$0) {
            return this.operation.apply($$0, this.value);
        }
    }

    public static class Append
    implements ListOperation {
        private static final Logger LOGGER = LogUtils.getLogger();
        public static final Append INSTANCE = new Append();
        public static final MapCodec<Append> MAP_CODEC = MapCodec.unit(() -> INSTANCE);

        private Append() {
        }

        @Override
        public Type mode() {
            return Type.APPEND;
        }

        @Override
        public <T> List<T> apply(List<T> $$0, List<T> $$1, int $$2) {
            if ($$0.size() + $$1.size() > $$2) {
                LOGGER.error("Contents overflow in section append");
                return $$0;
            }
            return Stream.concat($$0.stream(), $$1.stream()).toList();
        }
    }

    public record Insert(int offset) implements ListOperation
    {
        private static final Logger LOGGER = LogUtils.getLogger();
        public static final MapCodec<Insert> MAP_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("offset", (Object)0).forGetter(Insert::offset)).apply((Applicative)$$0, Insert::new));

        @Override
        public Type mode() {
            return Type.INSERT;
        }

        @Override
        public <T> List<T> apply(List<T> $$0, List<T> $$1, int $$2) {
            int $$3 = $$0.size();
            if (this.offset > $$3) {
                LOGGER.error("Cannot insert when offset is out of bounds");
                return $$0;
            }
            if ($$3 + $$1.size() > $$2) {
                LOGGER.error("Contents overflow in section insertion");
                return $$0;
            }
            ImmutableList.Builder $$4 = ImmutableList.builder();
            $$4.addAll($$0.subList(0, this.offset));
            $$4.addAll($$1);
            $$4.addAll($$0.subList(this.offset, $$3));
            return $$4.build();
        }
    }

    public static class ReplaceAll
    implements ListOperation {
        public static final ReplaceAll INSTANCE = new ReplaceAll();
        public static final MapCodec<ReplaceAll> MAP_CODEC = MapCodec.unit(() -> INSTANCE);

        private ReplaceAll() {
        }

        @Override
        public Type mode() {
            return Type.REPLACE_ALL;
        }

        @Override
        public <T> List<T> apply(List<T> $$0, List<T> $$1, int $$2) {
            return $$1;
        }
    }
}

