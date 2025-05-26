package net.krituximon.stalinium.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class StaliniumSwordItem extends SwordItem {
    public StaliniumSwordItem(Tier tier, Properties props) {
        super(tier, props);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        Level world = attacker.getCommandSenderWorld();
        if (!world.isClientSide && attacker instanceof Player player) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, 0, false, true));
            AABB box = player.getBoundingBox().inflate(5.0);
            List<Player> allies = world.getEntitiesOfClass(
                    Player.class, box,
                    p -> p instanceof ServerPlayer && player.isAlliedTo(p)
            );
            MobEffectInstance allyBuff = new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, 1, false, true);
            for (Player ally : allies) {
                ally.addEffect(allyBuff);
            }
        }
        return super.hurtEnemy(stack, target, attacker);
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