/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.objects.Object2BooleanFunction
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.client;

import com.google.common.collect.ImmutableMap;
import com.google.common.math.LongMath;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2BooleanFunction;
import java.io.BufferedReader;
import java.io.Reader;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

public class PeriodicNotificationManager
extends SimplePreparableReloadListener<Map<String, List<Notification>>>
implements AutoCloseable {
    private static final Codec<Map<String, List<Notification>>> CODEC = Codec.unboundedMap((Codec)Codec.STRING, (Codec)RecordCodecBuilder.create($$0 -> $$0.group((App)Codec.LONG.optionalFieldOf("delay", (Object)0L).forGetter(Notification::delay), (App)Codec.LONG.fieldOf("period").forGetter(Notification::period), (App)Codec.STRING.fieldOf("title").forGetter(Notification::title), (App)Codec.STRING.fieldOf("message").forGetter(Notification::message)).apply((Applicative)$$0, Notification::new)).listOf());
    private static final Logger LOGGER = LogUtils.getLogger();
    private final ResourceLocation notifications;
    private final Object2BooleanFunction<String> selector;
    @Nullable
    private Timer timer;
    @Nullable
    private NotificationTask notificationTask;

    public PeriodicNotificationManager(ResourceLocation $$0, Object2BooleanFunction<String> $$1) {
        this.notifications = $$0;
        this.selector = $$1;
    }

    @Override
    protected Map<String, List<Notification>> prepare(ResourceManager $$0, ProfilerFiller $$1) {
        Map map;
        block8: {
            BufferedReader $$2 = $$0.openAsReader(this.notifications);
            try {
                map = (Map)CODEC.parse((DynamicOps)JsonOps.INSTANCE, (Object)StrictJsonParser.parse($$2)).result().orElseThrow();
                if ($$2 == null) break block8;
            } catch (Throwable throwable) {
                try {
                    if ($$2 != null) {
                        try {
                            ((Reader)$$2).close();
                        } catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                } catch (Exception $$3) {
                    LOGGER.warn("Failed to load {}", (Object)this.notifications, (Object)$$3);
                    return ImmutableMap.of();
                }
            }
            ((Reader)$$2).close();
        }
        return map;
    }

    @Override
    protected void apply(Map<String, List<Notification>> $$02, ResourceManager $$1, ProfilerFiller $$2) {
        List<Notification> $$3 = $$02.entrySet().stream().filter($$0 -> (Boolean)this.selector.apply((Object)((String)$$0.getKey()))).map(Map.Entry::getValue).flatMap(Collection::stream).collect(Collectors.toList());
        if ($$3.isEmpty()) {
            this.stopTimer();
            return;
        }
        if ($$3.stream().anyMatch($$0 -> $$0.period == 0L)) {
            Util.logAndPauseIfInIde("A periodic notification in " + String.valueOf(this.notifications) + " has a period of zero minutes");
            this.stopTimer();
            return;
        }
        long $$4 = this.calculateInitialDelay($$3);
        long $$5 = this.calculateOptimalPeriod($$3, $$4);
        if (this.timer == null) {
            this.timer = new Timer();
        }
        this.notificationTask = this.notificationTask == null ? new NotificationTask($$3, $$4, $$5) : this.notificationTask.reset($$3, $$5);
        this.timer.scheduleAtFixedRate((TimerTask)this.notificationTask, TimeUnit.MINUTES.toMillis($$4), TimeUnit.MINUTES.toMillis($$5));
    }

    @Override
    public void close() {
        this.stopTimer();
    }

    private void stopTimer() {
        if (this.timer != null) {
            this.timer.cancel();
        }
    }

    private long calculateOptimalPeriod(List<Notification> $$0, long $$12) {
        return $$0.stream().mapToLong($$1 -> {
            long $$2 = $$1.delay - $$12;
            return LongMath.gcd($$2, $$1.period);
        }).reduce(LongMath::gcd).orElseThrow(() -> new IllegalStateException("Empty notifications from: " + String.valueOf(this.notifications)));
    }

    private long calculateInitialDelay(List<Notification> $$02) {
        return $$02.stream().mapToLong($$0 -> $$0.delay).min().orElse(0L);
    }

    @Override
    protected /* synthetic */ Object prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        return this.prepare(resourceManager, profilerFiller);
    }

    static class NotificationTask
    extends TimerTask {
        private final Minecraft minecraft = Minecraft.getInstance();
        private final List<Notification> notifications;
        private final long period;
        private final AtomicLong elapsed;

        public NotificationTask(List<Notification> $$0, long $$1, long $$2) {
            this.notifications = $$0;
            this.period = $$2;
            this.elapsed = new AtomicLong($$1);
        }

        public NotificationTask reset(List<Notification> $$0, long $$1) {
            this.cancel();
            return new NotificationTask($$0, this.elapsed.get(), $$1);
        }

        @Override
        public void run() {
            long $$0 = this.elapsed.getAndAdd(this.period);
            long $$1 = this.elapsed.get();
            for (Notification $$2 : this.notifications) {
                long $$4;
                long $$3;
                if ($$0 < $$2.delay || ($$3 = $$0 / $$2.period) == ($$4 = $$1 / $$2.period)) continue;
                this.minecraft.execute(() -> SystemToast.add(Minecraft.getInstance().getToastManager(), SystemToast.SystemToastId.PERIODIC_NOTIFICATION, Component.a($$0.title, $$3), Component.a($$0.message, $$3)));
                return;
            }
        }
    }

    public static final class Notification
    extends Record {
        final long delay;
        final long period;
        final String title;
        final String message;

        public Notification(long $$0, long $$1, String $$2, String $$3) {
            this.delay = $$0 != 0L ? $$0 : $$1;
            this.period = $$1;
            this.title = $$2;
            this.message = $$3;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Notification.class, "delay;period;title;message", "delay", "period", "title", "message"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Notification.class, "delay;period;title;message", "delay", "period", "title", "message"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Notification.class, "delay;period;title;message", "delay", "period", "title", "message"}, this, $$0);
        }

        public long delay() {
            return this.delay;
        }

        public long period() {
            return this.period;
        }

        public String title() {
            return this.title;
        }

        public String message() {
            return this.message;
        }
    }
}

