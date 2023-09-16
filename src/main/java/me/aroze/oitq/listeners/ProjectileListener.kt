package me.aroze.oitq.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileHitEvent

object ProjectileListener : Listener {

    @EventHandler
    fun onProjectileLand(event: ProjectileHitEvent) {
        event.entity.remove()
    }

}