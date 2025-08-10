/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Unit
 *  com.mojang.serialization.Codec
 */
package net.minecraft.client.gui.screens.options;

import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Difficulty;

public class OnlineOptionsScreen
extends OptionsSubScreen {
    private static final Component TITLE = Component.translatable("options.online.title");
    @Nullable
    private OptionInstance<Unit> difficultyDisplay;

    public OnlineOptionsScreen(Screen $$0, Options $$1) {
        super($$0, $$1, TITLE);
    }

    @Override
    protected void init() {
        AbstractWidget $$0;
        super.init();
        if (this.difficultyDisplay != null && ($$0 = this.list.findOption(this.difficultyDisplay)) != null) {
            $$0.active = false;
        }
    }

    private OptionInstance<?>[] a(Options $$0, Minecraft $$1) {
        ArrayList<OptionInstance> $$2 = new ArrayList<OptionInstance>();
        $$2.add($$0.realmsNotifications());
        $$2.add($$0.allowServerListing());
        OptionInstance $$3 = Optionull.map($$1.level, $$02 -> {
            Difficulty $$12 = $$02.getDifficulty();
            return new OptionInstance<Unit>("options.difficulty.online", OptionInstance.noTooltip(), ($$1, $$2) -> $$12.getDisplayName(), new OptionInstance.Enum(List.of((Object)Unit.INSTANCE), Codec.EMPTY.codec()), Unit.INSTANCE, $$0 -> {});
        });
        if ($$3 != null) {
            this.difficultyDisplay = $$3;
            $$2.add($$3);
        }
        return $$2.toArray(new OptionInstance[0]);
    }

    @Override
    protected void addOptions() {
        this.list.a(this.a(this.options, this.minecraft));
    }
}

