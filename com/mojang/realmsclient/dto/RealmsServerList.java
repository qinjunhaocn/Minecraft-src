/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 *  com.mojang.logging.LogUtils
 */
package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.dto.GuardedSerializer;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.ReflectionBasedSerialization;
import com.mojang.realmsclient.dto.ValueObject;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;

public class RealmsServerList
extends ValueObject
implements ReflectionBasedSerialization {
    private static final Logger LOGGER = LogUtils.getLogger();
    @SerializedName(value="servers")
    public List<RealmsServer> servers = new ArrayList<RealmsServer>();

    public static RealmsServerList parse(GuardedSerializer $$0, String $$1) {
        try {
            RealmsServerList $$2 = $$0.fromJson($$1, RealmsServerList.class);
            if ($$2 == null) {
                LOGGER.error("Could not parse McoServerList: {}", (Object)$$1);
                return new RealmsServerList();
            }
            $$2.servers.forEach(RealmsServer::finalize);
            return $$2;
        } catch (Exception $$3) {
            LOGGER.error("Could not parse McoServerList: {}", (Object)$$3.getMessage());
            return new RealmsServerList();
        }
    }
}

