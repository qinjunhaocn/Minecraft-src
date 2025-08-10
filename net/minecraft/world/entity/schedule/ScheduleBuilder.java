/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.schedule;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;

public class ScheduleBuilder {
    private final Schedule schedule;
    private final List<ActivityTransition> transitions = Lists.newArrayList();

    public ScheduleBuilder(Schedule $$0) {
        this.schedule = $$0;
    }

    public ScheduleBuilder changeActivityAt(int $$0, Activity $$1) {
        this.transitions.add(new ActivityTransition($$0, $$1));
        return this;
    }

    public Schedule build() {
        this.transitions.stream().map(ActivityTransition::getActivity).collect(Collectors.toSet()).forEach(this.schedule::ensureTimelineExistsFor);
        this.transitions.forEach($$0 -> {
            Activity $$12 = $$0.getActivity();
            this.schedule.getAllTimelinesExceptFor($$12).forEach($$1 -> $$1.addKeyframe($$0.getTime(), 0.0f));
            this.schedule.getTimelineFor($$12).addKeyframe($$0.getTime(), 1.0f);
        });
        return this.schedule;
    }

    static class ActivityTransition {
        private final int time;
        private final Activity activity;

        public ActivityTransition(int $$0, Activity $$1) {
            this.time = $$0;
            this.activity = $$1;
        }

        public int getTime() {
            return this.time;
        }

        public Activity getActivity() {
            return this.activity;
        }
    }
}

