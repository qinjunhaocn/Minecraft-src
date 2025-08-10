/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 */
package net.minecraft.client.telemetry.events;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.telemetry.TelemetryEventSender;
import net.minecraft.client.telemetry.TelemetryEventType;
import net.minecraft.client.telemetry.TelemetryProperty;
import net.minecraft.client.telemetry.TelemetryPropertyMap;
import net.minecraft.world.level.GameType;

public class WorldLoadEvent {
    private boolean eventSent;
    @Nullable
    private TelemetryProperty.GameMode gameMode;
    @Nullable
    private String serverBrand;
    @Nullable
    private final String minigameName;

    public WorldLoadEvent(@Nullable String $$0) {
        this.minigameName = $$0;
    }

    public void addProperties(TelemetryPropertyMap.Builder $$0) {
        if (this.serverBrand != null) {
            $$0.put(TelemetryProperty.SERVER_MODDED, !this.serverBrand.equals("vanilla"));
        }
        $$0.put(TelemetryProperty.SERVER_TYPE, this.getServerType());
    }

    private TelemetryProperty.ServerType getServerType() {
        ServerData $$0 = Minecraft.getInstance().getCurrentServer();
        if ($$0 != null && $$0.isRealm()) {
            return TelemetryProperty.ServerType.REALM;
        }
        if (Minecraft.getInstance().hasSingleplayerServer()) {
            return TelemetryProperty.ServerType.LOCAL;
        }
        return TelemetryProperty.ServerType.OTHER;
    }

    public boolean send(TelemetryEventSender $$02) {
        if (this.eventSent || this.gameMode == null || this.serverBrand == null) {
            return false;
        }
        this.eventSent = true;
        $$02.send(TelemetryEventType.WORLD_LOADED, $$0 -> {
            $$0.put(TelemetryProperty.GAME_MODE, this.gameMode);
            if (this.minigameName != null) {
                $$0.put(TelemetryProperty.REALMS_MAP_CONTENT, this.minigameName);
            }
        });
        return true;
    }

    public void setGameMode(GameType $$0, boolean $$1) {
        this.gameMode = switch ($$0) {
            default -> throw new MatchException(null, null);
            case GameType.SURVIVAL -> {
                if ($$1) {
                    yield TelemetryProperty.GameMode.HARDCORE;
                }
                yield TelemetryProperty.GameMode.SURVIVAL;
            }
            case GameType.CREATIVE -> TelemetryProperty.GameMode.CREATIVE;
            case GameType.ADVENTURE -> TelemetryProperty.GameMode.ADVENTURE;
            case GameType.SPECTATOR -> TelemetryProperty.GameMode.SPECTATOR;
        };
    }

    public void setServerBrand(String $$0) {
        this.serverBrand = $$0;
    }
}

