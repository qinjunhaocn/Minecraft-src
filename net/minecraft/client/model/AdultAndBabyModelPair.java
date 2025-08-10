/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model;

import net.minecraft.client.model.Model;

public record AdultAndBabyModelPair<T extends Model>(T adultModel, T babyModel) {
    public T getModel(boolean $$0) {
        return $$0 ? this.babyModel : this.adultModel;
    }
}

