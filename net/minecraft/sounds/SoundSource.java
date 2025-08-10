/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.sounds;

public final class SoundSource
extends Enum<SoundSource> {
    public static final /* enum */ SoundSource MASTER = new SoundSource("master");
    public static final /* enum */ SoundSource MUSIC = new SoundSource("music");
    public static final /* enum */ SoundSource RECORDS = new SoundSource("record");
    public static final /* enum */ SoundSource WEATHER = new SoundSource("weather");
    public static final /* enum */ SoundSource BLOCKS = new SoundSource("block");
    public static final /* enum */ SoundSource HOSTILE = new SoundSource("hostile");
    public static final /* enum */ SoundSource NEUTRAL = new SoundSource("neutral");
    public static final /* enum */ SoundSource PLAYERS = new SoundSource("player");
    public static final /* enum */ SoundSource AMBIENT = new SoundSource("ambient");
    public static final /* enum */ SoundSource VOICE = new SoundSource("voice");
    public static final /* enum */ SoundSource UI = new SoundSource("ui");
    private final String name;
    private static final /* synthetic */ SoundSource[] $VALUES;

    public static SoundSource[] values() {
        return (SoundSource[])$VALUES.clone();
    }

    public static SoundSource valueOf(String $$0) {
        return Enum.valueOf(SoundSource.class, $$0);
    }

    private SoundSource(String $$0) {
        this.name = $$0;
    }

    public String getName() {
        return this.name;
    }

    private static /* synthetic */ SoundSource[] b() {
        return new SoundSource[]{MASTER, MUSIC, RECORDS, WEATHER, BLOCKS, HOSTILE, NEUTRAL, PLAYERS, AMBIENT, VOICE, UI};
    }

    static {
        $VALUES = SoundSource.b();
    }
}

