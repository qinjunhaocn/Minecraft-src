/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 */
package net.minecraft.client.player;

import com.mojang.authlib.GameProfile;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractClientPlayer
extends Player {
    @Nullable
    private PlayerInfo playerInfo;
    protected Vec3 deltaMovementOnPreviousTick = Vec3.ZERO;
    public float elytraRotX;
    public float elytraRotY;
    public float elytraRotZ;
    public final ClientLevel clientLevel;
    public float walkDistO;
    public float walkDist;

    public AbstractClientPlayer(ClientLevel $$0, GameProfile $$1) {
        super($$0, $$1);
        this.clientLevel = $$0;
    }

    @Override
    @Nullable
    public GameType gameMode() {
        PlayerInfo $$0 = this.getPlayerInfo();
        return $$0 != null ? $$0.getGameMode() : null;
    }

    @Nullable
    protected PlayerInfo getPlayerInfo() {
        if (this.playerInfo == null) {
            this.playerInfo = Minecraft.getInstance().getConnection().getPlayerInfo(this.getUUID());
        }
        return this.playerInfo;
    }

    @Override
    public void tick() {
        this.walkDistO = this.walkDist;
        this.deltaMovementOnPreviousTick = this.getDeltaMovement();
        super.tick();
    }

    public Vec3 getDeltaMovementLerped(float $$0) {
        return this.deltaMovementOnPreviousTick.lerp(this.getDeltaMovement(), $$0);
    }

    public PlayerSkin getSkin() {
        PlayerInfo $$0 = this.getPlayerInfo();
        return $$0 == null ? DefaultPlayerSkin.get(this.getUUID()) : $$0.getSkin();
    }

    public float getFieldOfViewModifier(boolean $$0, float $$1) {
        float $$3;
        float $$2 = 1.0f;
        if (this.getAbilities().flying) {
            $$2 *= 1.1f;
        }
        if (($$3 = this.getAbilities().getWalkingSpeed()) != 0.0f) {
            float $$4 = (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED) / $$3;
            $$2 *= ($$4 + 1.0f) / 2.0f;
        }
        if (this.isUsingItem()) {
            if (this.getUseItem().is(Items.BOW)) {
                float $$5 = Math.min((float)this.getTicksUsingItem() / 20.0f, 1.0f);
                $$2 *= 1.0f - Mth.square($$5) * 0.15f;
            } else if ($$0 && this.isScoping()) {
                return 0.1f;
            }
        }
        return Mth.lerp($$1, 1.0f, $$2);
    }
}

