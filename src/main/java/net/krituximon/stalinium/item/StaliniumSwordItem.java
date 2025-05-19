package net.krituximon.stalinium.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;

import java.util.List;

public class StaliniumSwordItem extends SwordItem {
    public StaliniumSwordItem(Tier tier, Properties props) {
        super(tier, props);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        if (!attacker.getCommandSenderWorld().isClientSide) {
            MobEffectInstance strength = new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, 0, false, true);
            attacker.addEffect(strength);
            Level world = attacker.getCommandSenderWorld();
            var box = attacker.getBoundingBox().inflate(5.0);
            List<Player> nearby = world.getEntitiesOfClass(Player.class, box, p -> p instanceof ServerPlayer);
            for (Player p : nearby) {
                p.addEffect(strength);
            }
        }
        return result;
    }
}
