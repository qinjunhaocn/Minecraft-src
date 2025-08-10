/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 */
package net.minecraft.world.entity.player;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetPlayerInventoryPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.EntityEquipment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class Inventory
implements Container,
Nameable {
    public static final int POP_TIME_DURATION = 5;
    public static final int INVENTORY_SIZE = 36;
    public static final int SELECTION_SIZE = 9;
    public static final int SLOT_OFFHAND = 40;
    public static final int SLOT_BODY_ARMOR = 41;
    public static final int SLOT_SADDLE = 42;
    public static final int NOT_FOUND_INDEX = -1;
    public static final Int2ObjectMap<EquipmentSlot> EQUIPMENT_SLOT_MAPPING = new Int2ObjectArrayMap(Map.of((Object)EquipmentSlot.FEET.getIndex(36), (Object)EquipmentSlot.FEET, (Object)EquipmentSlot.LEGS.getIndex(36), (Object)EquipmentSlot.LEGS, (Object)EquipmentSlot.CHEST.getIndex(36), (Object)EquipmentSlot.CHEST, (Object)EquipmentSlot.HEAD.getIndex(36), (Object)EquipmentSlot.HEAD, (Object)40, (Object)EquipmentSlot.OFFHAND, (Object)41, (Object)EquipmentSlot.BODY, (Object)42, (Object)EquipmentSlot.SADDLE));
    private final NonNullList<ItemStack> items = NonNullList.withSize(36, ItemStack.EMPTY);
    private int selected;
    public final Player player;
    private final EntityEquipment equipment;
    private int timesChanged;

    public Inventory(Player $$0, EntityEquipment $$1) {
        this.player = $$0;
        this.equipment = $$1;
    }

    public int getSelectedSlot() {
        return this.selected;
    }

    public void setSelectedSlot(int $$0) {
        if (!Inventory.isHotbarSlot($$0)) {
            throw new IllegalArgumentException("Invalid selected slot");
        }
        this.selected = $$0;
    }

    public ItemStack getSelectedItem() {
        return this.items.get(this.selected);
    }

    public ItemStack setSelectedItem(ItemStack $$0) {
        return this.items.set(this.selected, $$0);
    }

    public static int getSelectionSize() {
        return 9;
    }

    public NonNullList<ItemStack> getNonEquipmentItems() {
        return this.items;
    }

    private boolean hasRemainingSpaceForItem(ItemStack $$0, ItemStack $$1) {
        return !$$0.isEmpty() && ItemStack.isSameItemSameComponents($$0, $$1) && $$0.isStackable() && $$0.getCount() < this.getMaxStackSize($$0);
    }

    public int getFreeSlot() {
        for (int $$0 = 0; $$0 < this.items.size(); ++$$0) {
            if (!this.items.get($$0).isEmpty()) continue;
            return $$0;
        }
        return -1;
    }

    public void addAndPickItem(ItemStack $$0) {
        int $$1;
        this.setSelectedSlot(this.getSuitableHotbarSlot());
        if (!this.items.get(this.selected).isEmpty() && ($$1 = this.getFreeSlot()) != -1) {
            this.items.set($$1, this.items.get(this.selected));
        }
        this.items.set(this.selected, $$0);
    }

    public void pickSlot(int $$0) {
        this.setSelectedSlot(this.getSuitableHotbarSlot());
        ItemStack $$1 = this.items.get(this.selected);
        this.items.set(this.selected, this.items.get($$0));
        this.items.set($$0, $$1);
    }

    public static boolean isHotbarSlot(int $$0) {
        return $$0 >= 0 && $$0 < 9;
    }

    public int findSlotMatchingItem(ItemStack $$0) {
        for (int $$1 = 0; $$1 < this.items.size(); ++$$1) {
            if (this.items.get($$1).isEmpty() || !ItemStack.isSameItemSameComponents($$0, this.items.get($$1))) continue;
            return $$1;
        }
        return -1;
    }

    public static boolean isUsableForCrafting(ItemStack $$0) {
        return !$$0.isDamaged() && !$$0.isEnchanted() && !$$0.has(DataComponents.CUSTOM_NAME);
    }

    public int findSlotMatchingCraftingIngredient(Holder<Item> $$0, ItemStack $$1) {
        for (int $$2 = 0; $$2 < this.items.size(); ++$$2) {
            ItemStack $$3 = this.items.get($$2);
            if ($$3.isEmpty() || !$$3.is($$0) || !Inventory.isUsableForCrafting($$3) || !$$1.isEmpty() && !ItemStack.isSameItemSameComponents($$1, $$3)) continue;
            return $$2;
        }
        return -1;
    }

    public int getSuitableHotbarSlot() {
        for (int $$0 = 0; $$0 < 9; ++$$0) {
            int $$1 = (this.selected + $$0) % 9;
            if (!this.items.get($$1).isEmpty()) continue;
            return $$1;
        }
        for (int $$2 = 0; $$2 < 9; ++$$2) {
            int $$3 = (this.selected + $$2) % 9;
            if (this.items.get($$3).isEnchanted()) continue;
            return $$3;
        }
        return this.selected;
    }

    public int clearOrCountMatchingItems(Predicate<ItemStack> $$0, int $$1, Container $$2) {
        int $$3 = 0;
        boolean $$4 = $$1 == 0;
        $$3 += ContainerHelper.clearOrCountMatchingItems(this, $$0, $$1 - $$3, $$4);
        $$3 += ContainerHelper.clearOrCountMatchingItems($$2, $$0, $$1 - $$3, $$4);
        ItemStack $$5 = this.player.containerMenu.getCarried();
        $$3 += ContainerHelper.clearOrCountMatchingItems($$5, $$0, $$1 - $$3, $$4);
        if ($$5.isEmpty()) {
            this.player.containerMenu.setCarried(ItemStack.EMPTY);
        }
        return $$3;
    }

    private int addResource(ItemStack $$0) {
        int $$1 = this.getSlotWithRemainingSpace($$0);
        if ($$1 == -1) {
            $$1 = this.getFreeSlot();
        }
        if ($$1 == -1) {
            return $$0.getCount();
        }
        return this.addResource($$1, $$0);
    }

    private int addResource(int $$0, ItemStack $$1) {
        int $$4;
        int $$5;
        int $$2 = $$1.getCount();
        ItemStack $$3 = this.getItem($$0);
        if ($$3.isEmpty()) {
            $$3 = $$1.copyWithCount(0);
            this.setItem($$0, $$3);
        }
        if (($$5 = Math.min($$2, $$4 = this.getMaxStackSize($$3) - $$3.getCount())) == 0) {
            return $$2;
        }
        $$3.grow($$5);
        $$3.setPopTime(5);
        return $$2 -= $$5;
    }

    public int getSlotWithRemainingSpace(ItemStack $$0) {
        if (this.hasRemainingSpaceForItem(this.getItem(this.selected), $$0)) {
            return this.selected;
        }
        if (this.hasRemainingSpaceForItem(this.getItem(40), $$0)) {
            return 40;
        }
        for (int $$1 = 0; $$1 < this.items.size(); ++$$1) {
            if (!this.hasRemainingSpaceForItem(this.items.get($$1), $$0)) continue;
            return $$1;
        }
        return -1;
    }

    public void tick() {
        for (int $$0 = 0; $$0 < this.items.size(); ++$$0) {
            ItemStack $$1 = this.getItem($$0);
            if ($$1.isEmpty()) continue;
            $$1.inventoryTick(this.player.level(), this.player, $$0 == this.selected ? EquipmentSlot.MAINHAND : null);
        }
    }

    public boolean add(ItemStack $$0) {
        return this.add(-1, $$0);
    }

    public boolean add(int $$0, ItemStack $$1) {
        if ($$1.isEmpty()) {
            return false;
        }
        try {
            if (!$$1.isDamaged()) {
                int $$2;
                do {
                    $$2 = $$1.getCount();
                    if ($$0 == -1) {
                        $$1.setCount(this.addResource($$1));
                        continue;
                    }
                    $$1.setCount(this.addResource($$0, $$1));
                } while (!$$1.isEmpty() && $$1.getCount() < $$2);
                if ($$1.getCount() == $$2 && this.player.hasInfiniteMaterials()) {
                    $$1.setCount(0);
                    return true;
                }
                return $$1.getCount() < $$2;
            }
            if ($$0 == -1) {
                $$0 = this.getFreeSlot();
            }
            if ($$0 >= 0) {
                this.items.set($$0, $$1.copyAndClear());
                this.items.get($$0).setPopTime(5);
                return true;
            }
            if (this.player.hasInfiniteMaterials()) {
                $$1.setCount(0);
                return true;
            }
            return false;
        } catch (Throwable $$3) {
            CrashReport $$4 = CrashReport.forThrowable($$3, "Adding item to inventory");
            CrashReportCategory $$5 = $$4.addCategory("Item being added");
            $$5.setDetail("Item ID", Item.getId($$1.getItem()));
            $$5.setDetail("Item data", $$1.getDamageValue());
            $$5.setDetail("Item name", () -> $$1.getHoverName().getString());
            throw new ReportedException($$4);
        }
    }

    public void placeItemBackInInventory(ItemStack $$0) {
        this.placeItemBackInInventory($$0, true);
    }

    public void placeItemBackInInventory(ItemStack $$0, boolean $$1) {
        while (!$$0.isEmpty()) {
            Player player;
            int $$2 = this.getSlotWithRemainingSpace($$0);
            if ($$2 == -1) {
                $$2 = this.getFreeSlot();
            }
            if ($$2 == -1) {
                this.player.drop($$0, false);
                break;
            }
            int $$3 = $$0.getMaxStackSize() - this.getItem($$2).getCount();
            if (!this.add($$2, $$0.split($$3)) || !$$1 || !((player = this.player) instanceof ServerPlayer)) continue;
            ServerPlayer $$4 = (ServerPlayer)player;
            $$4.connection.send(this.createInventoryUpdatePacket($$2));
        }
    }

    public ClientboundSetPlayerInventoryPacket createInventoryUpdatePacket(int $$0) {
        return new ClientboundSetPlayerInventoryPacket($$0, this.getItem($$0).copy());
    }

    @Override
    public ItemStack removeItem(int $$0, int $$1) {
        ItemStack $$3;
        if ($$0 < this.items.size()) {
            return ContainerHelper.removeItem(this.items, $$0, $$1);
        }
        EquipmentSlot $$2 = (EquipmentSlot)EQUIPMENT_SLOT_MAPPING.get($$0);
        if ($$2 != null && !($$3 = this.equipment.get($$2)).isEmpty()) {
            return $$3.split($$1);
        }
        return ItemStack.EMPTY;
    }

    public void removeItem(ItemStack $$0) {
        for (int $$1 = 0; $$1 < this.items.size(); ++$$1) {
            if (this.items.get($$1) != $$0) continue;
            this.items.set($$1, ItemStack.EMPTY);
            return;
        }
        for (EquipmentSlot $$2 : EQUIPMENT_SLOT_MAPPING.values()) {
            ItemStack $$3 = this.equipment.get($$2);
            if ($$3 != $$0) continue;
            this.equipment.set($$2, ItemStack.EMPTY);
            return;
        }
    }

    @Override
    public ItemStack removeItemNoUpdate(int $$0) {
        if ($$0 < this.items.size()) {
            ItemStack $$1 = this.items.get($$0);
            this.items.set($$0, ItemStack.EMPTY);
            return $$1;
        }
        EquipmentSlot $$2 = (EquipmentSlot)EQUIPMENT_SLOT_MAPPING.get($$0);
        if ($$2 != null) {
            return this.equipment.set($$2, ItemStack.EMPTY);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int $$0, ItemStack $$1) {
        EquipmentSlot $$2;
        if ($$0 < this.items.size()) {
            this.items.set($$0, $$1);
        }
        if (($$2 = (EquipmentSlot)EQUIPMENT_SLOT_MAPPING.get($$0)) != null) {
            this.equipment.set($$2, $$1);
        }
    }

    public void save(ValueOutput.TypedOutputList<ItemStackWithSlot> $$0) {
        for (int $$1 = 0; $$1 < this.items.size(); ++$$1) {
            ItemStack $$2 = this.items.get($$1);
            if ($$2.isEmpty()) continue;
            $$0.add(new ItemStackWithSlot($$1, $$2));
        }
    }

    public void load(ValueInput.TypedInputList<ItemStackWithSlot> $$0) {
        this.items.clear();
        for (ItemStackWithSlot $$1 : $$0) {
            if (!$$1.isValidInContainer(this.items.size())) continue;
            this.setItem($$1.slot(), $$1.stack());
        }
    }

    @Override
    public int getContainerSize() {
        return this.items.size() + EQUIPMENT_SLOT_MAPPING.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack $$0 : this.items) {
            if ($$0.isEmpty()) continue;
            return false;
        }
        for (EquipmentSlot $$1 : EQUIPMENT_SLOT_MAPPING.values()) {
            if (this.equipment.get($$1).isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int $$0) {
        if ($$0 < this.items.size()) {
            return this.items.get($$0);
        }
        EquipmentSlot $$1 = (EquipmentSlot)EQUIPMENT_SLOT_MAPPING.get($$0);
        if ($$1 != null) {
            return this.equipment.get($$1);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public Component getName() {
        return Component.translatable("container.inventory");
    }

    public void dropAll() {
        for (int $$0 = 0; $$0 < this.items.size(); ++$$0) {
            ItemStack $$1 = this.items.get($$0);
            if ($$1.isEmpty()) continue;
            this.player.drop($$1, true, false);
            this.items.set($$0, ItemStack.EMPTY);
        }
        this.equipment.dropAll(this.player);
    }

    @Override
    public void setChanged() {
        ++this.timesChanged;
    }

    public int getTimesChanged() {
        return this.timesChanged;
    }

    @Override
    public boolean stillValid(Player $$0) {
        return true;
    }

    public boolean contains(ItemStack $$0) {
        for (ItemStack $$1 : this) {
            if ($$1.isEmpty() || !ItemStack.isSameItemSameComponents($$1, $$0)) continue;
            return true;
        }
        return false;
    }

    public boolean contains(TagKey<Item> $$0) {
        for (ItemStack $$1 : this) {
            if ($$1.isEmpty() || !$$1.is($$0)) continue;
            return true;
        }
        return false;
    }

    public boolean contains(Predicate<ItemStack> $$0) {
        for (ItemStack $$1 : this) {
            if (!$$0.test($$1)) continue;
            return true;
        }
        return false;
    }

    public void replaceWith(Inventory $$0) {
        for (int $$1 = 0; $$1 < this.getContainerSize(); ++$$1) {
            this.setItem($$1, $$0.getItem($$1));
        }
        this.setSelectedSlot($$0.getSelectedSlot());
    }

    @Override
    public void clearContent() {
        this.items.clear();
        this.equipment.clear();
    }

    public void fillStackedContents(StackedItemContents $$0) {
        for (ItemStack $$1 : this.items) {
            $$0.accountSimpleStack($$1);
        }
    }

    public ItemStack removeFromSelected(boolean $$0) {
        ItemStack $$1 = this.getSelectedItem();
        if ($$1.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return this.removeItem(this.selected, $$0 ? $$1.getCount() : 1);
    }
}

