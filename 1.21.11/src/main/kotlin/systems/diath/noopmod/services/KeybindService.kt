package systems.diath.noopmod.services

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.util.Identifier
import org.lwjgl.glfw.GLFW
import systems.diath.noopmod.config.ConfigManager
import systems.diath.noopmod.config.NoOpConfigScreen

/**
 * MC 1.21.11 – KeyBinding.Category wurde ein Record; Instanz via create(Identifier).
 */
class KeybindService(
    private val config: ConfigManager,
    private val marketSync: MarketSyncService,
    private val merchantSync: MerchantSyncService,
) {
    companion object {
        private val CATEGORY: KeyBinding.Category =
            KeyBinding.Category.create(Identifier.of("noopmod", "keybindings"))
    }

    val keyOpenSettings: KeyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding("noopmod.key.open_settings",
            InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, CATEGORY)
    )
    val keyToggleHud: KeyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding("noopmod.key.toggle_hud",
            InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, CATEGORY)
    )
    val keyRefreshMarket: KeyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding("noopmod.key.refresh_market",
            InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, CATEGORY)
    )

    fun registerTick() {
        ClientTickEvents.END_CLIENT_TICK.register { mc ->
            while (keyOpenSettings.wasPressed()) {
                mc.setScreen(NoOpConfigScreen(mc.currentScreen))
            }
            while (keyToggleHud.wasPressed()) {
                config.getConfig().showHud = !config.getConfig().showHud
                config.save()
            }
            while (keyRefreshMarket.wasPressed()) {
                marketSync.refresh()
                merchantSync.refresh()
            }
        }
    }
}
