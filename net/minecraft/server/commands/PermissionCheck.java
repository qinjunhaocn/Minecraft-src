/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.server.commands;

import java.util.function.Predicate;

public interface PermissionCheck<T>
extends Predicate<T> {
    public int requiredLevel();
}

