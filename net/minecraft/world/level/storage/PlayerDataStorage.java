/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.world.level.storage;

import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.FileNameDateFormatter;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import org.slf4j.Logger;

public class PlayerDataStorage {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final File playerDir;
    protected final DataFixer fixerUpper;
    private static final DateTimeFormatter FORMATTER = FileNameDateFormatter.create();

    public PlayerDataStorage(LevelStorageSource.LevelStorageAccess $$0, DataFixer $$1) {
        this.fixerUpper = $$1;
        this.playerDir = $$0.getLevelPath(LevelResource.PLAYER_DATA_DIR).toFile();
        this.playerDir.mkdirs();
    }

    public void save(Player $$0) {
        try (ProblemReporter.ScopedCollector $$1 = new ProblemReporter.ScopedCollector($$0.problemPath(), LOGGER);){
            TagValueOutput $$2 = TagValueOutput.createWithContext($$1, $$0.registryAccess());
            $$0.saveWithoutId($$2);
            Path $$3 = this.playerDir.toPath();
            Path $$4 = Files.createTempFile($$3, $$0.getStringUUID() + "-", ".dat", new FileAttribute[0]);
            CompoundTag $$5 = $$2.buildResult();
            NbtIo.writeCompressed($$5, $$4);
            Path $$6 = $$3.resolve($$0.getStringUUID() + ".dat");
            Path $$7 = $$3.resolve($$0.getStringUUID() + ".dat_old");
            Util.safeReplaceFile($$6, $$4, $$7);
        } catch (Exception $$8) {
            LOGGER.warn("Failed to save player data for {}", (Object)$$0.getName().getString());
        }
    }

    private void backup(Player $$0, String $$1) {
        Path $$2 = this.playerDir.toPath();
        Path $$3 = $$2.resolve($$0.getStringUUID() + $$1);
        Path $$4 = $$2.resolve($$0.getStringUUID() + "_corrupted_" + LocalDateTime.now().format(FORMATTER) + $$1);
        if (!Files.isRegularFile($$3, new LinkOption[0])) {
            return;
        }
        try {
            Files.copy($$3, $$4, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
        } catch (Exception $$5) {
            LOGGER.warn("Failed to copy the player.dat file for {}", (Object)$$0.getName().getString(), (Object)$$5);
        }
    }

    private Optional<CompoundTag> load(Player $$0, String $$1) {
        File $$2 = new File(this.playerDir, $$0.getStringUUID() + $$1);
        if ($$2.exists() && $$2.isFile()) {
            try {
                return Optional.of(NbtIo.readCompressed($$2.toPath(), NbtAccounter.unlimitedHeap()));
            } catch (Exception $$3) {
                LOGGER.warn("Failed to load player data for {}", (Object)$$0.getName().getString());
            }
        }
        return Optional.empty();
    }

    public Optional<ValueInput> load(Player $$0, ProblemReporter $$1) {
        Optional<CompoundTag> $$22 = this.load($$0, ".dat");
        if ($$22.isEmpty()) {
            this.backup($$0, ".dat");
        }
        return $$22.or(() -> this.load($$0, ".dat_old")).map($$2 -> {
            int $$3 = NbtUtils.getDataVersion($$2, -1);
            $$2 = DataFixTypes.PLAYER.updateToCurrentVersion(this.fixerUpper, (CompoundTag)$$2, $$3);
            ValueInput $$4 = TagValueInput.create($$1, (HolderLookup.Provider)$$0.registryAccess(), $$2);
            $$0.load($$4);
            return $$4;
        });
    }
}

