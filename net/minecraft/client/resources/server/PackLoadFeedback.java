/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.resources.server;

import java.util.UUID;

public interface PackLoadFeedback {
    public void reportUpdate(UUID var1, Update var2);

    public void reportFinalResult(UUID var1, FinalResult var2);

    public static final class FinalResult
    extends Enum<FinalResult> {
        public static final /* enum */ FinalResult DECLINED = new FinalResult();
        public static final /* enum */ FinalResult APPLIED = new FinalResult();
        public static final /* enum */ FinalResult DISCARDED = new FinalResult();
        public static final /* enum */ FinalResult DOWNLOAD_FAILED = new FinalResult();
        public static final /* enum */ FinalResult ACTIVATION_FAILED = new FinalResult();
        private static final /* synthetic */ FinalResult[] $VALUES;

        public static FinalResult[] values() {
            return (FinalResult[])$VALUES.clone();
        }

        public static FinalResult valueOf(String $$0) {
            return Enum.valueOf(FinalResult.class, $$0);
        }

        private static /* synthetic */ FinalResult[] a() {
            return new FinalResult[]{DECLINED, APPLIED, DISCARDED, DOWNLOAD_FAILED, ACTIVATION_FAILED};
        }

        static {
            $VALUES = FinalResult.a();
        }
    }

    public static final class Update
    extends Enum<Update> {
        public static final /* enum */ Update ACCEPTED = new Update();
        public static final /* enum */ Update DOWNLOADED = new Update();
        private static final /* synthetic */ Update[] $VALUES;

        public static Update[] values() {
            return (Update[])$VALUES.clone();
        }

        public static Update valueOf(String $$0) {
            return Enum.valueOf(Update.class, $$0);
        }

        private static /* synthetic */ Update[] a() {
            return new Update[]{ACCEPTED, DOWNLOADED};
        }

        static {
            $VALUES = Update.a();
        }
    }
}

