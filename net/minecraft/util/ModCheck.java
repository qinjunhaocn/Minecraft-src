/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.util;

import java.util.function.Supplier;
import org.apache.commons.lang3.ObjectUtils;

public record ModCheck(Confidence confidence, String description) {
    public static ModCheck identify(String $$0, Supplier<String> $$1, String $$2, Class<?> $$3) {
        String $$4 = $$1.get();
        if (!$$0.equals($$4)) {
            return new ModCheck(Confidence.DEFINITELY, $$2 + " brand changed to '" + $$4 + "'");
        }
        if ($$3.getSigners() == null) {
            return new ModCheck(Confidence.VERY_LIKELY, $$2 + " jar signature invalidated");
        }
        return new ModCheck(Confidence.PROBABLY_NOT, $$2 + " jar signature and brand is untouched");
    }

    public boolean shouldReportAsModified() {
        return this.confidence.shouldReportAsModified;
    }

    public ModCheck merge(ModCheck $$0) {
        return new ModCheck((Confidence)((Object)ObjectUtils.max((Comparable[])new Confidence[]{this.confidence, $$0.confidence})), this.description + "; " + $$0.description);
    }

    public String fullDescription() {
        return this.confidence.description + " " + this.description;
    }

    public static final class Confidence
    extends Enum<Confidence> {
        public static final /* enum */ Confidence PROBABLY_NOT = new Confidence("Probably not.", false);
        public static final /* enum */ Confidence VERY_LIKELY = new Confidence("Very likely;", true);
        public static final /* enum */ Confidence DEFINITELY = new Confidence("Definitely;", true);
        final String description;
        final boolean shouldReportAsModified;
        private static final /* synthetic */ Confidence[] $VALUES;

        public static Confidence[] values() {
            return (Confidence[])$VALUES.clone();
        }

        public static Confidence valueOf(String $$0) {
            return Enum.valueOf(Confidence.class, $$0);
        }

        private Confidence(String $$0, boolean $$1) {
            this.description = $$0;
            this.shouldReportAsModified = $$1;
        }

        private static /* synthetic */ Confidence[] a() {
            return new Confidence[]{PROBABLY_NOT, VERY_LIKELY, DEFINITELY};
        }

        static {
            $VALUES = Confidence.a();
        }
    }
}

