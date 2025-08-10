/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 */
package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.util.JsonUtils;
import net.minecraft.util.LenientJsonParser;
import org.slf4j.Logger;

public class Subscription
extends ValueObject {
    private static final Logger LOGGER = LogUtils.getLogger();
    public long startDate;
    public int daysLeft;
    public SubscriptionType type = SubscriptionType.NORMAL;

    public static Subscription parse(String $$0) {
        Subscription $$1 = new Subscription();
        try {
            JsonObject $$2 = LenientJsonParser.parse($$0).getAsJsonObject();
            $$1.startDate = JsonUtils.getLongOr("startDate", $$2, 0L);
            $$1.daysLeft = JsonUtils.getIntOr("daysLeft", $$2, 0);
            $$1.type = Subscription.typeFrom(JsonUtils.getStringOr("subscriptionType", $$2, SubscriptionType.NORMAL.name()));
        } catch (Exception $$3) {
            LOGGER.error("Could not parse Subscription: {}", (Object)$$3.getMessage());
        }
        return $$1;
    }

    private static SubscriptionType typeFrom(String $$0) {
        try {
            return SubscriptionType.valueOf($$0);
        } catch (Exception $$1) {
            return SubscriptionType.NORMAL;
        }
    }

    public static final class SubscriptionType
    extends Enum<SubscriptionType> {
        public static final /* enum */ SubscriptionType NORMAL = new SubscriptionType();
        public static final /* enum */ SubscriptionType RECURRING = new SubscriptionType();
        private static final /* synthetic */ SubscriptionType[] $VALUES;

        public static SubscriptionType[] values() {
            return (SubscriptionType[])$VALUES.clone();
        }

        public static SubscriptionType valueOf(String $$0) {
            return Enum.valueOf(SubscriptionType.class, $$0);
        }

        private static /* synthetic */ SubscriptionType[] a() {
            return new SubscriptionType[]{NORMAL, RECURRING};
        }

        static {
            $VALUES = SubscriptionType.a();
        }
    }
}

