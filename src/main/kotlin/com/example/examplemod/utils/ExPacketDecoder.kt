package com.example.examplemod.utils

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import net.minecraft.network.Connection
import net.minecraft.network.ConnectionProtocol
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.PacketDecoder
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.PacketFlow
import java.io.IOException

class ExPacketDecoder(private val flow: PacketFlow) : PacketDecoder(flow) {

    fun exDecode(ctx: ChannelHandlerContext, msg: ByteBuf): Packet<*>? {
        val i: Int = msg.readableBytes()
        if (i != 0) {
            val friendlybytebuf = FriendlyByteBuf(msg)
            val j = friendlybytebuf.readVarInt()

            val packet: Packet<*>? = ctx
                .channel()
                .attr<ConnectionProtocol>(Connection.ATTRIBUTE_PROTOCOL)
                .get()
                .createPacket(this.flow, j, friendlybytebuf)

            if (packet == null) {
                throw IOException("Bad packet id $j")
            } else {
                if (friendlybytebuf.readableBytes() > 0) {
                    throw IOException(
                        "Packet " + ctx.channel().attr<ConnectionProtocol>(Connection.ATTRIBUTE_PROTOCOL).get()
                            .getId() + "/" + j + " (" + packet.javaClass.simpleName + ") was larger than I expected, found " + friendlybytebuf.readableBytes() + " bytes extra whilst reading packet " + j
                    )
                } else {
                    return packet
                }
            }
        }

        return null
    }
}