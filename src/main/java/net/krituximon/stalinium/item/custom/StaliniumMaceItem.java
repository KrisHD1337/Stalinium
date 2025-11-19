package net.krituximon.stalinium.item.custom;

import net.krituximon.stalinium.item.ModItems;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.windcharge.WindCharge;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class StaliniumMaceItem extends MaceItem {
    public StaliniumMaceItem(Properties properties) {
        super(properties);
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
    public int getEnchantmentValue() {
        return 25;
    }

    public static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder().add(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                        BASE_ATTACK_DAMAGE_ID, (double) 9.0F, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND).add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID,
                (double) -3F, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).build();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!level.isClientSide && player.isCrouching()) {
            smash(player);
        } else if (!level.isClientSide && !player.isCrouching()) {
            WindCharge windCharge = new WindCharge(EntityType.WIND_CHARGE, level);
            Vec3 direction = player.getLookAngle();

            windCharge.setPos(
                    player.getX() + direction.x * 1.2,
                    player.getEyeY() - 0.1,
                    player.getZ() + direction.z * 1.2
            );

            windCharge.shoot(direction.x, direction.y, direction.z, 1.5F, 0.0F);

            level.addFreshEntity(windCharge);

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BREEZE_CHARGE, SoundSource.PLAYERS, 1.0F, 1.0F);

            player.getCooldowns().addCooldown(this, 15);
        }

        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }

    public static void smash(Player player) {
        if (player.level().isClientSide) return;

        double radius = 5.0;
        double angle = 60.0;
        float damage = 4.0f;
        ServerLevel level = (ServerLevel) player.level();
        Vec3 playerPos = player.position();
        Vec3 direction = new Vec3(player.getLookAngle().x, 0, player.getLookAngle().z);
        List<Entity> targets = level.getEntities(player, player.getBoundingBox().inflate(radius));

        for (Entity entity : targets) {
            if (!(entity instanceof LivingEntity) || entity == player) continue;
            Vec3 target = entity.position().subtract(playerPos).normalize();
            double dot = direction.dot(target);
            if (dot > Math.cos(Math.toRadians(angle / 2))) {
                entity.hurt(player.damageSources().playerAttack(player), damage);
                Vec3 knockback = entity.position().subtract(playerPos).normalize().scale(0.2);
                entity.push(knockback.x, 0.2, knockback.z);
            }
        }

        BlockState block = level.getBlockState(player.blockPosition().below());
        for (int i = 0; i < 8; i++) {
            double distance = (radius / 8) * (i + 1);
            for (int j = 0; j < 25; j++) {
                double angleOffset = (level.random.nextDouble() - 0.5) * angle;
                double radOffset = Math.toRadians(angleOffset);

                Vec3 rotated = rotateY(direction, radOffset);

                Vec3 particlePos = playerPos.add(rotated.scale(distance).add(0, 1.0 + (level.random.nextDouble() - 0.5) * 0.5, 0));
                level.sendParticles(
                        new BlockParticleOption(ParticleTypes.BLOCK, block),
                        particlePos.x, particlePos.y, particlePos.z,
                        3, 0.1, 0.1, 0.1, 0.1
                );
            }
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.EVOKER_FANGS_ATTACK, SoundSource.PLAYERS, 1.0F, 1.0F);
        player.getCooldowns().addCooldown(ModItems.STALINIUM_MACE.get(), 100);
    }

    private static Vec3 rotateY(Vec3 vec, double radians) {
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        double x = vec.x * cos - vec.z * sin;
        double z = vec.x * sin + vec.z * cos;
        return new Vec3(x, vec.y, z);
    }

    @Override
    public void appendHoverText(ItemStack stack,
                                TooltipContext context,
                                List<Component> tooltipComponents,
                                TooltipFlag tooltipFlag) {
        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("item.stalinium_mace.tooltip_shift"));
        } else {
            tooltipComponents.add(Component.translatable("item.stalinium_mace.tooltip"));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
