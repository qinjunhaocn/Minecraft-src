/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.util.parsing.packrat;

import javax.annotation.Nullable;
import net.minecraft.util.parsing.packrat.Control;
import net.minecraft.util.parsing.packrat.ParseState;
import net.minecraft.util.parsing.packrat.Scope;
import net.minecraft.util.parsing.packrat.Term;

public interface Rule<S, T> {
    @Nullable
    public T parse(ParseState<S> var1);

    public static <S, T> Rule<S, T> fromTerm(Term<S> $$0, RuleAction<S, T> $$1) {
        return new WrappedTerm<S, T>($$1, $$0);
    }

    public static <S, T> Rule<S, T> fromTerm(Term<S> $$0, SimpleRuleAction<S, T> $$1) {
        return new WrappedTerm<S, T>($$1, $$0);
    }

    public record WrappedTerm<S, T>(RuleAction<S, T> action, Term<S> child) implements Rule<S, T>
    {
        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Nullable
        public T parse(ParseState<S> $$0) {
            Scope $$1 = $$0.scope();
            $$1.pushFrame();
            try {
                if (this.child.parse($$0, $$1, Control.UNBOUND)) {
                    T t = this.action.run($$0);
                    return t;
                }
                T t = null;
                return t;
            } finally {
                $$1.popFrame();
            }
        }
    }

    @FunctionalInterface
    public static interface RuleAction<S, T> {
        @Nullable
        public T run(ParseState<S> var1);
    }

    @FunctionalInterface
    public static interface SimpleRuleAction<S, T>
    extends RuleAction<S, T> {
        public T run(Scope var1);

        @Override
        default public T run(ParseState<S> $$0) {
            return this.run($$0.scope());
        }
    }
}

