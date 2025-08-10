/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.gametest.framework;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.GameRules;
import org.slf4j.Logger;

public interface TestEnvironmentDefinition {
    public static final Codec<TestEnvironmentDefinition> DIRECT_CODEC = BuiltInRegistries.TEST_ENVIRONMENT_DEFINITION_TYPE.byNameCodec().dispatch(TestEnvironmentDefinition::codec, $$0 -> $$0);
    public static final Codec<Holder<TestEnvironmentDefinition>> CODEC = RegistryFileCodec.create(Registries.TEST_ENVIRONMENT, DIRECT_CODEC);

    public static MapCodec<? extends TestEnvironmentDefinition> bootstrap(Registry<MapCodec<? extends TestEnvironmentDefinition>> $$0) {
        Registry.register($$0, "all_of", AllOf.CODEC);
        Registry.register($$0, "game_rules", SetGameRules.CODEC);
        Registry.register($$0, "time_of_day", TimeOfDay.CODEC);
        Registry.register($$0, "weather", Weather.CODEC);
        return Registry.register($$0, "function", Functions.CODEC);
    }

    public void setup(ServerLevel var1);

    default public void teardown(ServerLevel $$0) {
    }

    public MapCodec<? extends TestEnvironmentDefinition> codec();

    public record AllOf(List<Holder<TestEnvironmentDefinition>> definitions) implements TestEnvironmentDefinition
    {
        public static final MapCodec<AllOf> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)CODEC.listOf().fieldOf("definitions").forGetter(AllOf::definitions)).apply((Applicative)$$0, AllOf::new));

        public AllOf(TestEnvironmentDefinition ... $$0) {
            this(Arrays.stream($$0).map(Holder::direct).toList());
        }

        @Override
        public void setup(ServerLevel $$0) {
            this.definitions.forEach($$1 -> ((TestEnvironmentDefinition)$$1.value()).setup($$0));
        }

        @Override
        public void teardown(ServerLevel $$0) {
            this.definitions.forEach($$1 -> ((TestEnvironmentDefinition)$$1.value()).teardown($$0));
        }

        public MapCodec<AllOf> codec() {
            return CODEC;
        }
    }

    public record SetGameRules(List<Entry<Boolean, GameRules.BooleanValue>> boolRules, List<Entry<Integer, GameRules.IntegerValue>> intRules) implements TestEnvironmentDefinition
    {
        public static final MapCodec<SetGameRules> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)Entry.codec(GameRules.BooleanValue.class, Codec.BOOL).listOf().fieldOf("bool_rules").forGetter(SetGameRules::boolRules), (App)Entry.codec(GameRules.IntegerValue.class, Codec.INT).listOf().fieldOf("int_rules").forGetter(SetGameRules::intRules)).apply((Applicative)$$0, SetGameRules::new));

        @Override
        public void setup(ServerLevel $$0) {
            GameRules $$1 = $$0.getGameRules();
            MinecraftServer $$2 = $$0.getServer();
            for (Entry<Boolean, GameRules.BooleanValue> entry : this.boolRules) {
                $$1.getRule(entry.key()).set(entry.value(), $$2);
            }
            for (Entry<Comparable<Boolean>, GameRules.Value> entry : this.intRules) {
                ((GameRules.IntegerValue)$$1.getRule(entry.key())).set((Integer)entry.value(), $$2);
            }
        }

        @Override
        public void teardown(ServerLevel $$0) {
            GameRules $$1 = $$0.getGameRules();
            MinecraftServer $$2 = $$0.getServer();
            for (Entry<Boolean, GameRules.BooleanValue> entry : this.boolRules) {
                $$1.getRule(entry.key()).setFrom(GameRules.getType(entry.key()).createRule(), $$2);
            }
            for (Entry<Comparable<Boolean>, GameRules.Value> entry : this.intRules) {
                ((GameRules.IntegerValue)$$1.getRule(entry.key())).setFrom((GameRules.IntegerValue)GameRules.getType(entry.key()).createRule(), $$2);
            }
        }

        public MapCodec<SetGameRules> codec() {
            return CODEC;
        }

        public static <S, T extends GameRules.Value<T>> Entry<S, T> entry(GameRules.Key<T> $$0, S $$1) {
            return new Entry<S, T>($$0, $$1);
        }

        public record Entry<S, T extends GameRules.Value<T>>(GameRules.Key<T> key, S value) {
            public static <S, T extends GameRules.Value<T>> Codec<Entry<S, T>> codec(Class<T> $$0, Codec<S> $$1) {
                return RecordCodecBuilder.create($$2 -> $$2.group((App)GameRules.keyCodec($$0).fieldOf("rule").forGetter(Entry::key), (App)$$1.fieldOf("value").forGetter(Entry::value)).apply((Applicative)$$2, Entry::new));
            }
        }
    }

    public record TimeOfDay(int time) implements TestEnvironmentDefinition
    {
        public static final MapCodec<TimeOfDay> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)ExtraCodecs.NON_NEGATIVE_INT.fieldOf("time").forGetter(TimeOfDay::time)).apply((Applicative)$$0, TimeOfDay::new));

        @Override
        public void setup(ServerLevel $$0) {
            $$0.setDayTime(this.time);
        }

        public MapCodec<TimeOfDay> codec() {
            return CODEC;
        }
    }

    public record Weather(Type weather) implements TestEnvironmentDefinition
    {
        public static final MapCodec<Weather> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)Type.CODEC.fieldOf("weather").forGetter(Weather::weather)).apply((Applicative)$$0, Weather::new));

        @Override
        public void setup(ServerLevel $$0) {
            this.weather.apply($$0);
        }

        @Override
        public void teardown(ServerLevel $$0) {
            $$0.resetWeatherCycle();
        }

        public MapCodec<Weather> codec() {
            return CODEC;
        }

        public static final class Type
        extends Enum<Type>
        implements StringRepresentable {
            public static final /* enum */ Type CLEAR = new Type("clear", 100000, 0, false, false);
            public static final /* enum */ Type RAIN = new Type("rain", 0, 100000, true, false);
            public static final /* enum */ Type THUNDER = new Type("thunder", 0, 100000, true, true);
            public static final Codec<Type> CODEC;
            private final String id;
            private final int clearTime;
            private final int rainTime;
            private final boolean raining;
            private final boolean thundering;
            private static final /* synthetic */ Type[] $VALUES;

            public static Type[] values() {
                return (Type[])$VALUES.clone();
            }

            public static Type valueOf(String $$0) {
                return Enum.valueOf(Type.class, $$0);
            }

            private Type(String $$0, int $$1, int $$2, boolean $$3, boolean $$4) {
                this.id = $$0;
                this.clearTime = $$1;
                this.rainTime = $$2;
                this.raining = $$3;
                this.thundering = $$4;
            }

            void apply(ServerLevel $$0) {
                $$0.setWeatherParameters(this.clearTime, this.rainTime, this.raining, this.thundering);
            }

            @Override
            public String getSerializedName() {
                return this.id;
            }

            private static /* synthetic */ Type[] a() {
                return new Type[]{CLEAR, RAIN, THUNDER};
            }

            static {
                $VALUES = Type.a();
                CODEC = StringRepresentable.fromEnum(Type::values);
            }
        }
    }

    public record Functions(Optional<ResourceLocation> setupFunction, Optional<ResourceLocation> teardownFunction) implements TestEnvironmentDefinition
    {
        private static final Logger LOGGER = LogUtils.getLogger();
        public static final MapCodec<Functions> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)ResourceLocation.CODEC.optionalFieldOf("setup").forGetter(Functions::setupFunction), (App)ResourceLocation.CODEC.optionalFieldOf("teardown").forGetter(Functions::teardownFunction)).apply((Applicative)$$0, Functions::new));

        @Override
        public void setup(ServerLevel $$0) {
            this.setupFunction.ifPresent($$1 -> Functions.run($$0, $$1));
        }

        @Override
        public void teardown(ServerLevel $$0) {
            this.teardownFunction.ifPresent($$1 -> Functions.run($$0, $$1));
        }

        private static void run(ServerLevel $$0, ResourceLocation $$1) {
            MinecraftServer $$2 = $$0.getServer();
            ServerFunctionManager $$3 = $$2.getFunctions();
            Optional<CommandFunction<CommandSourceStack>> $$4 = $$3.get($$1);
            if ($$4.isPresent()) {
                CommandSourceStack $$5 = $$2.createCommandSourceStack().withPermission(2).withSuppressedOutput().withLevel($$0);
                $$3.execute($$4.get(), $$5);
            } else {
                LOGGER.error("Test Batch failed for non-existent function {}", (Object)$$1);
            }
        }

        public MapCodec<Functions> codec() {
            return CODEC;
        }
    }
}

