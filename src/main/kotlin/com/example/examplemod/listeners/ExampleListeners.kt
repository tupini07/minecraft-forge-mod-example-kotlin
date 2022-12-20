package com.example.examplemod.listeners

import com.example.examplemod.extensions.sendStringMessage
import com.mojang.logging.LogUtils
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.event.server.ServerStartingEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

class ExampleListeners {
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    fun onServerStarting(event: ServerStartingEvent?) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting")
    }

    @SubscribeEvent
    fun onBlockClicked(event: PlayerInteractEvent.LeftClickBlock) {
        val player = event.entity
        val hitBlockItem = event.level.getBlockState(event.pos).block.asItem()

        if (hitBlockItem != Items.GRASS_BLOCK) {
            // fail silently
            return
        }

        if (event.level.isClientSide) {
            player.sendStringMessage("Mod is running on the client, we can't give you diamond stuff!")
            return
        }


        val inventory = player.inventory
        if (inventory.isEmpty && hitBlockItem === Items.GRASS_BLOCK) {
            player.sendSystemMessage(Component.literal("Congratulations!"))

            player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack(Items.DIAMOND_SWORD, 1))
            player.setItemSlot(EquipmentSlot.HEAD, ItemStack(Items.DIAMOND_HELMET, 1))
            player.setItemSlot(EquipmentSlot.CHEST, ItemStack(Items.DIAMOND_CHESTPLATE, 1))
            player.setItemSlot(EquipmentSlot.LEGS, ItemStack(Items.DIAMOND_LEGGINGS, 1))
            player.setItemSlot(EquipmentSlot.FEET, ItemStack(Items.DIAMOND_BOOTS, 1))
        }
    }

    @SubscribeEvent
    fun onPlayerLoggedIn(event: PlayerEvent.PlayerLoggedInEvent) {
        val player = event.entity as Player
        player.sendSystemMessage(Component.literal("AAAAAAAAA!"))
    }

    companion object {
        // Directly reference a slf4j logger
        private val LOGGER = LogUtils.getLogger()
    }
}