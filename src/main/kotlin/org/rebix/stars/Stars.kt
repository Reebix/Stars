package org.rebix.stars

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents
import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.fabricmc.fabric.api.event.player.AttackEntityCallback
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.minecraft.entity.EntityType
import net.minecraft.entity.decoration.DisplayEntity
import net.minecraft.entity.passive.ChickenEntity
import net.minecraft.entity.projectile.ArrowEntity
import net.minecraft.inventory.Inventory
import net.minecraft.particle.ParticleTypes
import net.minecraft.scoreboard.Scoreboard
import net.minecraft.scoreboard.ScoreboardCriterion
import net.minecraft.scoreboard.ScoreboardDisplaySlot
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Formatting
import net.minecraft.util.math.AffineTransformation
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import org.joml.Quaternionf
import org.joml.Vector3f
import org.rebix.stars.combat.DamageIndicatorHandler
import org.rebix.stars.combat.SCombatEntity
import org.rebix.stars.combat.entity.SEntityType
import org.rebix.stars.combat.entity.SLivingEntity
import org.rebix.stars.dimensions.ModDimensions
import org.rebix.stars.item.SItem
import org.rebix.stars.stats.SStatHandler
import java.util.*


class Stars : ModInitializer {


    init {
        instance = this

    }

//    val invisList = mutableListOf<Pair<ArrowEntity, Long>>()

    fun animateHit(
        entity: DisplayEntity.ItemDisplayEntity,
    ) {
        dummyAnim = Pair(
            entity,
            0
        )
    }


    var dummyAnim: Pair<DisplayEntity.ItemDisplayEntity, Int>? = null

