package me.aroze.oitq.listeners

import me.aroze.oitq.listeners.DeathListener.resetPlayer
import me.aroze.oitq.listeners.DeathListener.spawnPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

object JoinListener : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        resetPlayer(event.player)
        spawnPlayer(event.player)
    }

}