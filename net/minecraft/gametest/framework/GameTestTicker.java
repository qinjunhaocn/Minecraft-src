/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.gametest.framework;

import com.google.common.collect.Lists;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.gametest.framework.GameTestRunner;

public class GameTestTicker {
    public static final GameTestTicker SINGLETON = new GameTestTicker();
    private final Collection<GameTestInfo> testInfos = Lists.newCopyOnWriteArrayList();
    @Nullable
    private GameTestRunner runner;
    private State state = State.IDLE;
    private volatile boolean ticking = false;

    private GameTestTicker() {
    }

    public void add(GameTestInfo $$0) {
        this.testInfos.add($$0);
    }

    public void clear() {
        if (this.state != State.IDLE) {
            this.state = State.HALTING;
            return;
        }
        this.testInfos.clear();
        if (this.runner != null) {
            this.runner.stop();
            this.runner = null;
        }
    }

    public void setRunner(GameTestRunner $$0) {
        if (this.runner != null) {
            Util.logAndPauseIfInIde("The runner was already set in GameTestTicker");
        }
        this.runner = $$0;
    }

    public void startTicking() {
        this.ticking = true;
    }

    public void tick() {
        if (this.runner == null || !this.ticking) {
            return;
        }
        this.state = State.RUNNING;
        this.testInfos.forEach($$0 -> $$0.tick(this.runner));
        this.testInfos.removeIf(GameTestInfo::isDone);
        State $$02 = this.state;
        this.state = State.IDLE;
        if ($$02 == State.HALTING) {
            this.clear();
        }
    }

    static final class State
    extends Enum<State> {
        public static final /* enum */ State IDLE = new State();
        public static final /* enum */ State RUNNING = new State();
        public static final /* enum */ State HALTING = new State();
        private static final /* synthetic */ State[] $VALUES;

        public static State[] values() {
            return (State[])$VALUES.clone();
        }

        public static State valueOf(String $$0) {
            return Enum.valueOf(State.class, $$0);
        }

        private static /* synthetic */ State[] a() {
            return new State[]{IDLE, RUNNING, HALTING};
        }

        static {
            $VALUES = State.a();
        }
    }
}