    override fun onInitialize() {
        // This is where you can initialize your mod. This method is called when Minecraft is starting.
        // You can use this to register commands, events, and other mod-related functionality.
        println("Stars has been initialized!")

        ModDimensions.register()

        CommandRegistry().registerCommands()

        ServerTickEvents.END_SERVER_TICK.register { server ->
            entityMap.forEach { (_, entity) ->
                if (entity is SCombatEntity) {
                    entity.update()
                }
            }

            server.playerManager.playerList.stream().forEach { player ->
                if (player is ServerPlayerEntity) {
                    if (player.isUsingItem || player.handSwinging) {
                        val itemStack = player.getStackInHand(player.activeHand)
                        var sItem = SItem(itemStack)
                        if (sItem.isShortbow) {
                            player.stopUsingItem()
                            // Shortbow logic
                            for (i in 0 until 1) {
                                val arrow = ArrowEntity(
                                    EntityType.ARROW,
                                    player.world as ServerWorld
                                )
                                val direction = player.rotationVector.normalize()
                                val factor = 0.075
                                val forewardOffset = 0.5
                                val velocity: Float = 2f
                                val spawnBase = Vec3d(player.x, player.eyeY - 0.1, player.z).add(
                                    direction.multiply(forewardOffset)
                                )
                                val rightDirection = Vec3d(
                                    -direction.z,
                                    0.0,
                                    direction.x
                                ).normalize()

                                val leftDirection = rightDirection.multiply(-1.0)

                                val arrowRight = ArrowEntity(
                                    EntityType.ARROW,
                                    player.world as ServerWorld
                                )
                                arrowRight.setPosition(spawnBase)
                                arrowRight.setVelocity(
                                    direction.x + rightDirection.x * factor,
                                    direction.y,
                                    direction.z + rightDirection.z * factor,
                                    velocity, 0.0f
                                )
                                player.world.spawnEntity(arrowRight)

                                val arrowLeft = ArrowEntity(
                                    EntityType.ARROW,
                                    player.world as ServerWorld
                                )
                                arrowLeft.setPosition(spawnBase)
                                arrowLeft.setVelocity(
                                    direction.x + leftDirection.x * factor,
                                    direction.y,
                                    direction.z + leftDirection.z * factor,
                                    velocity, 0.0f
                                )
                                player.world.spawnEntity(arrowLeft)
                                arrow.setPosition(spawnBase)
                                arrow.setVelocity(direction.x, direction.y, direction.z, velocity, 0.0f)
                                arrowLeft.owner = player
                                arrowRight.owner = player
                                arrow.owner = player
//                                val destroyPacket = EntitiesDestroyS2CPacket(
//                                    arrow.id, arrowRight.id, arrowLeft.id
//                                )
                                player.world.spawnEntity(arrow)
//                                player.networkHandler.sendPacket(destroyPacket)

//                                invisList.add(Pair(arrow, 0))
//                                invisList.add(Pair(arrowRight, 0))
//                                invisList.add(Pair(arrowLeft, 0))
                            }
                        }
                    }
                }
            }



            if (dummyAnim != null) {
                val i = dummyAnim!!.second
                val j = if (i < 5) i else 10 - i
                dummyAnim!!.first.setTransformation(
                    AffineTransformation(
                        Vector3f(0.0f, 0.0f, 0.0f),
                        Quaternionf(0.0, 0.0, 0.0, 1.0),
                        Vector3f(1f, 1f, 1f),
                        Quaternionf(0.0, 0.0, -j * 0.01, 1.0)
                    )
                )
                if (i >= 10) {
                    dummyAnim = null
                    return@register
                }
                dummyAnim = Pair(dummyAnim!!.first, i + 1)
            }
        }


        ServerLifecycleEvents.SERVER_STARTED.register { server ->
            server.worlds.forEach { world ->


                val dummy = SCombatEntity(
                    SEntityType.DUMMY,
                    world,
                    Text.literal("Dummy").formatted(Formatting.GOLD),
                    position = Vec3d(-788.50, 115.0, 1753.5),
                    _maxHealth = 0
                )
                dummy.addOnHitListener { dummy ->
                    val body = dummy.parts.last() as DisplayEntity.ItemDisplayEntity
                    animateHit(body)
                    false
                }


            }

        }



        ServerWorldEvents.LOAD.register { server, world ->
            val scoreboard: Scoreboard = server.scoreboard
            val healthObjective = scoreboard.getNullableObjective("health")
            if ((healthObjective == null) && (scoreboard.objectives.find { it.name == "health" } == null)) {
                scoreboard.addObjective(
                    "health",
                    ScoreboardCriterion.DUMMY,
                    Text.literal("❤").formatted(Formatting.RED),
                    ScoreboardCriterion.RenderType.INTEGER,
                    true,
                    null
                )
            }

            scoreboard.setObjectiveSlot(ScoreboardDisplaySlot.BELOW_NAME, healthObjective)
            return@register
        }





        AttackEntityCallback.EVENT.register { player, world, hand, entity, _ ->
            val sEntity = entityMap[entity.uuid]
            val sItem = SItem(player.getStackInHand(hand))
            if (!sItem.isShortbow) {
//            player.sendMessage(
//                Text.literal("You attacked ${sEntity?.name ?: "an entity"} with ${sItem.name}")
//                    .formatted(Formatting.GOLD),
//                false
//            )
                val handler = SStatHandler()
                handler.statManager = sItem.effectiveStats
                val damage = handler.calcDamage()
//            player.sendMessage(
//                Text.literal("You dealt $damage damage to ${sEntity?.name ?: "an entity"}")
//                    .formatted(Formatting.RED),
//                false
//            )
                val combatEntity = sEntity as? SCombatEntity
                combatEntity?.damage(damage.first, player.pos, damage.second)
            }
            ActionResult.PASS
        }


        AttackBlockCallback.EVENT.register { player, world, hand, pos, direction ->
            var cancel = false // Event nicht abbrechen
            if (!world.isClient) {
                val itemStack = player.getStackInHand(hand)
                var sItem = SItem(itemStack)

                if (sItem.isShortbow) {
                    cancel = true
                }
            }

            if (cancel) ActionResult.FAIL else ActionResult.PASS
        }


        UseEntityCallback.EVENT.register { player, world, hand, entity, _ ->
            var pass = true // Event nicht abbrechen
            if (!world.isClient) {
                val itemStack = player.getStackInHand(hand)
                var sItem = SItem(itemStack)

                if (sItem.isShortbow) {
                    player.setCurrentHand(hand)
                    pass = false
                }
            }
            if (pass) ActionResult.PASS else ActionResult.FAIL
        }

        UseItemCallback.EVENT.register { player, world, hand ->
            var pass = true // Event nicht abbrechen
            if (!world.isClient) {
                val itemStack = player.getStackInHand(hand)
                var sItem = SItem(itemStack)

                if (sItem.id == "HYPERION") {
                    player.sendMessage(Text.literal("Wither Impact!"), false)

                    var serverWorld = world as ServerWorld
                    serverWorld.spawnParticles(
                        ParticleTypes.EXPLOSION,
                        player.x,
                        player.y + 1F,
                        player.z,
                        20,
                        0.0,
                        0.0,
                        0.0,
                        0.01
                    )
                    val allEntities = serverWorld.getOtherEntities(
                        player, Box(
                            player.x - 5.0,
                            player.y - 5.0,
                            player.z - 5.0,
                            player.x + 5.0,
                            player.y + 5.0,
                            player.z + 5.0
                        )
                    )

                    allEntities.forEach {
                        val entity = entityMap[it.uuid]
                        entity?.let { sCombatEntity ->
                            if (sCombatEntity is SCombatEntity) {
                                val handler = SStatHandler()
                                handler.statManager = sItem.effectiveStats
                                val damage = handler.calcAbilityDamage(10_000.0, 0.3)
                                sCombatEntity.damage(damage.first, player.pos, damage.second)
                            }
                        }
                    }

                }


                if (sItem.id == "EGG") {
                    player.sendMessage(Text.literal("Egg!"), false)

                    var serverWorld = world as ServerWorld

                    val allEntities = serverWorld.getOtherEntities(
                        player, Box(
                            player.x - 50.0,
                            player.y - 50.0,
                            player.z - 50.0,
                            player.x + 50.0,
                            player.y + 50.0,
                            player.z + 50.0
                        )
                    )

                    allEntities.forEach {
                        val chicken: ChickenEntity =
                            EntityType.get("minecraft:chicken").get().create(world, null) as ChickenEntity
                        chicken.setPosition(it.x, it.y, it.z)
                        serverWorld.spawnEntity(chicken)
                    }
                }


            }
            if (pass)
                ActionResult.PASS
            else ActionResult.FAIL
        }

    }

    companion object {
        val MOD_ID: String = "stars"
        private var test: Boolean = false
        var instance: Stars? = null
        var inventoryMap = mutableMapOf<Int, Inventory>()
        var entityMap = mutableMapOf<UUID, SLivingEntity>()
        val damageIndicatorHandler = DamageIndicatorHandler()
    }
}
