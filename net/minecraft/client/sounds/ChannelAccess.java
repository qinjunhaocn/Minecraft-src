/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.sounds;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.audio.Channel;
import com.mojang.blaze3d.audio.Library;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class ChannelAccess {
    private final Set<ChannelHandle> channels = Sets.newIdentityHashSet();
    final Library library;
    final Executor executor;

    public ChannelAccess(Library $$0, Executor $$1) {
        this.library = $$0;
        this.executor = $$1;
    }

    public CompletableFuture<ChannelHandle> createHandle(Library.Pool $$0) {
        CompletableFuture<ChannelHandle> $$1 = new CompletableFuture<ChannelHandle>();
        this.executor.execute(() -> {
            Channel $$2 = this.library.acquireChannel($$0);
            if ($$2 != null) {
                ChannelHandle $$3 = new ChannelHandle($$2);
                this.channels.add($$3);
                $$1.complete($$3);
            } else {
                $$1.complete(null);
            }
        });
        return $$1;
    }

    public void executeOnChannels(Consumer<Stream<Channel>> $$0) {
        this.executor.execute(() -> $$0.accept(this.channels.stream().map($$0 -> $$0.channel).filter(Objects::nonNull)));
    }

    public void scheduleTick() {
        this.executor.execute(() -> {
            Iterator<ChannelHandle> $$0 = this.channels.iterator();
            while ($$0.hasNext()) {
                ChannelHandle $$1 = $$0.next();
                $$1.channel.updateStream();
                if (!$$1.channel.stopped()) continue;
                $$1.release();
                $$0.remove();
            }
        });
    }

    public void clear() {
        this.channels.forEach(ChannelHandle::release);
        this.channels.clear();
    }

    public class ChannelHandle {
        @Nullable
        Channel channel;
        private boolean stopped;

        public boolean isStopped() {
            return this.stopped;
        }

        public ChannelHandle(Channel $$1) {
            this.channel = $$1;
        }

        public void execute(Consumer<Channel> $$0) {
            ChannelAccess.this.executor.execute(() -> {
                if (this.channel != null) {
                    $$0.accept(this.channel);
                }
            });
        }

        public void release() {
            this.stopped = true;
            ChannelAccess.this.library.releaseChannel(this.channel);
            this.channel = null;
        }
    }
}

