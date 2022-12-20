package com.example.examplemod

import com.example.examplemod.listeners.ExampleListeners
import com.mojang.logging.LogUtils
import net.minecraft.client.Minecraft
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.Material
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ExampleMod.MODID)
class ExampleMod {
    init {
        val modEventBus = FMLJavaModLoadingContext.get().modEventBus

        // Register the commonSetup method for modloading
        modEventBus.addListener { event: FMLCommonSetupEvent -> commonSetup(event) }

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus)
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus)

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(ExampleListeners())
    }

    private fun commonSetup(event: FMLCommonSetupEvent) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP")
        LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT))
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
    object ClientModEvents {
        @SubscribeEvent
        fun onClientSetup(event: FMLClientSetupEvent?) {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP")
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().user.name)
        }
    }

    companion object {
        // Define mod id in a common place for everything to reference
        const val MODID = "examplemod"

        // Directly reference a slf4j logger
        private val LOGGER = LogUtils.getLogger()

        // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace
        val BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID)

        // Create a Deferred Register to hold Items which will all be registered under the "examplemod" namespace
        val ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID)

        // Creates a new Block with the id "examplemod:example_block", combining the namespace and path
        val EXAMPLE_BLOCK = BLOCKS.register("example_block") { Block(BlockBehaviour.Properties.of(Material.STONE)) }

        // Creates a new BlockItem with the id "examplemod:example_block", combining the namespace and path
        val EXAMPLE_BLOCK_ITEM = ITEMS.register<Item>("example_block") {
            BlockItem(
                EXAMPLE_BLOCK.get(),
                Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)
            )
        }
    }
}