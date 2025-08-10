/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.validation;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.world.level.validation.ForbiddenSymlinkInfo;

public class ContentValidationException
extends Exception {
    private final Path directory;
    private final List<ForbiddenSymlinkInfo> entries;

    public ContentValidationException(Path $$0, List<ForbiddenSymlinkInfo> $$1) {
        this.directory = $$0;
        this.entries = $$1;
    }

    @Override
    public String getMessage() {
        return ContentValidationException.getMessage(this.directory, this.entries);
    }

    public static String getMessage(Path $$02, List<ForbiddenSymlinkInfo> $$1) {
        return "Failed to validate '" + String.valueOf($$02) + "'. Found forbidden symlinks: " + $$1.stream().map($$0 -> String.valueOf($$0.link()) + "->" + String.valueOf($$0.target())).collect(Collectors.joining(", "));
    }
}

