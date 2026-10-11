package com.fireworkshaders.entity;

import com.fireworkshaders.client.FireworkScreenFx;
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

/**
 * La explosión: una sola entidad cuya "esfera" la dibuja
 * ShaderBurstRenderer con el shader fireworkshaders:burst.
 * En su primer tick de cliente dispara el efecto de pantalla.
 */
public class ShaderBurstEntity extends Entity {
    private static final EntityDataAccessor<Integer> COLOR =
            SynchedEntityData.defineId(ShaderBurstEntity.class, EntityDataSerializers.INT);

    /** Duración visible de la explosión, en ticks. */
    public static final int DURATION = 28;

    private int life = 0;

    public ShaderBurstEntity(EntityType<ShaderBurstEntity> type, Level level) {
        super(type, level);
    }

    public void setColor(int color) {
        this.entityData.set(COLOR, color);
    }

    public int getColor() {
        return this.entityData.get(COLOR);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(COLOR, 0xFFD34D);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.life = tag.getInt("Life");
        this.entityData.set(COLOR, tag.getInt("Color"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Life", this.life);
        tag.putInt("Color", this.getColor());
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
        if (this.life >= DURATION) {
            this.discard();
        }
    }
}
