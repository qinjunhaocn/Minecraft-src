/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.client.gui.font;

import com.mojang.serialization.Codec;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.util.StringRepresentable;

public final class FontOption
extends Enum<FontOption>
implements StringRepresentable {
    public static final /* enum */ FontOption UNIFORM = new FontOption("uniform");
    public static final /* enum */ FontOption JAPANESE_VARIANTS = new FontOption("jp");
    public static final Codec<FontOption> CODEC;
    private final String name;
    private static final /* synthetic */ FontOption[] $VALUES;

    public static FontOption[] values() {
        return (FontOption[])$VALUES.clone();
    }

    public static FontOption valueOf(String $$0) {
        return Enum.valueOf(FontOption.class, $$0);
    }

    private FontOption(String $$0) {
        this.name = $$0;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    private static /* synthetic */ FontOption[] a() {
        return new FontOption[]{UNIFORM, JAPANESE_VARIANTS};
    }

    static {
        $VALUES = FontOption.a();
        CODEC = StringRepresentable.fromEnum(FontOption::values);
    }

    public static class Filter {
        private final Map<FontOption, Boolean> values;
        public static final Codec<Filter> CODEC = Codec.unboundedMap(CODEC, (Codec)Codec.BOOL).xmap(Filter::new, $$0 -> $$0.values);
        public static final Filter ALWAYS_PASS = new Filter(Map.of());

        public Filter(Map<FontOption, Boolean> $$0) {
            this.values = $$0;
        }

        public boolean apply(Set<FontOption> $$0) {
            for (Map.Entry<FontOption, Boolean> $$1 : this.values.entrySet()) {
                if ($$0.contains($$1.getKey()) == $$1.getValue().booleanValue()) continue;
                return false;
            }
            return true;
        }

        public Filter merge(Filter $$0) {
            HashMap<FontOption, Boolean> $$1 = new HashMap<FontOption, Boolean>($$0.values);
            $$1.putAll(this.values);
            return new Filter(Map.copyOf($$1));
        }
    }
}

