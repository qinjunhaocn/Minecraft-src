/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.block.entity;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;

public class SignText {
    private static final Codec<Component[]> LINES_CODEC = ComponentSerialization.CODEC.listOf().comapFlatMap($$0 -> {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * java.lang.UnsupportedOperationException
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.NewAnonymousArray.getDimSize(NewAnonymousArray.java:142)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.isNewArrayLambda(LambdaRewriter.java:463)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteDynamicExpression(LambdaRewriter.java:417)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteDynamicExpression(LambdaRewriter.java:175)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:106)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.rewriters.ExpressionRewriterHelper.applyForwards(ExpressionRewriterHelper.java:12)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriterToArgs(AbstractMemberFunctionInvokation.java:101)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:88)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:104)
         *     at org.benf.cfr.reader.bytecode.analysis.structured.statement.StructuredReturn.rewriteExpressions(StructuredReturn.java:99)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewrite(LambdaRewriter.java:89)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.rewriteLambdas(Op04StructuredStatement.java:1137)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:912)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:538)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1050)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         *     at async.DecompilerRunnable.cfrDecompilation(DecompilerRunnable.java:348)
         *     at async.DecompilerRunnable.call(DecompilerRunnable.java:309)
         *     at async.DecompilerRunnable.call(DecompilerRunnable.java:31)
         *     at java.util.concurrent.FutureTask.run(FutureTask.java:266)
         *     at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
         *     at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
         *     at java.lang.Thread.run(Thread.java:750)
         */
        throw new IllegalStateException("Decompilation failed");
    }, $$0 -> List.of((Object)$$0[0], (Object)$$0[1], (Object)$$0[2], (Object)$$0[3]));
    public static final Codec<SignText> DIRECT_CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)LINES_CODEC.fieldOf("messages").forGetter($$0 -> $$0.messages), (App)LINES_CODEC.lenientOptionalFieldOf("filtered_messages").forGetter(SignText::filteredMessages), (App)DyeColor.CODEC.fieldOf("color").orElse((Object)DyeColor.BLACK).forGetter($$0 -> $$0.color), (App)Codec.BOOL.fieldOf("has_glowing_text").orElse((Object)false).forGetter($$0 -> $$0.hasGlowingText)).apply((Applicative)$$02, SignText::a));
    public static final int LINES = 4;
    private final Component[] messages;
    private final Component[] filteredMessages;
    private final DyeColor color;
    private final boolean hasGlowingText;
    @Nullable
    private FormattedCharSequence[] renderMessages;
    private boolean renderMessagedFiltered;

    public SignText() {
        this(SignText.c(), SignText.c(), DyeColor.BLACK, false);
    }

    public SignText(Component[] $$0, Component[] $$1, DyeColor $$2, boolean $$3) {
        this.messages = $$0;
        this.filteredMessages = $$1;
        this.color = $$2;
        this.hasGlowingText = $$3;
    }

    private static Component[] c() {
        return new Component[]{CommonComponents.EMPTY, CommonComponents.EMPTY, CommonComponents.EMPTY, CommonComponents.EMPTY};
    }

    private static SignText a(Component[] $$0, Optional<Component[]> $$1, DyeColor $$2, boolean $$3) {
        return new SignText($$0, $$1.orElse(Arrays.copyOf($$0, $$0.length)), $$2, $$3);
    }

    public boolean hasGlowingText() {
        return this.hasGlowingText;
    }

    public SignText setHasGlowingText(boolean $$0) {
        if ($$0 == this.hasGlowingText) {
            return this;
        }
        return new SignText(this.messages, this.filteredMessages, this.color, $$0);
    }

    public DyeColor getColor() {
        return this.color;
    }

    public SignText setColor(DyeColor $$0) {
        if ($$0 == this.getColor()) {
            return this;
        }
        return new SignText(this.messages, this.filteredMessages, $$0, this.hasGlowingText);
    }

    public Component getMessage(int $$0, boolean $$1) {
        return this.b($$1)[$$0];
    }

    public SignText setMessage(int $$0, Component $$1) {
        return this.setMessage($$0, $$1, $$1);
    }

    public SignText setMessage(int $$0, Component $$1, Component $$2) {
        Component[] $$3 = Arrays.copyOf(this.messages, this.messages.length);
        Component[] $$4 = Arrays.copyOf(this.filteredMessages, this.filteredMessages.length);
        $$3[$$0] = $$1;
        $$4[$$0] = $$2;
        return new SignText($$3, $$4, this.color, this.hasGlowingText);
    }

    public boolean hasMessage(Player $$02) {
        return Arrays.stream(this.b($$02.isTextFilteringEnabled())).anyMatch($$0 -> !$$0.getString().isEmpty());
    }

    public Component[] b(boolean $$0) {
        return $$0 ? this.filteredMessages : this.messages;
    }

    public FormattedCharSequence[] a(boolean $$0, Function<Component, FormattedCharSequence> $$1) {
        if (this.renderMessages == null || this.renderMessagedFiltered != $$0) {
            this.renderMessagedFiltered = $$0;
            this.renderMessages = new FormattedCharSequence[4];
            for (int $$2 = 0; $$2 < 4; ++$$2) {
                this.renderMessages[$$2] = $$1.apply(this.getMessage($$2, $$0));
            }
        }
        return this.renderMessages;
    }

    private Optional<Component[]> filteredMessages() {
        for (int $$0 = 0; $$0 < 4; ++$$0) {
            if (this.filteredMessages[$$0].equals(this.messages[$$0])) continue;
            return Optional.of(this.filteredMessages);
        }
        return Optional.empty();
    }

    public boolean hasAnyClickCommands(Player $$0) {
        for (Component $$1 : this.b($$0.isTextFilteringEnabled())) {
            Style $$2 = $$1.getStyle();
            ClickEvent $$3 = $$2.getClickEvent();
            if ($$3 == null || $$3.action() != ClickEvent.Action.RUN_COMMAND) continue;
            return true;
        }
        return false;
    }
}

