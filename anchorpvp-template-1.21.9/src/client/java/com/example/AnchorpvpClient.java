package com.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnchorpvpClient implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("Anchorpvp");
	public static boolean enabled = true;

	@Override
	public void onInitializeClient() {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(ClientCommandManager.literal("toggleanchor").executes(context -> {
						enabled = !enabled; // toggle the feature
						MinecraftClient.getInstance().player.sendMessage(Text.literal("anchor auto switch: " + enabled), true);
						return 1;
					})
			);
		});

		MinecraftClient mc = MinecraftClient.getInstance();

		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			if (!world.isClient() || player != mc.player) { // only run on client
				return ActionResult.PASS;
			}

			BlockPos blockPos = hitResult.getBlockPos(); // getting data
			var state = world.getBlockState(blockPos);
			var block = state.getBlock();

			if (block == Blocks.RESPAWN_ANCHOR && enabled) { // if anchor // if it has an charge
				if (player.getStackInHand(hand).isOf(Blocks.GLOWSTONE.asItem())) {
						MinecraftClient.getInstance().send(() -> {
							switchslot(player);
						});
				}
			}
			return ActionResult.PASS;
		});
	}

	private void switchslot(PlayerEntity player) {
		var inventory = player.getInventory(); // data n shit
		for (int i = 0; i < 9; i++) { // loop
			if (!inventory.getStack(i).isOf(Blocks.GLOWSTONE.asItem())) { // if there isnt glowstone at i
				inventory.setSelectedSlot(i); // change slot
				return;
			}
		}
	}
}