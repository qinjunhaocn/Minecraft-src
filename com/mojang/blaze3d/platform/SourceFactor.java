/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.DontObfuscate;

@DontObfuscate
public enum SourceFactor {
    CONSTANT_ALPHA,
    CONSTANT_COLOR,
    DST_ALPHA,
    DST_COLOR,
    ONE,
    ONE_MINUS_CONSTANT_ALPHA,
    ONE_MINUS_CONSTANT_COLOR,
    ONE_MINUS_DST_ALPHA,
    ONE_MINUS_DST_COLOR,
    ONE_MINUS_SRC_ALPHA,
    ONE_MINUS_SRC_COLOR,
    SRC_ALPHA,
    SRC_ALPHA_SATURATE,
    SRC_COLOR,
    ZERO;

}

