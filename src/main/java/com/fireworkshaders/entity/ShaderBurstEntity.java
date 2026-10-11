package com.fireworkshaders.entity;

import com.fireworkshaders.client.FireworkScreenFx;
import com.fireworkshaders.rocket.RocketDesign;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

/** Shader-rendered burst. Design attributes are synced for the client renderer. */
public class ShaderBurstEntity extends Entity {
    private static final EntityDataAccessor<Integer> COLOR =
            SynchedEntityData.defineId(ShaderBurstEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SHAPE =
            SynchedEntityData.defineId(ShaderBurstEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> MODIFIER_MASK =
            SynchedEntityData.defineId(ShaderBurstEntity.class, EntityDataSerializers.INT);

    /** Duration of the primary shader burst, in ticks. */
    public static final int DURATION = 36;
    private int life;

    public ShaderBurstEntity(EntityType<ShaderBurstEntity> type, Level level) {
        super(type, level);
    }

    public void setColor(int color) {
        this.entityData.set(COLOR, color & 0xFFFFFF);
    }

    public int getColor() {
        return this.entityData.get(COLOR);
    }

    public RocketDesign.Shape getShape() {
        return RocketDesign.Shape.byIndex(this.entityData.get(SHAPE));
    }

    public int getModifierMask() {
        return this.entityData.get(MODIFIER_MASK);
    }

    public void setDesign(RocketDesign design) {
        this.setColor(design.color());
        this.entityData.set(SHAPE, design.shape().ordinal());
        this.entityData.set(MODIFIER_MASK, design.modifierMask());
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(COLOR, 0xFFD34D);
        this.entityData.define(SHAPE, RocketDesign.Shape.SPHERE.ordinal());
        this.entityData.define(MODIFIER_MASK, 0);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.life = tag.getInt("Life");
        this.setColor(tag.getInt("Color"));
        this.entityData.set(SHAPE, RocketDesign.Shape.byIndex(tag.getInt("Shape")).ordinal());
        this.entityData.set(MODIFIER_MASK, tag.getInt("ModifierMask") & 0x7);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Life", this.life);
        tag.putInt("Color", this.getColor());
        tag.putInt("Shape", this.entityData.get(SHAPE));
        tag.putInt("ModifierMask", this.getModifierMask());
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void tick() {
        super.tick();
        this.life++;
        if (this.level().isClientSide && this.life == 1) {
            FireworkScreenFx.trigger();
        }
        if (this.life >= DURATION) this.discard();
    }
}
