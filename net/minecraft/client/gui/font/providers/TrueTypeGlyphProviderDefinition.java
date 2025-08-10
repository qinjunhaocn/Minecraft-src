/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 *  org.lwjgl.PointerBuffer
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 *  org.lwjgl.util.freetype.FT_Face
 *  org.lwjgl.util.freetype.FreeType
 */
package net.minecraft.client.gui.font.providers;

import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.TrueTypeGlyphProvider;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.client.gui.font.providers.FreeTypeUtil;
import net.minecraft.client.gui.font.providers.GlyphProviderDefinition;
import net.minecraft.client.gui.font.providers.GlyphProviderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.freetype.FT_Face;
import org.lwjgl.util.freetype.FreeType;

public record TrueTypeGlyphProviderDefinition(ResourceLocation location, float size, float oversample, Shift shift, String skip) implements GlyphProviderDefinition
{
    private static final Codec<String> SKIP_LIST_CODEC = Codec.withAlternative((Codec)Codec.STRING, (Codec)Codec.STRING.listOf(), $$0 -> String.join((CharSequence)"", $$0));
    public static final MapCodec<TrueTypeGlyphProviderDefinition> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)ResourceLocation.CODEC.fieldOf("file").forGetter(TrueTypeGlyphProviderDefinition::location), (App)Codec.FLOAT.optionalFieldOf("size", (Object)Float.valueOf(11.0f)).forGetter(TrueTypeGlyphProviderDefinition::size), (App)Codec.FLOAT.optionalFieldOf("oversample", (Object)Float.valueOf(1.0f)).forGetter(TrueTypeGlyphProviderDefinition::oversample), (App)Shift.CODEC.optionalFieldOf("shift", (Object)Shift.NONE).forGetter(TrueTypeGlyphProviderDefinition::shift), (App)SKIP_LIST_CODEC.optionalFieldOf("skip", (Object)"").forGetter(TrueTypeGlyphProviderDefinition::skip)).apply((Applicative)$$0, TrueTypeGlyphProviderDefinition::new));

    @Override
    public GlyphProviderType type() {
        return GlyphProviderType.TTF;
    }

    @Override
    public Either<GlyphProviderDefinition.Loader, GlyphProviderDefinition.Reference> unpack() {
        return Either.left(this::load);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private GlyphProvider load(ResourceManager $$0) throws IOException {
        FT_Face $$1 = null;
        ByteBuffer $$2 = null;
        try (InputStream $$3 = $$0.open(this.location.withPrefix("font/"));){
            $$2 = TextureUtil.readResource($$3);
            $$2.flip();
            Object object = FreeTypeUtil.LIBRARY_LOCK;
            synchronized (object) {
                try (MemoryStack $$4 = MemoryStack.stackPush();){
                    PointerBuffer $$5 = $$4.mallocPointer(1);
                    FreeTypeUtil.assertError(FreeType.FT_New_Memory_Face((long)FreeTypeUtil.getLibrary(), (ByteBuffer)$$2, (long)0L, (PointerBuffer)$$5), "Initializing font face");
                    $$1 = FT_Face.create((long)$$5.get());
                }
                String $$6 = FreeType.FT_Get_Font_Format((FT_Face)$$1);
                if (!"TrueType".equals($$6)) {
                    throw new IOException("Font is not in TTF format, was " + $$6);
                }
                FreeTypeUtil.assertError(FreeType.FT_Select_Charmap((FT_Face)$$1, (int)FreeType.FT_ENCODING_UNICODE), "Find unicode charmap");
                TrueTypeGlyphProvider trueTypeGlyphProvider = new TrueTypeGlyphProvider($$2, $$1, this.size, this.oversample, this.shift.x, this.shift.y, this.skip);
                return trueTypeGlyphProvider;
            }
        } catch (Exception $$7) {
            Object object = FreeTypeUtil.LIBRARY_LOCK;
            synchronized (object) {
                if ($$1 != null) {
                    FreeType.FT_Done_Face($$1);
                }
            }
            MemoryUtil.memFree((Buffer)$$2);
            throw $$7;
        }
    }

    public static final class Shift
    extends Record {
        final float x;
        final float y;
        public static final Shift NONE = new Shift(0.0f, 0.0f);
        public static final Codec<Shift> CODEC = Codec.floatRange((float)-512.0f, (float)512.0f).listOf().comapFlatMap($$02 -> Util.fixedSize($$02, 2).map($$0 -> new Shift(((Float)$$0.get(0)).floatValue(), ((Float)$$0.get(1)).floatValue())), $$0 -> List.of((Object)Float.valueOf($$0.x), (Object)Float.valueOf($$0.y)));

        public Shift(float $$0, float $$1) {
            this.x = $$0;
            this.y = $$1;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Shift.class, "x;y", "x", "y"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Shift.class, "x;y", "x", "y"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Shift.class, "x;y", "x", "y"}, this, $$0);
        }

        public float x() {
            return this.x;
        }

        public float y() {
            return this.y;
        }
    }
}

