package com.fireworkshaders.item;

import com.fireworkshaders.ModEntities;
import com.fireworkshaders.entity.ShaderRocketEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Cohete de shaders: clic derecho lo lanza. Al terminar la mecha explota
 * en una esfera con shader propio + efecto de pantalla. Sin partículas.
 */
public class ShaderRocketItem extends Item {
    public ShaderRocketItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            ShaderRocketEntity rocket = new ShaderRocketEntity(ModEntities.SHADER_ROCKET.get(), level);
            Vec3 look = player.getLookAngle();
            rocket.setPos(player.getX() + look.x * 0.5D,
                    player.getEyeY() - 0.1D,
                    player.getZ() + look.z * 0.5D);
            rocket.setDeltaMovement(look.x * 0.6D, 1.35D, look.z * 0.6D);
            rocket.setColor(ShaderRocketEntity.randomColor(level.random));
            level.addFreshEntity(rocket);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.PLAYERS, 1.0F, 1.0F);
            player.getCooldowns().addCooldown(this, 12);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
