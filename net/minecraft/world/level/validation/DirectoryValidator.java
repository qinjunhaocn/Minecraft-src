/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.validation;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.level.validation.ForbiddenSymlinkInfo;

public class DirectoryValidator {
    private final PathMatcher symlinkTargetAllowList;

    public DirectoryValidator(PathMatcher $$0) {
        this.symlinkTargetAllowList = $$0;
    }

    public void validateSymlink(Path $$0, List<ForbiddenSymlinkInfo> $$1) throws IOException {
        Path $$2 = Files.readSymbolicLink($$0);
        if (!this.symlinkTargetAllowList.matches($$2)) {
            $$1.add(new ForbiddenSymlinkInfo($$0, $$2));
        }
    }

    public List<ForbiddenSymlinkInfo> validateSymlink(Path $$0) throws IOException {
        ArrayList<ForbiddenSymlinkInfo> $$1 = new ArrayList<ForbiddenSymlinkInfo>();
        this.validateSymlink($$0, $$1);
        return $$1;
    }

    /*
     * WARNING - void declaration
     */
    public List<ForbiddenSymlinkInfo> validateDirectory(Path $$0, boolean $$1) throws IOException {
        void $$5;
        ArrayList<ForbiddenSymlinkInfo> $$2 = new ArrayList<ForbiddenSymlinkInfo>();
        try {
            BasicFileAttributes $$3 = Files.readAttributes($$0, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
        } catch (NoSuchFileException $$4) {
            return $$2;
        }
        if ($$5.isRegularFile()) {
            throw new IOException("Path " + String.valueOf($$0) + " is not a directory");
        }
        if ($$5.isSymbolicLink()) {
            if ($$1) {
                $$0 = Files.readSymbolicLink($$0);
            } else {
                this.validateSymlink($$0, $$2);
                return $$2;
            }
        }
        this.validateKnownDirectory($$0, $$2);
        return $$2;
    }

    public void validateKnownDirectory(Path $$0, final List<ForbiddenSymlinkInfo> $$1) throws IOException {
        Files.walkFileTree($$0, (FileVisitor<? super Path>)new SimpleFileVisitor<Path>(){

            private void validateSymlink(Path $$0, BasicFileAttributes $$12) throws IOException {
                if ($$12.isSymbolicLink()) {
                    DirectoryValidator.this.validateSymlink($$0, $$1);
                }
            }

            @Override
            public FileVisitResult preVisitDirectory(Path $$0, BasicFileAttributes $$12) throws IOException {
                this.validateSymlink($$0, $$12);
                return super.preVisitDirectory($$0, $$12);
            }

            @Override
            public FileVisitResult visitFile(Path $$0, BasicFileAttributes $$12) throws IOException {
                this.validateSymlink($$0, $$12);
                return super.visitFile($$0, $$12);
            }

            @Override
            public /* synthetic */ FileVisitResult visitFile(Object object, BasicFileAttributes basicFileAttributes) throws IOException {
                return this.visitFile((Path)object, basicFileAttributes);
            }

            @Override
            public /* synthetic */ FileVisitResult preVisitDirectory(Object object, BasicFileAttributes basicFileAttributes) throws IOException {
                return this.preVisitDirectory((Path)object, basicFileAttributes);
            }
        });
    }
}

