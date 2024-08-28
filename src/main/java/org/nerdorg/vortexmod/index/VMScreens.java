package org.nerdorg.vortexmod.index;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.nerdorg.vortexmod.VortexMod;
import org.nerdorg.vortexmod.gui.flight_computer.FlightComputerGuiMenu;

public class VMScreens {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, VortexMod.MODID);

    public static final RegistryObject<MenuType<FlightComputerGuiMenu>> FLIGHT_COMPUTER_MENU =
            registerMenuType("flight_computer_menu", FlightComputerGuiMenu::new);


    private static <T extends AbstractContainerMenu>RegistryObject<MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
