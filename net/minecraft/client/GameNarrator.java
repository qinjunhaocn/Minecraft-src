/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.text2speech.Narrator
 *  org.lwjgl.util.tinyfd.TinyFileDialogs
 */
package net.minecraft.client;

import com.mojang.logging.LogUtils;
import com.mojang.text2speech.Narrator;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.NarratorStatus;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.main.SilentInitException;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import org.slf4j.Logger;

public class GameNarrator {
    public static final Component NO_TITLE = CommonComponents.EMPTY;
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Minecraft minecraft;
    private final Narrator narrator = Narrator.getNarrator();

    public GameNarrator(Minecraft $$0) {
        this.minecraft = $$0;
    }

    public void sayChatQueued(Component $$0) {
        if (this.getStatus().shouldNarrateChat()) {
            this.narrateNotInterruptingMessage($$0);
        }
    }

    public void saySystemChatQueued(Component $$0) {
        if (this.getStatus().shouldNarrateSystemOrChat()) {
            this.narrateNotInterruptingMessage($$0);
        }
    }

    public void saySystemQueued(Component $$0) {
        if (this.getStatus().shouldNarrateSystem()) {
            this.narrateNotInterruptingMessage($$0);
        }
    }

    private void narrateNotInterruptingMessage(Component $$0) {
        String $$1 = $$0.getString();
        if (!$$1.isEmpty()) {
            this.logNarratedMessage($$1);
            this.narrateMessage($$1, false);
        }
    }

    public void saySystemNow(Component $$0) {
        this.saySystemNow($$0.getString());
    }

    public void saySystemNow(String $$0) {
        if (this.getStatus().shouldNarrateSystem() && !$$0.isEmpty()) {
            this.logNarratedMessage($$0);
            if (this.narrator.active()) {
                this.narrator.clear();
                this.narrateMessage($$0, true);
            }
        }
    }

    private void narrateMessage(String $$0, boolean $$1) {
        this.narrator.say($$0, $$1, this.minecraft.options.getSoundSourceVolume(SoundSource.VOICE) * this.minecraft.options.getSoundSourceVolume(SoundSource.MASTER));
    }

    private NarratorStatus getStatus() {
        return this.minecraft.options.narrator().get();
    }

    private void logNarratedMessage(String $$0) {
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            LOGGER.debug("Narrating: {}", (Object)$$0.replaceAll("\n", "\\\\n"));
        }
    }

    public void updateNarratorStatus(NarratorStatus $$0) {
        this.clear();
        this.narrateMessage(Component.translatable("options.narrator").append(" : ").append($$0.getName()).getString(), true);
        ToastManager $$1 = Minecraft.getInstance().getToastManager();
        if (this.narrator.active()) {
            if ($$0 == NarratorStatus.OFF) {
                SystemToast.addOrUpdate($$1, SystemToast.SystemToastId.NARRATOR_TOGGLE, Component.translatable("narrator.toast.disabled"), null);
            } else {
                SystemToast.addOrUpdate($$1, SystemToast.SystemToastId.NARRATOR_TOGGLE, Component.translatable("narrator.toast.enabled"), $$0.getName());
            }
        } else {
            SystemToast.addOrUpdate($$1, SystemToast.SystemToastId.NARRATOR_TOGGLE, Component.translatable("narrator.toast.disabled"), Component.translatable("options.narrator.notavailable"));
        }
    }

    public boolean isActive() {
        return this.narrator.active();
    }

    public void clear() {
        if (this.getStatus() == NarratorStatus.OFF || !this.narrator.active()) {
            return;
        }
        this.narrator.clear();
    }

    public void destroy() {
        this.narrator.destroy();
    }

    public void checkStatus(boolean $$0) {
        if ($$0 && !this.isActive() && !TinyFileDialogs.tinyfd_messageBox((CharSequence)"Minecraft", (CharSequence)"Failed to initialize text-to-speech library. Do you want to continue?\nIf this problem persists, please report it at bugs.mojang.com", (CharSequence)"yesno", (CharSequence)"error", (boolean)true)) {
            throw new NarratorInitException("Narrator library is not active");
        }
    }

    public static class NarratorInitException
    extends SilentInitException {
        public NarratorInitException(String $$0) {
            super($$0);
        }
    }
}

