/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.sounds;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.audio.SoundBuffer;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import net.minecraft.Util;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.JOrbisAudioStream;
import net.minecraft.client.sounds.LoopingAudioStream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;

public class SoundBufferLibrary {
    private final ResourceProvider resourceManager;
    private final Map<ResourceLocation, CompletableFuture<SoundBuffer>> cache = Maps.newHashMap();

    public SoundBufferLibrary(ResourceProvider $$0) {
        this.resourceManager = $$0;
    }

    public CompletableFuture<SoundBuffer> getCompleteBuffer(ResourceLocation $$02) {
        return this.cache.computeIfAbsent($$02, $$0 -> CompletableFuture.supplyAsync(() -> {
            try (InputStream $$1 = this.resourceManager.open((ResourceLocation)$$0);){
                SoundBuffer soundBuffer;
                try (JOrbisAudioStream $$2 = new JOrbisAudioStream($$1);){
                    ByteBuffer $$3 = $$2.readAll();
                    soundBuffer = new SoundBuffer($$3, $$2.getFormat());
                }
                return soundBuffer;
            } catch (IOException $$4) {
                throw new CompletionException($$4);
            }
        }, Util.nonCriticalIoPool()));
    }

    public CompletableFuture<AudioStream> getStream(ResourceLocation $$0, boolean $$1) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                InputStream $$2 = this.resourceManager.open($$0);
                return $$1 ? new LoopingAudioStream(JOrbisAudioStream::new, $$2) : new JOrbisAudioStream($$2);
            } catch (IOException $$3) {
                throw new CompletionException($$3);
            }
        }, Util.nonCriticalIoPool());
    }

    public void clear() {
        this.cache.values().forEach($$0 -> $$0.thenAccept(SoundBuffer::discardAlBuffer));
        this.cache.clear();
    }

    public CompletableFuture<?> preload(Collection<Sound> $$02) {
        return CompletableFuture.allOf((CompletableFuture[])$$02.stream().map($$0 -> this.getCompleteBuffer($$0.getPath())).toArray(CompletableFuture[]::new));
    }
}

