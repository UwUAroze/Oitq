package me.aroze.oitq

import me.aroze.arozeutils.minecraft.FancyPlugin
import me.aroze.oitq.util.getClassesInPackage
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

val oitq = Bukkit.getPluginManager().getPlugin("Oitq") as Oitq
val mm = MiniMessage.miniMessage()

class Oitq : FancyPlugin() {

    override fun onEnable() {
        saveDefaultConfig()
        registerListeners()
    }

    override fun onPluginDisable() {
        // Plugin shutdown logic
    }

    private fun registerListeners() {
        for (listener in getClassesInPackage("me.aroze.oitq.listeners") { Listener::class.java in it.interfaces })
            Bukkit.getPluginManager().registerEvents(listener.getField("INSTANCE")[null] as Listener, this)
    }

}
