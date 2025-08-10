/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.network.protocol.game;

import com.mojang.logging.LogUtils;
import net.minecraft.ReportedException;
import net.minecraft.network.ServerboundPacketListener;
import net.minecraft.network.protocol.Packet;
import org.slf4j.Logger;

public interface ServerPacketListener
extends ServerboundPacketListener {
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    default public void onPacketError(Packet $$0, Exception $$1) throws ReportedException {
        LOGGER.error("Failed to handle packet {}, suppressing error", (Object)$$0, (Object)$$1);
    }
}

