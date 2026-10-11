package com.fireworkshaders.entity;

import com.fireworkshaders.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

/**
 * Cohete que sube con leve bamboleo y explota al terminar la mecha
 * (o al chocar). La explosión spawnea ShaderBurstEntity. Sin partículas.
 */
public class ShaderRocketEntity extends Entity {
    private static final EntityDataAccessor<Integer> COLOR =
            SynchedEntityData.defineId(ShaderRocketEntity.class, EntityDataSerializers.INT);

    /** Paleta de la explosión: se elige un color al lanzar. */
    private static final int[] PALETTE = {
            0xFFD34D, // dorado
            0x7DF9C6, // menta
            0xC77DFF, // malva
            0x6DC6FF, // celeste
            0xFF6D6D, // coral
            0xFFF3B0, // crema
    };

    private static final int FUSE = 32; // ticks antes de explotar

    private int life = 0;

    public ShaderRocketEntity(EntityType<ShaderRocketEntity> type, Level level) {
        super(type, level);
    }

    public static int randomColor(RandomSource random) {
        return PALETTE[random.nextInt(PALETTE.length)];
    }

    public void setColor(int color) {
        this.entityData.set(COLOR, color);
    }

    public int getColor() {
        return this.entityData.get(COLOR);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(COLOR, PALETTE[0]);
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

        // Bamboleo leve mientras sube.
        double wobble = Math.sin(this.life * 0.55D) * 0.02D;
        this.setDeltaMovement(
                this.getDeltaMovement().x + wobble,
                this.getDeltaMovement().y,
                this.getDeltaMovement().z - wobble);
        this.move(MoverType.SELF, this.getDeltaMovement());

        if (!this.level().isClientSide) {
            if (this.life >= FUSE || this.horizontalCollision || this.verticalCollision) {
                this.explode();
            } else if (this.life > FUSE + 100) {
                this.discard(); // seguridad: nunca debe vivir tanto
            }
        }
    }

    private void explode() {
        ShaderBurstEntity burst = new ShaderBurstEntity(ModEntities.SHADER_BURST.get(), this.level());
        burst.setPos(this.getX(), this.getY(), this.getZ());
        burst.setColor(this.getColor());
        this.level().addFreshEntity(burst);
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.PLAYERS,
                1.2F, 0.9F + this.level().random.nextFloat() * 0.2F);
        this.discard();
    }
}
