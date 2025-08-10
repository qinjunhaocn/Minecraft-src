/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.server.players;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.server.players.StoredUserEntry;
import net.minecraft.util.GsonHelper;
import org.slf4j.Logger;

public abstract class StoredUserList<K, V extends StoredUserEntry<K>> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final File file;
    private final Map<String, V> map = Maps.newHashMap();

    public StoredUserList(File $$0) {
        this.file = $$0;
    }

    public File getFile() {
        return this.file;
    }

    public void add(V $$0) {
        this.map.put(this.getKeyForUser(((StoredUserEntry)$$0).getUser()), $$0);
        try {
            this.save();
        } catch (IOException $$1) {
            LOGGER.warn("Could not save the list after adding a user.", $$1);
        }
    }

    @Nullable
    public V get(K $$0) {
        this.removeExpired();
        return (V)((StoredUserEntry)this.map.get(this.getKeyForUser($$0)));
    }

    public void remove(K $$0) {
        this.map.remove(this.getKeyForUser($$0));
        try {
            this.save();
        } catch (IOException $$1) {
            LOGGER.warn("Could not save the list after removing a user.", $$1);
        }
    }

    public void remove(StoredUserEntry<K> $$0) {
        this.remove($$0.getUser());
    }

    public String[] a() {
        return this.map.keySet().toArray(new String[0]);
    }

    public boolean isEmpty() {
        return this.map.size() < 1;
    }

    protected String getKeyForUser(K $$0) {
        return $$0.toString();
    }

    protected boolean contains(K $$0) {
        return this.map.containsKey(this.getKeyForUser($$0));
    }

    private void removeExpired() {
        ArrayList<Object> $$0 = Lists.newArrayList();
        for (StoredUserEntry $$1 : this.map.values()) {
            if (!$$1.hasExpired()) continue;
            $$0.add($$1.getUser());
        }
        for (Object $$2 : $$0) {
            this.map.remove(this.getKeyForUser($$2));
        }
    }

    protected abstract StoredUserEntry<K> createEntry(JsonObject var1);

    public Collection<V> getEntries() {
        return this.map.values();
    }

    public void save() throws IOException {
        JsonArray $$02 = new JsonArray();
        this.map.values().stream().map($$0 -> Util.make(new JsonObject(), $$0::serialize)).forEach(arg_0 -> ((JsonArray)$$02).add(arg_0));
        try (BufferedWriter $$1 = Files.newWriter(this.file, StandardCharsets.UTF_8);){
            GSON.toJson((JsonElement)$$02, GSON.newJsonWriter((Writer)$$1));
        }
    }

    public void load() throws IOException {
        if (!this.file.exists()) {
            return;
        }
        try (BufferedReader $$0 = Files.newReader(this.file, StandardCharsets.UTF_8);){
            this.map.clear();
            JsonArray $$1 = (JsonArray)GSON.fromJson((Reader)$$0, JsonArray.class);
            if ($$1 == null) {
                return;
            }
            for (JsonElement $$2 : $$1) {
                JsonObject $$3 = GsonHelper.convertToJsonObject($$2, "entry");
                StoredUserEntry<K> $$4 = this.createEntry($$3);
                if ($$4.getUser() == null) continue;
                this.map.put(this.getKeyForUser($$4.getUser()), $$4);
            }
        }
    }
}

