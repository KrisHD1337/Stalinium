package net.krituximon.stalinium.screen;

import net.krituximon.stalinium.Stalinium;
import net.krituximon.stalinium.screen.custom.StaliniumPressMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, Stalinium.MODID);

    @SuppressWarnings("unchecked")
    private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<T>, MenuType<T>>
    registerMenuType(String name, IContainerFactory<T> factory) {
        return (DeferredHolder<MenuType<T>, MenuType<T>>) (Object) MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    // press menu
    public static final DeferredHolder<MenuType<StaliniumPressMenu>,MenuType<StaliniumPressMenu>>
            STALINIUM_PRESS_MENU = registerMenuType("stalinium_press_menu", StaliniumPressMenu::new);

    // cache menu — *also* use registerMenuType so the generics line up:
    public static final DeferredHolder<MenuType<ChestMenu>,MenuType<ChestMenu>>
            STALINIUM_CACHE_MENU = registerMenuType(
            "stalinium_cache",
            (windowId, inv, buf) -> ChestMenu.threeRows(windowId, inv, new SimpleContainer(27))
    );

    public static void register(IEventBus bus) {
        MENUS.register(bus);
    }
}