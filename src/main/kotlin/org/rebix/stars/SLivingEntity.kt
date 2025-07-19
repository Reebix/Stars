package org.rebix.stars

import net.minecraft.component.DataComponentTypes
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.decoration.DisplayEntity
import net.minecraft.entity.decoration.InteractionEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import kotlin.math.max


class SLivingEntity(
    val type: SEntityType,
    val world: World,
    val name: Text = Text.empty(),
    val position: Vec3d = Vec3d.ZERO,
    var _health: Long = 100
) {
    var health = _health
        set(value) {
            field = max(0, value)
            updateHealthText()
        }

    fun interface OnHitListener {
        fun onHit(entity: SLivingEntity): Boolean
    }

    var hitboxWidth = type.defaultHitbox.first
    var hitboxHeight = type.defaultHitbox.second

    var interactionEntity: InteractionEntity
    var healthTextEntity: DisplayEntity.TextDisplayEntity

    var parts: MutableList<Entity> = mutableListOf()

    init {
        interactionEntity = InteractionEntity(
            EntityType.INTERACTION,
            world
        ).apply {
            this.updatePosition(position.x, position.y, position.z)
            this.interactionWidth = hitboxWidth
            this.interactionHeight = hitboxHeight
        }
        interactionEntity.addCommandTag("REMOVE")
        interactionEntity.customName = name
        interactionEntity.isCustomNameVisible = true
        Stars.entityMap[interactionEntity.uuid] = this
//        print("Spawning entity with UUID: ${interactionEntity.uuid} at position: $position")
        world.spawnEntity(interactionEntity)

        healthTextEntity = DisplayEntity.TextDisplayEntity(EntityType.TEXT_DISPLAY, world).apply {
            this.updatePosition(position.x, position.y + hitboxHeight, position.z)
            this.text = Text.literal(health.toString()).append(Text.literal(" ❤").formatted(Formatting.RED))
            this.billboardMode = DisplayEntity.BillboardMode.CENTER
            this.addCommandTag("REMOVE")
        }
        world.spawnEntity(healthTextEntity)

        for (part in type.parts) {
            val entity = DisplayEntity.ItemDisplayEntity(EntityType.ITEM_DISPLAY, world)
            val entityPos = part.first.add(position)
            entity.updatePosition(entityPos.x, entityPos.y, entityPos.z)
            entity.itemStack = ItemStack(Items.WHITE_DYE)
            entity.itemStack.set(
                DataComponentTypes.ITEM_MODEL,
                Identifier.of(part.second)
            )
            parts.add(entity)
            entity.addCommandTag("REMOVE")
            world.spawnEntity(entity)
        }


    }

    fun updateHitbox() {
        interactionEntity.interactionWidth = hitboxWidth
        interactionEntity.interactionHeight = hitboxHeight
    }

    fun updateHealthText() {
        healthTextEntity.text = Text.literal(health.toString()).append(Text.literal(" ❤").formatted(Formatting.RED))
    }


    private val onHitListeners = mutableListOf<OnHitListener>()

    fun addOnHitListener(listener: OnHitListener) {
        onHitListeners.add(listener)
    }

    fun removeOnHitListener(listener: OnHitListener) {
        onHitListeners.remove(listener)
    }

    fun onHit(attacker: Entity? = null) {
        val attackerPos = attacker?.pos
        Stars.damageIndicatorHandler.spawnDageIndicator(
            position.add(0.0, hitboxHeight.toDouble() * 0.75, 0.0),
            world,
            1,
            attackerPos
        )

        onHitListeners.forEach { if (!it.onHit(this)) return }

        health = max(0, health - 1)
        if (health <= 0) {
            kill()
        } else {
            updateHealthText()
        }

    }

    fun kill() {
        interactionEntity.kill(world as ServerWorld?)
        healthTextEntity.kill(world)
        parts.forEach { it.kill(world) }
        Stars.entityMap.remove(interactionEntity.uuid)
    }


}