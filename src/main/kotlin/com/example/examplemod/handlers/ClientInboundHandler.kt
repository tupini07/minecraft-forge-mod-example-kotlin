package com.example.examplemod.handlers

import com.example.examplemod.extensions.copyToByteBufferSafe
import com.example.examplemod.extensions.into
import com.example.examplemod.utils.ExPacketDecoder
import com.example.examplemod.utils.ExPacketEncoder
import com.mojang.authlib.minecraft.client.ObjectMapper
import com.mojang.logging.LogUtils
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.PacketFlow
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.FileWriter
import java.nio.ByteBuffer

@ChannelHandler.Sharable
class ClientInboundHandler(name: String) : SimpleChannelInboundHandler<Any>(false) {
    val fileWriter: FileWriter

    init {

        FileUtils.getUserDirectoryPath()
        val outputFile = File(FileUtils.getUserDirectoryPath() + "/Downloads/packet_output/$name.txt")

        if (outputFile.exists()) {
            outputFile.delete()
            outputFile.createNewFile()
        }

        fileWriter = FileWriter(outputFile)
    }

    override fun channelRead0(ctx: ChannelHandlerContext?, msg: Any?) {

        if (msg is ByteBuf) {
            val byteBuffer: ByteBuffer = msg.copyToByteBufferSafe()
            val packet = serializer.writeValueAsString(byteBuffer)
            fileWriter.write(packet)
        } else {
            try {
                val penc = ExPacketEncoder(PacketFlow.CLIENTBOUND)
                val encoded = penc.exEncode(ctx!!, msg as Packet<*>)

                val serialized = serializer.writeValueAsString(encoded!!.copyToByteBufferSafe())

                // verify that deserialization works properly as well
                val pdec = ExPacketDecoder(PacketFlow.CLIENTBOUND)
                val decoded = pdec.exDecode(ctx, encoded.copy())

                // try to re-decode
                val deserialized = serializer
                    .readValue(serialized, ByteBuffer::class.java)
                    .let {
                        pdec.exDecode(ctx, it.into<ByteBuf>())
                    }

                fileWriter.write("${msg.javaClass.name} =>> $serialized")
            } catch (e: Exception) {
                LOGGER.error("Error while serializing packet", e)
            }
        }

        fileWriter.write("\n")
        fileWriter.flush()

        ctx!!.fireChannelRead(msg)
    }

    companion object {
        private val LOGGER = LogUtils.getLogger()
        private val serializer = ObjectMapper.create()
    }
}