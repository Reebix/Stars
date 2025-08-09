package org.rebix.stars.combat.entity

import net.minecraft.component.DataComponentTypes
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnReason
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
import org.rebix.stars.Stars
import org.rebix.stars.combat.DamageIndicatorStyleType
import kotlin.math.max


open class SLivingEntity(
    val type: SEntityType,
    val world: World,
    val name: Text = Text.empty(),
    var position: Vec3d = Vec3d.ZERO,
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

    var hitboxWidth = type.defaultHitbox?.first ?: 0.8f
    var hitboxHeight = type.defaultHitbox?.second ?: 1.9f

    var interactionEntity: InteractionEntity? = null
    var baseEntity: Entity? = null
//    var healthTextEntity: DisplayEntity.TextDisplayEntity

    var parts: MutableList<Entity> = mutableListOf()
    var healthText = Text.empty()

    init {
        if (type.defaultHitbox != null) {
            interactionEntity = InteractionEntity(
                EntityType.INTERACTION,
                world
            ).apply {
                this.updatePosition(position.x, position.y, position.z)
                this.interactionWidth = hitboxWidth
                this.interactionHeight = hitboxHeight
            }
            baseEntity = interactionEntity

        } else {
            baseEntity = type.baseEntity!!.create(world, SpawnReason.LOAD)
        }
        baseEntity!!.updatePosition(position.x, position.y, position.z)
        baseEntity!!.customName = name
        baseEntity!!.isCustomNameVisible = true
        baseEntity!!.addCommandTag("REMOVE")
        Stars.entityMap[baseEntity!!.uuid] = this
        world.spawnEntity(baseEntity!!)

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
        interactionEntity?.interactionWidth = hitboxWidth
        interactionEntity?.interactionHeight = hitboxHeight
    }

    fun updateName() {
        val newName = name.copy()

        baseEntity!!.customName = newName.append(healthText)

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

        parts.forEach { it.kill(world as ServerWorld?) }
        Stars.entityMap.remove(baseEntity?.uuid)
        baseEntity?.kill(world as ServerWorld?)
    }


}