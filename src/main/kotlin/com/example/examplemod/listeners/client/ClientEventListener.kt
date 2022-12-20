package com.example.examplemod.listeners.client

import com.example.examplemod.commands.client.SayHiCommand
import com.mojang.logging.LogUtils
import net.minecraftforge.client.event.RegisterClientCommandsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

/**
 * Listens for events that happen on the client.
 */
class ClientEventListener {

    @SubscribeEvent
    fun onRegisterClientSideCommands(event: RegisterClientCommandsEvent) {
        val clientSideDispatcher = event.dispatcher

        SayHiCommand.register(clientSideDispatcher)
    }

    companion object {
        // Directly reference a slf4j logger
        private val LOGGER = LogUtils.getLogger()
    }
}