/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.client;

import com.mojang.serialization.Codec;
import net.minecraft.util.OptionEnum;
import net.minecraft.util.StringRepresentable;

public final class CloudStatus
extends Enum<CloudStatus>
implements OptionEnum,
StringRepresentable {
    public static final /* enum */ CloudStatus OFF = new CloudStatus(0, "false", "options.off");
    public static final /* enum */ CloudStatus FAST = new CloudStatus(1, "fast", "options.clouds.fast");
    public static final /* enum */ CloudStatus FANCY = new CloudStatus(2, "true", "options.clouds.fancy");
    public static final Codec<CloudStatus> CODEC;
    private final int id;
    private final String legacyName;
    private final String key;
    private static final /* synthetic */ CloudStatus[] $VALUES;

    public static CloudStatus[] values() {
        return (CloudStatus[])$VALUES.clone();
    }

    public static CloudStatus valueOf(String $$0) {
        return Enum.valueOf(CloudStatus.class, $$0);
    }

    private CloudStatus(int $$0, String $$1, String $$2) {
        this.id = $$0;
        this.legacyName = $$1;
        this.key = $$2;
    }

    @Override
    public String getSerializedName() {
        return this.legacyName;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    private static /* synthetic */ CloudStatus[] e() {
        return new CloudStatus[]{OFF, FAST, FANCY};
    }

    static {
        $VALUES = CloudStatus.e();
        CODEC = StringRepresentable.fromEnum(CloudStatus::values);
    }
}

