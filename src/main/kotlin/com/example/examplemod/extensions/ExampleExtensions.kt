package com.example.examplemod.extensions

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player
import java.nio.ByteBuffer

fun Player.sendStringMessage(msg: String) {
    this.sendSystemMessage(Component.literal(msg))
}

fun ByteBuf.copyToByteBufferSafe(): ByteBuffer {
    if (this.hasArray()) {
        return ByteBuffer.wrap(this.array())
    }

    val bytes = ByteArray(this.readableBytes())
    this.getBytes(this.copy().readerIndex(), bytes)

    return ByteBuffer.wrap(bytes)
}

fun <T : ByteBuf> ByteBuffer.into(): ByteBuf {
    return Unpooled.wrappedBuffer(this)
}