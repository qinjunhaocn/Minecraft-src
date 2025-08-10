/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  org.jetbrains.annotations.VisibleForTesting
 */
package net.minecraft.network.chat;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayDeque;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.SignedMessageBody;
import org.jetbrains.annotations.VisibleForTesting;

public class MessageSignatureCache {
    public static final int NOT_FOUND = -1;
    private static final int DEFAULT_CAPACITY = 128;
    private final MessageSignature[] entries;

    public MessageSignatureCache(int $$0) {
        this.entries = new MessageSignature[$$0];
    }

    public static MessageSignatureCache createDefault() {
        return new MessageSignatureCache(128);
    }

    public int pack(MessageSignature $$0) {
        for (int $$1 = 0; $$1 < this.entries.length; ++$$1) {
            if (!$$0.equals((Object)this.entries[$$1])) continue;
            return $$1;
        }
        return -1;
    }

    @Nullable
    public MessageSignature unpack(int $$0) {
        return this.entries[$$0];
    }

    public void push(SignedMessageBody $$0, @Nullable MessageSignature $$1) {
        List<MessageSignature> $$2 = $$0.lastSeen().entries();
        ArrayDeque<MessageSignature> $$3 = new ArrayDeque<MessageSignature>($$2.size() + 1);
        $$3.addAll($$2);
        if ($$1 != null) {
            $$3.add($$1);
        }
        this.push($$3);
    }

    @VisibleForTesting
    void push(List<MessageSignature> $$0) {
        this.push(new ArrayDeque<MessageSignature>($$0));
    }

    private void push(ArrayDeque<MessageSignature> $$0) {
        ObjectOpenHashSet $$1 = new ObjectOpenHashSet($$0);
        for (int $$2 = 0; !$$0.isEmpty() && $$2 < this.entries.length; ++$$2) {
            MessageSignature $$3 = this.entries[$$2];
            this.entries[$$2] = $$0.removeLast();
            if ($$3 == null || $$1.contains((Object)$$3)) continue;
            $$0.addFirst($$3);
        }
    }
}

