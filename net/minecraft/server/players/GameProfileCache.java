/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.GameProfileRepository
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.server.players;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.UUIDUtil;
import net.minecraft.util.StringUtil;
import org.slf4j.Logger;

public class GameProfileCache {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int GAMEPROFILES_MRU_LIMIT = 1000;
    private static final int GAMEPROFILES_EXPIRATION_MONTHS = 1;
    private static boolean usesAuthentication;
    private final Map<String, GameProfileInfo> profilesByName = Maps.newConcurrentMap();
    private final Map<UUID, GameProfileInfo> profilesByUUID = Maps.newConcurrentMap();
    private final Map<String, CompletableFuture<Optional<GameProfile>>> requests = Maps.newConcurrentMap();
    private final GameProfileRepository profileRepository;
    private final Gson gson = new GsonBuilder().create();
    private final File file;
    private final AtomicLong operationCount = new AtomicLong();
    @Nullable
    private Executor executor;

    public GameProfileCache(GameProfileRepository $$0, File $$1) {
        this.profileRepository = $$0;
        this.file = $$1;
        Lists.reverse(this.load()).forEach(this::safeAdd);
    }

    private void safeAdd(GameProfileInfo $$0) {
        GameProfile $$1 = $$0.getProfile();
        $$0.setLastAccess(this.getNextOperation());
        this.profilesByName.put($$1.getName().toLowerCase(Locale.ROOT), $$0);
        this.profilesByUUID.put($$1.getId(), $$0);
    }

    private static Optional<GameProfile> lookupGameProfile(GameProfileRepository $$0, String $$1) {
        if (!StringUtil.isValidPlayerName($$1)) {
            return GameProfileCache.createUnknownProfile($$1);
        }
        Optional $$2 = $$0.findProfileByName($$1);
        if ($$2.isEmpty()) {
            return GameProfileCache.createUnknownProfile($$1);
        }
        return $$2;
    }

    private static Optional<GameProfile> createUnknownProfile(String $$0) {
        if (GameProfileCache.usesAuthentication()) {
            return Optional.empty();
        }
        return Optional.of(UUIDUtil.createOfflineProfile($$0));
    }

    public static void setUsesAuthentication(boolean $$0) {
        usesAuthentication = $$0;
    }

    private static boolean usesAuthentication() {
        return usesAuthentication;
    }

    public void add(GameProfile $$0) {
        Calendar $$1 = Calendar.getInstance();
        $$1.setTime(new Date());
        $$1.add(2, 1);
        Date $$2 = $$1.getTime();
        GameProfileInfo $$3 = new GameProfileInfo($$0, $$2);
        this.safeAdd($$3);
        this.save();
    }

    private long getNextOperation() {
        return this.operationCount.incrementAndGet();
    }

    public Optional<GameProfile> get(String $$0) {
        Optional<GameProfile> $$5;
        String $$1 = $$0.toLowerCase(Locale.ROOT);
        GameProfileInfo $$2 = this.profilesByName.get($$1);
        boolean $$3 = false;
        if ($$2 != null && new Date().getTime() >= $$2.expirationDate.getTime()) {
            this.profilesByUUID.remove($$2.getProfile().getId());
            this.profilesByName.remove($$2.getProfile().getName().toLowerCase(Locale.ROOT));
            $$3 = true;
            $$2 = null;
        }
        if ($$2 != null) {
            $$2.setLastAccess(this.getNextOperation());
            Optional<GameProfile> $$4 = Optional.of($$2.getProfile());
        } else {
            $$5 = GameProfileCache.lookupGameProfile(this.profileRepository, $$1);
            if ($$5.isPresent()) {
                this.add($$5.get());
                $$3 = false;
            }
        }
        if ($$3) {
            this.save();
        }
        return $$5;
    }

    public CompletableFuture<Optional<GameProfile>> getAsync(String $$0) {
        if (this.executor == null) {
            throw new IllegalStateException("No executor");
        }
        CompletableFuture<Optional<GameProfile>> $$12 = this.requests.get($$0);
        if ($$12 != null) {
            return $$12;
        }
        CompletionStage $$22 = CompletableFuture.supplyAsync(() -> this.get($$0), Util.backgroundExecutor().forName("getProfile")).whenCompleteAsync(($$1, $$2) -> this.requests.remove($$0), this.executor);
        this.requests.put($$0, (CompletableFuture<Optional<GameProfile>>)$$22);
        return $$22;
    }

