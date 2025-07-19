package org.rebix.stars

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.entity.EntityType
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import kotlin.experimental.or

class DamageIndicatorHandler {
    //CRIT: White, White, Yellow, Gold, Red   repeat
    //      ✧        7     7      3     ,7     65✧
    val removalList = mutableListOf<Pair<ArmorStandEntity, Long>>()

    init {
        ServerTickEvents.END_SERVER_TICK.register { server ->
            val listCopy = removalList.toList()
            removalList.clear()

            listCopy.forEach { entity ->
                if (entity.second > 20) {
                    if (entity.first.isAlive) {
                        entity.first.kill(entity.first.world as ServerWorld)
                    }
                } else {
                    removalList.add(Pair(entity.first, entity.second + 1))
//                        println(entity.second)

                }

            }
        }
    }

    fun spawnDageIndicator(
        position: Vec3d,
        world: World,
        damage: Long,
        attackerPosition: Vec3d? = null
    ) {
        var pos = position
        if (attackerPosition != null) {
            val direction = attackerPosition.subtract(position).normalize()
            val rotatedDirection = Vec3d(
                direction.x * 0.0 - direction.z * 1.0,
                direction.y,
                direction.x * 1.0 + direction.z * 0.0
            )
            crossProduct(rotatedDirection, Vec3d(0.0, 1.0, 0.0)).normalize().multiply(0.5)
            pos = position.add(direction.multiply(0.5))
            val randomOffset = Vec3d(
                (Math.random() - 0.5),
                (Math.random() - 0.5),
                (Math.random() - 0.5)
            )
            pos = pos.add(randomOffset.multiply(1.25))

        }

        val indicator = ArmorStandEntity(EntityType.ARMOR_STAND, world).apply {
            this.updatePosition(pos.x, pos.y, pos.z)
            this.setNoGravity(true)
            this.isInvisible = true
            this.isCustomNameVisible = true
            this.customName = Text.literal(damage.toString()).formatted(Formatting.GRAY)
            this.addCommandTag("REMOVE")
            this.dataTracker.set(
                ArmorStandEntity.ARMOR_STAND_FLAGS,
                (this.dataTracker.get(ArmorStandEntity.ARMOR_STAND_FLAGS) or 0x10)
            )
        }
        world.spawnEntity(indicator)

        removalList.add(Pair(indicator, 0L))
    }

    private fun crossProduct(a: Vec3d, b: Vec3d): Vec3d {
        return Vec3d(
            a.y * b.z - a.z * b.y,
            a.z * b.x - a.x * b.z,
            a.x * b.y - a.y * b.x
        )
    }
}