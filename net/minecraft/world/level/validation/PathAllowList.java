/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.world.level.validation;

import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;

public class PathAllowList
implements PathMatcher {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String COMMENT_PREFIX = "#";
    private final List<ConfigEntry> entries;
    private final Map<String, PathMatcher> compiledPaths = new ConcurrentHashMap<String, PathMatcher>();

    public PathAllowList(List<ConfigEntry> $$0) {
        this.entries = $$0;
    }

    public PathMatcher getForFileSystem(FileSystem $$0) {
        return this.compiledPaths.computeIfAbsent($$0.provider().getScheme(), $$12 -> {
            void $$4;
            try {
                List $$2 = this.entries.stream().map($$1 -> $$1.compile($$0)).toList();
            } catch (Exception $$3) {
                LOGGER.error("Failed to compile file pattern list", $$3);
                return $$0 -> false;
            }
            return switch ($$4.size()) {
                case 0 -> $$0 -> false;
                case 1 -> (PathMatcher)$$4.get(0);
                default -> arg_0 -> PathAllowList.lambda$getForFileSystem$3((List)$$4, arg_0);
            };
        });
    }

    @Override
    public boolean matches(Path $$0) {
        return this.getForFileSystem($$0.getFileSystem()).matches($$0);
    }

    public static PathAllowList readPlain(BufferedReader $$02) {
        return new PathAllowList($$02.lines().flatMap($$0 -> ConfigEntry.parse($$0).stream()).toList());
    }

    private static /* synthetic */ boolean lambda$getForFileSystem$3(List $$0, Path $$1) {
        for (PathMatcher $$2 : $$0) {
            if (!$$2.matches($$1)) continue;
            return true;
        }
        return false;
    }

    public record ConfigEntry(EntryType type, String pattern) {
        public PathMatcher compile(FileSystem $$0) {
            return this.type().compile($$0, this.pattern);
        }

        static Optional<ConfigEntry> parse(String $$0) {
            if ($$0.isBlank() || $$0.startsWith(PathAllowList.COMMENT_PREFIX)) {
                return Optional.empty();
            }
            if (!$$0.startsWith("[")) {
                return Optional.of(new ConfigEntry(EntryType.PREFIX, $$0));
            }
            int $$1 = $$0.indexOf(93, 1);
            if ($$1 == -1) {
                throw new IllegalArgumentException("Unterminated type in line '" + $$0 + "'");
            }
            String $$2 = $$0.substring(1, $$1);
            String $$3 = $$0.substring($$1 + 1);
            return switch ($$2) {
                case "glob", "regex" -> Optional.of(new ConfigEntry(EntryType.FILESYSTEM, $$2 + ":" + $$3));
                case "prefix" -> Optional.of(new ConfigEntry(EntryType.PREFIX, $$3));
                default -> throw new IllegalArgumentException("Unsupported definition type in line '" + $$0 + "'");
            };
        }

        static ConfigEntry glob(String $$0) {
            return new ConfigEntry(EntryType.FILESYSTEM, "glob:" + $$0);
        }

        static ConfigEntry regex(String $$0) {
            return new ConfigEntry(EntryType.FILESYSTEM, "regex:" + $$0);
        }

        static ConfigEntry prefix(String $$0) {
            return new ConfigEntry(EntryType.PREFIX, $$0);
        }
    }

    @FunctionalInterface
    public static interface EntryType {
        public static final EntryType FILESYSTEM = FileSystem::getPathMatcher;
        public static final EntryType PREFIX = ($$0, $$12) -> $$1 -> $$1.toString().startsWith($$12);

        public PathMatcher compile(FileSystem var1, String var2);
    }
}

