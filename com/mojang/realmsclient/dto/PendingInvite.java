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
import java.util.Date;
import java.util.UUID;
import net.minecraft.Util;
import org.slf4j.Logger;

public class PendingInvite
extends ValueObject {
    private static final Logger LOGGER = LogUtils.getLogger();
    public String invitationId;
    public String realmName;
    public String realmOwnerName;
    public UUID realmOwnerUuid;
    public Date date;

    public static PendingInvite parse(JsonObject $$0) {
        PendingInvite $$1 = new PendingInvite();
        try {
            $$1.invitationId = JsonUtils.getStringOr("invitationId", $$0, "");
            $$1.realmName = JsonUtils.getStringOr("worldName", $$0, "");
            $$1.realmOwnerName = JsonUtils.getStringOr("worldOwnerName", $$0, "");
            $$1.realmOwnerUuid = JsonUtils.getUuidOr("worldOwnerUuid", $$0, Util.NIL_UUID);
            $$1.date = JsonUtils.getDateOr("date", $$0);
        } catch (Exception $$2) {
            LOGGER.error("Could not parse PendingInvite: {}", (Object)$$2.getMessage());
        }
        return $$1;
    }
}

