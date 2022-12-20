package com.example.examplemod.extensions

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player

fun Player.sendStringMessage(msg: String) {
    this.sendSystemMessage(Component.literal(msg))
}
