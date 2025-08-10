/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.datafixers.util.Either
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.world.entity.player;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.math.IntMath;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.Unit;
import net.minecraft.world.Container;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.EntityAttachments;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityEquipment;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.warden.WardenSpawnTracker;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.PlayerEquipment;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.entity.TestBlockEntity;
import net.minecraft.world.level.block.entity.TestInstanceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import org.slf4j.Logger;

public abstract class Player
extends LivingEntity {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final HumanoidArm DEFAULT_MAIN_HAND = HumanoidArm.RIGHT;
    public static final int DEFAULT_MODEL_CUSTOMIZATION = 0;
    public static final int MAX_HEALTH = 20;
    public static final int SLEEP_DURATION = 100;
    public static final int WAKE_UP_DURATION = 10;
    public static final int ENDER_SLOT_OFFSET = 200;
    public static final int HELD_ITEM_SLOT = 499;
    public static final int CRAFTING_SLOT_OFFSET = 500;
    public static final float DEFAULT_BLOCK_INTERACTION_RANGE = 4.5f;
    public static final float DEFAULT_ENTITY_INTERACTION_RANGE = 3.0f;
    public static final float CROUCH_BB_HEIGHT = 1.5f;
    public static final float SWIMMING_BB_WIDTH = 0.6f;
    public static final float SWIMMING_BB_HEIGHT = 0.6f;
    public static final float DEFAULT_EYE_HEIGHT = 1.62f;
    private static final int CURRENT_IMPULSE_CONTEXT_RESET_GRACE_TIME_TICKS = 40;
    public static final Vec3 DEFAULT_VEHICLE_ATTACHMENT = new Vec3(0.0, 0.6, 0.0);
    public static final EntityDimensions STANDING_DIMENSIONS = EntityDimensions.scalable(0.6f, 1.8f).withEyeHeight(1.62f).withAttachments(EntityAttachments.builder().attach(EntityAttachment.VEHICLE, DEFAULT_VEHICLE_ATTACHMENT));
    private static final Map<Pose, EntityDimensions> POSES = ImmutableMap.builder().put(Pose.STANDING, STANDING_DIMENSIONS).put(Pose.SLEEPING, SLEEPING_DIMENSIONS).put(Pose.FALL_FLYING, EntityDimensions.scalable(0.6f, 0.6f).withEyeHeight(0.4f)).put(Pose.SWIMMING, EntityDimensions.scalable(0.6f, 0.6f).withEyeHeight(0.4f)).put(Pose.SPIN_ATTACK, EntityDimensions.scalable(0.6f, 0.6f).withEyeHeight(0.4f)).put(Pose.CROUCHING, EntityDimensions.scalable(0.6f, 1.5f).withEyeHeight(1.27f).withAttachments(EntityAttachments.builder().attach(EntityAttachment.VEHICLE, DEFAULT_VEHICLE_ATTACHMENT))).put(Pose.DYING, EntityDimensions.fixed(0.2f, 0.2f).withEyeHeight(1.62f)).build();
    private static final EntityDataAccessor<Float> DATA_PLAYER_ABSORPTION_ID = SynchedEntityData.defineId(Player.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_SCORE_ID = SynchedEntityData.defineId(Player.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Byte> DATA_PLAYER_MODE_CUSTOMISATION = SynchedEntityData.defineId(Player.class, EntityDataSerializers.BYTE);
    protected static final EntityDataAccessor<Byte> DATA_PLAYER_MAIN_HAND = SynchedEntityData.defineId(Player.class, EntityDataSerializers.BYTE);
    protected static final EntityDataAccessor<CompoundTag> DATA_SHOULDER_LEFT = SynchedEntityData.defineId(Player.class, EntityDataSerializers.COMPOUND_TAG);
    protected static final EntityDataAccessor<CompoundTag> DATA_SHOULDER_RIGHT = SynchedEntityData.defineId(Player.class, EntityDataSerializers.COMPOUND_TAG);
    public static final int CLIENT_LOADED_TIMEOUT_TIME = 60;
    private static final short DEFAULT_SLEEP_TIMER = 0;
    private static final float DEFAULT_EXPERIENCE_PROGRESS = 0.0f;
    private static final int DEFAULT_EXPERIENCE_LEVEL = 0;
    private static final int DEFAULT_TOTAL_EXPERIENCE = 0;
    private static final int NO_ENCHANTMENT_SEED = 0;
    private static final int DEFAULT_SELECTED_SLOT = 0;
    private static final int DEFAULT_SCORE = 0;
    private static final boolean DEFAULT_IGNORE_FALL_DAMAGE_FROM_CURRENT_IMPULSE = false;
    private static final int DEFAULT_CURRENT_IMPULSE_CONTEXT_RESET_GRACE_TIME = 0;
    private long timeEntitySatOnShoulder;
    final Inventory inventory;
    protected PlayerEnderChestContainer enderChestInventory = new PlayerEnderChestContainer();
    public final InventoryMenu inventoryMenu;
    public AbstractContainerMenu containerMenu;
    protected FoodData foodData = new FoodData();
    protected int jumpTriggerTime;
    private boolean clientLoaded = false;
    protected int clientLoadedTimeoutTimer = 60;
    public float oBob;
    public float bob;
    public int takeXpDelay;
    public double xCloakO;
    public double yCloakO;
    public double zCloakO;
    public double xCloak;
    public double yCloak;
    public double zCloak;
    private int sleepCounter = 0;
    protected boolean wasUnderwater;
    private final Abilities abilities = new Abilities();
    public int experienceLevel = 0;
    public int totalExperience = 0;
    public float experienceProgress = 0.0f;
    protected int enchantmentSeed = 0;
    protected final float defaultFlySpeed = 0.02f;
    private int lastLevelUpTime;
    private final GameProfile gameProfile;
    private boolean reducedDebugInfo;
    private ItemStack lastItemInMainHand = ItemStack.EMPTY;
    private final ItemCooldowns cooldowns = this.createItemCooldowns();
    private Optional<GlobalPos> lastDeathLocation = Optional.empty();
    @Nullable
    public FishingHook fishing;
    protected float hurtDir;
    @Nullable
    public Vec3 currentImpulseImpactPos;
    @Nullable
    public Entity currentExplosionCause;
    private boolean ignoreFallDamageFromCurrentImpulse = false;
    private int currentImpulseContextResetGraceTime = 0;

    public Player(Level $$0, GameProfile $$1) {
        super((EntityType<? extends LivingEntity>)EntityType.PLAYER, $$0);
        this.setUUID($$1.getId());
        this.gameProfile = $$1;
        this.inventory = new Inventory(this, this.equipment);
        this.inventoryMenu = new InventoryMenu(this.inventory, !$$0.isClientSide, this);
        this.containerMenu = this.inventoryMenu;
    }

    @Override
    protected EntityEquipment createEquipment() {
        return new PlayerEquipment(this);
    }

    public boolean blockActionRestricted(Level $$0, BlockPos $$1, GameType $$2) {
        if (!$$2.isBlockPlacingRestricted()) {
            return false;
        }
        if ($$2 == GameType.SPECTATOR) {
            return true;
        }
        if (this.mayBuild()) {
            return false;
        }
        ItemStack $$3 = this.getMainHandItem();
        return $$3.isEmpty() || !$$3.canBreakBlockInAdventureMode(new BlockInWorld($$0, $$1, false));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes().add(Attributes.ATTACK_DAMAGE, 1.0).add(Attributes.MOVEMENT_SPEED, 0.1f).add(Attributes.ATTACK_SPEED).add(Attributes.LUCK).add(Attributes.BLOCK_INTERACTION_RANGE, 4.5).add(Attributes.ENTITY_INTERACTION_RANGE, 3.0).add(Attributes.BLOCK_BREAK_SPEED).add(Attributes.SUBMERGED_MINING_SPEED).add(Attributes.SNEAKING_SPEED).add(Attributes.MINING_EFFICIENCY).add(Attributes.SWEEPING_DAMAGE_RATIO).add(Attributes.WAYPOINT_TRANSMIT_RANGE, 6.0E7).add(Attributes.WAYPOINT_RECEIVE_RANGE, 6.0E7);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder $$0) {
        super.defineSynchedData($$0);
        $$0.define(DATA_PLAYER_ABSORPTION_ID, Float.valueOf(0.0f));
        $$0.define(DATA_SCORE_ID, 0);
        $$0.define(DATA_PLAYER_MODE_CUSTOMISATION, (byte)0);
        $$0.define(DATA_PLAYER_MAIN_HAND, (byte)DEFAULT_MAIN_HAND.getId());
        $$0.define(DATA_SHOULDER_LEFT, new CompoundTag());
        $$0.define(DATA_SHOULDER_RIGHT, new CompoundTag());
    }

    @Override
    public void tick() {
        this.noPhysics = this.isSpectator();
        if (this.isSpectator() || this.isPassenger()) {
            this.setOnGround(false);
        }
        if (this.takeXpDelay > 0) {
            --this.takeXpDelay;
        }
        if (this.isSleeping()) {
            ++this.sleepCounter;
            if (this.sleepCounter > 100) {
                this.sleepCounter = 100;
            }
            if (!this.level().isClientSide && this.level().isBrightOutside()) {
                this.stopSleepInBed(false, true);
            }
        } else if (this.sleepCounter > 0) {
            ++this.sleepCounter;
            if (this.sleepCounter >= 110) {
                this.sleepCounter = 0;
            }
        }
        this.updateIsUnderwater();
        super.tick();
        if (!this.level().isClientSide && this.containerMenu != null && !this.containerMenu.stillValid(this)) {
            this.closeContainer();
            this.containerMenu = this.inventoryMenu;
        }
        this.moveCloak();
        Player player = this;
        if (player instanceof ServerPlayer) {
            ServerPlayer $$0 = (ServerPlayer)player;
            this.foodData.tick($$0);
            this.awardStat(Stats.PLAY_TIME);
            this.awardStat(Stats.TOTAL_WORLD_TIME);
            if (this.isAlive()) {
                this.awardStat(Stats.TIME_SINCE_DEATH);
            }
            if (this.isDiscrete()) {
                this.awardStat(Stats.CROUCH_TIME);
            }
            if (!this.isSleeping()) {
                this.awardStat(Stats.TIME_SINCE_REST);
            }
        }
        int $$1 = 29999999;
        double $$2 = Mth.clamp(this.getX(), -2.9999999E7, 2.9999999E7);
        double $$3 = Mth.clamp(this.getZ(), -2.9999999E7, 2.9999999E7);
        if ($$2 != this.getX() || $$3 != this.getZ()) {
            this.setPos($$2, this.getY(), $$3);
        }
        ++this.attackStrengthTicker;
        ItemStack $$4 = this.getMainHandItem();
        if (!ItemStack.matches(this.lastItemInMainHand, $$4)) {
            if (!ItemStack.isSameItem(this.lastItemInMainHand, $$4)) {
                this.resetAttackStrengthTicker();
            }
            this.lastItemInMainHand = $$4.copy();
        }
        if (!this.isEyeInFluid(FluidTags.WATER) && this.isEquipped(Items.TURTLE_HELMET)) {
            this.turtleHelmetTick();
        }
        this.cooldowns.tick();
        this.updatePlayerPose();
        if (this.currentImpulseContextResetGraceTime > 0) {
            --this.currentImpulseContextResetGraceTime;
        }
    }

    @Override
    protected float getMaxHeadRotationRelativeToBody() {
        if (this.isBlocking()) {
            return 15.0f;
        }
        return super.getMaxHeadRotationRelativeToBody();
    }

    public boolean isSecondaryUseActive() {
        return this.isShiftKeyDown();
    }

    protected boolean wantsToStopRiding() {
        return this.isShiftKeyDown();
    }

    protected boolean isStayingOnGroundSurface() {
        return this.isShiftKeyDown();
    }

    protected boolean updateIsUnderwater() {
        this.wasUnderwater = this.isEyeInFluid(FluidTags.WATER);
        return this.wasUnderwater;
    }

    @Override
    public void onAboveBubbleColumn(boolean $$0, BlockPos $$1) {
        if (!this.getAbilities().flying) {
            super.onAboveBubbleColumn($$0, $$1);
        }
    }

    @Override
    public void onInsideBubbleColumn(boolean $$0) {
        if (!this.getAbilities().flying) {
            super.onInsideBubbleColumn($$0);
        }
    }

    private void turtleHelmetTick() {
        this.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 200, 0, false, false, true));
    }

    private boolean isEquipped(Item $$0) {
        for (EquipmentSlot $$1 : EquipmentSlot.VALUES) {
            ItemStack $$2 = this.getItemBySlot($$1);
            Equippable $$3 = $$2.get(DataComponents.EQUIPPABLE);
            if (!$$2.is($$0) || $$3 == null || $$3.slot() != $$1) continue;
            return true;
        }
        return false;
    }

    protected ItemCooldowns createItemCooldowns() {
        return new ItemCooldowns();
    }

    private void moveCloak() {
        this.xCloakO = this.xCloak;
        this.yCloakO = this.yCloak;
        this.zCloakO = this.zCloak;
        double $$0 = this.getX() - this.xCloak;
        double $$1 = this.getY() - this.yCloak;
        double $$2 = this.getZ() - this.zCloak;
        double $$3 = 10.0;
        if ($$0 > 10.0) {
            this.xCloakO = this.xCloak = this.getX();
        }
        if ($$2 > 10.0) {
            this.zCloakO = this.zCloak = this.getZ();
        }
        if ($$1 > 10.0) {
            this.yCloakO = this.yCloak = this.getY();
        }
        if ($$0 < -10.0) {
            this.xCloakO = this.xCloak = this.getX();
        }
        if ($$2 < -10.0) {
            this.zCloakO = this.zCloak = this.getZ();
        }
        if ($$1 < -10.0) {
            this.yCloakO = this.yCloak = this.getY();
        }
        this.xCloak += $$0 * 0.25;
        this.zCloak += $$2 * 0.25;
        this.yCloak += $$1 * 0.25;
    }

    protected void updatePlayerPose() {
        Pose $$3;
        if (!this.canPlayerFitWithinBlocksAndEntitiesWhen(Pose.SWIMMING)) {
            return;
        }
        Pose $$0 = this.getDesiredPose();
        if (this.isSpectator() || this.isPassenger() || this.canPlayerFitWithinBlocksAndEntitiesWhen($$0)) {
            Pose $$1 = $$0;
        } else if (this.canPlayerFitWithinBlocksAndEntitiesWhen(Pose.CROUCHING)) {
            Pose $$2 = Pose.CROUCHING;
        } else {
            $$3 = Pose.SWIMMING;
        }
        this.setPose($$3);
    }

    private Pose getDesiredPose() {
        if (this.isSleeping()) {
            return Pose.SLEEPING;
        }
        if (this.isSwimming()) {
            return Pose.SWIMMING;
        }
        if (this.isFallFlying()) {
            return Pose.FALL_FLYING;
        }
        if (this.isAutoSpinAttack()) {
            return Pose.SPIN_ATTACK;
        }
        if (this.isShiftKeyDown() && !this.abilities.flying) {
            return Pose.CROUCHING;
        }
        return Pose.STANDING;
    }

    protected boolean canPlayerFitWithinBlocksAndEntitiesWhen(Pose $$0) {
        return this.level().noCollision(this, this.getDimensions($$0).makeBoundingBox(this.position()).deflate(1.0E-7));
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.PLAYER_SWIM;
    }

    @Override
    protected SoundEvent getSwimSplashSound() {
        return SoundEvents.PLAYER_SPLASH;
    }

    @Override
    protected SoundEvent getSwimHighSpeedSplashSound() {
        return SoundEvents.PLAYER_SPLASH_HIGH_SPEED;
    }

    @Override
    public int getDimensionChangingDelay() {
        return 10;
    }

    @Override
    public void playSound(SoundEvent $$0, float $$1, float $$2) {
        this.level().playSound((Entity)this, this.getX(), this.getY(), this.getZ(), $$0, this.getSoundSource(), $$1, $$2);
    }

    public void playNotifySound(SoundEvent $$0, SoundSource $$1, float $$2, float $$3) {
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.PLAYERS;
    }

    @Override
    protected int getFireImmuneTicks() {
        return 20;
    }

    @Override
    public void handleEntityEvent(byte $$0) {
        if ($$0 == 9) {
            this.completeUsingItem();
        } else if ($$0 == 23) {
            this.reducedDebugInfo = false;
        } else if ($$0 == 22) {
            this.reducedDebugInfo = true;
        } else {
            super.handleEntityEvent($$0);
        }
    }

    protected void closeContainer() {
        this.containerMenu = this.inventoryMenu;
    }

    protected void doCloseContainer() {
    }

    @Override
    public void rideTick() {
        if (!this.level().isClientSide && this.wantsToStopRiding() && this.isPassenger()) {
            this.stopRiding();
            this.setShiftKeyDown(false);
            return;
        }
        super.rideTick();
        this.oBob = this.bob;
        this.bob = 0.0f;
    }

    @Override
    public void aiStep() {
        float $$1;
        if (this.jumpTriggerTime > 0) {
            --this.jumpTriggerTime;
        }
        this.tickRegeneration();
        this.inventory.tick();
        this.oBob = this.bob;
        if (this.abilities.flying && !this.isPassenger()) {
            this.resetFallDistance();
        }
        super.aiStep();
        this.updateSwingTime();
        this.yHeadRot = this.getYRot();
        this.setSpeed((float)this.getAttributeValue(Attributes.MOVEMENT_SPEED));
        if (!this.onGround() || this.isDeadOrDying() || this.isSwimming()) {
            float $$0 = 0.0f;
        } else {
            $$1 = Math.min(0.1f, (float)this.getDeltaMovement().horizontalDistance());
        }
        this.bob += ($$1 - this.bob) * 0.4f;
        if (this.getHealth() > 0.0f && !this.isSpectator()) {
            AABB $$3;
            if (this.isPassenger() && !this.getVehicle().isRemoved()) {
                AABB $$2 = this.getBoundingBox().minmax(this.getVehicle().getBoundingBox()).inflate(1.0, 0.0, 1.0);
            } else {
                $$3 = this.getBoundingBox().inflate(1.0, 0.5, 1.0);
            }
            List<Entity> $$4 = this.level().getEntities(this, $$3);
            ArrayList<Entity> $$5 = Lists.newArrayList();
            for (Entity $$6 : $$4) {
                if ($$6.getType() == EntityType.EXPERIENCE_ORB) {
                    $$5.add($$6);
                    continue;
                }
                if ($$6.isRemoved()) continue;
                this.touch($$6);
            }
            if (!$$5.isEmpty()) {
                this.touch((Entity)Util.getRandom($$5, this.random));
            }
        }
        this.playShoulderEntityAmbientSound(this.getShoulderEntityLeft());
        this.playShoulderEntityAmbientSound(this.getShoulderEntityRight());
        if (!this.level().isClientSide && (this.fallDistance > 0.5 || this.isInWater()) || this.abilities.flying || this.isSleeping() || this.isInPowderSnow) {
            this.removeEntitiesOnShoulder();
        }
    }

    protected void tickRegeneration() {
    }

    private void playShoulderEntityAmbientSound(CompoundTag $$0) {
        EntityType $$1;
        if ($$0.isEmpty() || $$0.getBooleanOr("Silent", false)) {
            return;
        }
        if (this.level().random.nextInt(200) == 0 && ($$1 = (EntityType)$$0.read("id", EntityType.CODEC).orElse(null)) == EntityType.PARROT && !Parrot.imitateNearbyMobs(this.level(), this)) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), Parrot.getAmbient(this.level(), this.level().random), this.getSoundSource(), 1.0f, Parrot.getPitch(this.level().random));
        }
    }

    private void touch(Entity $$0) {
        $$0.playerTouch(this);
    }

    public int getScore() {
        return this.entityData.get(DATA_SCORE_ID);
    }

    public void setScore(int $$0) {
        this.entityData.set(DATA_SCORE_ID, $$0);
    }

    public void increaseScore(int $$0) {
        int $$1 = this.getScore();
        this.entityData.set(DATA_SCORE_ID, $$1 + $$0);
    }

    public void startAutoSpinAttack(int $$0, float $$1, ItemStack $$2) {
        this.autoSpinAttackTicks = $$0;
        this.autoSpinAttackDmg = $$1;
        this.autoSpinAttackItemStack = $$2;
        if (!this.level().isClientSide) {
            this.removeEntitiesOnShoulder();
            this.setLivingEntityFlag(4, true);
        }
    }

    @Override
    @Nonnull
    public ItemStack getWeaponItem() {
        if (this.isAutoSpinAttack() && this.autoSpinAttackItemStack != null) {
            return this.autoSpinAttackItemStack;
        }
        return super.getWeaponItem();
    }

    @Override
    public void die(DamageSource $$0) {
        Level level;
        super.die($$0);
        this.reapplyPosition();
        if (!this.isSpectator() && (level = this.level()) instanceof ServerLevel) {
            ServerLevel $$1 = (ServerLevel)level;
            this.dropAllDeathLoot($$1, $$0);
        }
        if ($$0 != null) {
            this.setDeltaMovement(-Mth.cos((this.getHurtDir() + this.getYRot()) * ((float)Math.PI / 180)) * 0.1f, 0.1f, -Mth.sin((this.getHurtDir() + this.getYRot()) * ((float)Math.PI / 180)) * 0.1f);
        } else {
            this.setDeltaMovement(0.0, 0.1, 0.0);
        }
        this.awardStat(Stats.DEATHS);
        this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_DEATH));
        this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
        this.clearFire();
        this.setSharedFlagOnFire(false);
        this.setLastDeathLocation(Optional.of(GlobalPos.of(this.level().dimension(), this.blockPosition())));
    }

    @Override
    protected void dropEquipment(ServerLevel $$0) {
        super.dropEquipment($$0);
        if (!$$0.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            this.destroyVanishingCursedItems();
            this.inventory.dropAll();
        }
    }

    protected void destroyVanishingCursedItems() {
        for (int $$0 = 0; $$0 < this.inventory.getContainerSize(); ++$$0) {
            ItemStack $$1 = this.inventory.getItem($$0);
            if ($$1.isEmpty() || !EnchantmentHelper.has($$1, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP)) continue;
            this.inventory.removeItemNoUpdate($$0);
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource $$0) {
        return $$0.type().effects().sound();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PLAYER_DEATH;
    }

    public void handleCreativeModeItemDrop(ItemStack $$0) {
    }

    @Nullable
    public ItemEntity drop(ItemStack $$0, boolean $$1) {
        return this.drop($$0, false, $$1);
    }

    public float getDestroySpeed(BlockState $$0) {
        float $$1 = this.inventory.getSelectedItem().getDestroySpeed($$0);
        if ($$1 > 1.0f) {
            $$1 += (float)this.getAttributeValue(Attributes.MINING_EFFICIENCY);
        }
        if (MobEffectUtil.hasDigSpeed(this)) {
            $$1 *= 1.0f + (float)(MobEffectUtil.getDigSpeedAmplification(this) + 1) * 0.2f;
        }
        if (this.hasEffect(MobEffects.MINING_FATIGUE)) {
            float $$2 = switch (this.getEffect(MobEffects.MINING_FATIGUE).getAmplifier()) {
                case 0 -> 0.3f;
                case 1 -> 0.09f;
                case 2 -> 0.0027f;
                default -> 8.1E-4f;
            };
            $$1 *= $$2;
        }
        $$1 *= (float)this.getAttributeValue(Attributes.BLOCK_BREAK_SPEED);
        if (this.isEyeInFluid(FluidTags.WATER)) {
            $$1 *= (float)this.getAttribute(Attributes.SUBMERGED_MINING_SPEED).getValue();
        }
        if (!this.onGround()) {
            $$1 /= 5.0f;
        }
        return $$1;
    }

    public boolean hasCorrectToolForDrops(BlockState $$0) {
        return !$$0.requiresCorrectToolForDrops() || this.inventory.getSelectedItem().isCorrectToolForDrops($$0);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput $$0) {
        super.readAdditionalSaveData($$0);
        this.setUUID(this.gameProfile.getId());
        this.inventory.load($$0.listOrEmpty("Inventory", ItemStackWithSlot.CODEC));
        this.inventory.setSelectedSlot($$0.getIntOr("SelectedItemSlot", 0));
        this.sleepCounter = $$0.getShortOr("SleepTimer", (short)0);
        this.experienceProgress = $$0.getFloatOr("XpP", 0.0f);
        this.experienceLevel = $$0.getIntOr("XpLevel", 0);
        this.totalExperience = $$0.getIntOr("XpTotal", 0);
        this.enchantmentSeed = $$0.getIntOr("XpSeed", 0);
        if (this.enchantmentSeed == 0) {
            this.enchantmentSeed = this.random.nextInt();
        }
        this.setScore($$0.getIntOr("Score", 0));
        this.foodData.readAdditionalSaveData($$0);
        $$0.read("abilities", Abilities.Packed.CODEC).ifPresent(this.abilities::apply);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.abilities.getWalkingSpeed());
        this.enderChestInventory.fromSlots($$0.listOrEmpty("EnderItems", ItemStackWithSlot.CODEC));
        this.setShoulderEntityLeft($$0.read("ShoulderEntityLeft", CompoundTag.CODEC).orElseGet(CompoundTag::new));
        this.setShoulderEntityRight($$0.read("ShoulderEntityRight", CompoundTag.CODEC).orElseGet(CompoundTag::new));
        this.setLastDeathLocation($$0.read("LastDeathLocation", GlobalPos.CODEC));
        this.currentImpulseImpactPos = $$0.read("current_explosion_impact_pos", Vec3.CODEC).orElse(null);
        this.ignoreFallDamageFromCurrentImpulse = $$0.getBooleanOr("ignore_fall_damage_from_current_explosion", false);
        this.currentImpulseContextResetGraceTime = $$0.getIntOr("current_impulse_context_reset_grace_time", 0);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput $$0) {
        super.addAdditionalSaveData($$0);
        NbtUtils.addCurrentDataVersion($$0);
        this.inventory.save($$0.list("Inventory", ItemStackWithSlot.CODEC));
        $$0.putInt("SelectedItemSlot", this.inventory.getSelectedSlot());
        $$0.putShort("SleepTimer", (short)this.sleepCounter);
        $$0.putFloat("XpP", this.experienceProgress);
        $$0.putInt("XpLevel", this.experienceLevel);
        $$0.putInt("XpTotal", this.totalExperience);
        $$0.putInt("XpSeed", this.enchantmentSeed);
        $$0.putInt("Score", this.getScore());
        this.foodData.addAdditionalSaveData($$0);
        $$0.store("abilities", Abilities.Packed.CODEC, this.abilities.pack());
        this.enderChestInventory.storeAsSlots($$0.list("EnderItems", ItemStackWithSlot.CODEC));
        if (!this.getShoulderEntityLeft().isEmpty()) {
            $$0.store("ShoulderEntityLeft", CompoundTag.CODEC, this.getShoulderEntityLeft());
        }
        if (!this.getShoulderEntityRight().isEmpty()) {
            $$0.store("ShoulderEntityRight", CompoundTag.CODEC, this.getShoulderEntityRight());
        }
        this.lastDeathLocation.ifPresent($$1 -> $$0.store("LastDeathLocation", GlobalPos.CODEC, $$1));
        $$0.storeNullable("current_explosion_impact_pos", Vec3.CODEC, this.currentImpulseImpactPos);
        $$0.putBoolean("ignore_fall_damage_from_current_explosion", this.ignoreFallDamageFromCurrentImpulse);
        $$0.putInt("current_impulse_context_reset_grace_time", this.currentImpulseContextResetGraceTime);
    }

    @Override
    public boolean isInvulnerableTo(ServerLevel $$0, DamageSource $$1) {
        if (super.isInvulnerableTo($$0, $$1)) {
            return true;
        }
        if ($$1.is(DamageTypeTags.IS_DROWNING)) {
            return !$$0.getGameRules().getBoolean(GameRules.RULE_DROWNING_DAMAGE);
        }
        if ($$1.is(DamageTypeTags.IS_FALL)) {
            return !$$0.getGameRules().getBoolean(GameRules.RULE_FALL_DAMAGE);
        }
        if ($$1.is(DamageTypeTags.IS_FIRE)) {
            return !$$0.getGameRules().getBoolean(GameRules.RULE_FIRE_DAMAGE);
        }
        if ($$1.is(DamageTypeTags.IS_FREEZING)) {
            return !$$0.getGameRules().getBoolean(GameRules.RULE_FREEZE_DAMAGE);
        }
        return false;
    }

    @Override
    public boolean hurtServer(ServerLevel $$0, DamageSource $$1, float $$2) {
        if (this.isInvulnerableTo($$0, $$1)) {
            return false;
        }
        if (this.abilities.invulnerable && !$$1.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
        }
        this.noActionTime = 0;
        if (this.isDeadOrDying()) {
            return false;
        }
        this.removeEntitiesOnShoulder();
        if ($$1.scalesWithDifficulty()) {
            if ($$0.getDifficulty() == Difficulty.PEACEFUL) {
                $$2 = 0.0f;
            }
            if ($$0.getDifficulty() == Difficulty.EASY) {
                $$2 = Math.min($$2 / 2.0f + 1.0f, $$2);
            }
            if ($$0.getDifficulty() == Difficulty.HARD) {
                $$2 = $$2 * 3.0f / 2.0f;
            }
        }
        if ($$2 == 0.0f) {
            return false;
        }
        return super.hurtServer($$0, $$1, $$2);
    }

    @Override
    protected void blockUsingItem(ServerLevel $$0, LivingEntity $$1) {
        super.blockUsingItem($$0, $$1);
        ItemStack $$2 = this.getItemBlockingWith();
        BlocksAttacks $$3 = $$2 != null ? $$2.get(DataComponents.BLOCKS_ATTACKS) : null;
        float $$4 = $$1.getSecondsToDisableBlocking();
        if ($$4 > 0.0f && $$3 != null) {
            $$3.disable($$0, this, $$4, $$2);
        }
    }

    @Override
    public boolean canBeSeenAsEnemy() {
        return !this.getAbilities().invulnerable && super.canBeSeenAsEnemy();
    }

    public boolean canHarmPlayer(Player $$0) {
        PlayerTeam $$1 = this.getTeam();
        PlayerTeam $$2 = $$0.getTeam();
        if ($$1 == null) {
            return true;
        }
        if (!$$1.isAlliedTo($$2)) {
            return true;
        }
        return ((Team)$$1).isAllowFriendlyFire();
    }

    @Override
    protected void hurtArmor(DamageSource $$0, float $$1) {
        this.a($$0, $$1, EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD);
    }

    @Override
    protected void hurtHelmet(DamageSource $$0, float $$1) {
        this.a($$0, $$1, EquipmentSlot.HEAD);
    }

    @Override
    protected void actuallyHurt(ServerLevel $$0, DamageSource $$1, float $$2) {
        if (this.isInvulnerableTo($$0, $$1)) {
            return;
        }
        $$2 = this.getDamageAfterArmorAbsorb($$1, $$2);
        float $$3 = $$2 = this.getDamageAfterMagicAbsorb($$1, $$2);
        $$2 = Math.max($$2 - this.getAbsorptionAmount(), 0.0f);
        this.setAbsorptionAmount(this.getAbsorptionAmount() - ($$3 - $$2));
        float $$4 = $$3 - $$2;
        if ($$4 > 0.0f && $$4 < 3.4028235E37f) {
            this.awardStat(Stats.DAMAGE_ABSORBED, Math.round($$4 * 10.0f));
        }
        if ($$2 == 0.0f) {
            return;
        }
        this.causeFoodExhaustion($$1.getFoodExhaustion());
        this.getCombatTracker().recordDamage($$1, $$2);
        this.setHealth(this.getHealth() - $$2);
        if ($$2 < 3.4028235E37f) {
            this.awardStat(Stats.DAMAGE_TAKEN, Math.round($$2 * 10.0f));
        }
        this.gameEvent(GameEvent.ENTITY_DAMAGE);
    }

    public boolean isTextFilteringEnabled() {
        return false;
    }

    public void openTextEdit(SignBlockEntity $$0, boolean $$1) {
    }

    public void openMinecartCommandBlock(BaseCommandBlock $$0) {
    }

    public void openCommandBlock(CommandBlockEntity $$0) {
    }

    public void openStructureBlock(StructureBlockEntity $$0) {
    }

    public void openTestBlock(TestBlockEntity $$0) {
    }

    public void openTestInstanceBlock(TestInstanceBlockEntity $$0) {
    }

    public void openJigsawBlock(JigsawBlockEntity $$0) {
    }

    public void openHorseInventory(AbstractHorse $$0, Container $$1) {
    }

    public OptionalInt openMenu(@Nullable MenuProvider $$0) {
        return OptionalInt.empty();
    }

    public void openDialog(Holder<Dialog> $$0) {
    }

    public void sendMerchantOffers(int $$0, MerchantOffers $$1, int $$2, int $$3, boolean $$4, boolean $$5) {
    }

    public void openItemGui(ItemStack $$0, InteractionHand $$1) {
    }

    public InteractionResult interactOn(Entity $$0, InteractionHand $$1) {
        if (this.isSpectator()) {
            if ($$0 instanceof MenuProvider) {
                this.openMenu((MenuProvider)((Object)$$0));
            }
            return InteractionResult.PASS;
        }
        ItemStack $$2 = this.getItemInHand($$1);
        ItemStack $$3 = $$2.copy();
        InteractionResult $$4 = $$0.interact(this, $$1);
        if ($$4.consumesAction()) {
            if (this.hasInfiniteMaterials() && $$2 == this.getItemInHand($$1) && $$2.getCount() < $$3.getCount()) {
                $$2.setCount($$3.getCount());
            }
            return $$4;
        }
        if (!$$2.isEmpty() && $$0 instanceof LivingEntity) {
            InteractionResult $$5;
            if (this.hasInfiniteMaterials()) {
                $$2 = $$3;
            }
            if (($$5 = $$2.interactLivingEntity(this, (LivingEntity)$$0, $$1)).consumesAction()) {
                this.level().gameEvent(GameEvent.ENTITY_INTERACT, $$0.position(), GameEvent.Context.of(this));
                if ($$2.isEmpty() && !this.hasInfiniteMaterials()) {
                    this.setItemInHand($$1, ItemStack.EMPTY);
                }
                return $$5;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void removeVehicle() {
        super.removeVehicle();
        this.boardingCooldown = 0;
    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || this.isSleeping();
    }

    @Override
    public boolean isAffectedByFluids() {
        return !this.abilities.flying;
    }

    @Override
    protected Vec3 maybeBackOffFromEdge(Vec3 $$0, MoverType $$1) {
        double $$3;
        float $$2 = this.maxUpStep();
        if (this.abilities.flying || $$0.y > 0.0 || $$1 != MoverType.SELF && $$1 != MoverType.PLAYER || !this.isStayingOnGroundSurface() || !this.isAboveGround($$2)) {
            return $$0;
        }
        double $$4 = $$0.z;
        double $$5 = 0.05;
        double $$6 = Math.signum($$3) * 0.05;
        double $$7 = Math.signum($$4) * 0.05;
        for ($$3 = $$0.x; $$3 != 0.0 && this.canFallAtLeast($$3, 0.0, $$2); $$3 -= $$6) {
            if (!(Math.abs($$3) <= 0.05)) continue;
            $$3 = 0.0;
            break;
        }
        while ($$4 != 0.0 && this.canFallAtLeast(0.0, $$4, $$2)) {
            if (Math.abs($$4) <= 0.05) {
                $$4 = 0.0;
                break;
            }
            $$4 -= $$7;
        }
        while ($$3 != 0.0 && $$4 != 0.0 && this.canFallAtLeast($$3, $$4, $$2)) {
            $$3 = Math.abs($$3) <= 0.05 ? 0.0 : ($$3 -= $$6);
            if (Math.abs($$4) <= 0.05) {
                $$4 = 0.0;
                continue;
            }
            $$4 -= $$7;
        }
        return new Vec3($$3, $$0.y, $$4);
    }

    private boolean isAboveGround(float $$0) {
        return this.onGround() || this.fallDistance < (double)$$0 && !this.canFallAtLeast(0.0, 0.0, (double)$$0 - this.fallDistance);
    }

    private boolean canFallAtLeast(double $$0, double $$1, double $$2) {
        AABB $$3 = this.getBoundingBox();
        return this.level().noCollision(this, new AABB($$3.minX + 1.0E-7 + $$0, $$3.minY - $$2 - 1.0E-7, $$3.minZ + 1.0E-7 + $$1, $$3.maxX - 1.0E-7 + $$0, $$3.minY, $$3.maxZ - 1.0E-7 + $$1));
    }

    public void attack(Entity $$0) {
        Projectile $$6;
        if (!$$0.isAttackable()) {
            return;
        }
        if ($$0.skipAttackInteraction(this)) {
            return;
        }
        float $$1 = this.isAutoSpinAttack() ? this.autoSpinAttackDmg : (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        ItemStack $$2 = this.getWeaponItem();
        DamageSource $$3 = Optional.ofNullable($$2.getItem().getDamageSource(this)).orElse(this.damageSources().playerAttack(this));
        float $$4 = this.getEnchantedDamage($$0, $$1, $$3) - $$1;
        float $$5 = this.getAttackStrengthScale(0.5f);
        $$1 *= 0.2f + $$5 * $$5 * 0.8f;
        $$4 *= $$5;
        this.resetAttackStrengthTicker();
        if ($$0.getType().is(EntityTypeTags.REDIRECTABLE_PROJECTILE) && $$0 instanceof Projectile && ($$6 = (Projectile)$$0).deflect(ProjectileDeflection.AIM_DEFLECT, this, this, true)) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_NODAMAGE, this.getSoundSource());
            return;
        }
        if ($$1 > 0.0f || $$4 > 0.0f) {
            double $$14;
            double $$13;
            boolean $$10;
            boolean $$9;
            boolean $$7;
            boolean bl = $$7 = $$5 > 0.9f;
            if (this.isSprinting() && $$7) {
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_KNOCKBACK, this.getSoundSource(), 1.0f, 1.0f);
                boolean $$8 = true;
            } else {
                $$9 = false;
            }
            $$1 += $$2.getItem().getAttackDamageBonus($$0, $$1, $$3);
            boolean bl2 = $$10 = $$7 && this.fallDistance > 0.0 && !this.onGround() && !this.onClimbable() && !this.isInWater() && !this.hasEffect(MobEffects.BLINDNESS) && !this.isPassenger() && $$0 instanceof LivingEntity && !this.isSprinting();
            if ($$10) {
                $$1 *= 1.5f;
            }
            float $$11 = $$1 + $$4;
            boolean $$12 = false;
            if ($$7 && !$$10 && !$$9 && this.onGround() && ($$13 = this.getKnownMovement().horizontalDistanceSqr()) < Mth.square($$14 = (double)this.getSpeed() * 2.5) && this.getItemInHand(InteractionHand.MAIN_HAND).is(ItemTags.SWORDS)) {
                $$12 = true;
            }
            float $$15 = 0.0f;
            if ($$0 instanceof LivingEntity) {
                LivingEntity $$16 = (LivingEntity)$$0;
                $$15 = $$16.getHealth();
            }
            Vec3 $$17 = $$0.getDeltaMovement();
            boolean $$18 = $$0.hurtOrSimulate($$3, $$11);
            if ($$18) {
                float $$19 = this.getKnockback($$0, $$3) + ($$9 ? 1.0f : 0.0f);
                if ($$19 > 0.0f) {
                    if ($$0 instanceof LivingEntity) {
                        LivingEntity $$20 = (LivingEntity)$$0;
                        $$20.knockback($$19 * 0.5f, Mth.sin(this.getYRot() * ((float)Math.PI / 180)), -Mth.cos(this.getYRot() * ((float)Math.PI / 180)));
                    } else {
                        $$0.push(-Mth.sin(this.getYRot() * ((float)Math.PI / 180)) * $$19 * 0.5f, 0.1, Mth.cos(this.getYRot() * ((float)Math.PI / 180)) * $$19 * 0.5f);
                    }
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 1.0, 0.6));
                    this.setSprinting(false);
                }
                if ($$12) {
                    float $$21 = 1.0f + (float)this.getAttributeValue(Attributes.SWEEPING_DAMAGE_RATIO) * $$1;
                    List<LivingEntity> $$22 = this.level().getEntitiesOfClass(LivingEntity.class, $$0.getBoundingBox().inflate(1.0, 0.25, 1.0));
                    for (LivingEntity livingEntity : $$22) {
                        ServerLevel $$26;
                        ArmorStand $$24;
                        if (livingEntity == this || livingEntity == $$0 || this.isAlliedTo(livingEntity) || livingEntity instanceof ArmorStand && ($$24 = (ArmorStand)livingEntity).isMarker() || !(this.distanceToSqr(livingEntity) < 9.0)) continue;
                        float $$25 = this.getEnchantedDamage(livingEntity, $$21, $$3) * $$5;
                        Level level = this.level();
                        if (!(level instanceof ServerLevel) || !livingEntity.hurtServer($$26 = (ServerLevel)level, $$3, $$25)) continue;
                        livingEntity.knockback(0.4f, Mth.sin(this.getYRot() * ((float)Math.PI / 180)), -Mth.cos(this.getYRot() * ((float)Math.PI / 180)));
                        EnchantmentHelper.doPostAttackEffects($$26, livingEntity, $$3);
                    }
                    this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, this.getSoundSource(), 1.0f, 1.0f);
                    this.sweepAttack();
                }
                if ($$0 instanceof ServerPlayer && $$0.hurtMarked) {
                    ((ServerPlayer)$$0).connection.send(new ClientboundSetEntityMotionPacket($$0));
                    $$0.hurtMarked = false;
                    $$0.setDeltaMovement($$17);
                }
                if ($$10) {
                    this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, this.getSoundSource(), 1.0f, 1.0f);
                    this.crit($$0);
                }
                if (!$$10 && !$$12) {
                    if ($$7) {
                        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_STRONG, this.getSoundSource(), 1.0f, 1.0f);
                    } else {
                        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_WEAK, this.getSoundSource(), 1.0f, 1.0f);
                    }
                }
                if ($$4 > 0.0f) {
                    this.magicCrit($$0);
                }
                this.setLastHurtMob($$0);
                Entity $$27 = $$0;
                if ($$0 instanceof EnderDragonPart) {
                    $$27 = ((EnderDragonPart)$$0).parentMob;
                }
                boolean $$28 = false;
                Level level = this.level();
                if (level instanceof ServerLevel) {
                    ServerLevel $$29 = (ServerLevel)level;
                    if ($$27 instanceof LivingEntity) {
                        LivingEntity livingEntity = (LivingEntity)$$27;
                        $$28 = $$2.hurtEnemy(livingEntity, this);
                    }
                    EnchantmentHelper.doPostAttackEffects($$29, $$0, $$3);
                }
                if (!this.level().isClientSide && !$$2.isEmpty() && $$27 instanceof LivingEntity) {
                    if ($$28) {
                        $$2.postHurtEnemy((LivingEntity)$$27, this);
                    }
                    if ($$2.isEmpty()) {
                        if ($$2 == this.getMainHandItem()) {
                            this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                        } else {
                            this.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
                        }
                    }
                }
                if ($$0 instanceof LivingEntity) {
                    float $$31 = $$15 - ((LivingEntity)$$0).getHealth();
                    this.awardStat(Stats.DAMAGE_DEALT, Math.round($$31 * 10.0f));
                    if (this.level() instanceof ServerLevel && $$31 > 2.0f) {
                        int n = (int)((double)$$31 * 0.5);
                        ((ServerLevel)this.level()).sendParticles(ParticleTypes.DAMAGE_INDICATOR, $$0.getX(), $$0.getY(0.5), $$0.getZ(), n, 0.1, 0.0, 0.1, 0.2);
                    }
                }
                this.causeFoodExhaustion(0.1f);
            } else {
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_NODAMAGE, this.getSoundSource(), 1.0f, 1.0f);
            }
        }
    }

    protected float getEnchantedDamage(Entity $$0, float $$1, DamageSource $$2) {
        return $$1;
    }

    @Override
    protected void doAutoAttackOnTouch(LivingEntity $$0) {
        this.attack($$0);
    }

    public void crit(Entity $$0) {
    }

    public void magicCrit(Entity $$0) {
    }

    public void sweepAttack() {
        double $$0 = -Mth.sin(this.getYRot() * ((float)Math.PI / 180));
        double $$1 = Mth.cos(this.getYRot() * ((float)Math.PI / 180));
        if (this.level() instanceof ServerLevel) {
            ((ServerLevel)this.level()).sendParticles(ParticleTypes.SWEEP_ATTACK, this.getX() + $$0, this.getY(0.5), this.getZ() + $$1, 0, $$0, 0.0, $$1, 0.0);
        }
    }

    public void respawn() {
    }

    @Override
    public void remove(Entity.RemovalReason $$0) {
        super.remove($$0);
        this.inventoryMenu.removed(this);
        if (this.containerMenu != null && this.hasContainerOpen()) {
            this.doCloseContainer();
        }
    }

    @Override
    public boolean isClientAuthoritative() {
        return true;
    }

    @Override
    protected boolean isLocalClientAuthoritative() {
        return this.isLocalPlayer();
    }

    public boolean isLocalPlayer() {
        return false;
    }

    @Override
    public boolean canSimulateMovement() {
        return !this.level().isClientSide || this.isLocalPlayer();
    }

    @Override
    public boolean isEffectiveAi() {
        return !this.level().isClientSide || this.isLocalPlayer();
    }

    public GameProfile getGameProfile() {
        return this.gameProfile;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public Abilities getAbilities() {
        return this.abilities;
    }

    @Override
    public boolean hasInfiniteMaterials() {
        return this.abilities.instabuild;
    }

    public boolean preventsBlockDrops() {
        return this.abilities.instabuild;
    }

    public void updateTutorialInventoryAction(ItemStack $$0, ItemStack $$1, ClickAction $$2) {
    }

    public boolean hasContainerOpen() {
        return this.containerMenu != this.inventoryMenu;
    }

    public boolean canDropItems() {
        return true;
    }

    public Either<BedSleepingProblem, Unit> startSleepInBed(BlockPos $$0) {
        this.startSleeping($$0);
        this.sleepCounter = 0;
        return Either.right((Object)((Object)Unit.INSTANCE));
    }

    public void stopSleepInBed(boolean $$0, boolean $$1) {
        super.stopSleeping();
        if (this.level() instanceof ServerLevel && $$1) {
            ((ServerLevel)this.level()).updateSleepingPlayerList();
        }
        this.sleepCounter = $$0 ? 0 : 100;
    }

    @Override
    public void stopSleeping() {
        this.stopSleepInBed(true, true);
    }

    public boolean isSleepingLongEnough() {
        return this.isSleeping() && this.sleepCounter >= 100;
    }

    public int getSleepTimer() {
        return this.sleepCounter;
    }

    public void displayClientMessage(Component $$0, boolean $$1) {
    }

    public void awardStat(ResourceLocation $$0) {
        this.awardStat(Stats.CUSTOM.get($$0));
    }

    public void awardStat(ResourceLocation $$0, int $$1) {
        this.awardStat(Stats.CUSTOM.get($$0), $$1);
    }

    public void awardStat(Stat<?> $$0) {
        this.awardStat($$0, 1);
    }

    public void awardStat(Stat<?> $$0, int $$1) {
    }

    public void resetStat(Stat<?> $$0) {
    }

    public int awardRecipes(Collection<RecipeHolder<?>> $$0) {
        return 0;
    }

    public void triggerRecipeCrafted(RecipeHolder<?> $$0, List<ItemStack> $$1) {
    }

    public void awardRecipesByKey(List<ResourceKey<Recipe<?>>> $$0) {
    }

    public int resetRecipes(Collection<RecipeHolder<?>> $$0) {
        return 0;
    }

    @Override
    public void travel(Vec3 $$0) {
        if (this.isPassenger()) {
            super.travel($$0);
            return;
        }
        if (this.isSwimming()) {
            double $$2;
            double $$1 = this.getLookAngle().y;
            double d = $$2 = $$1 < -0.2 ? 0.085 : 0.06;
            if ($$1 <= 0.0 || this.jumping || !this.level().getFluidState(BlockPos.containing(this.getX(), this.getY() + 1.0 - 0.1, this.getZ())).isEmpty()) {
                Vec3 $$3 = this.getDeltaMovement();
                this.setDeltaMovement($$3.add(0.0, ($$1 - $$3.y) * $$2, 0.0));
            }
        }
        if (this.getAbilities().flying) {
            double $$4 = this.getDeltaMovement().y;
            super.travel($$0);
            this.setDeltaMovement(this.getDeltaMovement().with(Direction.Axis.Y, $$4 * 0.6));
        } else {
            super.travel($$0);
        }
    }

    @Override
    protected boolean canGlide() {
        return !this.abilities.flying && super.canGlide();
    }

    @Override
    public void updateSwimming() {
        if (this.abilities.flying) {
            this.setSwimming(false);
        } else {
            super.updateSwimming();
        }
    }

    protected boolean freeAt(BlockPos $$0) {
        return !this.level().getBlockState($$0).isSuffocating(this.level(), $$0);
    }

    @Override
    public float getSpeed() {
        return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }

    @Override
    public boolean causeFallDamage(double $$0, float $$1, DamageSource $$2) {
        double $$6;
        boolean $$3;
        if (this.abilities.mayfly) {
            return false;
        }
        if ($$0 >= 2.0) {
            this.awardStat(Stats.FALL_ONE_CM, (int)Math.round($$0 * 100.0));
        }
        boolean bl = $$3 = this.currentImpulseImpactPos != null && this.ignoreFallDamageFromCurrentImpulse;
        if ($$3) {
            boolean $$5;
            double $$4 = Math.min($$0, this.currentImpulseImpactPos.y - this.getY());
            boolean bl2 = $$5 = $$4 <= 0.0;
            if ($$5) {
                this.resetCurrentImpulseContext();
            } else {
                this.tryResetCurrentImpulseContext();
            }
        } else {
            $$6 = $$0;
        }
        if ($$6 > 0.0 && super.causeFallDamage($$6, $$1, $$2)) {
            this.resetCurrentImpulseContext();
            return true;
        }
        this.propagateFallToPassengers($$0, $$1, $$2);
        return false;
    }

    public boolean tryToStartFallFlying() {
        if (!this.isFallFlying() && this.canGlide() && !this.isInWater()) {
            this.startFallFlying();
            return true;
        }
        return false;
    }

    public void startFallFlying() {
        this.setSharedFlag(7, true);
    }

    @Override
    protected void doWaterSplashEffect() {
        if (!this.isSpectator()) {
            super.doWaterSplashEffect();
        }
    }

    @Override
    protected void playStepSound(BlockPos $$0, BlockState $$1) {
        if (this.isInWater()) {
            this.waterSwimSound();
            this.playMuffledStepSound($$1);
        } else {
            BlockPos $$2 = this.getPrimaryStepSoundBlockPos($$0);
            if (!$$0.equals($$2)) {
                BlockState $$3 = this.level().getBlockState($$2);
                if ($$3.is(BlockTags.COMBINATION_STEP_SOUND_BLOCKS)) {
                    this.playCombinationStepSounds($$3, $$1);
                } else {
                    super.playStepSound($$2, $$3);
                }
            } else {
                super.playStepSound($$0, $$1);
            }
        }
    }

    @Override
    public LivingEntity.Fallsounds getFallSounds() {
        return new LivingEntity.Fallsounds(SoundEvents.PLAYER_SMALL_FALL, SoundEvents.PLAYER_BIG_FALL);
    }

    @Override
    public boolean killedEntity(ServerLevel $$0, LivingEntity $$1) {
        this.awardStat(Stats.ENTITY_KILLED.get($$1.getType()));
        return true;
    }

    @Override
    public void makeStuckInBlock(BlockState $$0, Vec3 $$1) {
        if (!this.abilities.flying) {
            super.makeStuckInBlock($$0, $$1);
        }
        this.tryResetCurrentImpulseContext();
    }

    public void giveExperiencePoints(int $$0) {
        this.increaseScore($$0);
        this.experienceProgress += (float)$$0 / (float)this.getXpNeededForNextLevel();
        this.totalExperience = Mth.clamp(this.totalExperience + $$0, 0, Integer.MAX_VALUE);
        while (this.experienceProgress < 0.0f) {
            float $$1 = this.experienceProgress * (float)this.getXpNeededForNextLevel();
            if (this.experienceLevel > 0) {
                this.giveExperienceLevels(-1);
                this.experienceProgress = 1.0f + $$1 / (float)this.getXpNeededForNextLevel();
                continue;
            }
            this.giveExperienceLevels(-1);
            this.experienceProgress = 0.0f;
        }
        while (this.experienceProgress >= 1.0f) {
            this.experienceProgress = (this.experienceProgress - 1.0f) * (float)this.getXpNeededForNextLevel();
            this.giveExperienceLevels(1);
            this.experienceProgress /= (float)this.getXpNeededForNextLevel();
        }
    }

    public int getEnchantmentSeed() {
        return this.enchantmentSeed;
    }

    public void onEnchantmentPerformed(ItemStack $$0, int $$1) {
        this.experienceLevel -= $$1;
        if (this.experienceLevel < 0) {
            this.experienceLevel = 0;
            this.experienceProgress = 0.0f;
            this.totalExperience = 0;
        }
        this.enchantmentSeed = this.random.nextInt();
    }

    public void giveExperienceLevels(int $$0) {
        this.experienceLevel = IntMath.saturatedAdd(this.experienceLevel, $$0);
        if (this.experienceLevel < 0) {
            this.experienceLevel = 0;
            this.experienceProgress = 0.0f;
            this.totalExperience = 0;
        }
        if ($$0 > 0 && this.experienceLevel % 5 == 0 && (float)this.lastLevelUpTime < (float)this.tickCount - 100.0f) {
            float $$1 = this.experienceLevel > 30 ? 1.0f : (float)this.experienceLevel / 30.0f;
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_LEVELUP, this.getSoundSource(), $$1 * 0.75f, 1.0f);
            this.lastLevelUpTime = this.tickCount;
        }
    }

    public int getXpNeededForNextLevel() {
        if (this.experienceLevel >= 30) {
            return 112 + (this.experienceLevel - 30) * 9;
        }
        if (this.experienceLevel >= 15) {
            return 37 + (this.experienceLevel - 15) * 5;
        }
        return 7 + this.experienceLevel * 2;
    }

    public void causeFoodExhaustion(float $$0) {
        if (this.abilities.invulnerable) {
            return;
        }
        if (!this.level().isClientSide) {
            this.foodData.addExhaustion($$0);
        }
    }

    public Optional<WardenSpawnTracker> getWardenSpawnTracker() {
        return Optional.empty();
    }

    public FoodData getFoodData() {
        return this.foodData;
    }

    public boolean canEat(boolean $$0) {
        return this.abilities.invulnerable || $$0 || this.foodData.needsFood();
    }

    public boolean isHurt() {
        return this.getHealth() > 0.0f && this.getHealth() < this.getMaxHealth();
    }

    public boolean mayBuild() {
        return this.abilities.mayBuild;
    }

    public boolean mayUseItemAt(BlockPos $$0, Direction $$1, ItemStack $$2) {
        if (this.abilities.mayBuild) {
            return true;
        }
        BlockPos $$3 = $$0.relative($$1.getOpposite());
        BlockInWorld $$4 = new BlockInWorld(this.level(), $$3, false);
        return $$2.canPlaceOnBlockInAdventureMode($$4);
    }

    @Override
    protected int getBaseExperienceReward(ServerLevel $$0) {
        if ($$0.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) || this.isSpectator()) {
            return 0;
        }
        return Math.min(this.experienceLevel * 7, 100);
    }

    @Override
    protected boolean isAlwaysExperienceDropper() {
        return true;
    }

    @Override
    public boolean shouldShowName() {
        return true;
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return !this.abilities.flying && (!this.onGround() || !this.isDiscrete()) ? Entity.MovementEmission.ALL : Entity.MovementEmission.NONE;
    }

    public void onUpdateAbilities() {
    }

    @Override
    public Component getName() {
        return Component.literal(this.gameProfile.getName());
    }

    public PlayerEnderChestContainer getEnderChestInventory() {
        return this.enderChestInventory;
    }

    @Override
    protected boolean doesEmitEquipEvent(EquipmentSlot $$0) {
        return $$0.getType() == EquipmentSlot.Type.HUMANOID_ARMOR;
    }

    public boolean addItem(ItemStack $$0) {
        return this.inventory.add($$0);
    }

    public boolean setEntityOnShoulder(CompoundTag $$0) {
        if (this.isPassenger() || !this.onGround() || this.isInWater() || this.isInPowderSnow) {
            return false;
        }
        if (this.getShoulderEntityLeft().isEmpty()) {
            this.setShoulderEntityLeft($$0);
            this.timeEntitySatOnShoulder = this.level().getGameTime();
            return true;
        }
        if (this.getShoulderEntityRight().isEmpty()) {
            this.setShoulderEntityRight($$0);
            this.timeEntitySatOnShoulder = this.level().getGameTime();
            return true;
        }
        return false;
    }

    protected void removeEntitiesOnShoulder() {
        if (this.timeEntitySatOnShoulder + 20L < this.level().getGameTime()) {
            this.respawnEntityOnShoulder(this.getShoulderEntityLeft());
            this.setShoulderEntityLeft(new CompoundTag());
            this.respawnEntityOnShoulder(this.getShoulderEntityRight());
            this.setShoulderEntityRight(new CompoundTag());
        }
    }

    private void respawnEntityOnShoulder(CompoundTag $$0) {
        Level level = this.level();
        if (level instanceof ServerLevel) {
            ServerLevel $$12 = (ServerLevel)level;
            if (!$$0.isEmpty()) {
                try (ProblemReporter.ScopedCollector $$2 = new ProblemReporter.ScopedCollector(this.problemPath(), LOGGER);){
                    EntityType.create(TagValueInput.create($$2.forChild(() -> ".shoulder"), (HolderLookup.Provider)$$12.registryAccess(), $$0), $$12, EntitySpawnReason.LOAD).ifPresent($$1 -> {
                        if ($$1 instanceof TamableAnimal) {
                            TamableAnimal $$2 = (TamableAnimal)$$1;
                            $$2.setOwner(this);
                        }
                        $$1.setPos(this.getX(), this.getY() + (double)0.7f, this.getZ());
                        $$12.addWithUUID((Entity)$$1);
                    });
                }
            }
        }
    }

    @Nullable
    public abstract GameType gameMode();

    @Override
    public boolean isSpectator() {
        return this.gameMode() == GameType.SPECTATOR;
    }

    @Override
    public boolean canBeHitByProjectile() {
        return !this.isSpectator() && super.canBeHitByProjectile();
    }

    @Override
    public boolean isSwimming() {
        return !this.abilities.flying && !this.isSpectator() && super.isSwimming();
    }

    public boolean isCreative() {
        return this.gameMode() == GameType.CREATIVE;
    }

    @Override
    public boolean isPushedByFluid() {
        return !this.abilities.flying;
    }

    public Scoreboard getScoreboard() {
        return this.level().getScoreboard();
    }

    @Override
    public Component getDisplayName() {
        MutableComponent $$0 = PlayerTeam.formatNameForTeam(this.getTeam(), this.getName());
        return this.decorateDisplayNameComponent($$0);
    }

    private MutableComponent decorateDisplayNameComponent(MutableComponent $$0) {
        String $$12 = this.getGameProfile().getName();
        return $$0.withStyle($$1 -> $$1.withClickEvent(new ClickEvent.SuggestCommand("/tell " + $$12 + " ")).withHoverEvent(this.createHoverEvent()).withInsertion($$12));
    }

    @Override
    public String getScoreboardName() {
        return this.getGameProfile().getName();
    }

    @Override
    protected void internalSetAbsorptionAmount(float $$0) {
        this.getEntityData().set(DATA_PLAYER_ABSORPTION_ID, Float.valueOf($$0));
    }

    @Override
    public float getAbsorptionAmount() {
        return this.getEntityData().get(DATA_PLAYER_ABSORPTION_ID).floatValue();
    }

    public boolean isModelPartShown(PlayerModelPart $$0) {
        return (this.getEntityData().get(DATA_PLAYER_MODE_CUSTOMISATION) & $$0.getMask()) == $$0.getMask();
    }

    @Override
    public SlotAccess getSlot(int $$0) {
        if ($$0 == 499) {
            return new SlotAccess(){

                @Override
                public ItemStack get() {
                    return Player.this.containerMenu.getCarried();
                }

                @Override
                public boolean set(ItemStack $$0) {
                    Player.this.containerMenu.setCarried($$0);
                    return true;
                }
            };
        }
        final int $$1 = $$0 - 500;
        if ($$1 >= 0 && $$1 < 4) {
            return new SlotAccess(){

                @Override
                public ItemStack get() {
                    return Player.this.inventoryMenu.getCraftSlots().getItem($$1);
                }

                @Override
                public boolean set(ItemStack $$0) {
                    Player.this.inventoryMenu.getCraftSlots().setItem($$1, $$0);
                    Player.this.inventoryMenu.slotsChanged(Player.this.inventory);
                    return true;
                }
            };
        }
        if ($$0 >= 0 && $$0 < this.inventory.getNonEquipmentItems().size()) {
            return SlotAccess.forContainer(this.inventory, $$0);
        }
        int $$2 = $$0 - 200;
        if ($$2 >= 0 && $$2 < this.enderChestInventory.getContainerSize()) {
            return SlotAccess.forContainer(this.enderChestInventory, $$2);
        }
        return super.getSlot($$0);
    }

    public boolean isReducedDebugInfo() {
        return this.reducedDebugInfo;
    }

    public void setReducedDebugInfo(boolean $$0) {
        this.reducedDebugInfo = $$0;
    }

    @Override
    public void setRemainingFireTicks(int $$0) {
        super.setRemainingFireTicks(this.abilities.invulnerable ? Math.min($$0, 1) : $$0);
    }

    @Override
    public HumanoidArm getMainArm() {
        return this.entityData.get(DATA_PLAYER_MAIN_HAND) == 0 ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
    }

    public void setMainArm(HumanoidArm $$0) {
        this.entityData.set(DATA_PLAYER_MAIN_HAND, (byte)($$0 != HumanoidArm.LEFT ? 1 : 0));
    }

    public CompoundTag getShoulderEntityLeft() {
        return this.entityData.get(DATA_SHOULDER_LEFT);
    }

    protected void setShoulderEntityLeft(CompoundTag $$0) {
        this.entityData.set(DATA_SHOULDER_LEFT, $$0);
    }

    public CompoundTag getShoulderEntityRight() {
        return this.entityData.get(DATA_SHOULDER_RIGHT);
    }

    protected void setShoulderEntityRight(CompoundTag $$0) {
        this.entityData.set(DATA_SHOULDER_RIGHT, $$0);
    }

    public float getCurrentItemAttackStrengthDelay() {
        return (float)(1.0 / this.getAttributeValue(Attributes.ATTACK_SPEED) * 20.0);
    }

    public float getAttackStrengthScale(float $$0) {
        return Mth.clamp(((float)this.attackStrengthTicker + $$0) / this.getCurrentItemAttackStrengthDelay(), 0.0f, 1.0f);
    }

    public void resetAttackStrengthTicker() {
        this.attackStrengthTicker = 0;
    }

    public ItemCooldowns getCooldowns() {
        return this.cooldowns;
    }

    @Override
    protected float getBlockSpeedFactor() {
        return this.abilities.flying || this.isFallFlying() ? 1.0f : super.getBlockSpeedFactor();
    }

    @Override
    public float getLuck() {
        return (float)this.getAttributeValue(Attributes.LUCK);
    }

    public boolean canUseGameMasterBlocks() {
        return this.abilities.instabuild && this.getPermissionLevel() >= 2;
    }

    public int getPermissionLevel() {
        return 0;
    }

    public boolean hasPermissions(int $$0) {
        return this.getPermissionLevel() >= $$0;
    }

    @Override
    public EntityDimensions getDefaultDimensions(Pose $$0) {
        return POSES.getOrDefault((Object)$$0, STANDING_DIMENSIONS);
    }

    @Override
    public ImmutableList<Pose> getDismountPoses() {
        return ImmutableList.of(Pose.STANDING, Pose.CROUCHING, Pose.SWIMMING);
    }

    @Override
    public ItemStack getProjectile(ItemStack $$0) {
        if (!($$0.getItem() instanceof ProjectileWeaponItem)) {
            return ItemStack.EMPTY;
        }
        Predicate<ItemStack> $$1 = ((ProjectileWeaponItem)$$0.getItem()).getSupportedHeldProjectiles();
        ItemStack $$2 = ProjectileWeaponItem.getHeldProjectile(this, $$1);
        if (!$$2.isEmpty()) {
            return $$2;
        }
        $$1 = ((ProjectileWeaponItem)$$0.getItem()).getAllSupportedProjectiles();
        for (int $$3 = 0; $$3 < this.inventory.getContainerSize(); ++$$3) {
            ItemStack $$4 = this.inventory.getItem($$3);
            if (!$$1.test($$4)) continue;
            return $$4;
        }
        return this.hasInfiniteMaterials() ? new ItemStack(Items.ARROW) : ItemStack.EMPTY;
    }

    @Override
    public Vec3 getRopeHoldPosition(float $$0) {
        double $$1 = 0.22 * (this.getMainArm() == HumanoidArm.RIGHT ? -1.0 : 1.0);
        float $$2 = Mth.lerp($$0 * 0.5f, this.getXRot(), this.xRotO) * ((float)Math.PI / 180);
        float $$3 = Mth.lerp($$0, this.yBodyRotO, this.yBodyRot) * ((float)Math.PI / 180);
        if (this.isFallFlying() || this.isAutoSpinAttack()) {
            float $$11;
            Vec3 $$4 = this.getViewVector($$0);
            Vec3 $$5 = this.getDeltaMovement();
            double $$6 = $$5.horizontalDistanceSqr();
            double $$7 = $$4.horizontalDistanceSqr();
            if ($$6 > 0.0 && $$7 > 0.0) {
                double $$8 = ($$5.x * $$4.x + $$5.z * $$4.z) / Math.sqrt($$6 * $$7);
                double $$9 = $$5.x * $$4.z - $$5.z * $$4.x;
                float $$10 = (float)(Math.signum($$9) * Math.acos($$8));
            } else {
                $$11 = 0.0f;
            }
            return this.getPosition($$0).add(new Vec3($$1, -0.11, 0.85).zRot(-$$11).xRot(-$$2).yRot(-$$3));
        }
        if (this.isVisuallySwimming()) {
            return this.getPosition($$0).add(new Vec3($$1, 0.2, -0.15).xRot(-$$2).yRot(-$$3));
        }
        double $$12 = this.getBoundingBox().getYsize() - 1.0;
        double $$13 = this.isCrouching() ? -0.2 : 0.07;
        return this.getPosition($$0).add(new Vec3($$1, $$12, $$13).yRot(-$$3));
    }

    @Override
    public boolean isAlwaysTicking() {
        return true;
    }

    public boolean isScoping() {
        return this.isUsingItem() && this.getUseItem().is(Items.SPYGLASS);
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    public Optional<GlobalPos> getLastDeathLocation() {
        return this.lastDeathLocation;
    }

    public void setLastDeathLocation(Optional<GlobalPos> $$0) {
        this.lastDeathLocation = $$0;
    }

    @Override
    public float getHurtDir() {
        return this.hurtDir;
    }

    @Override
    public void animateHurt(float $$0) {
        super.animateHurt($$0);
        this.hurtDir = $$0;
    }

    @Override
    public boolean canSprint() {
        return true;
    }

    @Override
    protected float getFlyingSpeed() {
        if (this.abilities.flying && !this.isPassenger()) {
            return this.isSprinting() ? this.abilities.getFlyingSpeed() * 2.0f : this.abilities.getFlyingSpeed();
        }
        return this.isSprinting() ? 0.025999999f : 0.02f;
    }

    public boolean hasClientLoaded() {
        return this.clientLoaded || this.clientLoadedTimeoutTimer <= 0;
    }

    public void tickClientLoadTimeout() {
        if (!this.clientLoaded) {
            --this.clientLoadedTimeoutTimer;
        }
    }

    public void setClientLoaded(boolean $$0) {
        this.clientLoaded = $$0;
        if (!this.clientLoaded) {
            this.clientLoadedTimeoutTimer = 60;
        }
    }

    public double blockInteractionRange() {
        return this.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);
    }

    public double entityInteractionRange() {
        return this.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE);
    }

    public boolean canInteractWithEntity(Entity $$0, double $$1) {
        if ($$0.isRemoved()) {
            return false;
        }
        return this.canInteractWithEntity($$0.getBoundingBox(), $$1);
    }

    public boolean canInteractWithEntity(AABB $$0, double $$1) {
        double $$2 = this.entityInteractionRange() + $$1;
        return $$0.distanceToSqr(this.getEyePosition()) < $$2 * $$2;
    }

    public boolean canInteractWithBlock(BlockPos $$0, double $$1) {
        double $$2 = this.blockInteractionRange() + $$1;
        return new AABB($$0).distanceToSqr(this.getEyePosition()) < $$2 * $$2;
    }

    public void setIgnoreFallDamageFromCurrentImpulse(boolean $$0) {
        this.ignoreFallDamageFromCurrentImpulse = $$0;
        this.currentImpulseContextResetGraceTime = $$0 ? 40 : 0;
    }

    public boolean isIgnoringFallDamageFromCurrentImpulse() {
        return this.ignoreFallDamageFromCurrentImpulse;
    }

    public void tryResetCurrentImpulseContext() {
        if (this.currentImpulseContextResetGraceTime == 0) {
            this.resetCurrentImpulseContext();
        }
    }

    public void resetCurrentImpulseContext() {
        this.currentImpulseContextResetGraceTime = 0;
        this.currentExplosionCause = null;
        this.currentImpulseImpactPos = null;
        this.ignoreFallDamageFromCurrentImpulse = false;
    }

    public boolean shouldRotateWithMinecart() {
        return false;
    }

    @Override
    public boolean onClimbable() {
        if (this.abilities.flying) {
            return false;
        }
        return super.onClimbable();
    }

    public String debugInfo() {
        return MoreObjects.toStringHelper(this).add("name", this.getName().getString()).add("id", this.getId()).add("pos", this.position()).add("mode", this.gameMode()).add("permission", this.getPermissionLevel()).toString();
    }

    public static final class BedSleepingProblem
    extends Enum<BedSleepingProblem> {
        public static final /* enum */ BedSleepingProblem NOT_POSSIBLE_HERE = new BedSleepingProblem();
        public static final /* enum */ BedSleepingProblem NOT_POSSIBLE_NOW = new BedSleepingProblem(Component.translatable("block.minecraft.bed.no_sleep"));
        public static final /* enum */ BedSleepingProblem TOO_FAR_AWAY = new BedSleepingProblem(Component.translatable("block.minecraft.bed.too_far_away"));
        public static final /* enum */ BedSleepingProblem OBSTRUCTED = new BedSleepingProblem(Component.translatable("block.minecraft.bed.obstructed"));
        public static final /* enum */ BedSleepingProblem OTHER_PROBLEM = new BedSleepingProblem();
        public static final /* enum */ BedSleepingProblem NOT_SAFE = new BedSleepingProblem(Component.translatable("block.minecraft.bed.not_safe"));
        @Nullable
        private final Component message;
        private static final /* synthetic */ BedSleepingProblem[] $VALUES;

        public static BedSleepingProblem[] values() {
            return (BedSleepingProblem[])$VALUES.clone();
        }

        public static BedSleepingProblem valueOf(String $$0) {
            return Enum.valueOf(BedSleepingProblem.class, $$0);
        }

        private BedSleepingProblem() {
            this.message = null;
        }

        private BedSleepingProblem(Component $$0) {
            this.message = $$0;
        }

        @Nullable
        public Component getMessage() {
            return this.message;
        }

        private static /* synthetic */ BedSleepingProblem[] b() {
            return new BedSleepingProblem[]{NOT_POSSIBLE_HERE, NOT_POSSIBLE_NOW, TOO_FAR_AWAY, OBSTRUCTED, OTHER_PROBLEM, NOT_SAFE};
        }

        static {
            $VALUES = BedSleepingProblem.b();
        }
    }
}

