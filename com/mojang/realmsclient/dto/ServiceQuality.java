/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.TypeAdapter
 *  com.google.gson.stream.JsonReader
 *  com.google.gson.stream.JsonWriter
 *  com.mojang.logging.LogUtils
 */
package com.mojang.realmsclient.dto;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public final class ServiceQuality
extends Enum<ServiceQuality> {
    public static final /* enum */ ServiceQuality GREAT = new ServiceQuality(1, "icon/ping_5");
    public static final /* enum */ ServiceQuality GOOD = new ServiceQuality(2, "icon/ping_4");
    public static final /* enum */ ServiceQuality OKAY = new ServiceQuality(3, "icon/ping_3");
    public static final /* enum */ ServiceQuality POOR = new ServiceQuality(4, "icon/ping_2");
    public static final /* enum */ ServiceQuality UNKNOWN = new ServiceQuality(5, "icon/ping_unknown");
    final int value;
    private final ResourceLocation icon;
    private static final /* synthetic */ ServiceQuality[] $VALUES;

    public static ServiceQuality[] values() {
        return (ServiceQuality[])$VALUES.clone();
    }

    public static ServiceQuality valueOf(String $$0) {
        return Enum.valueOf(ServiceQuality.class, $$0);
    }

    private ServiceQuality(int $$0, String $$1) {
        this.value = $$0;
        this.icon = ResourceLocation.withDefaultNamespace($$1);
    }

    @Nullable
    public static ServiceQuality byValue(int $$0) {
        for (ServiceQuality $$1 : ServiceQuality.values()) {
            if ($$1.getValue() != $$0) continue;
            return $$1;
        }
        return null;
    }

    public int getValue() {
        return this.value;
    }

    public ResourceLocation getIcon() {
        return this.icon;
    }

    private static /* synthetic */ ServiceQuality[] c() {
        return new ServiceQuality[]{GREAT, GOOD, OKAY, POOR, UNKNOWN};
    }

    static {
        $VALUES = ServiceQuality.c();
    }

    public static class RealmsServiceQualityJsonAdapter
    extends TypeAdapter<ServiceQuality> {
        private static final Logger LOGGER = LogUtils.getLogger();

        public void write(JsonWriter $$0, ServiceQuality $$1) throws IOException {
            $$0.value((long)$$1.value);
        }

        public ServiceQuality read(JsonReader $$0) throws IOException {
            int $$1 = $$0.nextInt();
            ServiceQuality $$2 = ServiceQuality.byValue($$1);
            if ($$2 == null) {
                LOGGER.warn("Unsupported ServiceQuality {}", (Object)$$1);
                return UNKNOWN;
            }
            return $$2;
        }

        public /* synthetic */ Object read(JsonReader jsonReader) throws IOException {
            return this.read(jsonReader);
        }

        public /* synthetic */ void write(JsonWriter jsonWriter, Object object) throws IOException {
            this.write(jsonWriter, (ServiceQuality)((Object)object));
        }
    }
}

