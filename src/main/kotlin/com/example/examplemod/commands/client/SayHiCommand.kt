package com.example.examplemod.commands.client

import com.example.examplemod.extensions.sendStringMessage
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.world.entity.player.Player

class SayHiCommand {
    companion object {
        fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
            dispatcher.register(
                Commands.literal("sayhi")
                    .executes { context ->
                        execute(context)
                    }
            )
        }

        private fun execute(command: CommandContext<CommandSourceStack>): Int {
            val player = command.source.entity as Player?
            player?.sendStringMessage("Hi!")

            return Command.SINGLE_SUCCESS
        }
    }
}