package com.shnupbups.tooltiptooltips.mixin;

import net.fabricmc.fabric.api.tool.attribute.v1.DynamicAttributeTool;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;


import java.util.List;

@Mixin(Item.class)
public abstract class ItemMixin {
	@Shadow public abstract int getMaxDamage();

	@Inject(method = "appendTooltip(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Ljava/util/List;Lnet/minecraft/client/item/TooltipContext;)V", at = @At("HEAD"))
	public void appendTooltipInject(ItemStack stack, World world, List<Text> tooltip, TooltipContext context, CallbackInfo ci) {
		if((Object)this instanceof ToolItem) {
			if(Screen.hasShiftDown()) {
				ToolItem tool = (ToolItem)(Object)this;
				ToolMaterial material = tool.getMaterial();
				if(tool instanceof MiningToolItem) {
					tooltip.add(new TranslatableText("tooltip.harvest_level").append(new LiteralText(String.valueOf(material.getMiningLevel()))).formatted(Formatting.GRAY));
					int efficiency = EnchantmentHelper.get(stack).getOrDefault(Enchantments.EFFICIENCY, 0);
					int efficiencyModifier = efficiency>0?(efficiency^2)+1:0;
					MutableText speedText = new TranslatableText("tooltip.harvest_speed").append(new LiteralText(String.valueOf(material.getMiningSpeedMultiplier()+efficiencyModifier))).formatted(Formatting.GRAY);
					if(efficiency > 0) {
						speedText.append(new LiteralText(" (+"+efficiencyModifier+")").formatted(Formatting.WHITE));
					}
					tooltip.add(speedText);
				}
				tooltip.add(new TranslatableText("tooltip.enchantability").append(new LiteralText(String.valueOf(material.getEnchantability()))).formatted(Formatting.GRAY));
				tooltip.add(new TranslatableText("tooltip.max_durability").append(new LiteralText(String.valueOf(stack.getMaxDamage()))).formatted(Formatting.GRAY));
			} else {
				tooltip.add(new TranslatableText("tooltip.press_shift").formatted(Formatting.GRAY));
			}
		} else if((Object)this instanceof ArmorItem) {
			if(Screen.hasShiftDown()) {
				ArmorItem armor = (ArmorItem)(Object)this;
				ArmorMaterial material = armor.getMaterial();
				tooltip.add(new TranslatableText("tooltip.enchantability").append(new LiteralText(String.valueOf(material.getEnchantability()))).formatted(Formatting.GRAY));
				tooltip.add(new TranslatableText("tooltip.max_durability").append(new LiteralText(String.valueOf(stack.getMaxDamage()))).formatted(Formatting.GRAY));
			} else {
				tooltip.add(new TranslatableText("tooltip.press_shift").formatted(Formatting.GRAY));
			}
		} else if(stack.isDamageable()) {
			if(Screen.hasShiftDown()) {
				tooltip.add(new TranslatableText("tooltip.max_durability").append(new LiteralText(String.valueOf(stack.getMaxDamage()))).formatted(Formatting.GRAY));
			} else {
				tooltip.add(new TranslatableText("tooltip.press_shift").formatted(Formatting.GRAY));
			}
		}
	}
}
