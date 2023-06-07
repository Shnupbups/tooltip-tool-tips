package com.shnupbups.tooltiptooltips.mixin;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Item.class)
public abstract class ItemMixin {
	@Shadow
	public abstract FoodComponent getFoodComponent();

	@Inject(method = "appendTooltip(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Ljava/util/List;Lnet/minecraft/client/item/TooltipContext;)V", at = @At("HEAD"))
	public void appendTooltipInject(ItemStack stack, World world, List<Text> tooltip, TooltipContext context, CallbackInfo ci) {
		List<Text> texts = new ArrayList<>();

		if ((Object) this instanceof ToolItem tool) {
			ToolMaterial material = tool.getMaterial();
			if (tool instanceof MiningToolItem) {
				texts.add(Text.translatable("tooltip.harvest_level", material.getMiningLevel()).formatted(Formatting.GRAY));
				int efficiency = EnchantmentHelper.get(stack).getOrDefault(Enchantments.EFFICIENCY, 0);
				int efficiencyModifier = efficiency > 0 ? (efficiency * efficiency) + 1 : 0;
				MutableText speedText = Text.translatable("tooltip.harvest_speed", material.getMiningSpeedMultiplier() + efficiencyModifier).formatted(Formatting.GRAY);
				if (efficiency > 0) {
					speedText.append(Text.literal(" ").append(Text.translatable("tooltip.efficiency_modifier", efficiencyModifier).formatted(Formatting.WHITE)));
				}
				texts.add(speedText);
			}
			texts.add(Text.translatable("tooltip.enchantability", material.getEnchantability()).formatted(Formatting.GRAY));
		} else if ((Object) this instanceof ArmorItem armor) {
			ArmorMaterial material = armor.getMaterial();
			texts.add(Text.translatable("tooltip.enchantability", material.getEnchantability()).formatted(Formatting.GRAY));
		}

		if (stack.isDamageable() && (!stack.isDamaged() || !context.isAdvanced())) {
			texts.add(Text.translatable("tooltip.durability", stack.getMaxDamage() - stack.getDamage(), stack.getMaxDamage()).formatted(Formatting.GRAY));
		}

		if (stack.isFood()) {
			FoodComponent foodComponent = this.getFoodComponent();
			texts.add(Text.translatable("tooltip.hunger", foodComponent.getHunger()).formatted(Formatting.GRAY));
			texts.add(Text.translatable("tooltip.saturation", foodComponent.getSaturationModifier()).formatted(Formatting.GRAY));
		}

		if (texts.size() == 1 || Screen.hasShiftDown() || context.isAdvanced()) {
			tooltip.addAll(texts);
		} else if (!texts.isEmpty()) {
			tooltip.add(Text.translatable("tooltip.press_shift").formatted(Formatting.GRAY));
		}
	}
}
