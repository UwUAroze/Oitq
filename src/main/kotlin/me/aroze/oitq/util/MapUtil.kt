package me.aroze.oitq.util

import me.aroze.oitq.Oitq
import me.aroze.oitq.oitq
import org.bukkit.Location
import kotlin.random.Random

object MapUtil {

    fun getRandomSpawnpoint(): Location? {
        val spawnpoints = oitq.config.get("spawnpoints") as List<List<*>>

        if (spawnpoints.isNotEmpty()) {
            val randomIndex = Random.nextInt(spawnpoints.size)
            val spawnpoint = spawnpoints[randomIndex]

            if (spawnpoint.size >= 6) {
                val x = spawnpoint[0] as Double + getRandomRadius(spawnpoint)
                val y = spawnpoint[1] as Double
                val z = spawnpoint[2] as Double + getRandomRadius(spawnpoint)
                val yaw = (spawnpoint[3] as Double).toFloat()
                val pitch = (spawnpoint[4] as Double).toFloat()

                return Location(oitq.server.getWorld("csgo"), x, y, z, yaw, pitch)
            }
        }

        return null

    }

    private fun getRandomRadius(spawnpoint: List<*>): Double {
        val radius = Random.nextDouble() * (spawnpoint[5] as Double)
        return if (Random.nextBoolean()) radius else radius * -1
    }

}