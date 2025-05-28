package net.krituximon.stalinium.item;

import net.krituximon.stalinium.Stalinium;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class StaliniumSwordItem extends SwordItem {
    private static final double RANGE = 6.0;
    private static final double HALF_ANGLE = Math.toRadians(30);
    private static final double COS_HALF_ANG = Math.cos(HALF_ANGLE);
    private static final int DURATION = 5 * 20;
    private static final int AMP = 0;
    private static final int COOLDOWN = 30 * 20;

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
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }
        if (!world.isClientSide) {
            Holder<MobEffect> chargeHolder = world
                    .registryAccess()
                    .registryOrThrow(Registries.MOB_EFFECT)
                    .getHolderOrThrow(
                            ResourceKey.create(
                                    Registries.MOB_EFFECT,
                                    ResourceLocation.fromNamespaceAndPath(Stalinium.MODID, "stalinium_charge")
                            )
                    );
            player.addEffect(new MobEffectInstance(chargeHolder, DURATION, AMP, false, true));
            AABB area = player.getBoundingBox().inflate(RANGE);
            Vec3 look = player.getLookAngle();
            List<Player> allies = world.getEntitiesOfClass(
                    Player.class, area,
                    p -> p instanceof ServerPlayer && player.isAlliedTo(p)
            );
            for (Player ally : allies) {
                Vec3 toAlly = ally.position().subtract(player.position()).normalize();
                if (toAlly.dot(look) >= COS_HALF_ANG) {
                    ally.addEffect(new MobEffectInstance(chargeHolder, DURATION, AMP, false, true));
                }
            }
            player.getCooldowns().addCooldown(this, COOLDOWN);
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
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if(Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("item.stalinium_sword.tooltip_shift"));
        } else {
            tooltipComponents.add(Component.translatable("item.stalinium_sword.tooltip"));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
