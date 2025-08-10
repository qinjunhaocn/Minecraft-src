/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.nbt;

import net.minecraft.nbt.TagType;

public interface StreamTagVisitor {
    public ValueResult visitEnd();

    public ValueResult visit(String var1);

    public ValueResult visit(byte var1);

    public ValueResult visit(short var1);

    public ValueResult visit(int var1);

    public ValueResult visit(long var1);

    public ValueResult visit(float var1);

    public ValueResult visit(double var1);

    public ValueResult a(byte[] var1);

    public ValueResult a(int[] var1);

    public ValueResult a(long[] var1);

    public ValueResult visitList(TagType<?> var1, int var2);

    public EntryResult visitEntry(TagType<?> var1);

    public EntryResult visitEntry(TagType<?> var1, String var2);

    public EntryResult visitElement(TagType<?> var1, int var2);

    public ValueResult visitContainerEnd();

    public ValueResult visitRootEntry(TagType<?> var1);

    public static final class EntryResult
    extends Enum<EntryResult> {
        public static final /* enum */ EntryResult ENTER = new EntryResult();
        public static final /* enum */ EntryResult SKIP = new EntryResult();
        public static final /* enum */ EntryResult BREAK = new EntryResult();
        public static final /* enum */ EntryResult HALT = new EntryResult();
        private static final /* synthetic */ EntryResult[] $VALUES;

        public static EntryResult[] values() {
            return (EntryResult[])$VALUES.clone();
        }

        public static EntryResult valueOf(String $$0) {
            return Enum.valueOf(EntryResult.class, $$0);
        }

        private static /* synthetic */ EntryResult[] a() {
            return new EntryResult[]{ENTER, SKIP, BREAK, HALT};
        }

        static {
            $VALUES = EntryResult.a();
        }
    }

    public static final class ValueResult
    extends Enum<ValueResult> {
        public static final /* enum */ ValueResult CONTINUE = new ValueResult();
        public static final /* enum */ ValueResult BREAK = new ValueResult();
        public static final /* enum */ ValueResult HALT = new ValueResult();
        private static final /* synthetic */ ValueResult[] $VALUES;

        public static ValueResult[] values() {
            return (ValueResult[])$VALUES.clone();
        }

        public static ValueResult valueOf(String $$0) {
            return Enum.valueOf(ValueResult.class, $$0);
        }

        private static /* synthetic */ ValueResult[] a() {
            return new ValueResult[]{CONTINUE, BREAK, HALT};
        }

        static {
            $VALUES = ValueResult.a();
        }
    }
}

