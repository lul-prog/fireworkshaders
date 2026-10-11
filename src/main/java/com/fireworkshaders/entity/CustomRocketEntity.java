package com.fireworkshaders.entity;

import com.fireworkshaders.ModEntities;
import com.fireworkshaders.rocket.RocketDesign;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

/**
 * Networked custom rocket projectile. The server owns its fuse and detonation;
 * clients receive the design fields for the shader renderer and burst effect.
 */
public class CustomRocketEntity extends Entity {
    private static final EntityDataAccessor<Integer> COLOR =
            SynchedEntityData.defineId(CustomRocketEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SHAPE =
            SynchedEntityData.defineId(CustomRocketEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> MODIFIER_MASK =
            SynchedEntityData.defineId(CustomRocketEntity.class, EntityDataSerializers.INT);

    private static final int ASCENT_TICKS = 24;
    private static final int FUSE_TICKS = 50;
    private int flightTicks;
    private String rocketName = RocketDesign.DEFAULT_NAME;

    public CustomRocketEntity(EntityType<CustomRocketEntity> type, Level level) {
        super(type, level);
    }

    public void setDesign(RocketDesign design) {
        this.entityData.set(COLOR, design.color());
        this.entityData.set(SHAPE, design.shape().ordinal());
        this.entityData.set(MODIFIER_MASK, design.modifierMask());
        this.rocketName = design.name();
    }

    public RocketDesign getDesign() {
        RocketDesign.Shape shape = RocketDesign.Shape.byIndex(this.entityData.get(SHAPE));
        int modifierMask = this.entityData.get(MODIFIER_MASK);
        return new RocketDesign(this.entityData.get(COLOR), shape, modifierMask, this.rocketName);
    }

    public int getColor() {
        return this.entityData.get(COLOR);
    }

    public int getShapeIndex() {
        return this.entityData.get(SHAPE);
    }

    public int getModifierMask() {
        return this.entityData.get(MODIFIER_MASK);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(COLOR, 0xFFD34D);
        this.entityData.define(SHAPE, RocketDesign.Shape.SPHERE.ordinal());
        this.entityData.define(MODIFIER_MASK, 0);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.flightTicks = tag.getInt("FlightTicks");
        this.entityData.set(COLOR, tag.getInt("Color") & 0xFFFFFF);
        this.entityData.set(SHAPE, RocketDesign.Shape.byIndex(tag.getInt("Shape")).ordinal());
        this.entityData.set(MODIFIER_MASK, tag.getInt("ModifierMask") & 0x7);
        this.rocketName = RocketDesign.sanitizeName(tag.getString("RocketName"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("FlightTicks", this.flightTicks);
        tag.putInt("Color", this.getColor());
        tag.putInt("Shape", this.getShapeIndex());
        tag.putInt("ModifierMask", this.getModifierMask());
        tag.putString("RocketName", this.rocketName);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void tick() {
        super.tick();
        this.flightTicks++;

        Vec3 motion = this.getDeltaMovement();
        if (this.flightTicks <= ASCENT_TICKS) {
            // Sustained lift eases toward a terminal climb speed instead of a straight ballistic throw.
            double lift = motion.y < 0.78D ? 0.026D : 0.0D;
            motion = motion.add(0.0D, lift, 0.0D);
        } else {
            motion = motion.add(0.0D, -0.045D, 0.0D);
        }

        // Low-amplitude deterministic crosswind; identical on both sides and stable under latency.
        double phase = (this.level().getGameTime() + this.getId() * 19L) * 0.012D;
        double windX = Math.sin(phase + this.getZ() * 0.07D) * 0.0018D;
        double windZ = Math.cos(phase + this.getX() * 0.07D) * 0.0018D;
        motion = motion.add(windX, 0.0D, windZ).multiply(0.985D, 0.992D, 0.985D);
        this.setDeltaMovement(motion);
        this.move(MoverType.SELF, motion);

        if (!this.level().isClientSide
                && (this.flightTicks >= FUSE_TICKS || this.horizontalCollision || this.verticalCollision)) {
            this.detonate();
        }
    }

    private void detonate() {
        if (this.isRemoved()) return;
        ShaderBurstEntity burst = new ShaderBurstEntity(ModEntities.SHADER_BURST.get(), this.level());
        burst.setPos(this.getX(), this.getY(), this.getZ());
        burst.setDesign(this.getDesign());
        this.level().addFreshEntity(burst);
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.BLOCKS,
                1.15F, 0.88F + this.level().random.nextFloat() * 0.24F);
        this.discard();
    }
}
