package me.aroze.oitq.listeners

import me.aroze.oitq.listeners.DeathListener.unbreakable
import me.aroze.oitq.mm
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

        event.setDamage(0.0)
        resetPlayer(victim)

        attacker.inventory.addItem(ItemStack(Material.ARROW))
        attacker.inventory.addItem(ItemStack(Material.GOLDEN_APPLE))

    }

    private fun handleChoppyDeath(killer: Player, deather: Player) {
        Bukkit.broadcast(mm.deserialize("<#ff6378>☠ <#ffb3bf>${deather.name} <#e3bac0>got chopped up by <#ffb3bf>${killer.name}"))
    }

    private fun handleQuiveringDeath(killer: Player, deather: Player) {
        Bukkit.broadcast(mm.deserialize("<#ff6378>☠ <#ffb3bf>${deather.name} <#e3bac0>got quivered by <#ffb3bf>${killer.name}"))
    }

    private fun resetPlayer(player: Player) {
        player.health = 20.0;
        player.absorptionAmount = 0.0
        player.fireTicks = 0;
        player.freezeTicks = 0;
        player.clearActivePotionEffects()

        player.inventory.clear()
        player.inventory.addItem(ItemStack(Material.IRON_SWORD).unbreakable())
        player.inventory.addItem(ItemStack(Material.BOW).unbreakable())
        player.inventory.addItem(ItemStack(Material.ARROW))

        player.inventory.armorContents = arrayOf(
            ItemStack(Material.AIR),
            ItemStack(Material.CHAINMAIL_CHESTPLATE).unbreakable(),
            ItemStack(Material.AIR),
            ItemStack(Material.CHAINMAIL_BOOTS).unbreakable()
        )
    }

    fun ItemStack.unbreakable(): ItemStack = this.apply {
        itemMeta.isUnbreakable = true
    }

}