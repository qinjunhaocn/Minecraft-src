/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.client.player;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.gui.screens.inventory.CommandBlockEditScreen;
import net.minecraft.client.gui.screens.inventory.HangingSignEditScreen;
import net.minecraft.client.gui.screens.inventory.JigsawBlockEditScreen;
import net.minecraft.client.gui.screens.inventory.MinecartCommandBlockEditScreen;
import net.minecraft.client.gui.screens.inventory.SignEditScreen;
import net.minecraft.client.gui.screens.inventory.StructureBlockEditScreen;
import net.minecraft.client.gui.screens.inventory.TestBlockEditScreen;
import net.minecraft.client.gui.screens.inventory.TestInstanceBlockEditScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.resources.sounds.AmbientSoundHandler;
import net.minecraft.client.resources.sounds.BiomeAmbientSoundsHandler;
import net.minecraft.client.resources.sounds.BubbleColumnAmbientSoundHandler;
import net.minecraft.client.resources.sounds.ElytraOnPlayerSoundInstance;
import net.minecraft.client.resources.sounds.RidingHappyGhastSoundInstance;
import net.minecraft.client.resources.sounds.RidingMinecartSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.UnderwaterAmbientSoundHandler;
import net.minecraft.client.resources.sounds.UnderwaterAmbientSoundInstances;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.network.protocol.game.ServerboundRecipeBookSeenRecipePacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.StatsCounter;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.TickThrottler;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.HappyGhast;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.entity.vehicle.AbstractBoat;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WritableBookContent;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.entity.TestBlockEntity;
import net.minecraft.world.level.block.entity.TestInstanceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.slf4j.Logger;

