package me.aroze.oitq.listeners

import me.aroze.oitq.listeners.DeathListener.unbreakable
import me.aroze.oitq.mm
import me.aroze.oitq.util.MapUtil.getRandomSpawnpoint
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack

object DeathListener : Listener {

    @EventHandler
    fun onDeath(event: EntityDamageByEntityEvent) {
        val victim = event.entity
        var attacker = event.damager

        if (victim !is Player) return

        if (attacker is Arrow) {
            attacker = attacker.shooter as Player
            handleQuiveringDeath(attacker, victim)
        } else {
            if (attacker !is Player) return
            if (event.finalDamage < victim.health) return // Damage didn't result in kill.
            handleChoppyDeath(attacker, victim)
        }

        if (attacker !is Player) return // just for the smart cast
        if (attacker == victim) return

        event.setDamage(0.0)
        resetPlayer(victim)
        spawnPlayer(victim, true)

        attacker.inventory.addItem(ItemStack(Material.ARROW))
        attacker.inventory.addItem(ItemStack(Material.GOLDEN_APPLE))

    }

    private fun handleChoppyDeath(killer: Player, deather: Player) {
        Bukkit.broadcast(mm.deserialize("<#ff6378>☠ <#ffb3bf>${deather.name} <#e3bac0>got chopped up by <#ffb3bf>${killer.name}"))
    }

    private fun handleQuiveringDeath(killer: Player, deather: Player) {
        Bukkit.broadcast(mm.deserialize("<#ff6378>☠ <#ffb3bf>${deather.name} <#e3bac0>got quivered by <#ffb3bf>${killer.name}"))
    }

    fun resetPlayer(player: Player) {
        player.health = 20.0;
        player.absorptionAmount = 0.0
        player.fireTicks = 0;
        player.freezeTicks = 0;
        player.arrowsInBody = 0;
        player.clearActivePotionEffects()

        player.inventory.clear()
        player.inventory.addItem(ItemStack(Material.IRON_SWORD).unbreakable())
        player.inventory.addItem(ItemStack(Material.BOW).unbreakable())
        player.inventory.addItem(ItemStack(Material.ARROW))

        player.inventory.armorContents = arrayOf(
            ItemStack(Material.CHAINMAIL_BOOTS).unbreakable(),
            ItemStack(Material.AIR),
            ItemStack(Material.CHAINMAIL_CHESTPLATE).unbreakable(),
            ItemStack(Material.AIR)
        )
    }

    fun spawnPlayer(player: Player, respawn: Boolean) {
        player.teleport(getRandomSpawnpoint()!!)
    }

    fun ItemStack.unbreakable(): ItemStack = this.apply {
        itemMeta.isUnbreakable = true
    }

}