package com.github.spookie6.mixin;

import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerEntity.class)
public class MixinVillager {
    @Inject(
            method = "fillRecipes",
            at = @At("HEAD"),
            cancellable = false
    )
    public void onFillRecipe(CallbackInfo ci) {
        VillagerEntity villager = (VillagerEntity) (Object) this;
        VillagerProfession profession = villager.getVillagerData().profession().value();
        TradeOfferList offers = villager.getOffers();
        int level = villager.getVillagerData().level();
        Random random = villager.getRandom();


    }
}
