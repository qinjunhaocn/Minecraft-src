/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.gui.screens.AddRealmPopupScreen;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.Dumpable;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.ReloadableTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureContents;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;

public class TextureManager
implements PreparableReloadListener,
Tickable,
AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final ResourceLocation INTENTIONAL_MISSING_TEXTURE = ResourceLocation.withDefaultNamespace("");
    private final Map<ResourceLocation, AbstractTexture> byPath = new HashMap<ResourceLocation, AbstractTexture>();
    private final Set<Tickable> tickableTextures = new HashSet<Tickable>();
    private final ResourceManager resourceManager;

    public TextureManager(ResourceManager $$0) {
        this.resourceManager = $$0;
        NativeImage $$1 = MissingTextureAtlasSprite.generateMissingImage();
        this.register(MissingTextureAtlasSprite.getLocation(), new DynamicTexture(() -> "(intentionally-)Missing Texture", $$1));
    }

    public void registerAndLoad(ResourceLocation $$0, ReloadableTexture $$1) {
        try {
            $$1.apply(this.loadContentsSafe($$0, $$1));
        } catch (Throwable $$2) {
            CrashReport $$3 = CrashReport.forThrowable($$2, "Uploading texture");
            CrashReportCategory $$4 = $$3.addCategory("Uploaded texture");
            $$4.setDetail("Resource location", $$1.resourceId());
            $$4.setDetail("Texture id", $$0);
            throw new ReportedException($$3);
        }
        this.register($$0, $$1);
    }

    private TextureContents loadContentsSafe(ResourceLocation $$0, ReloadableTexture $$1) {
        try {
            return TextureManager.loadContents(this.resourceManager, $$0, $$1);
        } catch (Exception $$2) {
            LOGGER.error("Failed to load texture {} into slot {}", $$1.resourceId(), $$0, $$2);
            return TextureContents.createMissing();
        }
    }

    public void registerForNextReload(ResourceLocation $$0) {
        this.register($$0, new SimpleTexture($$0));
    }

    public void register(ResourceLocation $$0, AbstractTexture $$1) {
        AbstractTexture $$2 = this.byPath.put($$0, $$1);
        if ($$2 != $$1) {
            if ($$2 != null) {
                this.safeClose($$0, $$2);
            }
            if ($$1 instanceof Tickable) {
                Tickable $$3 = (Tickable)((Object)$$1);
                this.tickableTextures.add($$3);
            }
        }
    }

    private void safeClose(ResourceLocation $$0, AbstractTexture $$1) {
        this.tickableTextures.remove($$1);
        try {
            $$1.close();
        } catch (Exception $$2) {
            LOGGER.warn("Failed to close texture {}", (Object)$$0, (Object)$$2);
        }
    }

    public AbstractTexture getTexture(ResourceLocation $$0) {
        AbstractTexture $$1 = this.byPath.get($$0);
        if ($$1 != null) {
            return $$1;
        }
        SimpleTexture $$2 = new SimpleTexture($$0);
        this.registerAndLoad($$0, $$2);
        return $$2;
    }

    @Override
    public void tick() {
        for (Tickable $$0 : this.tickableTextures) {
            $$0.tick();
        }
    }

    public void release(ResourceLocation $$0) {
        AbstractTexture $$1 = this.byPath.remove($$0);
        if ($$1 != null) {
            this.safeClose($$0, $$1);
        }
    }

    @Override
    public void close() {
        this.byPath.forEach(this::safeClose);
        this.byPath.clear();
        this.tickableTextures.clear();
    }

    @Override
    public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier $$0, ResourceManager $$12, Executor $$2, Executor $$32) {
        ArrayList $$42 = new ArrayList();
        this.byPath.forEach(($$3, $$4) -> {
            if ($$4 instanceof ReloadableTexture) {
                ReloadableTexture $$5 = (ReloadableTexture)$$4;
                $$42.add(TextureManager.scheduleLoad($$12, $$3, $$5, $$2));
            }
        });
        return ((CompletableFuture)CompletableFuture.allOf((CompletableFuture[])$$42.stream().map(PendingReload::newContents).toArray(CompletableFuture[]::new)).thenCompose($$0::wait)).thenAcceptAsync($$1 -> {
            AddRealmPopupScreen.updateCarouselImages(this.resourceManager);
            for (PendingReload $$2 : $$42) {
                $$2.texture.apply($$2.newContents.join());
            }
        }, $$32);
    }

    public void dumpAllSheets(Path $$0) {
        try {
            Files.createDirectories($$0, new FileAttribute[0]);
        } catch (IOException $$12) {
            LOGGER.error("Failed to create directory {}", (Object)$$0, (Object)$$12);
            return;
        }
        this.byPath.forEach(($$1, $$2) -> {
            if ($$2 instanceof Dumpable) {
                Dumpable $$3 = (Dumpable)((Object)$$2);
                try {
                    $$3.dumpContents((ResourceLocation)$$1, $$0);
                } catch (IOException $$4) {
                    LOGGER.error("Failed to dump texture {}", $$1, (Object)$$4);
                }
            }
        });
    }

    private static TextureContents loadContents(ResourceManager $$0, ResourceLocation $$1, ReloadableTexture $$2) throws IOException {
        try {
            return $$2.loadContents($$0);
        } catch (FileNotFoundException $$3) {
            if ($$1 != INTENTIONAL_MISSING_TEXTURE) {
                LOGGER.warn("Missing resource {} referenced from {}", (Object)$$2.resourceId(), (Object)$$1);
            }
            return TextureContents.createMissing();
        }
    }

    private static PendingReload scheduleLoad(ResourceManager $$0, ResourceLocation $$1, ReloadableTexture $$2, Executor $$3) {
        return new PendingReload($$2, CompletableFuture.supplyAsync(() -> {
            try {
                return TextureManager.loadContents($$0, $$1, $$2);
            } catch (IOException $$3) {
                throw new UncheckedIOException($$3);
            }
        }, $$3));
    }

    static final class PendingReload
    extends Record {
        final ReloadableTexture texture;
        final CompletableFuture<TextureContents> newContents;

        PendingReload(ReloadableTexture $$0, CompletableFuture<TextureContents> $$1) {
            this.texture = $$0;
            this.newContents = $$1;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{PendingReload.class, "texture;newContents", "texture", "newContents"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{PendingReload.class, "texture;newContents", "texture", "newContents"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{PendingReload.class, "texture;newContents", "texture", "newContents"}, this, $$0);
        }

        public ReloadableTexture texture() {
            return this.texture;
        }

        public CompletableFuture<TextureContents> newContents() {
            return this.newContents;
        }
    }
}

