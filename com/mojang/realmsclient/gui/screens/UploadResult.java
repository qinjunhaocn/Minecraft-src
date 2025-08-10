/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.realmsclient.gui.screens;

import javax.annotation.Nullable;

public class UploadResult {
    public final int statusCode;
    @Nullable
    public final String errorMessage;

    UploadResult(int $$0, String $$1) {
        this.statusCode = $$0;
        this.errorMessage = $$1;
    }

    @Nullable
    public String getSimplifiedErrorMessage() {
        if (this.statusCode < 200 || this.statusCode >= 300) {
            if (this.statusCode == 400 && this.errorMessage != null) {
                return this.errorMessage;
            }
            return String.valueOf(this.statusCode);
        }
        return null;
    }

    public static class Builder {
        private int statusCode = -1;
        private String errorMessage;

        public Builder withStatusCode(int $$0) {
            this.statusCode = $$0;
            return this;
        }

        public Builder withErrorMessage(@Nullable String $$0) {
            this.errorMessage = $$0;
            return this;
        }

        public UploadResult build() {
            return new UploadResult(this.statusCode, this.errorMessage);
        }
    }
}

