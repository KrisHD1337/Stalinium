package net.krituximon.stalinium.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class StaliniumSwordItem extends SwordItem {
    private static final double RANGE = 6.0;
    private static final double HALF_ANGLE = Math.toRadians(30);
    private static final double COS_HALF_ANGLE = Math.cos(HALF_ANGLE);
    private static final int DURATION = 5 * 20;
    private static final int SPEED_AMP = 1;
    private static final int RESIST_AMP = 0;

    public StaliniumSwordItem(Tier tier, Properties props) {
        super(tier, props);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        Level world = attacker.getCommandSenderWorld();
        if (!world.isClientSide && attacker instanceof Player player) {
            AABB box = player.getBoundingBox().inflate(5.0);
            List<Player> allies = world.getEntitiesOfClass(
                    Player.class, box,
                    p -> p instanceof ServerPlayer && player.isAlliedTo(p)
            );
            MobEffectInstance allyBuff = new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, 0, false, true);
            for (Player ally : allies) {
                ally.addEffect(allyBuff);
            }
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }
        if (!world.isClientSide) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,    DURATION, SPEED_AMP, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, DURATION, RESIST_AMP, false, true));

            AABB area = player.getBoundingBox().inflate(RANGE);
            List<Player> allies = world.getEntitiesOfClass(
                    Player.class, area,
                    p -> p instanceof ServerPlayer && player.isAlliedTo(p)
            );
            Vec3 look = player.getLookAngle();
            for (Player ally : allies) {
                Vec3 toAlly = ally.position().subtract(player.position()).normalize();
                if (toAlly.dot(look) >= COS_HALF_ANGLE) {
                    ally.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,    DURATION, SPEED_AMP, false, true));
                    ally.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, DURATION, RESIST_AMP, false, true));
                }
            }
            player.getCooldowns().addCooldown(this, 20 * 20);
        }
        player.startUsingItem(hand);
        player.swing(hand, true);
        return InteractionResultHolder.sidedSuccess(stack, world.isClientSide);
    }


    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }


    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isDamaged(ItemStack stack) {
        return false;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return 2;
    }
}