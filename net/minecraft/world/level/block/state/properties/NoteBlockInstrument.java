/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block.state.properties;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.StringRepresentable;

public final class NoteBlockInstrument
extends Enum<NoteBlockInstrument>
implements StringRepresentable {
    public static final /* enum */ NoteBlockInstrument HARP = new NoteBlockInstrument("harp", SoundEvents.NOTE_BLOCK_HARP, Type.BASE_BLOCK);
    public static final /* enum */ NoteBlockInstrument BASEDRUM = new NoteBlockInstrument("basedrum", SoundEvents.NOTE_BLOCK_BASEDRUM, Type.BASE_BLOCK);
    public static final /* enum */ NoteBlockInstrument SNARE = new NoteBlockInstrument("snare", SoundEvents.NOTE_BLOCK_SNARE, Type.BASE_BLOCK);
    public static final /* enum */ NoteBlockInstrument HAT = new NoteBlockInstrument("hat", SoundEvents.NOTE_BLOCK_HAT, Type.BASE_BLOCK);
    public static final /* enum */ NoteBlockInstrument BASS = new NoteBlockInstrument("bass", SoundEvents.NOTE_BLOCK_BASS, Type.BASE_BLOCK);
    public static final /* enum */ NoteBlockInstrument FLUTE = new NoteBlockInstrument("flute", SoundEvents.NOTE_BLOCK_FLUTE, Type.BASE_BLOCK);
    public static final /* enum */ NoteBlockInstrument BELL = new NoteBlockInstrument("bell", SoundEvents.NOTE_BLOCK_BELL, Type.BASE_BLOCK);
    public static final /* enum */ NoteBlockInstrument GUITAR = new NoteBlockInstrument("guitar", SoundEvents.NOTE_BLOCK_GUITAR, Type.BASE_BLOCK);
    public static final /* enum */ NoteBlockInstrument CHIME = new NoteBlockInstrument("chime", SoundEvents.NOTE_BLOCK_CHIME, Type.BASE_BLOCK);
    public static final /* enum */ NoteBlockInstrument XYLOPHONE = new NoteBlockInstrument("xylophone", SoundEvents.NOTE_BLOCK_XYLOPHONE, Type.BASE_BLOCK);
    public static final /* enum */ NoteBlockInstrument IRON_XYLOPHONE = new NoteBlockInstrument("iron_xylophone", SoundEvents.NOTE_BLOCK_IRON_XYLOPHONE, Type.BASE_BLOCK);
    public static final /* enum */ NoteBlockInstrument COW_BELL = new NoteBlockInstrument("cow_bell", SoundEvents.NOTE_BLOCK_COW_BELL, Type.BASE_BLOCK);
    public static final /* enum */ NoteBlockInstrument DIDGERIDOO = new NoteBlockInstrument("didgeridoo", SoundEvents.NOTE_BLOCK_DIDGERIDOO, Type.BASE_BLOCK);
    public static final /* enum */ NoteBlockInstrument BIT = new NoteBlockInstrument("bit", SoundEvents.NOTE_BLOCK_BIT, Type.BASE_BLOCK);
    public static final /* enum */ NoteBlockInstrument BANJO = new NoteBlockInstrument("banjo", SoundEvents.NOTE_BLOCK_BANJO, Type.BASE_BLOCK);
    public static final /* enum */ NoteBlockInstrument PLING = new NoteBlockInstrument("pling", SoundEvents.NOTE_BLOCK_PLING, Type.BASE_BLOCK);
    public static final /* enum */ NoteBlockInstrument ZOMBIE = new NoteBlockInstrument("zombie", SoundEvents.NOTE_BLOCK_IMITATE_ZOMBIE, Type.MOB_HEAD);
    public static final /* enum */ NoteBlockInstrument SKELETON = new NoteBlockInstrument("skeleton", SoundEvents.NOTE_BLOCK_IMITATE_SKELETON, Type.MOB_HEAD);
    public static final /* enum */ NoteBlockInstrument CREEPER = new NoteBlockInstrument("creeper", SoundEvents.NOTE_BLOCK_IMITATE_CREEPER, Type.MOB_HEAD);
    public static final /* enum */ NoteBlockInstrument DRAGON = new NoteBlockInstrument("dragon", SoundEvents.NOTE_BLOCK_IMITATE_ENDER_DRAGON, Type.MOB_HEAD);
    public static final /* enum */ NoteBlockInstrument WITHER_SKELETON = new NoteBlockInstrument("wither_skeleton", SoundEvents.NOTE_BLOCK_IMITATE_WITHER_SKELETON, Type.MOB_HEAD);
    public static final /* enum */ NoteBlockInstrument PIGLIN = new NoteBlockInstrument("piglin", SoundEvents.NOTE_BLOCK_IMITATE_PIGLIN, Type.MOB_HEAD);
    public static final /* enum */ NoteBlockInstrument CUSTOM_HEAD = new NoteBlockInstrument("custom_head", SoundEvents.UI_BUTTON_CLICK, Type.CUSTOM);
    private final String name;
    private final Holder<SoundEvent> soundEvent;
    private final Type type;
    private static final /* synthetic */ NoteBlockInstrument[] $VALUES;

    public static NoteBlockInstrument[] values() {
        return (NoteBlockInstrument[])$VALUES.clone();
    }

    public static NoteBlockInstrument valueOf(String $$0) {
        return Enum.valueOf(NoteBlockInstrument.class, $$0);
    }

    private NoteBlockInstrument(String $$0, Holder<SoundEvent> $$1, Type $$2) {
        this.name = $$0;
        this.soundEvent = $$1;
        this.type = $$2;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public Holder<SoundEvent> getSoundEvent() {
        return this.soundEvent;
    }

    public boolean isTunable() {
        return this.type == Type.BASE_BLOCK;
    }

    public boolean hasCustomSound() {
        return this.type == Type.CUSTOM;
    }

    public boolean worksAboveNoteBlock() {
        return this.type != Type.BASE_BLOCK;
    }

    private static /* synthetic */ NoteBlockInstrument[] f() {
        return new NoteBlockInstrument[]{HARP, BASEDRUM, SNARE, HAT, BASS, FLUTE, BELL, GUITAR, CHIME, XYLOPHONE, IRON_XYLOPHONE, COW_BELL, DIDGERIDOO, BIT, BANJO, PLING, ZOMBIE, SKELETON, CREEPER, DRAGON, WITHER_SKELETON, PIGLIN, CUSTOM_HEAD};
    }

    static {
        $VALUES = NoteBlockInstrument.f();
    }

    static final class Type
    extends Enum<Type> {
        public static final /* enum */ Type BASE_BLOCK = new Type();
        public static final /* enum */ Type MOB_HEAD = new Type();
        public static final /* enum */ Type CUSTOM = new Type();
        private static final /* synthetic */ Type[] $VALUES;

        public static Type[] values() {
            return (Type[])$VALUES.clone();
        }

        public static Type valueOf(String $$0) {
            return Enum.valueOf(Type.class, $$0);
        }

        private static /* synthetic */ Type[] a() {
            return new Type[]{BASE_BLOCK, MOB_HEAD, CUSTOM};
        }

        static {
            $VALUES = Type.a();
        }
    }
}

