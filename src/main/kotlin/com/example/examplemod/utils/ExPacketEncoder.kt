package com.example.examplemod.utils

import io.netty.buffer.ByteBufAllocator
import io.netty.channel.ChannelHandlerContext
import net.minecraft.network.Connection
import net.minecraft.network.ConnectionProtocol
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.PacketEncoder
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.PacketFlow
import java.io.IOException

class ExPacketEncoder(private val flow: PacketFlow) : PacketEncoder(flow) {

    fun exEncode(ctx: ChannelHandlerContext, msg: Packet<*>): FriendlyByteBuf? {
        val byteBuffer = ByteBufAllocator.DEFAULT.buffer()
        val connectionProtocol: ConnectionProtocol = ctx
            .channel()
            .attr<ConnectionProtocol>(Connection.ATTRIBUTE_PROTOCOL)
            .get()

        if (connectionProtocol == null) {
            throw RuntimeException("ConnectionProtocol unknown: $msg")
        } else {
            val integer = connectionProtocol.getPacketId(flow, msg)
            if (integer == null) {
                throw IOException("Can't serialize unregistered packet")
            } else {
                val friendlybytebuf = FriendlyByteBuf(byteBuffer)
                friendlybytebuf.writeVarInt(integer)
                try {
                    val i = friendlybytebuf.writerIndex()

                    msg.write(friendlybytebuf)
                    val j = friendlybytebuf.writerIndex() - i
                    require(j <= 8388608) { "Packet too big (is $j, should be less than 8388608): $msg" }

                    return friendlybytebuf
                } catch (throwable: Throwable) {
                    if (!msg.isSkippable()) {
                        throw throwable
                    }
                }
            }
        }

        return null
    }
}