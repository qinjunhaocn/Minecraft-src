/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  org.joml.Matrix4fc
 *  org.joml.Vector2f
 *  org.joml.Vector2fc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector3i
 *  org.joml.Vector3ic
 *  org.joml.Vector4f
 *  org.joml.Vector4fc
 */
package net.minecraft.client.renderer;

import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import org.joml.Matrix4fc;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.joml.Vector4f;
import org.joml.Vector4fc;

public interface UniformValue {
    public static final Codec<UniformValue> CODEC = Type.CODEC.dispatch(UniformValue::type, $$0 -> $$0.valueCodec);

    public void writeTo(Std140Builder var1);

    public void addSize(Std140SizeCalculator var1);

    public Type type();

    public static final class Type
    extends Enum<Type>
    implements StringRepresentable {
        public static final /* enum */ Type INT = new Type("int", IntUniform.CODEC);
        public static final /* enum */ Type IVEC3 = new Type("ivec3", IVec3Uniform.CODEC);
        public static final /* enum */ Type FLOAT = new Type("float", FloatUniform.CODEC);
        public static final /* enum */ Type VEC2 = new Type("vec2", Vec2Uniform.CODEC);
        public static final /* enum */ Type VEC3 = new Type("vec3", Vec3Uniform.CODEC);
        public static final /* enum */ Type VEC4 = new Type("vec4", Vec4Uniform.CODEC);
        public static final /* enum */ Type MATRIX4X4 = new Type("matrix4x4", Matrix4x4Uniform.CODEC);
        public static final StringRepresentable.EnumCodec<Type> CODEC;
        private final String name;
        final MapCodec<? extends UniformValue> valueCodec;
        private static final /* synthetic */ Type[] $VALUES;

        public static Type[] values() {
            return (Type[])$VALUES.clone();
        }

        public static Type valueOf(String $$0) {
            return Enum.valueOf(Type.class, $$0);
        }

        private Type(String $$0, Codec<? extends UniformValue> $$1) {
            this.name = $$0;
            this.valueCodec = $$1.fieldOf("value");
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        private static /* synthetic */ Type[] a() {
            return new Type[]{INT, IVEC3, FLOAT, VEC2, VEC3, VEC4, MATRIX4X4};
        }

        static {
            $VALUES = Type.a();
            CODEC = StringRepresentable.fromEnum(Type::values);
        }
    }

    public record Matrix4x4Uniform(Matrix4fc value) implements UniformValue
    {
        public static final Codec<Matrix4x4Uniform> CODEC = ExtraCodecs.MATRIX4F.xmap(Matrix4x4Uniform::new, Matrix4x4Uniform::value);

        @Override
        public void writeTo(Std140Builder $$0) {
            $$0.putMat4f(this.value);
        }

        @Override
        public void addSize(Std140SizeCalculator $$0) {
            $$0.putMat4f();
        }

        @Override
        public Type type() {
            return Type.MATRIX4X4;
        }
    }

    public record Vec4Uniform(Vector4f value) implements UniformValue
    {
        public static final Codec<Vec4Uniform> CODEC = ExtraCodecs.VECTOR4F.xmap(Vec4Uniform::new, Vec4Uniform::value);

        @Override
        public void writeTo(Std140Builder $$0) {
            $$0.putVec4((Vector4fc)this.value);
        }

        @Override
        public void addSize(Std140SizeCalculator $$0) {
            $$0.putVec4();
        }

        @Override
        public Type type() {
            return Type.VEC4;
        }
    }

    public record Vec3Uniform(Vector3f value) implements UniformValue
    {
        public static final Codec<Vec3Uniform> CODEC = ExtraCodecs.VECTOR3F.xmap(Vec3Uniform::new, Vec3Uniform::value);

        @Override
        public void writeTo(Std140Builder $$0) {
            $$0.putVec3((Vector3fc)this.value);
        }

        @Override
        public void addSize(Std140SizeCalculator $$0) {
            $$0.putVec3();
        }

        @Override
        public Type type() {
            return Type.VEC3;
        }
    }

    public record Vec2Uniform(Vector2f value) implements UniformValue
    {
        public static final Codec<Vec2Uniform> CODEC = ExtraCodecs.VECTOR2F.xmap(Vec2Uniform::new, Vec2Uniform::value);

        @Override
        public void writeTo(Std140Builder $$0) {
            $$0.putVec2((Vector2fc)this.value);
        }

        @Override
        public void addSize(Std140SizeCalculator $$0) {
            $$0.putVec2();
        }

        @Override
        public Type type() {
            return Type.VEC2;
        }
    }

    public record FloatUniform(float value) implements UniformValue
    {
        public static final Codec<FloatUniform> CODEC = Codec.FLOAT.xmap(FloatUniform::new, FloatUniform::value);

        @Override
        public void writeTo(Std140Builder $$0) {
            $$0.putFloat(this.value);
        }

        @Override
        public void addSize(Std140SizeCalculator $$0) {
            $$0.putFloat();
        }

        @Override
        public Type type() {
            return Type.FLOAT;
        }
    }

    public record IVec3Uniform(Vector3i value) implements UniformValue
    {
        public static final Codec<IVec3Uniform> CODEC = ExtraCodecs.VECTOR3I.xmap(IVec3Uniform::new, IVec3Uniform::value);

        @Override
        public void writeTo(Std140Builder $$0) {
            $$0.putIVec3((Vector3ic)this.value);
        }

        @Override
        public void addSize(Std140SizeCalculator $$0) {
            $$0.putIVec3();
        }

        @Override
        public Type type() {
            return Type.IVEC3;
        }
    }

    public record IntUniform(int value) implements UniformValue
    {
        public static final Codec<IntUniform> CODEC = Codec.INT.xmap(IntUniform::new, IntUniform::value);

        @Override
        public void writeTo(Std140Builder $$0) {
            $$0.putInt(this.value);
        }

        @Override
        public void addSize(Std140SizeCalculator $$0) {
            $$0.putInt();
        }

        @Override
        public Type type() {
            return Type.INT;
        }
    }
}

