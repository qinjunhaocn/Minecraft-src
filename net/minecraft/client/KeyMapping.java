/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ToggleKeyMapping;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

public class KeyMapping
implements Comparable<KeyMapping> {
    private static final Map<String, KeyMapping> ALL = Maps.newHashMap();
    private static final Map<InputConstants.Key, KeyMapping> MAP = Maps.newHashMap();
    private static final Set<String> CATEGORIES = Sets.newHashSet();
    public static final String CATEGORY_MOVEMENT = "key.categories.movement";
    public static final String CATEGORY_MISC = "key.categories.misc";
    public static final String CATEGORY_MULTIPLAYER = "key.categories.multiplayer";
    public static final String CATEGORY_GAMEPLAY = "key.categories.gameplay";
    public static final String CATEGORY_INVENTORY = "key.categories.inventory";
    public static final String CATEGORY_INTERFACE = "key.categories.ui";
    public static final String CATEGORY_CREATIVE = "key.categories.creative";
    private static final Map<String, Integer> CATEGORY_SORT_ORDER = Util.make(Maps.newHashMap(), $$0 -> {
        $$0.put(CATEGORY_MOVEMENT, 1);
        $$0.put(CATEGORY_GAMEPLAY, 2);
        $$0.put(CATEGORY_INVENTORY, 3);
        $$0.put(CATEGORY_CREATIVE, 4);
        $$0.put(CATEGORY_MULTIPLAYER, 5);
        $$0.put(CATEGORY_INTERFACE, 6);
        $$0.put(CATEGORY_MISC, 7);
    });
    private final String name;
    private final InputConstants.Key defaultKey;
    private final String category;
    private InputConstants.Key key;
    private boolean isDown;
    private int clickCount;

    public static void click(InputConstants.Key $$0) {
        KeyMapping $$1 = MAP.get($$0);
        if ($$1 != null) {
            ++$$1.clickCount;
        }
    }

    public static void set(InputConstants.Key $$0, boolean $$1) {
        KeyMapping $$2 = MAP.get($$0);
        if ($$2 != null) {
            $$2.setDown($$1);
        }
    }

    public static void setAll() {
        for (KeyMapping $$0 : ALL.values()) {
            if ($$0.key.getType() != InputConstants.Type.KEYSYM || $$0.key.getValue() == InputConstants.UNKNOWN.getValue()) continue;
            $$0.setDown(InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), $$0.key.getValue()));
        }
    }

    public static void releaseAll() {
        for (KeyMapping $$0 : ALL.values()) {
            $$0.release();
        }
    }

    public static void resetToggleKeys() {
        for (KeyMapping $$0 : ALL.values()) {
            if (!($$0 instanceof ToggleKeyMapping)) continue;
            ToggleKeyMapping $$1 = (ToggleKeyMapping)$$0;
            $$1.reset();
        }
    }

    public static void resetMapping() {
        MAP.clear();
        for (KeyMapping $$0 : ALL.values()) {
            MAP.put($$0.key, $$0);
        }
    }

    public KeyMapping(String $$0, int $$1, String $$2) {
        this($$0, InputConstants.Type.KEYSYM, $$1, $$2);
    }

    public KeyMapping(String $$0, InputConstants.Type $$1, int $$2, String $$3) {
        this.name = $$0;
        this.defaultKey = this.key = $$1.getOrCreate($$2);
        this.category = $$3;
        ALL.put($$0, this);
        MAP.put(this.key, this);
        CATEGORIES.add($$3);
    }

    public boolean isDown() {
        return this.isDown;
    }

    public String getCategory() {
        return this.category;
    }

    public boolean consumeClick() {
        if (this.clickCount == 0) {
            return false;
        }
        --this.clickCount;
        return true;
    }

    private void release() {
        this.clickCount = 0;
        this.setDown(false);
    }

    public String getName() {
        return this.name;
    }

    public InputConstants.Key getDefaultKey() {
        return this.defaultKey;
    }

    public void setKey(InputConstants.Key $$0) {
        this.key = $$0;
    }

    @Override
    public int compareTo(KeyMapping $$0) {
        if (this.category.equals($$0.category)) {
            return I18n.a(this.name, new Object[0]).compareTo(I18n.a($$0.name, new Object[0]));
        }
        return CATEGORY_SORT_ORDER.get(this.category).compareTo(CATEGORY_SORT_ORDER.get($$0.category));
    }

    public static Supplier<Component> createNameSupplier(String $$0) {
        KeyMapping $$1 = ALL.get($$0);
        if ($$1 == null) {
            return () -> Component.translatable($$0);
        }
        return $$1::getTranslatedKeyMessage;
    }

    public boolean same(KeyMapping $$0) {
        return this.key.equals($$0.key);
    }

    public boolean isUnbound() {
        return this.key.equals(InputConstants.UNKNOWN);
    }

    public boolean matches(int $$0, int $$1) {
        if ($$0 == InputConstants.UNKNOWN.getValue()) {
            return this.key.getType() == InputConstants.Type.SCANCODE && this.key.getValue() == $$1;
        }
        return this.key.getType() == InputConstants.Type.KEYSYM && this.key.getValue() == $$0;
    }

    public boolean matchesMouse(int $$0) {
        return this.key.getType() == InputConstants.Type.MOUSE && this.key.getValue() == $$0;
    }

    public Component getTranslatedKeyMessage() {
        return this.key.getDisplayName();
    }

    public boolean isDefault() {
        return this.key.equals(this.defaultKey);
    }

    public String saveString() {
        return this.key.getName();
    }

    public void setDown(boolean $$0) {
        this.isDown = $$0;
    }

    @Nullable
    public static KeyMapping get(String $$0) {
        return ALL.get($$0);
    }

    @Override
    public /* synthetic */ int compareTo(Object object) {
        return this.compareTo((KeyMapping)object);
    }
}

