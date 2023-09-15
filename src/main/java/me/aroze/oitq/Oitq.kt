package me.aroze.oitq

import me.aroze.oitq.util.getClassesInPackage
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

val clorie = Bukkit.getPluginManager().getPlugin("Oitq") as Oitq
val mm = MiniMessage.miniMessage()

class Oitq : JavaPlugin() {

    override fun onEnable() {
        saveDefaultConfig()
        registerListeners()
    }

    override fun onDisable() {

    }

    private fun registerListeners() {
        for (listener in getClassesInPackage("me.aroze.oitq.listeners") { Listener::class.java in it.interfaces })
            Bukkit.getPluginManager().registerEvents(listener.getField("INSTANCE")[null] as Listener, this)
    }

}
