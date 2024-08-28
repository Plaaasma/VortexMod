package org.nerdorg.vortexmod;

import com.mojang.logging.LogUtils;
import com.simibubi.create.content.fluids.tank.BoilerHeaters;
import com.simibubi.create.content.kinetics.BlockStressValues;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.nerdorg.vortexmod.config.Config;
import org.nerdorg.vortexmod.gui.flight_computer.FlightComputerGuiScreen;
import org.nerdorg.vortexmod.index.*;
import org.nerdorg.vortexmod.packets.AssemblePacket;
import org.nerdorg.vortexmod.packets.DisassemblePacket;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(VortexMod.MODID)
public class VortexMod {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "vortexmod";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static boolean CC_ACTIVE = false;

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(VortexMod.MODID);

    private static final String PROTOCOL = "1";
    public static final SimpleChannel Network = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(MODID, "main"))
            .clientAcceptedVersions(PROTOCOL::equals)
            .serverAcceptedVersions(PROTOCOL::equals)
            .networkProtocolVersion(() -> PROTOCOL)
            .simpleChannel();

    static {
        REGISTRATE.setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, TooltipHelper.Palette.STANDARD_CREATE)
                .andThen(TooltipModifier.mapNull(KineticStats.create(item))));
    }

    public VortexMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::postInit);

        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);
        Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve("vortexmod-common.toml"));

        CC_ACTIVE = ModList.get().isLoaded("computercraft");

        VMCreativeModeTabs.register(eventBus);
        REGISTRATE.registerEventListeners(eventBus);
        VMBlocks.register();
        VMBlockEntities.register();
        VMScreens.register(eventBus);
    }

    private void setup(final FMLCommonSetupEvent event) {
        BlockStressValues.registerProvider(MODID, AllConfigs.server().kinetics.stressValues);
        /*BoilerHeaters.registerHeater(CABlocks.LIQUID_BLAZE_BURNER.get(), (level, pos, state) -> {
            BlazeBurnerBlock.HeatLevel value = state.getValue(LiquidBlazeBurnerBlock.HEAT_LEVEL);
            if (value == BlazeBurnerBlock.HeatLevel.NONE) return -1;
            if (value == BlazeBurnerBlock.HeatLevel.SEETHING) return 2;
            if (value.isAtLeast(BlazeBurnerBlock.HeatLevel.FADING)) return 1;
            return 0;
        });*/
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        event.enqueueWork(VMPonder::register);
        event.enqueueWork(VMItemProperties::register);

        RenderType cutout = RenderType.cutoutMipped();

        MenuScreens.register(VMScreens.FLIGHT_COMPUTER_MENU.get(), FlightComputerGuiScreen::new);

        ItemBlockRenderTypes.setRenderLayer(VMBlocks.TIME_ROTOR.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(VMBlocks.FLIGHT_COMPUTER.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(VMBlocks.FLIGHT_CONTROLLER.get(), cutout);
    }

    public void postInit(FMLLoadCompleteEvent evt) {
        Network.registerMessage(0, AssemblePacket.class, AssemblePacket::encode, AssemblePacket::decode, AssemblePacket::handle);
        Network.registerMessage(1, DisassemblePacket.class, DisassemblePacket::encode, DisassemblePacket::decode, DisassemblePacket::handle);

        System.out.println("Create: The Time Vortex Initialized!");
    }
}
