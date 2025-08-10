/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.yggdrasil.request.AbuseReportRequest$ClientInfo
 *  com.mojang.authlib.yggdrasil.request.AbuseReportRequest$RealmInfo
 *  com.mojang.authlib.yggdrasil.request.AbuseReportRequest$ThirdPartyServerInfo
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.client.multiplayer.chat.report;

import com.mojang.authlib.yggdrasil.request.AbuseReportRequest;
import com.mojang.realmsclient.dto.RealmsServer;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;

public record ReportEnvironment(String clientVersion, @Nullable Server server) {
    public static ReportEnvironment local() {
        return ReportEnvironment.create(null);
    }

    public static ReportEnvironment thirdParty(String $$0) {
        return ReportEnvironment.create(new Server.ThirdParty($$0));
    }

    public static ReportEnvironment realm(RealmsServer $$0) {
        return ReportEnvironment.create(new Server.Realm($$0));
    }

    public static ReportEnvironment create(@Nullable Server $$0) {
        return new ReportEnvironment(ReportEnvironment.getClientVersion(), $$0);
    }

    public AbuseReportRequest.ClientInfo clientInfo() {
        return new AbuseReportRequest.ClientInfo(this.clientVersion, Locale.getDefault().toLanguageTag());
    }

    @Nullable
    public AbuseReportRequest.ThirdPartyServerInfo thirdPartyServerInfo() {
        Server server = this.server;
        if (server instanceof Server.ThirdParty) {
            Server.ThirdParty $$0 = (Server.ThirdParty)server;
            return new AbuseReportRequest.ThirdPartyServerInfo($$0.ip);
        }
        return null;
    }

    @Nullable
    public AbuseReportRequest.RealmInfo realmInfo() {
        Server server = this.server;
        if (server instanceof Server.Realm) {
            Server.Realm $$0 = (Server.Realm)server;
            return new AbuseReportRequest.RealmInfo(String.valueOf($$0.realmId()), $$0.slotId());
        }
        return null;
    }

    private static String getClientVersion() {
        StringBuilder $$0 = new StringBuilder();
        $$0.append("1.21.8");
        if (Minecraft.checkModStatus().shouldReportAsModified()) {
            $$0.append(" (modded)");
        }
        return $$0.toString();
    }

    @Nullable
    public Server server() {
        return this.server;
    }

    public static interface Server {

        public record Realm(long realmId, int slotId) implements Server
        {
            public Realm(RealmsServer $$0) {
                this($$0.id, $$0.activeSlot);
            }
        }

        public static final class ThirdParty
        extends Record
        implements Server {
            final String ip;

            public ThirdParty(String $$0) {
                this.ip = $$0;
            }

            public final String toString() {
                return ObjectMethods.bootstrap("toString", new MethodHandle[]{ThirdParty.class, "ip", "ip"}, this);
            }

            public final int hashCode() {
                return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ThirdParty.class, "ip", "ip"}, this);
            }

            public final boolean equals(Object $$0) {
                return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ThirdParty.class, "ip", "ip"}, this, $$0);
            }

            public String ip() {
                return this.ip;
            }
        }
    }
}

