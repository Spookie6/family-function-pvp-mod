package com.github.spookie6.mixin;

import com.github.spookie6.FamilyFunction;
import com.github.spookie6.utils.VillagerUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.VillagerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(VillagerEntity.class)
public abstract class MixinVillagerEntity {

    @Shadow
    public abstract VillagerData getVillagerData();

    @Unique
    private static final Set<String> BLOCKED_BOOKS = Set.of(
            "aqua_affinity",
            "breach",
            "density",
            "depth_strider",
            "efficiency",
            "feather_falling",
            "fire_aspect",
            "flame",
            "fortune",
            "infinity",
            "looting",
            "multishot",
            "piercing",
            "power",
            "protection",
            "punch",
            "quick_charge",
            "respiration",
            "riptide",
            "silk_touch",
            "sharpness",
            "sweeping_edge"
    );

    @Inject(method = "fillRecipes", at = @At("TAIL"))
    public void familyfunction$onFillRecipes(CallbackInfo ci) {
        VillagerEntity villager = (VillagerEntity)(Object)this;
        TradeOfferList tradeOffers = villager.getOffers();
        VillagerData data = getVillagerData();

//        if ("minecraft:librarian".equals(getVillagerData().profession().getIdAsString())) {
//            TradeOfferList filtered = new TradeOfferList();
//            boolean removedBookTrade = false;
//
//            for (TradeOffer offer : tradeOffers) {
//                ItemStack sell = offer.getSellItem();
//
//                // Remove blacklisted enchanted books
//                if (sell.isOf(Items.ENCHANTED_BOOK)) {
//                    Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(sell);
//                    boolean blocked = enchants.keySet().stream()
//                            .map(e -> Registries.ENCHANT.getId(e).getPath())
//                            .anyMatch(BLOCKED_BOOKS::contains);
//
//                    if (blocked) {
//                        removedBookTrade = true;
//                        FamilyFunction.INSTANCE.getLogger().info("Removed blocked book trade: {}", enchants);
//                        continue;
//                    }
//                }
//
//                filtered.add(offer);
//            }
//
//            // Replace trade list
//            tradeOffers.clear();
//            tradeOffers.addAll(filtered);
//
//            if (removedBookTrade) {
//                boolean hasPaperTrade = tradeOffers.stream().anyMatch(o -> o.getFirstBuyItem().isOf(Items.PAPER));
//                boolean hasBookTrade = tradeOffers.stream().anyMatch(o -> o.getFirstBuyItem().isOf(Items.BOOK));
//
//                if (hasPaperTrade && !hasBookTrade) {
//                    // Add emeralds → books
//                    TradeOffer newTrade = new TradeOffer(
//                            new ItemStack(Items.EMERALD, 1),
//                            ItemStack.EMPTY,
//                            new ItemStack(Items.BOOK, 3),
//                            10,  // max uses
//                            2,   // villager xp
//                            0.05F // price multiplier
//                    );
//                    tradeOffers.add(newTrade);
//                    FamilyFunction.INSTANCE.getLogger().info("Added Emeralds-for-Books trade.");
//                } else if (hasBookTrade && !hasPaperTrade) {
//                    // Add emeralds → paper
//                    TradeOffer newTrade = new TradeOffer(
//                            new ItemStack(Items.EMERALD, 1),
//                            ItemStack.EMPTY,
//                            new ItemStack(Items.PAPER, 6),
//                            10,
//                            2,
//                            0.05F
//                    );
//                    tradeOffers.add(newTrade);
//                    FamilyFunction.INSTANCE.getLogger().info("Added Emeralds-for-Paper trade.");
//                }
//            }
//            return;
//        }

        List<TradeOffer> newOffers = new ArrayList<>();

        Iterator<TradeOffer> it = tradeOffers.iterator();
        while (it.hasNext()) {
            TradeOffer offer = it.next();
            String idStr = Registries.ITEM.getId(offer.getSellItem().getItem()).toString();

            if (idStr.contains("diamond_")) {
                it.remove();

                String newPath = idStr.split(":")[1].replace("diamond_", "iron_");
                Item newItem = Registries.ITEM.get(Identifier.of("minecraft", newPath));

                if (newItem == null || newItem == net.minecraft.item.Items.AIR) continue;

                ItemStack newResult = VillagerUtils.INSTANCE.enchantItem(newItem);

                TradeOffer newOffer = new TradeOffer(
                        offer.getFirstBuyItem(),
                        offer.getSecondBuyItem(),
                        newResult,
                        offer.getUses(),
                        offer.getMaxUses(),
                        offer.getMerchantExperience(),
                        offer.getPriceMultiplier(),
                        offer.getDemandBonus()
                );

                newOffers.add(newOffer);
            }
        }

        // ✅ Apply changes after iteration
        tradeOffers.addAll(newOffers);
    }

}
