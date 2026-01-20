package net.krituximon.stalinium.item.custom;

import net.krituximon.stalinium.Stalinium;
import net.krituximon.stalinium.event.ComradeHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class StaliniumSwordItem extends SwordItem {
    private static final double RANGE = 6.0;
    private static final double HALF_ANGLE = Math.toRadians(30);
    private static final double COS_HALF_ANG = Math.cos(HALF_ANGLE);
    private static final int DURATION = 5 * 20;
    private static final int AMP = 0;
    private static final int COOLDOWN = 30 * 20;

    public StaliniumSwordItem(ToolMaterial tier, float attackDamage, float attackSpeed, Properties props) {
        super(tier, attackDamage, attackSpeed, props);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack,
                             LivingEntity target,
                             LivingEntity attacker) {
        Level world = attacker.level();
        if (!world.isClientSide && attacker instanceof Player player) {
            Optional<ComradeHandler.Party> partyOpt = ComradeHandler.findPartyOf(player.getUUID());
            if (partyOpt.isPresent()) {
                List<Player> allies = new ArrayList<>();
                ServerPlayer serverPlayer = (ServerPlayer) player;
                for (UUID memberUuid : partyOpt.get().members) {
                    if (memberUuid.equals(player.getUUID())) continue;
                    ServerPlayer online = serverPlayer.level()
                            .getServer()
                            .getPlayerList()
                            .getPlayer(memberUuid);
                    if (online != null && online.distanceTo(player) <= 5.0) {
                        allies.add(online);
                    }
                }
                MobEffectInstance allyStrength = new MobEffectInstance(
                        MobEffects.DAMAGE_BOOST, 100, 0, false, true
                );
                for (Player ally : allies) {
                    ally.addEffect(allyStrength);
                }
            }
        }
        return true;
    }

    @Override
    public InteractionResult use(Level world,
                                 Player player,
                                 InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResult.FAIL;
        }
        if (!world.isClientSide) {
            Holder<MobEffect> chargeHolder = world
                    .registryAccess()
                    .registryOrThrow(Registries.MOB_EFFECT)
                    .getHolderOrThrow(
                            ResourceKey.create(
                                    Registries.MOB_EFFECT,
                                    ResourceLocation.fromNamespaceAndPath(
                                            Stalinium.MODID, "stalinium_charge"
                                    )
                            )
                    );
            player.addEffect(new MobEffectInstance(chargeHolder, DURATION, AMP, false, true));
            AABB area = player.getBoundingBox().inflate(RANGE);
            Vec3 look = player.getLookAngle();
            Optional<ComradeHandler.Party> partyOpt = ComradeHandler.findPartyOf(player.getUUID());
            if (partyOpt.isPresent()) {
                List<Player> allies = new ArrayList<>();
                ServerPlayer serverPlayer = (ServerPlayer) player;
                for (UUID memberUuid : partyOpt.get().members) {
                    if (memberUuid.equals(player.getUUID())) continue;
                    ServerPlayer online = serverPlayer.level()
                            .getServer()
                            .getPlayerList()
                            .getPlayer(memberUuid);
                    if (online != null) {
                        Vec3 toAlly = online.position().subtract(player.position()).normalize();
                        if (toAlly.dot(look) >= COS_HALF_ANG && online.distanceTo(player) <= RANGE) {
                            allies.add(online);
                        }
                    }
                }
                for (Player ally : allies) {
                    ally.addEffect(new MobEffectInstance(chargeHolder, DURATION, AMP, false, true));
                }
            }

            player.getCooldowns().addCooldown(this, COOLDOWN);
        }
        player.startUsingItem(hand);
        player.swing(hand, true);
        return InteractionResult.SUCCESS;
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
    public void appendHoverText(ItemStack stack,
                                TooltipContext context,
                                List<Component> tooltipComponents,
                                TooltipFlag tooltipFlag) {
        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("item.stalinium_sword.tooltip_shift"));
        } else {
            tooltipComponents.add(Component.translatable("item.stalinium_sword.tooltip"));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}