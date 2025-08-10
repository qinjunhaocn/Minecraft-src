/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.gametest.framework;

import com.google.common.collect.Sets;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTestInstance;

public class FailedTestTracker {
    private static final Set<Holder.Reference<GameTestInstance>> LAST_FAILED_TESTS = Sets.newHashSet();

    public static Stream<Holder.Reference<GameTestInstance>> getLastFailedTests() {
        return LAST_FAILED_TESTS.stream();
    }

    public static void rememberFailedTest(Holder.Reference<GameTestInstance> $$0) {
        LAST_FAILED_TESTS.add($$0);
    }

    public static void forgetFailedTests() {
        LAST_FAILED_TESTS.clear();
    }
}

