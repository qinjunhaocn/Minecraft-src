/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.IOUtils
 */
package com.mojang.realmsclient.client;

import com.google.common.collect.Lists;
import com.mojang.realmsclient.dto.RegionPingResult;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.Util;
import org.apache.commons.io.IOUtils;

public class Ping {
    public static List<RegionPingResult> a(Region ... $$0) {
        for (Region $$1 : $$0) {
            Ping.ping($$1.endpoint);
        }
        ArrayList<RegionPingResult> $$2 = Lists.newArrayList();
        for (Region $$3 : $$0) {
            $$2.add(new RegionPingResult($$3.name, Ping.ping($$3.endpoint)));
        }
        $$2.sort(Comparator.comparingInt(RegionPingResult::ping));
        return $$2;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static int ping(String $$0) {
        int $$1 = 700;
        long $$2 = 0L;
        Socket $$3 = null;
        for (int $$4 = 0; $$4 < 5; ++$$4) {
            try {
                InetSocketAddress $$5 = new InetSocketAddress($$0, 80);
                $$3 = new Socket();
                long $$6 = Ping.now();
                $$3.connect($$5, 700);
                $$2 += Ping.now() - $$6;
                IOUtils.closeQuietly((Socket)$$3);
                continue;
            } catch (Exception $$7) {
                $$2 += 700L;
                continue;
            } finally {
                IOUtils.closeQuietly($$3);
            }
        }
        return (int)((double)$$2 / 5.0);
    }

    private static long now() {
        return Util.getMillis();
    }

    public static List<RegionPingResult> pingAllRegions() {
        return Ping.a(Region.values());
    }

    static final class Region
    extends Enum<Region> {
        public static final /* enum */ Region US_EAST_1 = new Region("us-east-1", "ec2.us-east-1.amazonaws.com");
        public static final /* enum */ Region US_WEST_2 = new Region("us-west-2", "ec2.us-west-2.amazonaws.com");
        public static final /* enum */ Region US_WEST_1 = new Region("us-west-1", "ec2.us-west-1.amazonaws.com");
        public static final /* enum */ Region EU_WEST_1 = new Region("eu-west-1", "ec2.eu-west-1.amazonaws.com");
        public static final /* enum */ Region AP_SOUTHEAST_1 = new Region("ap-southeast-1", "ec2.ap-southeast-1.amazonaws.com");
        public static final /* enum */ Region AP_SOUTHEAST_2 = new Region("ap-southeast-2", "ec2.ap-southeast-2.amazonaws.com");
        public static final /* enum */ Region AP_NORTHEAST_1 = new Region("ap-northeast-1", "ec2.ap-northeast-1.amazonaws.com");
        public static final /* enum */ Region SA_EAST_1 = new Region("sa-east-1", "ec2.sa-east-1.amazonaws.com");
        final String name;
        final String endpoint;
        private static final /* synthetic */ Region[] $VALUES;

        public static Region[] values() {
            return (Region[])$VALUES.clone();
        }

        public static Region valueOf(String $$0) {
            return Enum.valueOf(Region.class, $$0);
        }

        private Region(String $$0, String $$1) {
            this.name = $$0;
            this.endpoint = $$1;
        }

        private static /* synthetic */ Region[] a() {
            return new Region[]{US_EAST_1, US_WEST_2, US_WEST_1, EU_WEST_1, AP_SOUTHEAST_1, AP_SOUTHEAST_2, AP_NORTHEAST_1, SA_EAST_1};
        }

        static {
            $VALUES = Region.a();
        }
    }
}

