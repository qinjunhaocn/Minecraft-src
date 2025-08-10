/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.client.sounds;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.audio.Channel;
import com.mojang.blaze3d.audio.Library;
import com.mojang.blaze3d.audio.Listener;
import com.mojang.blaze3d.audio.ListenerTransform;
import com.mojang.blaze3d.audio.SoundBuffer;
import com.mojang.logging.LogUtils;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Options;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.client.sounds.SoundEngineExecutor;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class SoundEngine {
    private static final Marker MARKER = MarkerFactory.getMarker("SOUNDS");
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final float PITCH_MIN = 0.5f;
    private static final float PITCH_MAX = 2.0f;
    private static final float VOLUME_MIN = 0.0f;
    private static final float VOLUME_MAX = 1.0f;
    private static final int MIN_SOURCE_LIFETIME = 20;
    private static final Set<ResourceLocation> ONLY_WARN_ONCE = Sets.newHashSet();
    private static final long DEFAULT_DEVICE_CHECK_INTERVAL_MS = 1000L;
    public static final String MISSING_SOUND = "FOR THE DEBUG!";
    public static final String OPEN_AL_SOFT_PREFIX = "OpenAL Soft on ";
    public static final int OPEN_AL_SOFT_PREFIX_LENGTH = "OpenAL Soft on ".length();
    private final MusicManager musicManager;
    private final SoundManager soundManager;
    private final Options options;
    private boolean loaded;
    private final Library library = new Library();
    private final Listener listener = this.library.getListener();
    private final SoundBufferLibrary soundBuffers;
    private final SoundEngineExecutor executor = new SoundEngineExecutor();
    private final ChannelAccess channelAccess = new ChannelAccess(this.library, this.executor);
    private int tickCount;
    private long lastDeviceCheckTime;
    private final AtomicReference<DeviceCheckState> devicePoolState = new AtomicReference<DeviceCheckState>(DeviceCheckState.NO_CHANGE);
    private final Map<SoundInstance, ChannelAccess.ChannelHandle> instanceToChannel = Maps.newHashMap();
    private final Multimap<SoundSource, SoundInstance> instanceBySource = HashMultimap.create();
    private final List<TickableSoundInstance> tickingSounds = Lists.newArrayList();
    private final Map<SoundInstance, Integer> queuedSounds = Maps.newHashMap();
    private final Map<SoundInstance, Integer> soundDeleteTime = Maps.newHashMap();
    private final List<SoundEventListener> listeners = Lists.newArrayList();
    private final List<TickableSoundInstance> queuedTickableSounds = Lists.newArrayList();
    private final List<Sound> preloadQueue = Lists.newArrayList();

    public SoundEngine(MusicManager $$0, SoundManager $$1, Options $$2, ResourceProvider $$3) {
        this.musicManager = $$0;
        this.soundManager = $$1;
        this.options = $$2;
        this.soundBuffers = new SoundBufferLibrary($$3);
    }

    public void reload() {
        ONLY_WARN_ONCE.clear();
        for (SoundEvent $$0 : BuiltInRegistries.SOUND_EVENT) {
            ResourceLocation $$1;
            if ($$0 == SoundEvents.EMPTY || this.soundManager.getSoundEvent($$1 = $$0.location()) != null) continue;
            LOGGER.warn("Missing sound for event: {}", (Object)BuiltInRegistries.SOUND_EVENT.getKey($$0));
            ONLY_WARN_ONCE.add($$1);
        }
        this.destroy();
        this.loadLibrary();
    }

    private synchronized void loadLibrary() {
        if (this.loaded) {
            return;
        }
        try {
            String $$0 = this.options.soundDevice().get();
            this.library.init("".equals($$0) ? null : $$0, this.options.directionalAudio().get());
            this.listener.reset();
            this.listener.setGain(this.options.getSoundSourceVolume(SoundSource.MASTER));
            this.soundBuffers.preload(this.preloadQueue).thenRun(this.preloadQueue::clear);
            this.loaded = true;
            LOGGER.info(MARKER, "Sound engine started");
        } catch (RuntimeException $$1) {
            LOGGER.error(MARKER, "Error starting SoundSystem. Turning off sounds & music", $$1);
        }
    }

    private float getVolume(@Nullable SoundSource $$0) {
        if ($$0 == null || $$0 == SoundSource.MASTER) {
            return 1.0f;
        }
        return this.options.getSoundSourceVolume($$0);
    }

    public void updateCategoryVolume(SoundSource $$02, float $$1) {
        if (!this.loaded) {
            return;
        }
        if ($$02 == SoundSource.MASTER) {
            this.listener.setGain($$1);
            return;
        }
        if ($$02 == SoundSource.MUSIC && this.options.getSoundSourceVolume(SoundSource.MUSIC) > 0.0f) {
            this.musicManager.showNowPlayingToastIfNeeded();
        }
        this.instanceToChannel.forEach(($$0, $$12) -> {
            float $$2 = this.calculateVolume((SoundInstance)$$0);
            $$12.execute($$1 -> $$1.setVolume($$2));
        });
    }

    public void destroy() {
        if (this.loaded) {
            this.stopAll();
            this.soundBuffers.clear();
            this.library.cleanup();
            this.loaded = false;
        }
    }

    public void emergencyShutdown() {
        if (this.loaded) {
            this.library.cleanup();
        }
    }

    public void stop(SoundInstance $$0) {
        ChannelAccess.ChannelHandle $$1;
        if (this.loaded && ($$1 = this.instanceToChannel.get($$0)) != null) {
            $$1.execute(Channel::stop);
        }
    }

    public void setVolume(SoundInstance $$0, float $$1) {
        ChannelAccess.ChannelHandle $$22;
        if (this.loaded && ($$22 = this.instanceToChannel.get($$0)) != null) {
            $$22.execute($$2 -> $$2.setVolume($$1 * this.calculateVolume($$0)));
        }
    }

    public void stopAll() {
        if (this.loaded) {
            this.executor.flush();
            this.instanceToChannel.values().forEach($$0 -> $$0.execute(Channel::stop));
            this.instanceToChannel.clear();
            this.channelAccess.clear();
            this.queuedSounds.clear();
            this.tickingSounds.clear();
            this.instanceBySource.clear();
            this.soundDeleteTime.clear();
            this.queuedTickableSounds.clear();
        }
    }

    public void addEventListener(SoundEventListener $$0) {
        this.listeners.add($$0);
    }

    public void removeEventListener(SoundEventListener $$0) {
        this.listeners.remove($$0);
    }

    private boolean shouldChangeDevice() {
        boolean $$1;
        if (this.library.isCurrentDeviceDisconnected()) {
            LOGGER.info("Audio device was lost!");
            return true;
        }
        long $$0 = Util.getMillis();
        boolean bl = $$1 = $$0 - this.lastDeviceCheckTime >= 1000L;
        if ($$1) {
            this.lastDeviceCheckTime = $$0;
            if (this.devicePoolState.compareAndSet(DeviceCheckState.NO_CHANGE, DeviceCheckState.ONGOING)) {
                String $$2 = this.options.soundDevice().get();
                Util.ioPool().execute(() -> {
                    if ("".equals($$2)) {
                        if (this.library.hasDefaultDeviceChanged()) {
                            LOGGER.info("System default audio device has changed!");
                            this.devicePoolState.compareAndSet(DeviceCheckState.ONGOING, DeviceCheckState.CHANGE_DETECTED);
                        }
                    } else if (!this.library.getCurrentDeviceName().equals($$2) && this.library.getAvailableSoundDevices().contains($$2)) {
                        LOGGER.info("Preferred audio device has become available!");
                        this.devicePoolState.compareAndSet(DeviceCheckState.ONGOING, DeviceCheckState.CHANGE_DETECTED);
                    }
                    this.devicePoolState.compareAndSet(DeviceCheckState.ONGOING, DeviceCheckState.NO_CHANGE);
                });
            }
        }
        return this.devicePoolState.compareAndSet(DeviceCheckState.CHANGE_DETECTED, DeviceCheckState.NO_CHANGE);
    }

    public void tick(boolean $$0) {
        if (this.shouldChangeDevice()) {
            this.reload();
        }
        if (!$$0) {
            this.tickInGameSound();
        } else {
            this.tickMusicWhenPaused();
        }
        this.channelAccess.scheduleTick();
    }

    private void tickInGameSound() {
        ++this.tickCount;
        this.queuedTickableSounds.stream().filter(SoundInstance::canPlaySound).forEach(this::play);
        this.queuedTickableSounds.clear();
        for (TickableSoundInstance $$0 : this.tickingSounds) {
            if (!$$0.canPlaySound()) {
                this.stop($$0);
            }
            $$0.tick();
            if ($$0.isStopped()) {
                this.stop($$0);
                continue;
            }
            float $$1 = this.calculateVolume($$0);
            float $$2 = this.calculatePitch($$0);
            Vec3 $$32 = new Vec3($$0.getX(), $$0.getY(), $$0.getZ());
            ChannelAccess.ChannelHandle $$4 = this.instanceToChannel.get($$0);
            if ($$4 == null) continue;
            $$4.execute($$3 -> {
                $$3.setVolume($$1);
                $$3.setPitch($$2);
                $$3.setSelfPosition($$32);
            });
        }
        Iterator<Map.Entry<SoundInstance, ChannelAccess.ChannelHandle>> $$5 = this.instanceToChannel.entrySet().iterator();
        while ($$5.hasNext()) {
            int $$9;
            Map.Entry<SoundInstance, ChannelAccess.ChannelHandle> $$6 = $$5.next();
            ChannelAccess.ChannelHandle $$7 = $$6.getValue();
            SoundInstance $$8 = $$6.getKey();
            if (!$$7.isStopped() || ($$9 = this.soundDeleteTime.get($$8).intValue()) > this.tickCount) continue;
            if (SoundEngine.shouldLoopManually($$8)) {
                this.queuedSounds.put($$8, this.tickCount + $$8.getDelay());
            }
            $$5.remove();
            LOGGER.debug(MARKER, "Removed channel {} because it's not playing anymore", (Object)$$7);
            this.soundDeleteTime.remove($$8);
            try {
                this.instanceBySource.remove((Object)$$8.getSource(), $$8);
            } catch (RuntimeException runtimeException) {
                // empty catch block
            }
            if (!($$8 instanceof TickableSoundInstance)) continue;
            this.tickingSounds.remove($$8);
        }
        Iterator<Map.Entry<SoundInstance, Integer>> $$10 = this.queuedSounds.entrySet().iterator();
        while ($$10.hasNext()) {
            Map.Entry<SoundInstance, Integer> $$11 = $$10.next();
            if (this.tickCount < $$11.getValue()) continue;
            SoundInstance $$12 = $$11.getKey();
            if ($$12 instanceof TickableSoundInstance) {
                ((TickableSoundInstance)$$12).tick();
            }
            this.play($$12);
            $$10.remove();
        }
    }

    private void tickMusicWhenPaused() {
        Iterator<Map.Entry<SoundInstance, ChannelAccess.ChannelHandle>> $$0 = this.instanceToChannel.entrySet().iterator();
        while ($$0.hasNext()) {
            Map.Entry<SoundInstance, ChannelAccess.ChannelHandle> $$1 = $$0.next();
            ChannelAccess.ChannelHandle $$2 = $$1.getValue();
            SoundInstance $$3 = $$1.getKey();
            if ($$3.getSource() != SoundSource.MUSIC || !$$2.isStopped()) continue;
            $$0.remove();
            LOGGER.debug(MARKER, "Removed channel {} because it's not playing anymore", (Object)$$2);
            this.soundDeleteTime.remove($$3);
            this.instanceBySource.remove((Object)$$3.getSource(), $$3);
        }
    }

    private static boolean requiresManualLooping(SoundInstance $$0) {
        return $$0.getDelay() > 0;
    }

    private static boolean shouldLoopManually(SoundInstance $$0) {
        return $$0.isLooping() && SoundEngine.requiresManualLooping($$0);
    }

    private static boolean shouldLoopAutomatically(SoundInstance $$0) {
        return $$0.isLooping() && !SoundEngine.requiresManualLooping($$0);
    }

    public boolean isActive(SoundInstance $$0) {
        if (!this.loaded) {
            return false;
        }
        if (this.soundDeleteTime.containsKey($$0) && this.soundDeleteTime.get($$0) <= this.tickCount) {
            return true;
        }
        return this.instanceToChannel.containsKey($$0);
    }

    public PlayResult play(SoundInstance $$0) {
        if (!this.loaded) {
            return PlayResult.NOT_STARTED;
        }
        if (!$$0.canPlaySound()) {
            return PlayResult.NOT_STARTED;
        }
        WeighedSoundEvents $$1 = $$0.resolve(this.soundManager);
        ResourceLocation $$2 = $$0.getLocation();
        if ($$1 == null) {
            if (ONLY_WARN_ONCE.add($$2)) {
                LOGGER.warn(MARKER, "Unable to play unknown soundEvent: {}", (Object)$$2);
            }
            return PlayResult.NOT_STARTED;
        }
        Sound $$3 = $$0.getSound();
        if ($$3 == SoundManager.INTENTIONALLY_EMPTY_SOUND) {
            return PlayResult.NOT_STARTED;
        }
        if ($$3 == SoundManager.EMPTY_SOUND) {
            if (ONLY_WARN_ONCE.add($$2)) {
                LOGGER.warn(MARKER, "Unable to play empty soundEvent: {}", (Object)$$2);
            }
            return PlayResult.NOT_STARTED;
        }
        float $$4 = $$0.getVolume();
        float $$5 = Math.max($$4, 1.0f) * (float)$$3.getAttenuationDistance();
        SoundSource $$6 = $$0.getSource();
        float $$7 = this.calculateVolume($$4, $$6);
        float $$82 = this.calculatePitch($$0);
        SoundInstance.Attenuation $$9 = $$0.getAttenuation();
        boolean $$10 = $$0.isRelative();
        if (!this.listeners.isEmpty()) {
            float $$11 = $$10 || $$9 == SoundInstance.Attenuation.NONE ? Float.POSITIVE_INFINITY : $$5;
            for (SoundEventListener $$122 : this.listeners) {
                $$122.onPlaySound($$0, $$1, $$11);
            }
        }
        boolean $$13 = false;
        if ($$7 == 0.0f) {
            if ($$0.canStartSilent() || $$6 == SoundSource.MUSIC) {
                $$13 = true;
            } else {
                LOGGER.debug(MARKER, "Skipped playing sound {}, volume was zero.", (Object)$$3.getLocation());
                return PlayResult.NOT_STARTED;
            }
        }
        Vec3 $$14 = new Vec3($$0.getX(), $$0.getY(), $$0.getZ());
        if (this.listener.getGain() <= 0.0f && $$6 != SoundSource.MUSIC) {
            LOGGER.debug(MARKER, "Skipped playing soundEvent: {}, master volume was zero", (Object)$$2);
            return PlayResult.NOT_STARTED;
        }
        boolean $$15 = SoundEngine.shouldLoopAutomatically($$0);
        boolean $$16 = $$3.shouldStream();
        CompletableFuture<ChannelAccess.ChannelHandle> $$17 = this.channelAccess.createHandle($$3.shouldStream() ? Library.Pool.STREAMING : Library.Pool.STATIC);
        ChannelAccess.ChannelHandle $$18 = $$17.join();
        if ($$18 == null) {
            if (SharedConstants.IS_RUNNING_IN_IDE) {
                LOGGER.warn("Failed to create new sound handle");
            }
            return PlayResult.NOT_STARTED;
        }
        LOGGER.debug(MARKER, "Playing sound {} for event {}", (Object)$$3.getLocation(), (Object)$$2);
        this.soundDeleteTime.put($$0, this.tickCount + 20);
        this.instanceToChannel.put($$0, $$18);
        this.instanceBySource.put($$6, $$0);
        $$18.execute($$8 -> {
            $$8.setPitch($$82);
            $$8.setVolume($$7);
            if ($$9 == SoundInstance.Attenuation.LINEAR) {
                $$8.linearAttenuation($$5);
            } else {
                $$8.disableAttenuation();
            }
            $$8.setLooping($$15 && !$$16);
            $$8.setSelfPosition($$14);
            $$8.setRelative($$10);
        });
        if (!$$16) {
            this.soundBuffers.getCompleteBuffer($$3.getPath()).thenAccept($$12 -> $$18.execute($$1 -> {
                $$1.attachStaticBuffer((SoundBuffer)$$12);
                $$1.play();
            }));
        } else {
            this.soundBuffers.getStream($$3.getPath(), $$15).thenAccept($$12 -> $$18.execute($$1 -> {
                $$1.attachBufferStream((AudioStream)$$12);
                $$1.play();
            }));
        }
        if ($$0 instanceof TickableSoundInstance) {
            this.tickingSounds.add((TickableSoundInstance)$$0);
        }
        if ($$13) {
            return PlayResult.STARTED_SILENTLY;
        }
        return PlayResult.STARTED;
    }

    public void queueTickingSound(TickableSoundInstance $$0) {
        this.queuedTickableSounds.add($$0);
    }

    public void requestPreload(Sound $$0) {
        this.preloadQueue.add($$0);
    }

    private float calculatePitch(SoundInstance $$0) {
        return Mth.clamp($$0.getPitch(), 0.5f, 2.0f);
    }

    private float calculateVolume(SoundInstance $$0) {
        return this.calculateVolume($$0.getVolume(), $$0.getSource());
    }

    private float calculateVolume(float $$0, SoundSource $$1) {
        return Mth.clamp($$0 * this.getVolume($$1), 0.0f, 1.0f);
    }

    public void a(SoundSource ... $$0) {
        if (!this.loaded) {
            return;
        }
        for (Map.Entry<SoundInstance, ChannelAccess.ChannelHandle> $$1 : this.instanceToChannel.entrySet()) {
            if (List.of((Object[])$$0).contains((Object)$$1.getKey().getSource())) continue;
            $$1.getValue().execute(Channel::pause);
        }
    }

    public void resume() {
        if (this.loaded) {
            this.channelAccess.executeOnChannels($$0 -> $$0.forEach(Channel::unpause));
        }
    }

    public void playDelayed(SoundInstance $$0, int $$1) {
        this.queuedSounds.put($$0, this.tickCount + $$1);
    }

    public void updateSource(Camera $$0) {
        if (!this.loaded || !$$0.isInitialized()) {
            return;
        }
        ListenerTransform $$1 = new ListenerTransform($$0.getPosition(), new Vec3($$0.getLookVector()), new Vec3($$0.getUpVector()));
        this.executor.execute(() -> this.listener.setTransform($$1));
    }

    public void stop(@Nullable ResourceLocation $$0, @Nullable SoundSource $$1) {
        if ($$1 != null) {
            for (SoundInstance $$2 : this.instanceBySource.get($$1)) {
                if ($$0 != null && !$$2.getLocation().equals($$0)) continue;
                this.stop($$2);
            }
        } else if ($$0 == null) {
            this.stopAll();
        } else {
            for (SoundInstance $$3 : this.instanceToChannel.keySet()) {
                if (!$$3.getLocation().equals($$0)) continue;
                this.stop($$3);
            }
        }
    }

    public String getDebugString() {
        return this.library.getDebugString();
    }

    public List<String> getAvailableSoundDevices() {
        return this.library.getAvailableSoundDevices();
    }

    public ListenerTransform getListenerTransform() {
        return this.listener.getTransform();
    }

    static final class DeviceCheckState
    extends Enum<DeviceCheckState> {
        public static final /* enum */ DeviceCheckState ONGOING = new DeviceCheckState();
        public static final /* enum */ DeviceCheckState CHANGE_DETECTED = new DeviceCheckState();
        public static final /* enum */ DeviceCheckState NO_CHANGE = new DeviceCheckState();
        private static final /* synthetic */ DeviceCheckState[] $VALUES;

        public static DeviceCheckState[] values() {
            return (DeviceCheckState[])$VALUES.clone();
        }

        public static DeviceCheckState valueOf(String $$0) {
            return Enum.valueOf(DeviceCheckState.class, $$0);
        }

        private static /* synthetic */ DeviceCheckState[] a() {
            return new DeviceCheckState[]{ONGOING, CHANGE_DETECTED, NO_CHANGE};
        }

        static {
            $VALUES = DeviceCheckState.a();
        }
    }

    public static final class PlayResult
    extends Enum<PlayResult> {
        public static final /* enum */ PlayResult STARTED = new PlayResult();
        public static final /* enum */ PlayResult STARTED_SILENTLY = new PlayResult();
        public static final /* enum */ PlayResult NOT_STARTED = new PlayResult();
        private static final /* synthetic */ PlayResult[] $VALUES;

        public static PlayResult[] values() {
            return (PlayResult[])$VALUES.clone();
        }

        public static PlayResult valueOf(String $$0) {
            return Enum.valueOf(PlayResult.class, $$0);
        }

        private static /* synthetic */ PlayResult[] a() {
            return new PlayResult[]{STARTED, STARTED_SILENTLY, NOT_STARTED};
        }

        static {
            $VALUES = PlayResult.a();
        }
    }
}

