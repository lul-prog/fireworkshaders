package com.fireworkshaders;

import com.fireworkshaders.menu.PyrotechnicWorkstationMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, FireworkShaders.MOD_ID);

    public static final RegistryObject<MenuType<PyrotechnicWorkstationMenu>> PYROTECHNIC_WORKSTATION =
            MENUS.register("pyrotechnic_workstation",
                    () -> IForgeMenuType.create(PyrotechnicWorkstationMenu::new));

    private ModMenus() {
    }
}