    public Optional<GameProfile> get(UUID $$0) {
        GameProfileInfo $$1 = this.profilesByUUID.get($$0);
        if ($$1 == null) {
            return Optional.empty();
        }
        $$1.setLastAccess(this.getNextOperation());
        return Optional.of($$1.getProfile());
    }

    public void setExecutor(Executor $$0) {
        this.executor = $$0;
    }

    public void clearExecutor() {
        this.executor = null;
    }

    private static DateFormat createDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.ROOT);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public List<GameProfileInfo> load() {
        ArrayList<GameProfileInfo> $$0 = Lists.newArrayList();
        try (BufferedReader $$12222 = Files.newReader(this.file, StandardCharsets.UTF_8);){
            JsonArray $$22 = (JsonArray)this.gson.fromJson((Reader)$$12222, JsonArray.class);
            if ($$22 == null) {
                ArrayList<GameProfileInfo> arrayList = $$0;
                return arrayList;
            }
            DateFormat $$3 = GameProfileCache.createDateFormat();
            $$22.forEach($$2 -> GameProfileCache.readGameProfile($$2, $$3).ifPresent($$0::add));
            return $$0;
        } catch (FileNotFoundException $$12222) {
            return $$0;
        } catch (JsonParseException | IOException $$4) {
            LOGGER.warn("Failed to load profile cache {}", (Object)this.file, (Object)$$4);
        }
        return $$0;
    }

    public void save() {
        JsonArray $$0 = new JsonArray();
        DateFormat $$1 = GameProfileCache.createDateFormat();
        this.getTopMRUProfiles(1000).forEach($$2 -> $$0.add(GameProfileCache.writeGameProfile($$2, $$1)));
        String $$22 = this.gson.toJson((JsonElement)$$0);
        try (BufferedWriter $$3 = Files.newWriter(this.file, StandardCharsets.UTF_8);){
            $$3.write($$22);
        } catch (IOException iOException) {
            // empty catch block
        }
    }

    private Stream<GameProfileInfo> getTopMRUProfiles(int $$0) {
        return ImmutableList.copyOf(this.profilesByUUID.values()).stream().sorted(Comparator.comparing(GameProfileInfo::getLastAccess).reversed()).limit($$0);
    }

    private static JsonElement writeGameProfile(GameProfileInfo $$0, DateFormat $$1) {
        JsonObject $$2 = new JsonObject();
        $$2.addProperty("name", $$0.getProfile().getName());
        $$2.addProperty("uuid", $$0.getProfile().getId().toString());
        $$2.addProperty("expiresOn", $$1.format($$0.getExpirationDate()));
        return $$2;
    }

    /*
     * WARNING - void declaration
     */
    private static Optional<GameProfileInfo> readGameProfile(JsonElement $$0, DateFormat $$1) {
        if ($$0.isJsonObject()) {
            void $$11;
            JsonObject $$2 = $$0.getAsJsonObject();
            JsonElement $$3 = $$2.get("name");
            JsonElement $$4 = $$2.get("uuid");
            JsonElement $$5 = $$2.get("expiresOn");
            if ($$3 == null || $$4 == null) {
                return Optional.empty();
            }
            String $$6 = $$4.getAsString();
            String $$7 = $$3.getAsString();
            Date $$8 = null;
            if ($$5 != null) {
                try {
                    $$8 = $$1.parse($$5.getAsString());
                } catch (ParseException parseException) {
                    // empty catch block
                }
            }
            if ($$7 == null || $$6 == null || $$8 == null) {
                return Optional.empty();
            }
            try {
                UUID $$9 = UUID.fromString($$6);
            } catch (Throwable $$10) {
                return Optional.empty();
            }
            return Optional.of(new GameProfileInfo(new GameProfile((UUID)$$11, $$7), $$8));
        }
        return Optional.empty();
    }

    static class GameProfileInfo {
        private final GameProfile profile;
        final Date expirationDate;
        private volatile long lastAccess;

        GameProfileInfo(GameProfile $$0, Date $$1) {
            this.profile = $$0;
            this.expirationDate = $$1;
        }

        public GameProfile getProfile() {
            return this.profile;
        }

        public Date getExpirationDate() {
            return this.expirationDate;
        }

        public void setLastAccess(long $$0) {
            this.lastAccess = $$0;
        }

        public long getLastAccess() {
            return this.lastAccess;
        }
    }
}

