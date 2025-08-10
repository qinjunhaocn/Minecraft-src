/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.client.multiplayer.resolver;

import com.mojang.logging.LogUtils;
import java.util.Hashtable;
import java.util.Optional;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import org.slf4j.Logger;

@FunctionalInterface
public interface ServerRedirectHandler {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final ServerRedirectHandler EMPTY = $$0 -> Optional.empty();

    public Optional<ServerAddress> lookupRedirect(ServerAddress var1);

    /*
     * WARNING - void declaration
     */
    public static ServerRedirectHandler createDnsSrvRedirectHandler() {
        void $$4;
        try {
            String $$0 = "com.sun.jndi.dns.DnsContextFactory";
            Class.forName("com.sun.jndi.dns.DnsContextFactory");
            Hashtable<String, String> $$1 = new Hashtable<String, String>();
            $$1.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            $$1.put("java.naming.provider.url", "dns:");
            $$1.put("com.sun.jndi.dns.timeout.retries", "1");
            InitialDirContext $$2 = new InitialDirContext($$1);
        } catch (Throwable $$3) {
            LOGGER.error("Failed to initialize SRV redirect resolved, some servers might not work", $$3);
            return EMPTY;
        }
        return arg_0 -> ServerRedirectHandler.lambda$createDnsSrvRedirectHandler$1((DirContext)$$4, arg_0);
    }

    private static /* synthetic */ Optional lambda$createDnsSrvRedirectHandler$1(DirContext $$0, ServerAddress $$1) {
        if ($$1.getPort() == 25565) {
            try {
                Attributes $$2 = $$0.getAttributes("_minecraft._tcp." + $$1.getHost(), new String[]{"SRV"});
                Attribute $$3 = $$2.get("srv");
                if ($$3 != null) {
                    String[] $$4 = $$3.get().toString().split(" ", 4);
                    return Optional.of(new ServerAddress($$4[3], ServerAddress.parsePort($$4[2])));
                }
            } catch (Throwable throwable) {
                // empty catch block
            }
        }
        return Optional.empty();
    }
}