public class LocalPlayer
extends AbstractClientPlayer {
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final int POSITION_REMINDER_INTERVAL = 20;
    private static final int WATER_VISION_MAX_TIME = 600;
    private static final int WATER_VISION_QUICK_TIME = 100;
    private static final float WATER_VISION_QUICK_PERCENT = 0.6f;
    private static final double SUFFOCATING_COLLISION_CHECK_SCALE = 0.35;
    private static final double MINOR_COLLISION_ANGLE_THRESHOLD_RADIAN = 0.13962633907794952;
    public static final float USING_ITEM_SPEED_FACTOR = 0.2f;
    public final ClientPacketListener connection;
    private final StatsCounter stats;
    private final ClientRecipeBook recipeBook;
    private final TickThrottler dropSpamThrottler = new TickThrottler(20, 1280);
    private final List<AmbientSoundHandler> ambientSoundHandlers = Lists.newArrayList();
    private int permissionLevel = 0;
    private double xLast;
    private double yLast;
    private double zLast;
    private float yRotLast;
    private float xRotLast;
    private boolean lastOnGround;
    private boolean lastHorizontalCollision;
    private boolean crouching;
    private boolean wasSprinting;
    private int positionReminder;
    private boolean flashOnSetHealth;
    public ClientInput input = new ClientInput();
    private Input lastSentInput;
    protected final Minecraft minecraft;
    protected int sprintTriggerTime;
    public int experienceDisplayStartTick;
    public float yBob;
    public float xBob;
    public float yBobO;
    public float xBobO;
    private int jumpRidingTicks;
    private float jumpRidingScale;
    public float portalEffectIntensity;
    public float oPortalEffectIntensity;
    private boolean startedUsingItem;
    @Nullable
    private InteractionHand usingItemHand;
    private boolean handsBusy;
    private boolean autoJumpEnabled = true;
    private int autoJumpTime;
    private boolean wasFallFlying;
    private int waterVisionTime;
    private boolean showDeathScreen = true;
    private boolean doLimitedCrafting = false;

    public LocalPlayer(Minecraft $$0, ClientLevel $$1, ClientPacketListener $$2, StatsCounter $$3, ClientRecipeBook $$4, Input $$5, boolean $$6) {
        super($$1, $$2.getLocalGameProfile());
        this.minecraft = $$0;
        this.connection = $$2;
        this.stats = $$3;
        this.recipeBook = $$4;
        this.lastSentInput = $$5;
        this.wasSprinting = $$6;
        this.ambientSoundHandlers.add(new UnderwaterAmbientSoundHandler(this, $$0.getSoundManager()));
        this.ambientSoundHandlers.add(new BubbleColumnAmbientSoundHandler(this));
        this.ambientSoundHandlers.add(new BiomeAmbientSoundsHandler(this, $$0.getSoundManager(), $$1.getBiomeManager()));
    }

    @Override
    public void heal(float $$0) {
    }

    @Override
    public boolean startRiding(Entity $$0, boolean $$1) {
        if (!super.startRiding($$0, $$1)) {
            return false;
        }
        if ($$0 instanceof AbstractMinecart) {
            this.minecraft.getSoundManager().play(new RidingMinecartSoundInstance(this, (AbstractMinecart)$$0, true));
            this.minecraft.getSoundManager().play(new RidingMinecartSoundInstance(this, (AbstractMinecart)$$0, false));
        } else if ($$0 instanceof HappyGhast) {
            this.minecraft.getSoundManager().play(new RidingHappyGhastSoundInstance(this, (HappyGhast)$$0));
        }
        return true;
    }

    @Override
    public void removeVehicle() {
        super.removeVehicle();
        this.handsBusy = false;
    }

    @Override
    public float getViewXRot(float $$0) {
        return this.getXRot();
    }

    @Override
    public float getViewYRot(float $$0) {
        if (this.isPassenger()) {
            return super.getViewYRot($$0);
        }
        return this.getYRot();
    }

    @Override
    public void tick() {
        this.tickClientLoadTimeout();
        if (!this.hasClientLoaded()) {
            return;
        }
        this.dropSpamThrottler.tick();
        super.tick();
        if (!this.lastSentInput.equals((Object)this.input.keyPresses)) {
            this.connection.send(new ServerboundPlayerInputPacket(this.input.keyPresses));
            this.lastSentInput = this.input.keyPresses;
        }
        if (this.isPassenger()) {
            this.connection.send(new ServerboundMovePlayerPacket.Rot(this.getYRot(), this.getXRot(), this.onGround(), this.horizontalCollision));
            Entity $$0 = this.getRootVehicle();
            if ($$0 != this && $$0.isLocalInstanceAuthoritative()) {
                this.connection.send(ServerboundMoveVehiclePacket.fromEntity($$0));
                this.sendIsSprintingIfNeeded();
            }
        } else {
            this.sendPosition();
        }
        for (AmbientSoundHandler $$1 : this.ambientSoundHandlers) {
            $$1.tick();
        }
    }

    public float getCurrentMood() {
        for (AmbientSoundHandler $$0 : this.ambientSoundHandlers) {
            if (!($$0 instanceof BiomeAmbientSoundsHandler)) continue;
            return ((BiomeAmbientSoundsHandler)$$0).getMoodiness();
        }
        return 0.0f;
    }

    private void sendPosition() {
        this.sendIsSprintingIfNeeded();
        if (this.isControlledCamera()) {
            boolean $$6;
            double $$0 = this.getX() - this.xLast;
            double $$1 = this.getY() - this.yLast;
            double $$2 = this.getZ() - this.zLast;
            double $$3 = this.getYRot() - this.yRotLast;
            double $$4 = this.getXRot() - this.xRotLast;
            ++this.positionReminder;
            boolean $$5 = Mth.lengthSquared($$0, $$1, $$2) > Mth.square(2.0E-4) || this.positionReminder >= 20;
            boolean bl = $$6 = $$3 != 0.0 || $$4 != 0.0;
            if ($$5 && $$6) {
                this.connection.send(new ServerboundMovePlayerPacket.PosRot(this.position(), this.getYRot(), this.getXRot(), this.onGround(), this.horizontalCollision));
            } else if ($$5) {
                this.connection.send(new ServerboundMovePlayerPacket.Pos(this.position(), this.onGround(), this.horizontalCollision));
            } else if ($$6) {
                this.connection.send(new ServerboundMovePlayerPacket.Rot(this.getYRot(), this.getXRot(), this.onGround(), this.horizontalCollision));
            } else if (this.lastOnGround != this.onGround() || this.lastHorizontalCollision != this.horizontalCollision) {
                this.connection.send(new ServerboundMovePlayerPacket.StatusOnly(this.onGround(), this.horizontalCollision));
            }
            if ($$5) {
                this.xLast = this.getX();
                this.yLast = this.getY();
                this.zLast = this.getZ();
                this.positionReminder = 0;
            }
            if ($$6) {
                this.yRotLast = this.getYRot();
                this.xRotLast = this.getXRot();
            }
            this.lastOnGround = this.onGround();
            this.lastHorizontalCollision = this.horizontalCollision;
            this.autoJumpEnabled = this.minecraft.options.autoJump().get();
        }
    }

    private void sendIsSprintingIfNeeded() {
        boolean $$0 = this.isSprinting();
        if ($$0 != this.wasSprinting) {
            ServerboundPlayerCommandPacket.Action $$1 = $$0 ? ServerboundPlayerCommandPacket.Action.START_SPRINTING : ServerboundPlayerCommandPacket.Action.STOP_SPRINTING;
            this.connection.send(new ServerboundPlayerCommandPacket(this, $$1));
            this.wasSprinting = $$0;
        }
    }

    public boolean drop(boolean $$0) {
        ServerboundPlayerActionPacket.Action $$1 = $$0 ? ServerboundPlayerActionPacket.Action.DROP_ALL_ITEMS : ServerboundPlayerActionPacket.Action.DROP_ITEM;
        ItemStack $$2 = this.getInventory().removeFromSelected($$0);
        this.connection.send(new ServerboundPlayerActionPacket($$1, BlockPos.ZERO, Direction.DOWN));
        return !$$2.isEmpty();
    }

    @Override
    public void swing(InteractionHand $$0) {
        super.swing($$0);
        this.connection.send(new ServerboundSwingPacket($$0));
    }

    @Override
    public void respawn() {
        this.connection.send(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.PERFORM_RESPAWN));
        KeyMapping.resetToggleKeys();
    }

    @Override
    public void closeContainer() {
        this.connection.send(new ServerboundContainerClosePacket(this.containerMenu.containerId));
        this.clientSideCloseContainer();
    }

    public void clientSideCloseContainer() {
        super.closeContainer();
        this.minecraft.setScreen(null);
    }

    public void hurtTo(float $$0) {
        if (this.flashOnSetHealth) {
            float $$1 = this.getHealth() - $$0;
            if ($$1 <= 0.0f) {
                this.setHealth($$0);
                if ($$1 < 0.0f) {
                    this.invulnerableTime = 10;
                }
            } else {
                this.lastHurt = $$1;
                this.invulnerableTime = 20;
                this.setHealth($$0);
                this.hurtTime = this.hurtDuration = 10;
            }
        } else {
            this.setHealth($$0);
            this.flashOnSetHealth = true;
        }
    }

    @Override
    public void onUpdateAbilities() {
        this.connection.send(new ServerboundPlayerAbilitiesPacket(this.getAbilities()));
    }

    @Override
    public boolean isLocalPlayer() {
        return true;
    }

    @Override
    public boolean isSuppressingSlidingDownLadder() {
        return !this.getAbilities().flying && super.isSuppressingSlidingDownLadder();
    }

    @Override
    public boolean canSpawnSprintParticle() {
        return !this.getAbilities().flying && super.canSpawnSprintParticle();
    }

    protected void sendRidingJump() {
        this.connection.send(new ServerboundPlayerCommandPacket(this, ServerboundPlayerCommandPacket.Action.START_RIDING_JUMP, Mth.floor(this.getJumpRidingScale() * 100.0f)));
    }

    public void sendOpenInventory() {
        this.connection.send(new ServerboundPlayerCommandPacket(this, ServerboundPlayerCommandPacket.Action.OPEN_INVENTORY));
    }

    public StatsCounter getStats() {
        return this.stats;
    }

    public ClientRecipeBook getRecipeBook() {
        return this.recipeBook;
    }

    public void removeRecipeHighlight(RecipeDisplayId $$0) {
        if (this.recipeBook.willHighlight($$0)) {
            this.recipeBook.removeHighlight($$0);
            this.connection.send(new ServerboundRecipeBookSeenRecipePacket($$0));
        }
    }

    @Override
    public int getPermissionLevel() {
        return this.permissionLevel;
    }

    public void setPermissionLevel(int $$0) {
        this.permissionLevel = $$0;
    }

    @Override
    public void displayClientMessage(Component $$0, boolean $$1) {
        this.minecraft.getChatListener().handleSystemMessage($$0, $$1);
    }

    private void moveTowardsClosestSpace(double $$0, double $$1) {
        Direction[] $$7;
        BlockPos $$2 = BlockPos.containing($$0, this.getY(), $$1);
        if (!this.suffocatesAt($$2)) {
            return;
        }
        double $$3 = $$0 - (double)$$2.getX();
        double $$4 = $$1 - (double)$$2.getZ();
        Direction $$5 = null;
        double $$6 = Double.MAX_VALUE;
        for (Direction $$8 : $$7 = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH}) {
            double $$10;
            double $$9 = $$8.getAxis().choose($$3, 0.0, $$4);
            double d = $$10 = $$8.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1.0 - $$9 : $$9;
            if (!($$10 < $$6) || this.suffocatesAt($$2.relative($$8))) continue;
            $$6 = $$10;
            $$5 = $$8;
        }
        if ($$5 != null) {
            Vec3 $$11 = this.getDeltaMovement();
            if ($$5.getAxis() == Direction.Axis.X) {
                this.setDeltaMovement(0.1 * (double)$$5.getStepX(), $$11.y, $$11.z);
            } else {
                this.setDeltaMovement($$11.x, $$11.y, 0.1 * (double)$$5.getStepZ());
            }
        }
    }

    private boolean suffocatesAt(BlockPos $$0) {
        AABB $$1 = this.getBoundingBox();
        AABB $$2 = new AABB($$0.getX(), $$1.minY, $$0.getZ(), (double)$$0.getX() + 1.0, $$1.maxY, (double)$$0.getZ() + 1.0).deflate(1.0E-7);
        return this.level().collidesWithSuffocatingBlock(this, $$2);
    }

    public void setExperienceValues(float $$0, int $$1, int $$2) {
        this.experienceProgress = $$0;
        this.totalExperience = $$1;
        this.experienceLevel = $$2;
        this.experienceDisplayStartTick = this.tickCount;
    }

    @Override
    public void handleEntityEvent(byte $$0) {
        if ($$0 >= 24 && $$0 <= 28) {
            this.setPermissionLevel($$0 - 24);
        } else {
            super.handleEntityEvent($$0);
        }
    }

    public void setShowDeathScreen(boolean $$0) {
        this.showDeathScreen = $$0;
    }

    public boolean shouldShowDeathScreen() {
        return this.showDeathScreen;
    }

    public void setDoLimitedCrafting(boolean $$0) {
        this.doLimitedCrafting = $$0;
    }

    public boolean getDoLimitedCrafting() {
        return this.doLimitedCrafting;
    }

    @Override
    public void playSound(SoundEvent $$0, float $$1, float $$2) {
        this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), $$0, this.getSoundSource(), $$1, $$2, false);
    }

    @Override
    public void playNotifySound(SoundEvent $$0, SoundSource $$1, float $$2, float $$3) {
        this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), $$0, $$1, $$2, $$3, false);
    }

    @Override
    public void startUsingItem(InteractionHand $$0) {
        ItemStack $$1 = this.getItemInHand($$0);
        if ($$1.isEmpty() || this.isUsingItem()) {
            return;
        }
        super.startUsingItem($$0);
        this.startedUsingItem = true;
        this.usingItemHand = $$0;
    }

    @Override
    public boolean isUsingItem() {
        return this.startedUsingItem;
    }

    @Override
    public void stopUsingItem() {
        super.stopUsingItem();
        this.startedUsingItem = false;
    }

    @Override
    public InteractionHand getUsedItemHand() {
        return (InteractionHand)((Object)Objects.requireNonNullElse((Object)((Object)this.usingItemHand), (Object)((Object)InteractionHand.MAIN_HAND)));
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> $$0) {
        super.onSyncedDataUpdated($$0);
        if (DATA_LIVING_ENTITY_FLAGS.equals($$0)) {
            InteractionHand $$2;
            boolean $$1 = ((Byte)this.entityData.get(DATA_LIVING_ENTITY_FLAGS) & 1) > 0;
            InteractionHand interactionHand = $$2 = ((Byte)this.entityData.get(DATA_LIVING_ENTITY_FLAGS) & 2) > 0 ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
            if ($$1 && !this.startedUsingItem) {
                this.startUsingItem($$2);
            } else if (!$$1 && this.startedUsingItem) {
                this.stopUsingItem();
            }
        }
        if (DATA_SHARED_FLAGS_ID.equals($$0) && this.isFallFlying() && !this.wasFallFlying) {
            this.minecraft.getSoundManager().play(new ElytraOnPlayerSoundInstance(this));
        }
    }

    @Nullable
    public PlayerRideableJumping jumpableVehicle() {
        PlayerRideableJumping $$0;
        Entity entity = this.getControlledVehicle();
        return entity instanceof PlayerRideableJumping && ($$0 = (PlayerRideableJumping)((Object)entity)).canJump() ? $$0 : null;
    }

    public float getJumpRidingScale() {
        return this.jumpRidingScale;
    }

    @Override
    public boolean isTextFilteringEnabled() {
        return this.minecraft.isTextFilteringEnabled();
    }

    @Override
    public void openTextEdit(SignBlockEntity $$0, boolean $$1) {
        if ($$0 instanceof HangingSignBlockEntity) {
            HangingSignBlockEntity $$2 = (HangingSignBlockEntity)$$0;
            this.minecraft.setScreen(new HangingSignEditScreen($$2, $$1, this.minecraft.isTextFilteringEnabled()));
        } else {
            this.minecraft.setScreen(new SignEditScreen($$0, $$1, this.minecraft.isTextFilteringEnabled()));
        }
    }

    @Override
    public void openMinecartCommandBlock(BaseCommandBlock $$0) {
        this.minecraft.setScreen(new MinecartCommandBlockEditScreen($$0));
    }

    @Override
    public void openCommandBlock(CommandBlockEntity $$0) {
        this.minecraft.setScreen(new CommandBlockEditScreen($$0));
    }

    @Override
    public void openStructureBlock(StructureBlockEntity $$0) {
        this.minecraft.setScreen(new StructureBlockEditScreen($$0));
    }

    @Override
    public void openTestBlock(TestBlockEntity $$0) {
        this.minecraft.setScreen(new TestBlockEditScreen($$0));
    }

    @Override
    public void openTestInstanceBlock(TestInstanceBlockEntity $$0) {
        this.minecraft.setScreen(new TestInstanceBlockEditScreen($$0));
    }

    @Override
    public void openJigsawBlock(JigsawBlockEntity $$0) {
        this.minecraft.setScreen(new JigsawBlockEditScreen($$0));
    }

    @Override
    public void openDialog(Holder<Dialog> $$0) {
        this.connection.showDialog($$0, this.minecraft.screen);
    }

    @Override
    public void openItemGui(ItemStack $$0, InteractionHand $$1) {
        WritableBookContent $$2 = $$0.get(DataComponents.WRITABLE_BOOK_CONTENT);
        if ($$2 != null) {
            this.minecraft.setScreen(new BookEditScreen(this, $$0, $$1, $$2));
        }
    }

    @Override
    public void crit(Entity $$0) {
        this.minecraft.particleEngine.createTrackingEmitter($$0, ParticleTypes.CRIT);
    }

    @Override
    public void magicCrit(Entity $$0) {
        this.minecraft.particleEngine.createTrackingEmitter($$0, ParticleTypes.ENCHANTED_HIT);
    }

    @Override
    public boolean isShiftKeyDown() {
        return this.input.keyPresses.shift();
    }

    @Override
    public boolean isCrouching() {
        return this.crouching;
    }

    public boolean isMovingSlowly() {
        return this.isCrouching() || this.isVisuallyCrawling();
    }

    @Override
    public void applyInput() {
        if (this.isControlledCamera()) {
            Vec2 $$0 = this.modifyInput(this.input.getMoveVector());
            this.xxa = $$0.x;
            this.zza = $$0.y;
            this.jumping = this.input.keyPresses.jump();
            this.yBobO = this.yBob;
            this.xBobO = this.xBob;
            this.xBob += (this.getXRot() - this.xBob) * 0.5f;
            this.yBob += (this.getYRot() - this.yBob) * 0.5f;
        } else {
            super.applyInput();
        }
    }

    private Vec2 modifyInput(Vec2 $$0) {
        if ($$0.lengthSquared() == 0.0f) {
            return $$0;
        }
        Vec2 $$1 = $$0.scale(0.98f);
        if (this.isUsingItem() && !this.isPassenger()) {
            $$1 = $$1.scale(0.2f);
        }
        if (this.isMovingSlowly()) {
            float $$2 = (float)this.getAttributeValue(Attributes.SNEAKING_SPEED);
            $$1 = $$1.scale($$2);
        }
        return LocalPlayer.modifyInputSpeedForSquareMovement($$1);
    }

    private static Vec2 modifyInputSpeedForSquareMovement(Vec2 $$0) {
        float $$1 = $$0.length();
        if ($$1 <= 0.0f) {
            return $$0;
        }
        Vec2 $$2 = $$0.scale(1.0f / $$1);
        float $$3 = LocalPlayer.distanceToUnitSquare($$2);
        float $$4 = Math.min($$1 * $$3, 1.0f);
        return $$2.scale($$4);
    }

    private static float distanceToUnitSquare(Vec2 $$0) {
        float $$1 = Math.abs($$0.x);
        float $$2 = Math.abs($$0.y);
        float $$3 = $$2 > $$1 ? $$1 / $$2 : $$2 / $$1;
        return Mth.sqrt(1.0f + Mth.square($$3));
    }

    protected boolean isControlledCamera() {
        return this.minecraft.getCameraEntity() == this;
    }

    public void resetPos() {
        this.setPose(Pose.STANDING);
        if (this.level() != null) {
            for (double $$0 = this.getY(); $$0 > (double)this.level().getMinY() && $$0 <= (double)this.level().getMaxY(); $$0 += 1.0) {
                this.setPos(this.getX(), $$0, this.getZ());
                if (this.level().noCollision(this)) break;
            }
            this.setDeltaMovement(Vec3.ZERO);
            this.setXRot(0.0f);
        }
        this.setHealth(this.getMaxHealth());
        this.deathTime = 0;
    }

    @Override
    public void aiStep() {
        PlayerRideableJumping $$8;
        if (this.sprintTriggerTime > 0) {
            --this.sprintTriggerTime;
        }
        if (!(this.minecraft.screen instanceof ReceivingLevelScreen)) {
            this.handlePortalTransitionEffect(this.getActivePortalLocalTransition() == Portal.Transition.CONFUSION);
            this.processPortalCooldown();
        }
        boolean $$0 = this.input.keyPresses.jump();
        boolean $$1 = this.input.keyPresses.shift();
        boolean $$2 = this.input.hasForwardImpulse();
        Abilities $$3 = this.getAbilities();
        this.crouching = !$$3.flying && !this.isSwimming() && !this.isPassenger() && this.canPlayerFitWithinBlocksAndEntitiesWhen(Pose.CROUCHING) && (this.isShiftKeyDown() || !this.isSleeping() && !this.canPlayerFitWithinBlocksAndEntitiesWhen(Pose.STANDING));
        this.input.tick();
        this.minecraft.getTutorial().onInput(this.input);
        boolean $$4 = false;
        if (this.autoJumpTime > 0) {
            --this.autoJumpTime;
            $$4 = true;
            this.input.makeJump();
        }
        if (!this.noPhysics) {
            this.moveTowardsClosestSpace(this.getX() - (double)this.getBbWidth() * 0.35, this.getZ() + (double)this.getBbWidth() * 0.35);
            this.moveTowardsClosestSpace(this.getX() - (double)this.getBbWidth() * 0.35, this.getZ() - (double)this.getBbWidth() * 0.35);
            this.moveTowardsClosestSpace(this.getX() + (double)this.getBbWidth() * 0.35, this.getZ() - (double)this.getBbWidth() * 0.35);
            this.moveTowardsClosestSpace(this.getX() + (double)this.getBbWidth() * 0.35, this.getZ() + (double)this.getBbWidth() * 0.35);
        }
        if ($$1 || this.isUsingItem() && !this.isPassenger() || this.input.keyPresses.backward()) {
            this.sprintTriggerTime = 0;
        }
        if (this.canStartSprinting()) {
            if (!$$2) {
                if (this.sprintTriggerTime > 0) {
                    this.setSprinting(true);
                } else {
                    this.sprintTriggerTime = 7;
                }
            }
            if (this.input.keyPresses.sprint()) {
                this.setSprinting(true);
            }
        }
        if (this.isSprinting()) {
            if (this.isSwimming()) {
                if (this.shouldStopSwimSprinting()) {
                    this.setSprinting(false);
                }
            } else if (this.shouldStopRunSprinting()) {
                this.setSprinting(false);
            }
        }
        boolean $$5 = false;
        if ($$3.mayfly) {
            if (this.minecraft.gameMode.isAlwaysFlying()) {
                if (!$$3.flying) {
                    $$3.flying = true;
                    $$5 = true;
                    this.onUpdateAbilities();
                }
            } else if (!$$0 && this.input.keyPresses.jump() && !$$4) {
                if (this.jumpTriggerTime == 0) {
                    this.jumpTriggerTime = 7;
                } else if (!this.isSwimming()) {
                    boolean bl = $$3.flying = !$$3.flying;
                    if ($$3.flying && this.onGround()) {
                        this.jumpFromGround();
                    }
                    $$5 = true;
                    this.onUpdateAbilities();
                    this.jumpTriggerTime = 0;
                }
            }
        }
        if (this.input.keyPresses.jump() && !$$5 && !$$0 && !this.onClimbable() && this.tryToStartFallFlying()) {
            this.connection.send(new ServerboundPlayerCommandPacket(this, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
        }
        this.wasFallFlying = this.isFallFlying();
        if (this.isInWater() && this.input.keyPresses.shift() && this.isAffectedByFluids()) {
            this.goDownInWater();
        }
        if (this.isEyeInFluid(FluidTags.WATER)) {
            int $$6 = this.isSpectator() ? 10 : 1;
            this.waterVisionTime = Mth.clamp(this.waterVisionTime + $$6, 0, 600);
        } else if (this.waterVisionTime > 0) {
            this.isEyeInFluid(FluidTags.WATER);
            this.waterVisionTime = Mth.clamp(this.waterVisionTime - 10, 0, 600);
        }
        if ($$3.flying && this.isControlledCamera()) {
            int $$7 = 0;
            if (this.input.keyPresses.shift()) {
                --$$7;
            }
            if (this.input.keyPresses.jump()) {
                ++$$7;
            }
            if ($$7 != 0) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, (float)$$7 * $$3.getFlyingSpeed() * 3.0f, 0.0));
            }
        }
        if (($$8 = this.jumpableVehicle()) != null && $$8.getJumpCooldown() == 0) {
            if (this.jumpRidingTicks < 0) {
                ++this.jumpRidingTicks;
                if (this.jumpRidingTicks == 0) {
                    this.jumpRidingScale = 0.0f;
                }
            }
            if ($$0 && !this.input.keyPresses.jump()) {
                this.jumpRidingTicks = -10;
                $$8.onPlayerJump(Mth.floor(this.getJumpRidingScale() * 100.0f));
                this.sendRidingJump();
            } else if (!$$0 && this.input.keyPresses.jump()) {
                this.jumpRidingTicks = 0;
                this.jumpRidingScale = 0.0f;
            } else if ($$0) {
                ++this.jumpRidingTicks;
                this.jumpRidingScale = this.jumpRidingTicks < 10 ? (float)this.jumpRidingTicks * 0.1f : 0.8f + 2.0f / (float)(this.jumpRidingTicks - 9) * 0.1f;
            }
        } else {
            this.jumpRidingScale = 0.0f;
        }
        super.aiStep();
        if (this.onGround() && $$3.flying && !this.minecraft.gameMode.isAlwaysFlying()) {
            $$3.flying = false;
            this.onUpdateAbilities();
        }
    }

    private boolean shouldStopRunSprinting() {
        return this.hasBlindness() || this.isPassenger() && !this.vehicleCanSprint(this.getVehicle()) || !this.input.hasForwardImpulse() || !this.hasEnoughFoodToSprint() || this.horizontalCollision && !this.minorHorizontalCollision || this.isInWater() && !this.isUnderWater();
    }

    private boolean shouldStopSwimSprinting() {
        return this.hasBlindness() || this.isPassenger() && !this.vehicleCanSprint(this.getVehicle()) || !this.isInWater() || !this.input.hasForwardImpulse() && !this.onGround() && !this.input.keyPresses.shift() || !this.hasEnoughFoodToSprint();
    }

    private boolean hasBlindness() {
        return this.hasEffect(MobEffects.BLINDNESS);
    }

    public Portal.Transition getActivePortalLocalTransition() {
        return this.portalProcess == null ? Portal.Transition.NONE : this.portalProcess.getPortalLocalTransition();
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 20) {
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    private void handlePortalTransitionEffect(boolean $$0) {
        this.oPortalEffectIntensity = this.portalEffectIntensity;
        float $$1 = 0.0f;
        if ($$0 && this.portalProcess != null && this.portalProcess.isInsidePortalThisTick()) {
            if (!(this.minecraft.screen == null || this.minecraft.screen.isPauseScreen() || this.minecraft.screen instanceof DeathScreen || this.minecraft.screen instanceof WinScreen)) {
                if (this.minecraft.screen instanceof AbstractContainerScreen) {
                    this.closeContainer();
                }
                this.minecraft.setScreen(null);
            }
            if (this.portalEffectIntensity == 0.0f) {
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forLocalAmbience(SoundEvents.PORTAL_TRIGGER, this.random.nextFloat() * 0.4f + 0.8f, 0.25f));
            }
            $$1 = 0.0125f;
            this.portalProcess.setAsInsidePortalThisTick(false);
        } else if (this.portalEffectIntensity > 0.0f) {
            $$1 = -0.05f;
        }
        this.portalEffectIntensity = Mth.clamp(this.portalEffectIntensity + $$1, 0.0f, 1.0f);
    }

    @Override
    public void rideTick() {
        super.rideTick();
        this.handsBusy = false;
        Entity entity = this.getControlledVehicle();
        if (entity instanceof AbstractBoat) {
            AbstractBoat $$0 = (AbstractBoat)entity;
            $$0.setInput(this.input.keyPresses.left(), this.input.keyPresses.right(), this.input.keyPresses.forward(), this.input.keyPresses.backward());
            this.handsBusy |= this.input.keyPresses.left() || this.input.keyPresses.right() || this.input.keyPresses.forward() || this.input.keyPresses.backward();
        }
    }

    public boolean isHandsBusy() {
        return this.handsBusy;
    }

    @Override
    public void move(MoverType $$0, Vec3 $$1) {
        double $$2 = this.getX();
        double $$3 = this.getZ();
        super.move($$0, $$1);
        float $$4 = (float)(this.getX() - $$2);
        float $$5 = (float)(this.getZ() - $$3);
        this.updateAutoJump($$4, $$5);
        this.walkDist += Mth.length($$4, $$5) * 0.6f;
    }

    public boolean isAutoJumpEnabled() {
        return this.autoJumpEnabled;
    }

    @Override
    public boolean shouldRotateWithMinecart() {
        return this.minecraft.options.rotateWithMinecart().get();
    }

    protected void updateAutoJump(float $$02, float $$1) {
        if (!this.canAutoJump()) {
            return;
        }
        Vec3 $$2 = this.position();
        Vec3 $$3 = $$2.add($$02, 0.0, $$1);
        Vec3 $$4 = new Vec3($$02, 0.0, $$1);
        float $$5 = this.getSpeed();
        float $$6 = (float)$$4.lengthSqr();
        if ($$6 <= 0.001f) {
            Vec2 $$7 = this.input.getMoveVector();
            float $$8 = $$5 * $$7.x;
            float $$9 = $$5 * $$7.y;
            float $$10 = Mth.sin(this.getYRot() * ((float)Math.PI / 180));
            float $$11 = Mth.cos(this.getYRot() * ((float)Math.PI / 180));
            $$4 = new Vec3($$8 * $$11 - $$9 * $$10, $$4.y, $$9 * $$11 + $$8 * $$10);
            $$6 = (float)$$4.lengthSqr();
            if ($$6 <= 0.001f) {
                return;
            }
        }
        float $$12 = Mth.invSqrt($$6);
        Vec3 $$13 = $$4.scale($$12);
        Vec3 $$14 = this.getForward();
        float $$15 = (float)($$14.x * $$13.x + $$14.z * $$13.z);
        if ($$15 < -0.15f) {
            return;
        }
        CollisionContext $$16 = CollisionContext.of(this);
        BlockPos $$17 = BlockPos.containing(this.getX(), this.getBoundingBox().maxY, this.getZ());
        BlockState $$18 = this.level().getBlockState($$17);
        if (!$$18.getCollisionShape(this.level(), $$17, $$16).isEmpty()) {
            return;
        }
        $$17 = $$17.above();
        BlockState $$19 = this.level().getBlockState($$17);
        if (!$$19.getCollisionShape(this.level(), $$17, $$16).isEmpty()) {
            return;
        }
        float $$20 = 7.0f;
        float $$21 = 1.2f;
        if (this.hasEffect(MobEffects.JUMP_BOOST)) {
            $$21 += (float)(this.getEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.75f;
        }
        float $$22 = Math.max($$5 * 7.0f, 1.0f / $$12);
        Vec3 $$23 = $$2;
        Vec3 $$24 = $$3.add($$13.scale($$22));
        float $$25 = this.getBbWidth();
        float $$26 = this.getBbHeight();
        AABB $$27 = new AABB($$23, $$24.add(0.0, $$26, 0.0)).inflate($$25, 0.0, $$25);
        $$23 = $$23.add(0.0, 0.51f, 0.0);
        $$24 = $$24.add(0.0, 0.51f, 0.0);
        Vec3 $$28 = $$13.cross(new Vec3(0.0, 1.0, 0.0));
        Vec3 $$29 = $$28.scale($$25 * 0.5f);
        Vec3 $$30 = $$23.subtract($$29);
        Vec3 $$31 = $$24.subtract($$29);
        Vec3 $$32 = $$23.add($$29);
        Vec3 $$33 = $$24.add($$29);
        Iterable<VoxelShape> $$34 = this.level().getCollisions(this, $$27);
        Iterator $$35 = StreamSupport.stream($$34.spliterator(), false).flatMap($$0 -> $$0.toAabbs().stream()).iterator();
        float $$36 = Float.MIN_VALUE;
        while ($$35.hasNext()) {
            AABB $$37 = (AABB)$$35.next();
            if (!$$37.intersects($$30, $$31) && !$$37.intersects($$32, $$33)) continue;
            $$36 = (float)$$37.maxY;
            Vec3 $$38 = $$37.getCenter();
            BlockPos $$39 = BlockPos.containing($$38);
            int $$40 = 1;
            while ((float)$$40 < $$21) {
                BlockPos $$41 = $$39.above($$40);
                BlockState $$42 = this.level().getBlockState($$41);
                VoxelShape $$43 = $$42.getCollisionShape(this.level(), $$41, $$16);
                if (!$$43.isEmpty() && (double)($$36 = (float)$$43.max(Direction.Axis.Y) + (float)$$41.getY()) - this.getY() > (double)$$21) {
                    return;
                }
                if ($$40 > 1) {
                    $$17 = $$17.above();
                    BlockState $$44 = this.level().getBlockState($$17);
                    if (!$$44.getCollisionShape(this.level(), $$17, $$16).isEmpty()) {
                        return;
                    }
                }
                ++$$40;
            }
            break block0;
        }
        if ($$36 == Float.MIN_VALUE) {
            return;
        }
        float $$45 = (float)((double)$$36 - this.getY());
        if ($$45 <= 0.5f || $$45 > $$21) {
            return;
        }
        this.autoJumpTime = 1;
    }

    @Override
    protected boolean isHorizontalCollisionMinor(Vec3 $$0) {
        float $$1 = this.getYRot() * ((float)Math.PI / 180);
        double $$2 = Mth.sin($$1);
        double $$3 = Mth.cos($$1);
        double $$4 = (double)this.xxa * $$3 - (double)this.zza * $$2;
        double $$5 = (double)this.zza * $$3 + (double)this.xxa * $$2;
        double $$6 = Mth.square($$4) + Mth.square($$5);
        double $$7 = Mth.square($$0.x) + Mth.square($$0.z);
        if ($$6 < (double)1.0E-5f || $$7 < (double)1.0E-5f) {
            return false;
        }
        double $$8 = $$4 * $$0.x + $$5 * $$0.z;
        double $$9 = Math.acos($$8 / Math.sqrt($$6 * $$7));
        return $$9 < 0.13962633907794952;
    }

    private boolean canAutoJump() {
        return this.isAutoJumpEnabled() && this.autoJumpTime <= 0 && this.onGround() && !this.isStayingOnGroundSurface() && !this.isPassenger() && this.isMoving() && (double)this.getBlockJumpFactor() >= 1.0;
    }

    private boolean isMoving() {
        return this.input.getMoveVector().lengthSquared() > 0.0f;
    }

    private boolean canStartSprinting() {
        return !(this.isSprinting() || !this.input.hasForwardImpulse() || !this.hasEnoughFoodToSprint() || this.isUsingItem() || this.hasBlindness() || this.isPassenger() && !this.vehicleCanSprint(this.getVehicle()) || this.isFallFlying() && !this.isUnderWater() || this.isMovingSlowly() && !this.isUnderWater() || this.isInWater() && !this.isUnderWater());
    }

    private boolean vehicleCanSprint(Entity $$0) {
        return $$0.canSprint() && $$0.isLocalInstanceAuthoritative();
    }

    private boolean hasEnoughFoodToSprint() {
        return this.isPassenger() || (float)this.getFoodData().getFoodLevel() > 6.0f || this.getAbilities().mayfly;
    }

    public float getWaterVision() {
        if (!this.isEyeInFluid(FluidTags.WATER)) {
            return 0.0f;
        }
        float $$0 = 600.0f;
        float $$1 = 100.0f;
        if ((float)this.waterVisionTime >= 600.0f) {
            return 1.0f;
        }
        float $$2 = Mth.clamp((float)this.waterVisionTime / 100.0f, 0.0f, 1.0f);
        float $$3 = (float)this.waterVisionTime < 100.0f ? 0.0f : Mth.clamp(((float)this.waterVisionTime - 100.0f) / 500.0f, 0.0f, 1.0f);
        return $$2 * 0.6f + $$3 * 0.39999998f;
    }

    public void onGameModeChanged(GameType $$0) {
        if ($$0 == GameType.SPECTATOR) {
            this.setDeltaMovement(this.getDeltaMovement().with(Direction.Axis.Y, 0.0));
        }
    }

    @Override
    public boolean isUnderWater() {
        return this.wasUnderwater;
    }

    @Override
    protected boolean updateIsUnderwater() {
        boolean $$0 = this.wasUnderwater;
        boolean $$1 = super.updateIsUnderwater();
        if (this.isSpectator()) {
            return this.wasUnderwater;
        }
        if (!$$0 && $$1) {
            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.AMBIENT_UNDERWATER_ENTER, SoundSource.AMBIENT, 1.0f, 1.0f, false);
            this.minecraft.getSoundManager().play(new UnderwaterAmbientSoundInstances.UnderwaterAmbientSoundInstance(this));
        }
        if ($$0 && !$$1) {
            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.AMBIENT_UNDERWATER_EXIT, SoundSource.AMBIENT, 1.0f, 1.0f, false);
        }
        return this.wasUnderwater;
    }

    @Override
    public Vec3 getRopeHoldPosition(float $$0) {
        if (this.minecraft.options.getCameraType().isFirstPerson()) {
            float $$1 = Mth.lerp($$0 * 0.5f, this.getYRot(), this.yRotO) * ((float)Math.PI / 180);
            float $$2 = Mth.lerp($$0 * 0.5f, this.getXRot(), this.xRotO) * ((float)Math.PI / 180);
            double $$3 = this.getMainArm() == HumanoidArm.RIGHT ? -1.0 : 1.0;
            Vec3 $$4 = new Vec3(0.39 * $$3, -0.6, 0.3);
            return $$4.xRot(-$$2).yRot(-$$1).add(this.getEyePosition($$0));
        }
        return super.getRopeHoldPosition($$0);
    }

    @Override
    public void updateTutorialInventoryAction(ItemStack $$0, ItemStack $$1, ClickAction $$2) {
        this.minecraft.getTutorial().onInventoryAction($$0, $$1, $$2);
    }

    @Override
    public float getVisualRotationYInDegrees() {
        return this.getYRot();
    }

    @Override
    public void handleCreativeModeItemDrop(ItemStack $$0) {
        this.minecraft.gameMode.handleCreativeModeItemDrop($$0);
    }

    @Override
    public boolean canDropItems() {
        return this.dropSpamThrottler.isUnderThreshold();
    }

    public TickThrottler getDropSpamThrottler() {
        return this.dropSpamThrottler;
    }

    public Input getLastSentInput() {
        return this.lastSentInput;
    }
}

