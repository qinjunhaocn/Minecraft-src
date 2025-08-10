/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.inventory;

import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

public class EnchantmentNames {
    private static final ResourceLocation ALT_FONT = ResourceLocation.withDefaultNamespace("alt");
    private static final Style ROOT_STYLE = Style.EMPTY.withFont(ALT_FONT);
    private static final EnchantmentNames INSTANCE = new EnchantmentNames();
    private final RandomSource random = RandomSource.create();
    private final String[] words = new String[]{"the", "elder", "scrolls", "klaatu", "berata", "niktu", "xyzzy", "bless", "curse", "light", "darkness", "fire", "air", "earth", "water", "hot", "dry", "cold", "wet", "ignite", "snuff", "embiggen", "twist", "shorten", "stretch", "fiddle", "destroy", "imbue", "galvanize", "enchant", "free", "limited", "range", "of", "towards", "inside", "sphere", "cube", "self", "other", "ball", "mental", "physical", "grow", "shrink", "demon", "elemental", "spirit", "animal", "creature", "beast", "humanoid", "undead", "fresh", "stale", "phnglui", "mglwnafh", "cthulhu", "rlyeh", "wgahnagl", "fhtagn", "baguette"};

    private EnchantmentNames() {
    }

    public static EnchantmentNames getInstance() {
        return INSTANCE;
    }

    public FormattedText getRandomName(Font $$0, int $$1) {
        StringBuilder $$2 = new StringBuilder();
        int $$3 = this.random.nextInt(2) + 3;
        for (int $$4 = 0; $$4 < $$3; ++$$4) {
            if ($$4 != 0) {
                $$2.append(" ");
            }
            $$2.append(Util.a(this.words, this.random));
        }
        return $$0.getSplitter().headByWidth(Component.literal($$2.toString()).withStyle(ROOT_STYLE), $$1, Style.EMPTY);
    }

    public void initSeed(long $$0) {
        this.random.setSeed($$0);
    }
}

