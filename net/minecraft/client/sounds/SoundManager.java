/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.reflect.TypeToken
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.client.sounds;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mojang.blaze3d.audio.ListenerTransform;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.client.Camera;
import net.minecraft.client.Options;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundEventRegistration;
import net.minecraft.client.resources.sounds.SoundEventRegistrationSerializer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.client.sounds.Weighted;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.Zone;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.MultipliedFloats;
import org.slf4j.Logger;

public class SoundManager
extends SimplePreparableReloadListener<Preparations> {
    public static final ResourceLocation EMPTY_SOUND_LOCATION = ResourceLocation.withDefaultNamespace("empty");
    public static final Sound EMPTY_SOUND = new Sound(EMPTY_SOUND_LOCATION, ConstantFloat.of(1.0f), ConstantFloat.of(1.0f), 1, Sound.Type.FILE, false, false, 16);
    public static final ResourceLocation INTENTIONALLY_EMPTY_SOUND_LOCATION = ResourceLocation.withDefaultNamespace("intentionally_empty");
    public static final WeighedSoundEvents INTENTIONALLY_EMPTY_SOUND_EVENT = new WeighedSoundEvents(INTENTIONALLY_EMPTY_SOUND_LOCATION, null);
    public static final Sound INTENTIONALLY_EMPTY_SOUND = new Sound(INTENTIONALLY_EMPTY_SOUND_LOCATION, ConstantFloat.of(1.0f), ConstantFloat.of(1.0f), 1, Sound.Type.FILE, false, false, 16);
    static final Logger LOGGER = LogUtils.getLogger();
    private static final String SOUNDS_PATH = "sounds.json";
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(SoundEventRegistration.class, (Object)new SoundEventRegistrationSerializer()).create();
    private static final TypeToken<Map<String, SoundEventRegistration>> SOUND_EVENT_REGISTRATION_TYPE = new TypeToken<Map<String, SoundEventRegistration>>(){};
    private final Map<ResourceLocation, WeighedSoundEvents> registry = Maps.newHashMap();
    private final SoundEngine soundEngine;
    private final Map<ResourceLocation, Resource> soundCache = new HashMap<ResourceLocation, Resource>();

    public SoundManager(Options $$0, MusicManager $$1) {
        this.soundEngine = new SoundEngine($$1, this, $$0, ResourceProvider.fromMap(this.soundCache));
    }

    @Override
    protected Preparations prepare(ResourceManager $$0, ProfilerFiller $$1) {
        Preparations $$2 = new Preparations();
        try (Zone $$3 = $$1.zone("list");){
            $$2.listResources($$0);
        }
        for (String $$4 : $$0.getNamespaces()) {
            try {
                Zone $$5 = $$1.zone($$4);
                try {
                    List<Resource> $$6 = $$0.getResourceStack(ResourceLocation.fromNamespaceAndPath($$4, SOUNDS_PATH));
                    for (Resource $$7 : $$6) {
                        $$1.push($$7.sourcePackId());
                        try (BufferedReader $$8 = $$7.openAsReader();){
                            $$1.push("parse");
                            Map<String, SoundEventRegistration> $$9 = GsonHelper.fromJson(GSON, (Reader)$$8, SOUND_EVENT_REGISTRATION_TYPE);
                            $$1.popPush("register");
                            for (Map.Entry<String, SoundEventRegistration> $$10 : $$9.entrySet()) {
                                $$2.handleRegistration(ResourceLocation.fromNamespaceAndPath($$4, $$10.getKey()), $$10.getValue());
                            }
                            $$1.pop();
                        } catch (RuntimeException $$11) {
                            LOGGER.warn("Invalid {} in resourcepack: '{}'", SOUNDS_PATH, $$7.sourcePackId(), $$11);
                        }
                        $$1.pop();
                    }
                } finally {
                    if ($$5 == null) continue;
                    $$5.close();
                }
            } catch (IOException iOException) {}
        }
        return $$2;
    }

    @Override
    protected void apply(Preparations $$0, ResourceManager $$1, ProfilerFiller $$2) {
        $$0.apply(this.registry, this.soundCache, this.soundEngine);
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            for (ResourceLocation $$3 : this.registry.keySet()) {
                WeighedSoundEvents $$4 = this.registry.get($$3);
                if (ComponentUtils.isTranslationResolvable($$4.getSubtitle()) || !BuiltInRegistries.SOUND_EVENT.containsKey($$3)) continue;
                LOGGER.error("Missing subtitle {} for sound event: {}", (Object)$$4.getSubtitle(), (Object)$$3);
            }
        }
        if (LOGGER.isDebugEnabled()) {
            for (ResourceLocation $$5 : this.registry.keySet()) {
                if (BuiltInRegistries.SOUND_EVENT.containsKey($$5)) continue;
                LOGGER.debug("Not having sound event for: {}", (Object)$$5);
            }
        }
        this.soundEngine.reload();
    }

    public List<String> getAvailableSoundDevices() {
        return this.soundEngine.getAvailableSoundDevices();
    }

    public ListenerTransform getListenerTransform() {
        return this.soundEngine.getListenerTransform();
    }

    static boolean validateSoundResource(Sound $$0, ResourceLocation $$1, ResourceProvider $$2) {
        ResourceLocation $$3 = $$0.getPath();
        if ($$2.getResource($$3).isEmpty()) {
            LOGGER.warn("File {} does not exist, cannot add it to event {}", (Object)$$3, (Object)$$1);
            return false;
        }
        return true;
    }

    @Nullable
    public WeighedSoundEvents getSoundEvent(ResourceLocation $$0) {
        return this.registry.get($$0);
    }

    public Collection<ResourceLocation> getAvailableSounds() {
        return this.registry.keySet();
    }

    public void queueTickingSound(TickableSoundInstance $$0) {
        this.soundEngine.queueTickingSound($$0);
    }

    public SoundEngine.PlayResult play(SoundInstance $$0) {
        return this.soundEngine.play($$0);
    }

    public void playDelayed(SoundInstance $$0, int $$1) {
        this.soundEngine.playDelayed($$0, $$1);
    }

    public void updateSource(Camera $$0) {
        this.soundEngine.updateSource($$0);
    }

    public void a(SoundSource ... $$0) {
        this.soundEngine.a($$0);
    }

    public void stop() {
        this.soundEngine.stopAll();
    }

    public void destroy() {
        this.soundEngine.destroy();
    }

    public void emergencyShutdown() {
        this.soundEngine.emergencyShutdown();
    }

    public void tick(boolean $$0) {
        this.soundEngine.tick($$0);
    }

    public void resume() {
        this.soundEngine.resume();
    }

    public void updateSourceVolume(SoundSource $$0, float $$1) {
        this.soundEngine.updateCategoryVolume($$0, $$1);
    }

    public void stop(SoundInstance $$0) {
        this.soundEngine.stop($$0);
    }

    public void setVolume(SoundInstance $$0, float $$1) {
        this.soundEngine.setVolume($$0, $$1);
    }

    public boolean isActive(SoundInstance $$0) {
        return this.soundEngine.isActive($$0);
    }

    public void addListener(SoundEventListener $$0) {
        this.soundEngine.addEventListener($$0);
    }

    public void removeListener(SoundEventListener $$0) {
        this.soundEngine.removeEventListener($$0);
    }

    public void stop(@Nullable ResourceLocation $$0, @Nullable SoundSource $$1) {
        this.soundEngine.stop($$0, $$1);
    }

    public String getDebugString() {
        return this.soundEngine.getDebugString();
    }

    public void reload() {
        this.soundEngine.reload();
    }

    @Override
    protected /* synthetic */ Object prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        return this.prepare(resourceManager, profilerFiller);
    }

    protected static class Preparations {
        final Map<ResourceLocation, WeighedSoundEvents> registry = Maps.newHashMap();
        private Map<ResourceLocation, Resource> soundCache = Map.of();

        protected Preparations() {
        }

        void listResources(ResourceManager $$0) {
            this.soundCache = Sound.SOUND_LISTER.listMatchingResources($$0);
        }

        /*
         * WARNING - void declaration
         */
        void handleRegistration(ResourceLocation $$0, SoundEventRegistration $$1) {
            boolean $$3;
            WeighedSoundEvents $$2 = this.registry.get($$0);
            boolean bl = $$3 = $$2 == null;
            if ($$3 || $$1.isReplace()) {
                if (!$$3) {
                    LOGGER.debug("Replaced sound event location {}", (Object)$$0);
                }
                $$2 = new WeighedSoundEvents($$0, $$1.getSubtitle());
                this.registry.put($$0, $$2);
            }
            ResourceProvider $$4 = ResourceProvider.fromMap(this.soundCache);
            block4: for (final Sound $$5 : $$1.getSounds()) {
                void $$9;
                final ResourceLocation $$6 = $$5.getLocation();
                switch ($$5.getType()) {
                    case FILE: {
                        if (!SoundManager.validateSoundResource($$5, $$0, $$4)) continue block4;
                        Sound $$7 = $$5;
                        break;
                    }
                    case SOUND_EVENT: {
                        Weighted<Sound> $$8 = new Weighted<Sound>(){

                            @Override
                            public int getWeight() {
                                WeighedSoundEvents $$0 = registry.get($$6);
                                return $$0 == null ? 0 : $$0.getWeight();
                            }

                            @Override
                            public Sound getSound(RandomSource $$0) {
                                WeighedSoundEvents $$1 = registry.get($$6);
                                if ($$1 == null) {
                                    return EMPTY_SOUND;
                                }
                                Sound $$2 = $$1.getSound($$0);
                                return new Sound($$2.getLocation(), new MultipliedFloats($$2.getVolume(), $$5.getVolume()), new MultipliedFloats($$2.getPitch(), $$5.getPitch()), $$5.getWeight(), Sound.Type.FILE, $$2.shouldStream() || $$5.shouldStream(), $$2.shouldPreload(), $$2.getAttenuationDistance());
                            }

                            @Override
                            public void preloadIfRequired(SoundEngine $$0) {
                                WeighedSoundEvents $$1 = registry.get($$6);
                                if ($$1 == null) {
                                    return;
                                }
                                $$1.preloadIfRequired($$0);
                            }

                            @Override
                            public /* synthetic */ Object getSound(RandomSource randomSource) {
                                return this.getSound(randomSource);
                            }
                        };
                        break;
                    }
                    default: {
                        throw new IllegalStateException("Unknown SoundEventRegistration type: " + String.valueOf((Object)$$5.getType()));
                    }
                }
                $$2.addSound((Weighted<Sound>)$$9);
            }
        }

        public void apply(Map<ResourceLocation, WeighedSoundEvents> $$0, Map<ResourceLocation, Resource> $$1, SoundEngine $$2) {
            $$0.clear();
            $$1.clear();
            $$1.putAll(this.soundCache);
            for (Map.Entry<ResourceLocation, WeighedSoundEvents> $$3 : this.registry.entrySet()) {
                $$0.put($$3.getKey(), $$3.getValue());
                $$3.getValue().preloadIfRequired($$2);
            }
        }
    }
}

