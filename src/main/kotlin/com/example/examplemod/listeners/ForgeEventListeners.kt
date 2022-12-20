package com.example.examplemod.listeners

import com.example.examplemod.commands.client.SayHiCommand
import com.mojang.logging.LogUtils
import net.minecraftforge.client.event.RegisterClientCommandsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

class ForgeEventListeners {

    companion object {
        // Directly reference a slf4j logger
        private val LOGGER = LogUtils.getLogger()
    }
}