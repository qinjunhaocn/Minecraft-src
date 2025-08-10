/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 *  java.lang.runtime.SwitchBootstraps
 */
package net.minecraft.client.resources.model;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import java.lang.runtime.SwitchBootstraps;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;

public class QuadCollection {
    public static final QuadCollection EMPTY = new QuadCollection(List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), List.of());
    private final List<BakedQuad> all;
    private final List<BakedQuad> unculled;
    private final List<BakedQuad> north;
    private final List<BakedQuad> south;
    private final List<BakedQuad> east;
    private final List<BakedQuad> west;
    private final List<BakedQuad> up;
    private final List<BakedQuad> down;

    QuadCollection(List<BakedQuad> $$0, List<BakedQuad> $$1, List<BakedQuad> $$2, List<BakedQuad> $$3, List<BakedQuad> $$4, List<BakedQuad> $$5, List<BakedQuad> $$6, List<BakedQuad> $$7) {
        this.all = $$0;
        this.unculled = $$1;
        this.north = $$2;
        this.south = $$3;
        this.east = $$4;
        this.west = $$5;
        this.up = $$6;
        this.down = $$7;
    }

    public List<BakedQuad> getQuads(@Nullable Direction $$0) {
        Direction direction = $$0;
        int n = 0;
        return switch (SwitchBootstraps.enumSwitch("enumSwitch", new Object[]{"NORTH", "SOUTH", "EAST", "WEST", "UP", "DOWN"}, (Direction)direction, (int)n)) {
            default -> throw new MatchException(null, null);
            case -1 -> this.unculled;
            case 0 -> this.north;
            case 1 -> this.south;
            case 2 -> this.east;
            case 3 -> this.west;
            case 4 -> this.up;
            case 5 -> this.down;
        };
    }

    public List<BakedQuad> getAll() {
        return this.all;
    }

    public static class Builder {
        private final ImmutableList.Builder<BakedQuad> unculledFaces = ImmutableList.builder();
        private final Multimap<Direction, BakedQuad> culledFaces = ArrayListMultimap.create();

        public Builder addCulledFace(Direction $$0, BakedQuad $$1) {
            this.culledFaces.put($$0, $$1);
            return this;
        }

        public Builder addUnculledFace(BakedQuad $$0) {
            this.unculledFaces.add((Object)$$0);
            return this;
        }

        private static QuadCollection createFromSublists(List<BakedQuad> $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7) {
            int $$8 = 0;
            List<BakedQuad> $$9 = $$0.subList($$8, $$8 += $$1);
            List<BakedQuad> $$10 = $$0.subList($$8, $$8 += $$2);
            List<BakedQuad> $$11 = $$0.subList($$8, $$8 += $$3);
            List<BakedQuad> $$12 = $$0.subList($$8, $$8 += $$4);
            List<BakedQuad> $$13 = $$0.subList($$8, $$8 += $$5);
            List<BakedQuad> $$14 = $$0.subList($$8, $$8 += $$6);
            List<BakedQuad> $$15 = $$0.subList($$8, $$8 + $$7);
            return new QuadCollection($$0, $$9, $$10, $$11, $$12, $$13, $$14, $$15);
        }

        public QuadCollection build() {
            ImmutableCollection $$0 = this.unculledFaces.build();
            if (this.culledFaces.isEmpty()) {
                if ($$0.isEmpty()) {
                    return EMPTY;
                }
                return new QuadCollection((List<BakedQuad>)((Object)$$0), (List<BakedQuad>)((Object)$$0), List.of(), List.of(), List.of(), List.of(), List.of(), List.of());
            }
            ImmutableList.Builder $$1 = ImmutableList.builder();
            $$1.addAll((Iterable)$$0);
            Collection<BakedQuad> $$2 = this.culledFaces.get(Direction.NORTH);
            $$1.addAll($$2);
            Collection<BakedQuad> $$3 = this.culledFaces.get(Direction.SOUTH);
            $$1.addAll($$3);
            Collection<BakedQuad> $$4 = this.culledFaces.get(Direction.EAST);
            $$1.addAll($$4);
            Collection<BakedQuad> $$5 = this.culledFaces.get(Direction.WEST);
            $$1.addAll($$5);
            Collection<BakedQuad> $$6 = this.culledFaces.get(Direction.UP);
            $$1.addAll($$6);
            Collection<BakedQuad> $$7 = this.culledFaces.get(Direction.DOWN);
            $$1.addAll($$7);
            return Builder.createFromSublists((List<BakedQuad>)((Object)$$1.build()), $$0.size(), $$2.size(), $$3.size(), $$4.size(), $$5.size(), $$6.size(), $$7.size());
        }
    }
}

