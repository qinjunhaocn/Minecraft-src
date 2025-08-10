/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.client;

import com.google.common.base.Charsets;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Collection;
import net.minecraft.util.ArrayListDeque;
import org.slf4j.Logger;

public class CommandHistory {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int MAX_PERSISTED_COMMAND_HISTORY = 50;
    private static final String PERSISTED_COMMANDS_FILE_NAME = "command_history.txt";
    private final Path commandsPath;
    private final ArrayListDeque<String> lastCommands = new ArrayListDeque(50);

    public CommandHistory(Path $$0) {
        this.commandsPath = $$0.resolve(PERSISTED_COMMANDS_FILE_NAME);
        if (Files.exists(this.commandsPath, new LinkOption[0])) {
            try (BufferedReader $$1 = Files.newBufferedReader(this.commandsPath, Charsets.UTF_8);){
                this.lastCommands.addAll($$1.lines().toList());
            } catch (Exception $$2) {
                LOGGER.error("Failed to read {}, command history will be missing", (Object)PERSISTED_COMMANDS_FILE_NAME, (Object)$$2);
            }
        }
    }

    public void addCommand(String $$0) {
        if (!$$0.equals(this.lastCommands.peekLast())) {
            if (this.lastCommands.size() >= 50) {
                this.lastCommands.removeFirst();
            }
            this.lastCommands.addLast($$0);
            this.save();
        }
    }

    private void save() {
        try (BufferedWriter $$0 = Files.newBufferedWriter(this.commandsPath, Charsets.UTF_8, new OpenOption[0]);){
            for (String $$1 : this.lastCommands) {
                $$0.write($$1);
                $$0.newLine();
            }
        } catch (IOException $$2) {
            LOGGER.error("Failed to write {}, command history will be missing", (Object)PERSISTED_COMMANDS_FILE_NAME, (Object)$$2);
        }
    }

    public Collection<String> history() {
        return this.lastCommands;
    }
}

