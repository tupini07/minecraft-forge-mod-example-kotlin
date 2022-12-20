package com.example.examplemod.listeners.client

import com.example.examplemod.commands.client.SayHiCommand
import com.example.examplemod.extensions.sendStringMessage
import com.example.examplemod.listeners.ClientInboundHandler
import com.mojang.logging.LogUtils
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.player.Player
import net.minecraftforge.client.event.RegisterClientCommandsEvent
import net.minecraftforge.event.entity.EntityJoinLevelEvent
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

    @SubscribeEvent
    fun onClientJoinLevel(event: EntityJoinLevelEvent) {
        val entityName = event.entity.name
        val currentPlayerName = Minecraft.getInstance().player?.name

        if (!entityName.equals(currentPlayerName)) {
            return;
        }

        val player = event.entity as Player
        player.sendStringMessage(
            "${ChatFormatting.YELLOW}You just joined dimension ${ChatFormatting.GREEN} ${
                event.level.dimension().location()
            }"
        )
        // TODO: need to unsubscribe when player disconnects from server
        val pipeline = Minecraft
            .getInstance()
            .connection!!
            .connection
            .channel()
            .pipeline()


//        [[[ ORDER OF NETTY HANDLERS IN PIPELINE ]]]
//        DefaultChannelPipeline$HeadContext#0
//        timeout
//        splitter
//        decompress
//        decoder
//        prepender
//        compress
//        encoder
//        packet_handler
//        DefaultChannelPipeline$TailContext#0
        pipeline.addBefore("packet_handler", "custom_packet_handler", ClientInboundHandler())
    }

    companion object {
        // Directly reference a slf4j logger
        private val LOGGER = LogUtils.getLogger()
    }
}