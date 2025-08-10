/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.client.quickplay;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.time.Instant;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.GameType;
import org.slf4j.Logger;

public class QuickPlayLog {
    private static final QuickPlayLog INACTIVE = new QuickPlayLog(""){

        @Override
        public void log(Minecraft $$0) {
        }

        @Override
        public void setWorldData(Type $$0, String $$1, String $$2) {
        }
    };
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().create();
    private final Path path;
    @Nullable
    private QuickPlayWorld worldData;

    QuickPlayLog(String $$0) {
        this.path = Minecraft.getInstance().gameDirectory.toPath().resolve($$0);
    }

    public static QuickPlayLog of(@Nullable String $$0) {
        if ($$0 == null) {
            return INACTIVE;
        }
        return new QuickPlayLog($$0);
    }

    public void setWorldData(Type $$0, String $$1, String $$2) {
        this.worldData = new QuickPlayWorld($$0, $$1, $$2);
    }

    public void log(Minecraft $$0) {
        if ($$0.gameMode == null || this.worldData == null) {
            LOGGER.error("Failed to log session for quickplay. Missing world data or gamemode");
            return;
        }
        Util.ioPool().execute(() -> {
            try {
                Files.deleteIfExists(this.path);
            } catch (IOException $$1) {
                LOGGER.error("Failed to delete quickplay log file {}", (Object)this.path, (Object)$$1);
            }
            QuickPlayEntry $$2 = new QuickPlayEntry(this.worldData, Instant.now(), $$02.gameMode.getPlayerMode());
            Codec.list(QuickPlayEntry.CODEC).encodeStart((DynamicOps)JsonOps.INSTANCE, (Object)List.of((Object)((Object)$$2))).resultOrPartial(Util.prefix("Quick Play: ", LOGGER::error)).ifPresent($$0 -> {
                try {
                    Files.createDirectories(this.path.getParent(), new FileAttribute[0]);
                    Files.writeString((Path)this.path, (CharSequence)GSON.toJson($$0), (OpenOption[])new OpenOption[0]);
                } catch (IOException $$1) {
                    LOGGER.error("Failed to write to quickplay log file {}", (Object)this.path, (Object)$$1);
                }
            });
        });
    }

    record QuickPlayWorld(Type type, String id, String name) {
        public static final MapCodec<QuickPlayWorld> MAP_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)Type.CODEC.fieldOf("type").forGetter(QuickPlayWorld::type), (App)ExtraCodecs.ESCAPED_STRING.fieldOf("id").forGetter(QuickPlayWorld::id), (App)Codec.STRING.fieldOf("name").forGetter(QuickPlayWorld::name)).apply((Applicative)$$0, QuickPlayWorld::new));
    }

    public static final class Type
    extends Enum<Type>
    implements StringRepresentable {
        public static final /* enum */ Type SINGLEPLAYER = new Type("singleplayer");
        public static final /* enum */ Type MULTIPLAYER = new Type("multiplayer");
        public static final /* enum */ Type REALMS = new Type("realms");
        static final Codec<Type> CODEC;
        private final String name;
        private static final /* synthetic */ Type[] $VALUES;

        public static Type[] values() {
            return (Type[])$VALUES.clone();
        }

        public static Type valueOf(String $$0) {
            return Enum.valueOf(Type.class, $$0);
        }

        private Type(String $$0) {
            this.name = $$0;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        private static /* synthetic */ Type[] a() {
            return new Type[]{SINGLEPLAYER, MULTIPLAYER, REALMS};
        }

        static {
            $VALUES = Type.a();
            CODEC = StringRepresentable.fromEnum(Type::values);
        }
    }

    record QuickPlayEntry(QuickPlayWorld quickPlayWorld, Instant lastPlayedTime, GameType gamemode) {
        public static final Codec<QuickPlayEntry> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)QuickPlayWorld.MAP_CODEC.forGetter(QuickPlayEntry::quickPlayWorld), (App)ExtraCodecs.INSTANT_ISO8601.fieldOf("lastPlayedTime").forGetter(QuickPlayEntry::lastPlayedTime), (App)GameType.CODEC.fieldOf("gamemode").forGetter(QuickPlayEntry::gamemode)).apply((Applicative)$$0, QuickPlayEntry::new));
    }
}

