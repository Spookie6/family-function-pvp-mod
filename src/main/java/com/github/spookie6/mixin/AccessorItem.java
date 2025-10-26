package com.github.spookie6.mixin;

import net.minecraft.component.ComponentMap;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Item.class)
public interface AccessorItem {
   @Accessor("components")
   ComponentMap familyfunction$getComponents();
}
