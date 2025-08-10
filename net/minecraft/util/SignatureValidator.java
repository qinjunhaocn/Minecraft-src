/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.yggdrasil.ServicesKeySet
 *  com.mojang.authlib.yggdrasil.ServicesKeyType
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.util;

import com.mojang.authlib.yggdrasil.ServicesKeySet;
import com.mojang.authlib.yggdrasil.ServicesKeyType;
import com.mojang.logging.LogUtils;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.util.SignatureUpdater;
import org.slf4j.Logger;

public interface SignatureValidator {
    public static final SignatureValidator NO_VALIDATION = ($$0, $$1) -> true;
    public static final Logger LOGGER = LogUtils.getLogger();

    public boolean validate(SignatureUpdater var1, byte[] var2);

    default public boolean a(byte[] $$0, byte[] $$12) {
        return this.validate($$1 -> $$1.update($$0), $$12);
    }

    private static boolean a(SignatureUpdater $$0, byte[] $$1, Signature $$2) throws SignatureException {
        $$0.update($$2::update);
        return $$2.verify($$1);
    }

    public static SignatureValidator from(PublicKey $$0, String $$1) {
        return ($$2, $$3) -> {
            try {
                Signature $$4 = Signature.getInstance($$1);
                $$4.initVerify($$0);
                return SignatureValidator.a($$2, $$3, $$4);
            } catch (Exception $$5) {
                LOGGER.error("Failed to verify signature", $$5);
                return false;
            }
        };
    }

    @Nullable
    public static SignatureValidator from(ServicesKeySet $$0, ServicesKeyType $$12) {
        Collection $$2 = $$0.keys($$12);
        if ($$2.isEmpty()) {
            return null;
        }
        return ($$1, $$22) -> $$2.stream().anyMatch($$2 -> {
            Signature $$3 = $$2.signature();
            try {
                return SignatureValidator.a($$1, $$22, $$3);
            } catch (SignatureException $$4) {
                LOGGER.error("Failed to verify Services signature", $$4);
                return false;
            }
        });
    }
}

