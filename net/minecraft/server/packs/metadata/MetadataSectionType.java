/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.server.packs.metadata;

import com.mojang.serialization.Codec;

public record MetadataSectionType<T>(String name, Codec<T> codec) {
}

