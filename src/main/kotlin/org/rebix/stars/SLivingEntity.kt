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


open class SLivingEntity(
    val type: SEntityType,
    val world: World,
    val name: Text = Text.empty(),
    val position: Vec3d = Vec3d.ZERO,
    _health: Long = 100,
    var _maxHealth: Long = 100
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
//    var healthTextEntity: DisplayEntity.TextDisplayEntity

    var parts: MutableList<Entity> = mutableListOf()
    var healthText = Text.empty()

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

//        healthTextEntity = DisplayEntity.TextDisplayEntity(EntityType.TEXT_DISPLAY, world).apply {
//            this.updatePosition(position.x, position.y + hitboxHeight, position.z)
//            this.text = Text.empty()
//            this.billboardMode = DisplayEntity.BillboardMode.CENTER
//            this.addCommandTag("REMOVE")
//        }
//        world.spawnEntity(healthTextEntity)

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

        updateHealthText()


    }

    fun updateHitbox() {
        interactionEntity.interactionWidth = hitboxWidth
        interactionEntity.interactionHeight = hitboxHeight
    }

    fun updateName() {
        val newName = name.copy()
        interactionEntity.customName = newName.append(healthText)
    }

    fun updateHealthText() {
        if (_maxHealth <= 0) {
            healthText =
                Text.literal(" N/A").formatted(Formatting.RED).append(Text.literal("❤").formatted(Formatting.RED))
            updateName()
            return
        }

        val healthString: String =
            if (_maxHealth / 100_000 >= 1) if (health / 1_000_000 >= 1) if (health / 1_000_000_000 >= 1) "${
                String.format("%.1f", health.toDouble() / 1_000_000_000).replace(",", ".").replace(".0", "")
            }B" else "${
                String.format("%.1f", health.toDouble() / 1_000_000).replace(",", ".").replace(".0", "")
            }M" else "${health / 1_000}k" else health.toString()

        val maxHealthString =
            if (_maxHealth / 100_000 >= 1) if (_maxHealth / 1_000_000 >= 1) if (_maxHealth / 1_000_000_000 >= 1) "${
                String.format("%.1f", _maxHealth.toDouble() / 1_000_000_000).replace(",", ".").replace(".0", "")
            }B" else "${
                String.format("%.1f", _maxHealth.toDouble() / 1_000_000).replace(",", ".").replace(".0", "")
            }M" else "${_maxHealth / 1_000}k" else _maxHealth.toString()
        healthText =
            Text.literal(" $healthString")
                .formatted(if (_maxHealth / 2 >= health) Formatting.YELLOW else Formatting.GREEN)
                .append(
                    Text.literal("/").formatted(
                        Formatting.WHITE
                    )
                ).append(Text.literal(maxHealthString).formatted(Formatting.GREEN))
                .append(Text.literal("❤").formatted(Formatting.RED))
        updateName()
    }


    private val onHitListeners = mutableListOf<OnHitListener>()

    fun addOnHitListener(listener: OnHitListener) {
        onHitListeners.add(listener)
    }

    fun removeOnHitListener(listener: OnHitListener) {
        onHitListeners.remove(listener)
    }

    fun onHit(
        attackerPosition: Vec3d? = null,
        hitType: DamageIndicatorStyleType = DamageIndicatorStyleType.NORMAL,
        damage: Long = 1
    ) {
        val attackerPos = attackerPosition
        Stars.damageIndicatorHandler.spawnDageIndicator(
            position.add(0.0, hitboxHeight.toDouble() * 0.75, 0.0),
            world,
            damage,
            attackerPos,
            hitType
        )

        onHitListeners.forEach { if (!it.onHit(this)) return }

        health = max(0, health - damage)
        if (health <= 0) {
            kill()
        } else {
            updateHealthText()
        }

    }

    fun kill() {
        interactionEntity.kill(world as ServerWorld?)
        parts.forEach { it.kill(world) }
        Stars.entityMap.remove(interactionEntity.uuid)
    }


}