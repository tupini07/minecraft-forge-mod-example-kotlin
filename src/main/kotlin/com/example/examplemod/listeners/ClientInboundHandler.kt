package com.example.examplemod.listeners

import com.mojang.logging.LogUtils
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import net.minecraft.network.PacketDecoder
import net.minecraft.network.PacketEncoder
import net.minecraft.network.protocol.Packet

@ChannelHandler.Sharable
class ClientInboundHandler : SimpleChannelInboundHandler<Packet<*>>(false) {

    override fun channelRead0(ctx: ChannelHandlerContext?, msg: Packet<*>?) {
        LOGGER.info("Got packet ${msg!!.javaClass.name}")
        ctx!!.fireChannelRead(msg)
    }

    companion object {
        private val LOGGER = LogUtils.getLogger()
    }
}