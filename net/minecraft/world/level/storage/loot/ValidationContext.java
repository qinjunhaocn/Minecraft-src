/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.LootContextUser;

public class ValidationContext {
    private final ProblemReporter reporter;
    private final ContextKeySet contextKeySet;
    private final Optional<HolderGetter.Provider> resolver;
    private final Set<ResourceKey<?>> visitedElements;

    public ValidationContext(ProblemReporter $$0, ContextKeySet $$1, HolderGetter.Provider $$2) {
        this($$0, $$1, Optional.of($$2), Set.of());
    }

    public ValidationContext(ProblemReporter $$0, ContextKeySet $$1) {
        this($$0, $$1, Optional.empty(), Set.of());
    }

    private ValidationContext(ProblemReporter $$0, ContextKeySet $$1, Optional<HolderGetter.Provider> $$2, Set<ResourceKey<?>> $$3) {
        this.reporter = $$0;
        this.contextKeySet = $$1;
        this.resolver = $$2;
        this.visitedElements = $$3;
    }

    public ValidationContext forChild(ProblemReporter.PathElement $$0) {
        return new ValidationContext(this.reporter.forChild($$0), this.contextKeySet, this.resolver, this.visitedElements);
    }

    public ValidationContext enterElement(ProblemReporter.PathElement $$0, ResourceKey<?> $$1) {
        ImmutableCollection $$2 = ((ImmutableSet.Builder)((ImmutableSet.Builder)ImmutableSet.builder().addAll(this.visitedElements)).add($$1)).build();
        return new ValidationContext(this.reporter.forChild($$0), this.contextKeySet, this.resolver, (Set<ResourceKey<?>>)((Object)$$2));
    }

    public boolean hasVisitedElement(ResourceKey<?> $$0) {
        return this.visitedElements.contains($$0);
    }

    public void reportProblem(ProblemReporter.Problem $$0) {
        this.reporter.report($$0);
    }

    public void validateContextUsage(LootContextUser $$0) {
        Set<ContextKey<?>> $$1 = $$0.getReferencedContextParams();
        Sets.SetView<ContextKey<?>> $$2 = Sets.difference($$1, this.contextKeySet.allowed());
        if (!$$2.isEmpty()) {
            this.reporter.report(new ParametersNotProvidedProblem($$2));
        }
    }

    public HolderGetter.Provider resolver() {
        return this.resolver.orElseThrow(() -> new UnsupportedOperationException("References not allowed"));
    }

    public boolean allowsReferences() {
        return this.resolver.isPresent();
    }

    public ValidationContext setContextKeySet(ContextKeySet $$0) {
        return new ValidationContext(this.reporter, $$0, this.resolver, this.visitedElements);
    }

    public ProblemReporter reporter() {
        return this.reporter;
    }

    public record ParametersNotProvidedProblem(Set<ContextKey<?>> notProvided) implements ProblemReporter.Problem
    {
        @Override
        public String description() {
            return "Parameters " + String.valueOf(this.notProvided) + " are not provided in this context";
        }
    }

    public record MissingReferenceProblem(ResourceKey<?> referenced) implements ProblemReporter.Problem
    {
        @Override
        public String description() {
            return "Missing element " + String.valueOf(this.referenced.location()) + " of type " + String.valueOf(this.referenced.registry());
        }
    }

    public record RecursiveReferenceProblem(ResourceKey<?> referenced) implements ProblemReporter.Problem
    {
        @Override
        public String description() {
            return String.valueOf(this.referenced.location()) + " of type " + String.valueOf(this.referenced.registry()) + " is recursively called";
        }
    }

    public record ReferenceNotAllowedProblem(ResourceKey<?> referenced) implements ProblemReporter.Problem
    {
        @Override
        public String description() {
            return "Reference to " + String.valueOf(this.referenced.location()) + " of type " + String.valueOf(this.referenced.registry()) + " was used, but references are not allowed";
        }
    }
}

