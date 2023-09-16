package me.aroze.oitq.listeners

import me.aroze.arozeutils.minecraft.generic.async
import me.aroze.arozeutils.minecraft.generic.delay
import me.aroze.arozeutils.minecraft.generic.sync
import me.aroze.arozeutils.minecraft.generic.timer
import me.aroze.oitq.listeners.DeathListener.unbreakable
import me.aroze.oitq.mm
import me.aroze.oitq.oitq
import me.aroze.oitq.util.MapUtil.getRandomSpawnpoint
import net.kyori.adventure.title.Title
import net.kyori.adventure.title.TitlePart
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitScheduler
import java.time.Duration
import kotlin.math.ceil
import kotlin.random.Random

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

        victim.inventory.clear()
        victim.gameMode = GameMode.SPECTATOR
        victim.teleport(attacker)

        spawnPlayer(victim, true)

        attacker.inventory.addItem(ItemStack(Material.ARROW))
        attacker.inventory.addItem(ItemStack(Material.GOLDEN_APPLE))

        attacker.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 60, 0, false, false, false))
        attacker.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 60, 0, false, false, false))

    }

    private fun handleChoppyDeath(killer: Player, deather: Player) {
        Bukkit.broadcast(mm.deserialize("<#ff6378>☠ <#ffb3bf>${deather.name} <#e3bac0>got chopped up by <#ffb3bf>${killer.name}"))
    }

    private fun handleQuiveringDeath(killer: Player, deather: Player) {
        Bukkit.broadcast(mm.deserialize("<#ff6378>☠ <#ffb3bf>${deather.name} <#e3bac0>got quivered by <#ffb3bf>${killer.name}"))
    }

    fun resetPlayer(player: Player, resetGamemode: Boolean = true, giveBackInventory: Boolean = true) {
        player.health = 20.0;
        player.absorptionAmount = 0.0
        player.fireTicks = 0;
        player.freezeTicks = 0;
        player.arrowsInBody = 0;

        player.inventory.clear()

        if (giveBackInventory) {

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

        if (resetGamemode) player.gameMode = GameMode.ADVENTURE
        player.clearTitle()
        player.clearActivePotionEffects()
    }

    fun spawnPlayer(player: Player, respawn: Boolean) {

        if (!respawn) {
            player.teleport(getRandomSpawnpoint()!!)
            return
        }

        resetPlayer(player, false, false)

        player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, PotionEffect.INFINITE_DURATION, 1, false, false, false))
        player.addPotionEffect(PotionEffect(PotionEffectType.SLOW, PotionEffect.INFINITE_DURATION, 1, false, false, false))

        val rand = Random.nextInt(4)
        var theText = "";
        if(rand == 0) {theText = "<#ff6378>☠ Awh you dieddd ;c"}
        if(rand == 1) {theText = "<#ff6378>☠ Back to the locker with you..."}
        if(rand == 2) {theText = "<#ff6378>☠ Gutter ball!"}
        if(rand == 3) {theText = "<#ff6378>☠ Gotta work on the aim..."}

        var ticksDone = 0
        timer({
            ticksDone++

            val title = Title.title(
                mm.deserialize(theText),
                mm.deserialize("<#e3bac0>Respawning in <#ffb3bf>${"%.1f".format((60 - ticksDone) / 20.0)} seconds..."),
                Title.Times.times(Duration.ofMillis(0), Duration.ofMillis(5 * 50), Duration.ofMillis(0))
            )

            player.showTitle(title)

            if (ticksDone == 60) {
                player.clearTitle()
                delay({
                    resetPlayer(player)
                    player.teleport(getRandomSpawnpoint()!!)
                }, 1)
            }

        }, 60, 1)


    }

    fun ItemStack.unbreakable(): ItemStack = this.apply {
        itemMeta.isUnbreakable = true
    }

}