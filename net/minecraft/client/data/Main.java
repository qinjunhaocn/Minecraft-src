/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  joptsimple.AbstractOptionSpec
 *  joptsimple.ArgumentAcceptingOptionSpec
 *  joptsimple.OptionParser
 *  joptsimple.OptionSet
 *  joptsimple.OptionSpec
 *  joptsimple.OptionSpecBuilder
 */
package net.minecraft.client.data;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import joptsimple.AbstractOptionSpec;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.OptionSpecBuilder;
import net.minecraft.SharedConstants;
import net.minecraft.SuppressForbidden;
import net.minecraft.client.ClientBootstrap;
import net.minecraft.client.data.AtlasProvider;
import net.minecraft.client.data.models.EquipmentAssetProvider;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.WaypointStyleProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.server.Bootstrap;

public class Main {
    @DontObfuscate
    @SuppressForbidden(a="System.out needed before bootstrap")
    public static void main(String[] $$0) throws IOException {
        SharedConstants.tryDetectVersion();
        OptionParser $$1 = new OptionParser();
        AbstractOptionSpec $$2 = $$1.accepts("help", "Show the help menu").forHelp();
        OptionSpecBuilder $$3 = $$1.accepts("client", "Include client generators");
        OptionSpecBuilder $$4 = $$1.accepts("all", "Include all generators");
        ArgumentAcceptingOptionSpec $$5 = $$1.accepts("output", "Output folder").withRequiredArg().defaultsTo((Object)"generated", (Object[])new String[0]);
        OptionSet $$6 = $$1.parse($$0);
        if ($$6.has((OptionSpec)$$2) || !$$6.hasOptions()) {
            $$1.printHelpOn((OutputStream)System.out);
            return;
        }
        Path $$7 = Paths.get((String)$$5.value($$6), new String[0]);
        boolean $$8 = $$6.has((OptionSpec)$$4);
        boolean $$9 = $$8 || $$6.has((OptionSpec)$$3);
        Bootstrap.bootStrap();
        ClientBootstrap.bootstrap();
        DataGenerator $$10 = new DataGenerator($$7, SharedConstants.getCurrentVersion(), true);
        Main.addClientProviders($$10, $$9);
        $$10.run();
    }

    public static void addClientProviders(DataGenerator $$0, boolean $$1) {
        DataGenerator.PackGenerator $$2 = $$0.getVanillaPack($$1);
        $$2.addProvider(ModelProvider::new);
        $$2.addProvider(EquipmentAssetProvider::new);
        $$2.addProvider(WaypointStyleProvider::new);
        $$2.addProvider(AtlasProvider::new);
    }
}

