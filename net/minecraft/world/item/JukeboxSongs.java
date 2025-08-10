/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.item;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.JukeboxSong;

public interface JukeboxSongs {
    public static final ResourceKey<JukeboxSong> THIRTEEN = JukeboxSongs.create("13");
    public static final ResourceKey<JukeboxSong> CAT = JukeboxSongs.create("cat");
    public static final ResourceKey<JukeboxSong> BLOCKS = JukeboxSongs.create("blocks");
    public static final ResourceKey<JukeboxSong> CHIRP = JukeboxSongs.create("chirp");
    public static final ResourceKey<JukeboxSong> FAR = JukeboxSongs.create("far");
    public static final ResourceKey<JukeboxSong> MALL = JukeboxSongs.create("mall");
    public static final ResourceKey<JukeboxSong> MELLOHI = JukeboxSongs.create("mellohi");
    public static final ResourceKey<JukeboxSong> STAL = JukeboxSongs.create("stal");
    public static final ResourceKey<JukeboxSong> STRAD = JukeboxSongs.create("strad");
    public static final ResourceKey<JukeboxSong> WARD = JukeboxSongs.create("ward");
    public static final ResourceKey<JukeboxSong> ELEVEN = JukeboxSongs.create("11");
    public static final ResourceKey<JukeboxSong> WAIT = JukeboxSongs.create("wait");
    public static final ResourceKey<JukeboxSong> PIGSTEP = JukeboxSongs.create("pigstep");
    public static final ResourceKey<JukeboxSong> OTHERSIDE = JukeboxSongs.create("otherside");
    public static final ResourceKey<JukeboxSong> FIVE = JukeboxSongs.create("5");
    public static final ResourceKey<JukeboxSong> RELIC = JukeboxSongs.create("relic");
    public static final ResourceKey<JukeboxSong> PRECIPICE = JukeboxSongs.create("precipice");
    public static final ResourceKey<JukeboxSong> CREATOR = JukeboxSongs.create("creator");
    public static final ResourceKey<JukeboxSong> CREATOR_MUSIC_BOX = JukeboxSongs.create("creator_music_box");
    public static final ResourceKey<JukeboxSong> TEARS = JukeboxSongs.create("tears");
    public static final ResourceKey<JukeboxSong> LAVA_CHICKEN = JukeboxSongs.create("lava_chicken");

    private static ResourceKey<JukeboxSong> create(String $$0) {
        return ResourceKey.create(Registries.JUKEBOX_SONG, ResourceLocation.withDefaultNamespace($$0));
    }

    private static void register(BootstrapContext<JukeboxSong> $$0, ResourceKey<JukeboxSong> $$1, Holder.Reference<SoundEvent> $$2, int $$3, int $$4) {
        $$0.register($$1, new JukeboxSong($$2, Component.translatable(Util.makeDescriptionId("jukebox_song", $$1.location())), $$3, $$4));
    }

    public static void bootstrap(BootstrapContext<JukeboxSong> $$0) {
        JukeboxSongs.register($$0, THIRTEEN, SoundEvents.MUSIC_DISC_13, 178, 1);
        JukeboxSongs.register($$0, CAT, SoundEvents.MUSIC_DISC_CAT, 185, 2);
        JukeboxSongs.register($$0, BLOCKS, SoundEvents.MUSIC_DISC_BLOCKS, 345, 3);
        JukeboxSongs.register($$0, CHIRP, SoundEvents.MUSIC_DISC_CHIRP, 185, 4);
        JukeboxSongs.register($$0, FAR, SoundEvents.MUSIC_DISC_FAR, 174, 5);
        JukeboxSongs.register($$0, MALL, SoundEvents.MUSIC_DISC_MALL, 197, 6);
        JukeboxSongs.register($$0, MELLOHI, SoundEvents.MUSIC_DISC_MELLOHI, 96, 7);
        JukeboxSongs.register($$0, STAL, SoundEvents.MUSIC_DISC_STAL, 150, 8);
        JukeboxSongs.register($$0, STRAD, SoundEvents.MUSIC_DISC_STRAD, 188, 9);
        JukeboxSongs.register($$0, WARD, SoundEvents.MUSIC_DISC_WARD, 251, 10);
        JukeboxSongs.register($$0, ELEVEN, SoundEvents.MUSIC_DISC_11, 71, 11);
        JukeboxSongs.register($$0, WAIT, SoundEvents.MUSIC_DISC_WAIT, 238, 12);
        JukeboxSongs.register($$0, PIGSTEP, SoundEvents.MUSIC_DISC_PIGSTEP, 149, 13);
        JukeboxSongs.register($$0, OTHERSIDE, SoundEvents.MUSIC_DISC_OTHERSIDE, 195, 14);
        JukeboxSongs.register($$0, FIVE, SoundEvents.MUSIC_DISC_5, 178, 15);
        JukeboxSongs.register($$0, RELIC, SoundEvents.MUSIC_DISC_RELIC, 218, 14);
        JukeboxSongs.register($$0, PRECIPICE, SoundEvents.MUSIC_DISC_PRECIPICE, 299, 13);
        JukeboxSongs.register($$0, CREATOR, SoundEvents.MUSIC_DISC_CREATOR, 176, 12);
        JukeboxSongs.register($$0, CREATOR_MUSIC_BOX, SoundEvents.MUSIC_DISC_CREATOR_MUSIC_BOX, 73, 11);
        JukeboxSongs.register($$0, TEARS, SoundEvents.MUSIC_DISC_TEARS, 175, 10);
        JukeboxSongs.register($$0, LAVA_CHICKEN, SoundEvents.MUSIC_DISC_LAVA_CHICKEN, 134, 9);
    }
}

