/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Vector2i
 */
package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.ScrollWheelHandler;
import net.minecraft.client.gui.ItemSlotMouseAction;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ServerboundSelectBundleItemPacket;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector2i;

public class BundleMouseActions
implements ItemSlotMouseAction {
    private final Minecraft minecraft;
    private final ScrollWheelHandler scrollWheelHandler;

    public BundleMouseActions(Minecraft $$0) {
        this.minecraft = $$0;
        this.scrollWheelHandler = new ScrollWheelHandler();
    }

    @Override
    public boolean matches(Slot $$0) {
        return $$0.getItem().is(ItemTags.BUNDLES);
    }

    @Override
    public boolean onMouseScrolled(double $$0, double $$1, int $$2, ItemStack $$3) {
        int $$8;
        int $$7;
        int $$6;
        int $$4 = BundleItem.getNumberOfItemsToShow($$3);
        if ($$4 == 0) {
            return false;
        }
        Vector2i $$5 = this.scrollWheelHandler.onMouseScroll($$0, $$1);
        int n = $$6 = $$5.y == 0 ? -$$5.x : $$5.y;
        if ($$6 != 0 && ($$7 = BundleItem.getSelectedItem($$3)) != ($$8 = ScrollWheelHandler.getNextScrollWheelSelection($$6, $$7, $$4))) {
            this.toggleSelectedBundleItem($$3, $$2, $$8);
        }
        return true;
    }

    @Override
    public void onStopHovering(Slot $$0) {
        this.unselectedBundleItem($$0.getItem(), $$0.index);
    }

    @Override
    public void onSlotClicked(Slot $$0, ClickType $$1) {
        if ($$1 == ClickType.QUICK_MOVE || $$1 == ClickType.SWAP) {
            this.unselectedBundleItem($$0.getItem(), $$0.index);
        }
    }

    private void toggleSelectedBundleItem(ItemStack $$0, int $$1, int $$2) {
        if (this.minecraft.getConnection() != null && $$2 < BundleItem.getNumberOfItemsToShow($$0)) {
            ClientPacketListener $$3 = this.minecraft.getConnection();
            BundleItem.toggleSelectedItem($$0, $$2);
            $$3.send(new ServerboundSelectBundleItemPacket($$1, $$2));
        }
    }

    public void unselectedBundleItem(ItemStack $$0, int $$1) {
        this.toggleSelectedBundleItem($$0, $$1, -1);
    }
}

