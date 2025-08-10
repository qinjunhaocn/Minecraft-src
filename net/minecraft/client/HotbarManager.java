/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.client;

import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.nio.file.Path;
import net.minecraft.client.player.inventory.Hotbar;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.util.datafix.DataFixTypes;
import org.slf4j.Logger;

public class HotbarManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int NUM_HOTBAR_GROUPS = 9;
    private final Path optionsFile;
    private final DataFixer fixerUpper;
    private final Hotbar[] hotbars = new Hotbar[9];
    private boolean loaded;

    public HotbarManager(Path $$0, DataFixer $$1) {
        this.optionsFile = $$0.resolve("hotbar.nbt");
        this.fixerUpper = $$1;
        for (int $$2 = 0; $$2 < 9; ++$$2) {
            this.hotbars[$$2] = new Hotbar();
        }
    }

    private void load() {
        try {
            CompoundTag $$02 = NbtIo.read(this.optionsFile);
            if ($$02 == null) {
                return;
            }
            int $$1 = NbtUtils.getDataVersion($$02, 1343);
            $$02 = DataFixTypes.HOTBAR.updateToCurrentVersion(this.fixerUpper, $$02, $$1);
            for (int $$2 = 0; $$2 < 9; ++$$2) {
                this.hotbars[$$2] = Hotbar.CODEC.parse((DynamicOps)NbtOps.INSTANCE, (Object)$$02.get(String.valueOf($$2))).resultOrPartial($$0 -> LOGGER.warn("Failed to parse hotbar: {}", $$0)).orElseGet(Hotbar::new);
            }
        } catch (Exception $$3) {
            LOGGER.error("Failed to load creative mode options", $$3);
        }
    }

    public void save() {
        try {
            CompoundTag $$0 = NbtUtils.addCurrentDataVersion(new CompoundTag());
            for (int $$1 = 0; $$1 < 9; ++$$1) {
                Hotbar $$2 = this.get($$1);
                DataResult $$3 = Hotbar.CODEC.encodeStart((DynamicOps)NbtOps.INSTANCE, (Object)$$2);
                $$0.put(String.valueOf($$1), (Tag)$$3.getOrThrow());
            }
            NbtIo.write($$0, this.optionsFile);
        } catch (Exception $$4) {
            LOGGER.error("Failed to save creative mode options", $$4);
        }
    }

    public Hotbar get(int $$0) {
        if (!this.loaded) {
            this.load();
            this.loaded = true;
        }
        return this.hotbars[$$0];
    }
}

