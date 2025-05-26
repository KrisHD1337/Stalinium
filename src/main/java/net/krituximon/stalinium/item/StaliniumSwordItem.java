package net.krituximon.stalinium.item;

import net.krituximon.stalinium.Stalinium;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class StaliniumSwordItem extends SwordItem {
    private static final double RANGE        = 6.0;
    private static final double HALF_ANGLE   = Math.toRadians(30);
    private static final double COS_HALF_ANG = Math.cos(HALF_ANGLE);
    private static final int    DURATION     = 5 * 20;
    private static final int    AMP          = 0;
    private static final int    COOLDOWN     = 20 * 20; // 20s

    public StaliniumSwordItem(Tier tier, Properties props) {
        super(tier, props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // 1) if still on cooldown, fail
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

    // keep it unbreakable
    @Override public boolean isDamageable(ItemStack stack)   { return false; }
    @Override public boolean isDamaged(ItemStack stack)      { return false; }
    @Override public int     getMaxDamage(ItemStack stack)   { return 2; }
}
