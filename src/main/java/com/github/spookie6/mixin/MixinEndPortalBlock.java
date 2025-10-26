package com.github.spookie6.mixin;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.EndPortalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

@Mixin(EndPortalBlock.class)
public class MixinEndPortalBlock {

    @Unique
    private static final Set<UUID> warnedPlayers = new HashSet<>();

    static {
        ServerTickEvents.END_SERVER_TICK.register((MinecraftServer server) -> {
            Iterator<UUID> it = warnedPlayers.iterator();
            while(it.hasNext()) {
                UUID uuid = it.next();
                ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
                if (player == null) {
                    it.remove();
                    continue;
                }
                BlockState block_bottom = player.getEntityWorld().getBlockState(player.getBlockPos());
                BlockState block_top = player.getEntityWorld().getBlockState(player.getBlockPos().up());
                if (block_bottom.getBlock() != Blocks.END_PORTAL && block_top.getBlock() != Blocks.END_PORTAL) {
                    it.remove();
                }
            }
        });
    }

    @Inject(method = "onEntityCollision", at = @At("HEAD"), cancellable = true)
    private void familyfunction$disableEndPortal(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler, boolean bl, CallbackInfo ci) {
        if (entity instanceof ServerPlayerEntity player) {

            if (!warnedPlayers.contains(player.getUuid())) {
                player.sendMessage(net.minecraft.text.Text.of("§cThe End is disabled!"), false);
                warnedPlayers.add(player.getUuid());
            }
        }
        ci.cancel();
    }
}