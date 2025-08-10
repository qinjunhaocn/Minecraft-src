/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.options;

import java.util.Arrays;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;

public class SoundOptionsScreen
extends OptionsSubScreen {
    private static final Component TITLE = Component.translatable("options.sounds.title");

    public SoundOptionsScreen(Screen $$0, Options $$1) {
        super($$0, $$1, TITLE);
    }

    @Override
    protected void addOptions() {
        this.list.addBig(this.options.getSoundSourceOptionInstance(SoundSource.MASTER));
        this.list.a(this.F());
        this.list.addBig(this.options.soundDevice());
        this.list.a(this.options.showSubtitles(), this.options.directionalAudio());
        this.list.a(this.options.musicFrequency(), this.options.showNowPlayingToast());
    }

    private OptionInstance<?>[] F() {
        return (OptionInstance[])Arrays.stream(SoundSource.values()).filter($$0 -> $$0 != SoundSource.MASTER).map(this.options::getSoundSourceOptionInstance).toArray(OptionInstance[]::new);
    }
}

