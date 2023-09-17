package me.aroze.oitq.listeners

import me.aroze.arozeutils.kotlin.type.Randomiser
import me.aroze.arozeutils.minecraft.generic.delay
import me.aroze.arozeutils.minecraft.generic.timer
import me.aroze.oitq.mm
import me.aroze.oitq.util.MapUtil.getRandomSpawnpoint
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.time.Duration

object DeathListener : Listener {

    private val deathTitles = Randomiser(listOf(
        "Awh you dieddd ;c",
        "Gotta work on the aim...",
        "Time for a respawn, buddy!",
        "Swing and a miss!",
        "You're dead, kiddo.",
        "Oopsy woopsy!",
        "Oh no! Anyways...",
        "You suck.",
        "You're a lost cause",
        "Pathetic",
        "Just quit already",
        "I'm sure you'll do better next time!",
        "Sleepin with the fishies again eh?",
        "I'm amazed you even try anymore",
        "You're a waste of a respawn opportunity",
        "Death must be getting tired of you by now...",
        "You're a true underachiever.",
        "I've seen better gameplay from toddlers.",
        "You're like a magnet for failure.",
        "Even zombies have better survival skills.",
        "You died. Want a tutorial on how not to?",
        "You died. It's like you're allergic to success.",
        "Respawning is the only skill you've mastered.",
        "Practice makes perfect, you are the exception.",
        "Death's highlight reel: You died... again."
    ))

    @EventHandler
    fun onDeath(event: EntityDamageByEntityEvent) {
        val victim = event.entity
        var attacker = event.damager

        if (victim !is Player) return

        if (attacker is Arrow) {
            attacker = attacker.shooter as Player
            if (attacker == victim) return
            handleQuiveringDeath(attacker, victim)
        } else {
            if (attacker !is Player) return
            if (event.finalDamage < victim.health) return // Damage didn't result in kill.
            handleChoppyDeath(attacker, victim)
        }

        event.setDamage(0.0)

        playRespawnSequence(victim, attacker)

        attacker.inventory.addItem(ItemStack(Material.ARROW))
        attacker.inventory.addItem(ItemStack(Material.GOLDEN_APPLE))

        attacker.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 100, 1, false, false, false))
        attacker.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 80, 2, false, false, false))
        attacker.addPotionEffect(PotionEffect(PotionEffectType.INCREASE_DAMAGE, 40, 0, false, false, false))

        attacker.playSound(attacker, Sound.BLOCK_AMETHYST_BLOCK_RESONATE, 1.0f, 1.0f)

    }

    private fun handleChoppyDeath(killer: Player, deather: Player) {
        Bukkit.broadcast(mm.deserialize("<#ff6378>☠ <#ffb3bf>${deather.name} <#e3bac0>got chopped up by <#ffb3bf>${killer.name}"))
        killer.sendActionBar(mm.deserialize("<#ffb899>\uD83D\uDDE1 <#baa59b>| <#ffdac9>You've choppied ${deather.name}"))
    }

    private fun handleQuiveringDeath(killer: Player, deather: Player) {
        Bukkit.broadcast(mm.deserialize("<#ff6378>☠ <#ffb3bf>${deather.name} <#e3bac0>got quivered by <#ffb3bf>${killer.name}"))
        killer.sendActionBar(mm.deserialize("<#ffb899>\uD83C\uDFF9 <#baa59b>| <#ffdac9>You've quivered ${deather.name}"))
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

    fun spawnPlayer(player: Player) {
        player.teleport(getRandomSpawnpoint()!!)
    }

    private fun playRespawnSequence(player: Player, killer: Player) {

        player.inventory.clear()
        player.gameMode = GameMode.SPECTATOR
        player.spectatorTarget = player
        player.playSound(player, Sound.ENTITY_ALLAY_DEATH, 1.0f, 1.0f)
        player.playSound(player, Sound.ENTITY_ALLAY_DEATH, 1.0f, 2.0f)

        resetPlayer(player, false, false)

        player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, PotionEffect.INFINITE_DURATION, 0, false, false, false))
        player.addPotionEffect(PotionEffect(PotionEffectType.SLOW, PotionEffect.INFINITE_DURATION, 0, false, false, false))

        val deathTitle = deathTitles.next()
        var ticksDone = 0
        timer({
            ticksDone++

            val title = Title.title(
                mm.deserialize("<#ff6378>☠ $deathTitle"),
                mm.deserialize("<#e3bac0>Respawning in <#ffb3bf>${"%.1f".format((60 - ticksDone) / 20.0)} seconds..."),
                Title.Times.times(Duration.ofMillis(0), Duration.ofMillis(5 * 50), Duration.ofMillis(0))
            )

            player.showTitle(title)

            if (ticksDone == 60) {
                player.clearTitle()
                delay({
                    resetPlayer(player)
                    spawnPlayer(player)
                }, 1)
            }

        }, 60, 1)

    }

    private fun ItemStack.unbreakable(): ItemStack = this.apply {
        val meta = itemMeta
        meta.isUnbreakable = true
        itemMeta = meta
    }

}