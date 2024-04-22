package com.example.examplemod.listeners.client

import com.example.examplemod.commands.client.SayHiCommand
import com.example.examplemod.extensions.sendStringMessage
import com.example.examplemod.handlers.ClientInboundHandler
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
        // val pipeline = Minecraft
        //     .getInstance()
        //     .connection!!
        //     .connection
        //     .channel()
        //     .pipeline()


//        [[[ ORDER OF NETTY HANDLERS IN PIPELINE ]]]
//        DefaultChannelPipeline$HeadContext#0         // packet payload is not changed here
//        timeout                                      // packet payload is not changed here
//        splitter
//        decompress
//        decoder
//        prepender
//        compress                                     // packet payload is not changed here
//        encoder
//        packet_handler
//        DefaultChannelPipeline$TailContext#0

        // pipeline.addBefore("packet_handler", "custom_packet_handler", ClientInboundHandler())

        //? TODO: is it good enough to just capture packets at the head and tail of the pipeline?
        //? ^ at the head we have the packet as it's received, and before packet_handler we have the packet just before
        //? ^ it's processed by Minecraft
        // pipeline.addBefore("timeout", "1_custom_packet_handler", ClientInboundHandler("1_before_timeout"))
//        pipeline.addBefore("splitter", "2_custom_packet_handler", ClientInboundHandler("2_before_splitter"))
//        pipeline.addBefore("decompress", "3_custom_packet_handler", ClientInboundHandler("3_before_decompress"))
//        pipeline.addBefore("decoder", "4_custom_packet_handler", ClientInboundHandler("4_before_decoder"))
//        pipeline.addBefore("prepender", "5_custom_packet_handler", ClientInboundHandler("5_before_prepender"))
//        pipeline.addBefore("compress", "6_custom_packet_handler", ClientInboundHandler("6_before_compress"))
//        pipeline.addBefore("encoder", "7_custom_packet_handler", ClientInboundHandler("7_before_encoder"))
        // pipeline.addBefore("packet_handler", "8_custom_packet_handler", ClientInboundHandler("8_before_packet_handler"))
    }

    companion object {
        // Directly reference a slf4j logger
        private val LOGGER = LogUtils.getLogger()
    }
}