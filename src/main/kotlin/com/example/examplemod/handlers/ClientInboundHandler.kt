package com.example.examplemod.handlers

import com.example.examplemod.extensions.copyToByteBufferSafe
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
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.nio.ByteBuffer

@ChannelHandler.Sharable
class ClientInboundHandler(name: String) : SimpleChannelInboundHandler<Any>(false) {
    private val bufferedWriter: BufferedWriter
    private var numWrites = 0

    init {

        FileUtils.getUserDirectoryPath()
        val outputFile = File(FileUtils.getUserDirectoryPath() + "/Downloads/packet_output/$name.txt")

        if (outputFile.exists()) {
            outputFile.delete()
            outputFile.createNewFile()
        }

        // NOTE: writes happen on the network thread, so it might cause the game to lag. Better to move this
        // to a separate thread
        bufferedWriter = BufferedWriter(FileWriter(outputFile, Charsets.UTF_8), 512_000)
    }

    override fun channelRead0(ctx: ChannelHandlerContext?, msg: Any?) {
        // TODO: How do we know which `tick` number the current event happened in?

        if (msg is ByteBuf) {
            val byteBuffer: ByteBuffer = msg.copyToByteBufferSafe()
            val packet = serializer.writeValueAsString(byteBuffer)

            bufferedWriter.write(packet)
        } else {
            try {
                val penc = ExPacketEncoder(PacketFlow.CLIENTBOUND)
                val encoded = penc.exEncode(ctx!!, msg as Packet<*>)

                // NOTE: it's a bit weird but the `serialized` output is enclosed in double quotes, and these are
                // necessary for the deserializer to work properly!
                val serialized = serializer.writeValueAsString(encoded!!.copyToByteBufferSafe())

                // NOTE: The below can be used to re-read the "serialized" packet
//                // verify that deserialization works properly as well
//                val pdec = ExPacketDecoder(PacketFlow.CLIENTBOUND)
//                val justDecoded = pdec.exDecode(ctx, encoded.copy())
//
//                // try to re-decode
//                val deserializedAndDecoded = serializer
//                    .readValue(serialized, ByteBuffer::class.java)
//                    .let {
//                        pdec.exDecode(ctx, it.into<ByteBuf>())
//                    }

                // there's no need to include the class name in the output, since it's already in the packet (the packet id)
                bufferedWriter.write(serialized)
            } catch (e: Exception) {
                LOGGER.error("Error while serializing packet", e)
            }
        }

        bufferedWriter.write("\n")

        this.numWrites += 1
        if (this.numWrites == 1000) {
            // TODO: for performance, flush every 100 writes
            bufferedWriter.flush()
            LOGGER.info("Flushed $numWrites writes")
            this.numWrites = 0
        }

        ctx!!.fireChannelRead(msg)
    }

    companion object {
        private val LOGGER = LogUtils.getLogger()
        private val serializer = ObjectMapper.create()
    }
}