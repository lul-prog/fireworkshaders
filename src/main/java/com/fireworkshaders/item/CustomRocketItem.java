package com.fireworkshaders.item;

import com.fireworkshaders.ModEntities;
import com.fireworkshaders.entity.CustomRocketEntity;
import com.fireworkshaders.rocket.RocketDesign;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** Fires the validated design stored by the Pyrotechnic Workstation. */
public class CustomRocketItem extends Item {
    public CustomRocketItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        RocketDesign design = RocketDesign.readFromStack(stack).orElseGet(RocketDesign::defaultDesign);

        if (!level.isClientSide) {
            Vec3 look = player.getLookAngle();
            CustomRocketEntity rocket = new CustomRocketEntity(ModEntities.CUSTOM_ROCKET.get(), level);
            rocket.setPos(player.getX() + look.x * 0.5D,
                    player.getEyeY() - 0.1D,
                    player.getZ() + look.z * 0.5D);
            rocket.setDeltaMovement(look.x * 0.22D, 0.42D + look.y * 0.18D, look.z * 0.22D);
            rocket.setDesign(design);
            level.addFreshEntity(rocket);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.PLAYERS, 0.9F, 0.92F);
            player.getCooldowns().addCooldown(this, 10);
            if (!player.getAbilities().instabuild) stack.shrink(1);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
