package net.krituximon.stalinium.effect;

import net.krituximon.stalinium.Stalinium;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class StaliniumChargeEffect extends MobEffect {
    public StaliniumChargeEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xAAAAFF);
        this.addAttributeModifier(
                Attributes.MOVEMENT_SPEED,
                ResourceLocation.fromNamespaceAndPath(Stalinium.MODID, "stalinium_charge_speed"),
                0.1,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE
        );
        this.addAttributeModifier(
                Attributes.KNOCKBACK_RESISTANCE,
                ResourceLocation.fromNamespaceAndPath(Stalinium.MODID, "stalinium_charge_knockback"),
                0.33,
                AttributeModifier.Operation.ADD_VALUE
        );
        this.addAttributeModifier(
                Attributes.ATTACK_SPEED,
                ResourceLocation.fromNamespaceAndPath(Stalinium.MODID, "stalinium_charge_toughness"),
                0.075,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE
        );
        this.addAttributeModifier(
                Attributes.ATTACK_DAMAGE,
                ResourceLocation.fromNamespaceAndPath(Stalinium.MODID, "stalinium_charge_damage"),
                1,
                AttributeModifier.Operation.ADD_VALUE
        );
    }
}