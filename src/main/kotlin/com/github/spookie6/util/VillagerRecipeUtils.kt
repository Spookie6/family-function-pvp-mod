package com.github.spookie6.util

import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.village.VillagerProfession

object VillagerRecipeUtils {
    val ironArmorSet = listOf(ItemStack(Items.IRON_HELMET), ItemStack(Items.IRON_CHESTPLATE), ItemStack(Items.IRON_LEGGINGS),
        ItemStack(Items.IRON_BOOTS))
    val weapons = listOf(ItemStack(Items.IRON_SWORD), ItemStack(Items.IRON_AXE))
    val tools = listOf(ItemStack(Items.IRON_PICKAXE), ItemStack(Items.IRON_SHOVEL), ItemStack(Items.IRON_HOE))

    fun getRecipe(prof: VillagerProfession) {
        ;
    }
}