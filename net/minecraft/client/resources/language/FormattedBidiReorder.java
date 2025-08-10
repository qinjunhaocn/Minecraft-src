/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.ibm.icu.lang.UCharacter
 *  com.ibm.icu.text.ArabicShaping
 *  com.ibm.icu.text.Bidi
 *  com.ibm.icu.text.BidiRun
 */
package net.minecraft.client.resources.language;

import com.google.common.collect.Lists;
import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.Bidi;
import com.ibm.icu.text.BidiRun;
import java.util.ArrayList;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.SubStringSource;
import net.minecraft.util.FormattedCharSequence;

public class FormattedBidiReorder {
    public static FormattedCharSequence reorder(FormattedText $$0, boolean $$1) {
        SubStringSource $$2 = SubStringSource.create($$0, UCharacter::getMirror, FormattedBidiReorder::shape);
        Bidi $$3 = new Bidi($$2.getPlainText(), $$1 ? 127 : 126);
        $$3.setReorderingMode(0);
        ArrayList<FormattedCharSequence> $$4 = Lists.newArrayList();
        int $$5 = $$3.countRuns();
        for (int $$6 = 0; $$6 < $$5; ++$$6) {
            BidiRun $$7 = $$3.getVisualRun($$6);
            $$4.addAll($$2.substring($$7.getStart(), $$7.getLength(), $$7.isOddRun()));
        }
        return FormattedCharSequence.composite($$4);
    }

    private static String shape(String $$0) {
        try {
            return new ArabicShaping(8).shape($$0);
        } catch (Exception $$1) {
            return $$0;
        }
    }
}

