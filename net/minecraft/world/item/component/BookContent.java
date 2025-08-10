/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.item.component;

import java.util.List;
import net.minecraft.server.network.Filterable;

public interface BookContent<T, C> {
    public List<Filterable<T>> pages();

    public C withReplacedPages(List<Filterable<T>> var1);
}

