package org.rebix.stars

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents
import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.fabricmc.fabric.api.event.player.AttackEntityCallback
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.minecraft.command.argument.ItemStackArgumentType
import net.minecraft.component.ComponentType
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.TooltipDisplayComponent
import net.minecraft.entity.EntityType
import net.minecraft.entity.decoration.DisplayEntity
import net.minecraft.entity.passive.ChickenEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.entity.projectile.thrown.EggEntity
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.particle.ParticleTypes
import net.minecraft.scoreboard.Scoreboard
import net.minecraft.scoreboard.ScoreboardCriterion
import net.minecraft.scoreboard.ScoreboardDisplaySlot
import net.minecraft.screen.GenericContainerScreenHandler
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.math.AffineTransformation
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import org.joml.Quaternionf
import org.joml.Vector3f
import java.util.*
import kotlin.math.min


class Stars : ModInitializer {

    val colors: Vector<Formatting> = Vector()


    init {
        instance = this
        colors.add(Formatting.GOLD)
        colors.add(Formatting.LIGHT_PURPLE)
        colors.add(Formatting.AQUA)
        colors.add(Formatting.RED)
        colors.add(Formatting.GREEN)
        colors.add(Formatting.YELLOW)
    }

    fun getStarText(amount: Int): Text {
        if (amount == 0) return Text.empty()
        val remainder = amount % 5
//        println(Integer.parseInt((amount / 5).toString()))

        val starText = Text.literal(" ")
        for (i in 1..min(5, amount)) {
            var color: Formatting?
            if (i <= remainder) {
                color = colors[amount / 5]
            } else {
                color = colors[amount / 5 - 1]
            }
            starText.append(Text.literal("✪").formatted(color))
        }

        return starText
    }

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

        ServerEntityEvents.ENTITY_LOAD.register { entity, world ->

        }





        ServerTickEvents.END_SERVER_TICK.register { server ->
            server.playerManager.playerList.stream().forEach { player ->
                if (player is ServerPlayerEntity) {
                    if (player.isUsingItem || player.handSwinging) {
                        val itemStack = player.getStackInHand(player.activeHand)
                        var sItem = SItem(itemStack)
                        if (sItem.isShortbow) {
                            player.stopUsingItem()
                            // Shortbow logic
                            for (i in 0 until 1) {
                                val arrow = EggEntity(
                                    EntityType.EGG,
                                    player.world as ServerWorld
                                )
                                val direction = player.rotationVector
                                arrow.setPosition(player.x, player.eyeY - 0.1, player.z)
                                arrow.setVelocity(direction.x, direction.y, direction.z, 1.0f, 0.0f)
//                                arrow.addVelocity(direction)
                                player.world.spawnEntity(arrow)
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
                world.iterateEntities().forEach { entity ->
                    if (entity.commandTags.contains("REMOVE")) {
                        entity.kill(world)
                    }
                }

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

        CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, environment ->
            dispatcher.register(
                CommandManager.literal("removetagged").executes { context: CommandContext<ServerCommandSource?>? ->
                    context!!.source!!.world.iterateEntities().forEach { entity ->
                        if (entity.commandTags.contains("REMOVE")) {
                            entity.kill(context.source!!.world)
                        }
                    }
                    1
                }
            )
        }


        CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, environment ->
            dispatcher.register(
                CommandManager.literal("testing").executes { context: CommandContext<ServerCommandSource?>? ->
                    if (context!!.source?.player == null) {
                        context.getSource()!!.sendFeedback({ Text.literal("You are not a player!") }, false)
                    }
                    val player = context.source?.player!!
                    val healthTextEntity =
                        DisplayEntity.TextDisplayEntity(EntityType.TEXT_DISPLAY, player.world).apply {
                            this.updatePosition(player.x, player.y + 2.0, player.z)
                            this.text =
                                Text.literal(100.toString()).append(Text.literal(" ❤").formatted(Formatting.RED))
                            this.billboardMode = DisplayEntity.BillboardMode.CENTER
                            this.addCommandTag("REMOVE")
                        }
                    player.world.spawnEntity(healthTextEntity)
                    healthTextEntity.startRiding(player, true)
                    1
                }
            )
        }


        CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, environment ->
            dispatcher.register(
                CommandManager.literal("updateitem").executes { context: CommandContext<ServerCommandSource?>? ->
                    if (context!!.source?.player == null) {
                        context.getSource()!!.sendFeedback({ Text.literal("You are not a player!") }, false)
                    }
                    val player = context.source?.player!!
                    val itemStack = player.getStackInHand(Hand.MAIN_HAND)
                    val item = SItem(itemStack)
                    item.updateItemStack()
                    1
                }
            )
        }

        CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, environment ->
            dispatcher.register(
                CommandManager.literal("recombobulate").executes { context: CommandContext<ServerCommandSource?>? ->
                    if (context!!.source?.player == null) {
                        context.getSource()!!.sendFeedback({ Text.literal("You are not a player!") }, false)
                    }
                    val player = context.source?.player!!
                    val itemStack = player.getStackInHand(Hand.MAIN_HAND)
                    val item = SItem(itemStack)
                    item.recombobulated = true
                    item.updateItemStack()
                    1
                }
            )
        }

        CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, environment ->
            dispatcher.register(
                CommandManager.literal("analyze").executes { context: CommandContext<ServerCommandSource?>? ->
                    if (context!!.source?.player == null) {
                        context.getSource()!!.sendFeedback({ Text.literal("You are not a player!") }, false)
                    }
                    val player = context.source?.player!!
                    val itemStack = player.getStackInHand(Hand.MAIN_HAND)
                    val item = SItem(itemStack)
                    player.sendMessage(Text.literal(item.id).append(item.name))
                    1
                }
            )
        }
        CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, environment ->
            dispatcher.register(
                CommandManager.literal("setrarity").then(
                    CommandManager.argument("rarity", StringArgumentType.word())
                        .suggests { _, builder ->
                            for (rarity in SRarity.entries) {
                                builder.suggest(rarity.name.lowercase())
                            }
                            builder.buildFuture()
                        }.executes { context: CommandContext<ServerCommandSource?>? ->
                            if (context!!.source?.player == null) {
                                context.getSource()!!.sendFeedback({ Text.literal("You are not a player!") }, false)
                            }
                            val player = context.source?.player!!
                            val itemStack = player.getStackInHand(Hand.MAIN_HAND)
                            val item = SItem(itemStack)
                            val rarityName = StringArgumentType.getString(context, "rarity").uppercase()
                            val rarity = SRarity.valueOf(rarityName)
                            item.rarity = rarity
                            item.updateItemStack()
                            1
                        }
                ))
        }

        CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, environment ->
            dispatcher.register(
                CommandManager.literal("giveitem").then(
                    CommandManager.argument("id", StringArgumentType.string()).then(
                        CommandManager.argument(
                            "item",
                            ItemStackArgumentType.itemStack(registryAccess)
                        ).executes { context: CommandContext<ServerCommandSource?>? ->
                            if (context!!.source?.player == null) {
                                context.getSource()!!.sendFeedback({ Text.literal("You are not a player!") }, false)
                            }
                            val player = context.source?.player!!
                            val id = StringArgumentType.getString(context, "id")
                            val itemStack = ItemStackArgumentType.getItemStackArgument(context, "item").item
                            val item = SItem(id, itemStack)
                            item.updateItemStack()
                            player.inventory.offerOrDrop(item.itemStack)
                            1
                        }
                    )))
        }


        CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, environment ->
            // Register a command that can be used in the game
            dispatcher.register(
                CommandManager.literal("star").executes { context: CommandContext<ServerCommandSource?>? ->
                    if (context!!.source?.player == null) {
                        context.getSource()!!.sendFeedback({ Text.literal("You are not a player!") }, false)
                    }
                    val player = context.source?.player!!


                    var inv = SimpleInventory(9 * 6)
                    val screenHanderFactory = object : NamedScreenHandlerFactory {
                        override fun createMenu(
                            syncId: Int,
                            playerInventory: PlayerInventory,
                            player: PlayerEntity
                        ): ScreenHandler {
                            inventoryMap[syncId] = inv
                            return GenericContainerScreenHandler.createGeneric9x6(
                                syncId,
                                playerInventory,
                                inv
                            )
                        }


                        override fun getDisplayName(): Text = Text.literal("Star Inventory").append(getStarText(11))

                    }


                    val emptySequencedSet: SequencedSet<ComponentType<*>> = LinkedHashSet()
                    var component = TooltipDisplayComponent(true, emptySequencedSet)
                    val item = ItemStack(Items.BLACK_STAINED_GLASS_PANE)
                    item.set(DataComponentTypes.ITEM_NAME, Text.literal(" "))
                    item.set(DataComponentTypes.TOOLTIP_DISPLAY, component)

                    for (i in 0 until inv.size()) {
                        inv.setStack(i, item.copy())
                    }

                    val necronHandle = SItem("NECRON_HANDLE", Items.STICK)
                    necronHandle.name = "Necron's Handle"
                    necronHandle.rarity = SRarity.LEGENDARY
                    necronHandle.type = SItemType.ITEM
                    necronHandle.isDungeon = true
                    necronHandle.baseStats.add(SStat(SStatType.DAMAGE, 100000))
                    necronHandle.baseStats.add(SStat(SStatType.STRENGTH, 1000))

                    necronHandle.updateItemStack()
                    inv.setStack(9 * 1 + 4, necronHandle.itemStack)


                    val sHelmet = SItem("PERFECT_HELMET_13", Items.DIAMOND_HELMET)
                    sHelmet.name = "Perfect Helmet - Tier XIII"
                    sHelmet.rarity = SRarity.LEGENDARY
                    sHelmet.type = SItemType.HELMET
                    sHelmet.reforgeable = true
                    sHelmet.gemstoneSlots.add(SGemstoneSlotType.AMETHYST, true, SGemstoneType.FINE_AMETHYST)
                    sHelmet.gemstoneSlots.add(SGemstoneSlotType.AMETHYST, true, SGemstoneType.FINE_AMETHYST)
                    sHelmet.gemstoneSlots.add(SGemstoneSlotType.AMETHYST, true, SGemstoneType.FINE_AMETHYST)
                    sHelmet.baseStats.add(SStat(SStatType.DEFENSE, 350))
                    sHelmet.baseStats.add(SStatType.DAMAGE, 10000)
                    sHelmet.baseStats.add(SStatType.STRENGTH, 10000)
                    sHelmet.baseStats.add(SStatType.CRIT_CHANCE, 10000)
                    sHelmet.baseStats.add(SStatType.CRIT_DAMAGE, 10000)

                    val terminator = SItem("TERMINATOR", Items.BOW)
                    terminator.name = "Terminator"
                    terminator.rarity = SRarity.LEGENDARY
                    terminator.type = SItemType.BOW
                    terminator.baseStats.add(SStatType.DAMAGE, 310)
                    terminator.baseStats.add(SStatType.STRENGTH, 50)
                    terminator.baseStats.add(SStatType.CRIT_DAMAGE, 250)
                    terminator.baseStats.add(SStatType.BONUS_ATTACK_SPEED, 40)
                    terminator.baseStats.add(SStatType.SHOT_COOLDOWN, 0.5)
                    terminator.isShortbow = true
                    terminator.reforgeable = true

                    terminator.updateItemStack()
                    inv.setStack(0, terminator.itemStack)

                    sHelmet.updateItemStack()
                    inv.setStack(4, sHelmet.itemStack)

                    val lividDagger = SItem("LIVID_DAGGER", Items.IRON_SWORD)
                    lividDagger.name = "Livid Dagger"
                    lividDagger.rarity = SRarity.LEGENDARY
                    lividDagger.baseStats.add(SStatType.GEAR_SCORE, 1039)
                    lividDagger.baseStats.add(SStatType.DAMAGE, 210)
                    lividDagger.baseStats.add(SStatType.STRENGTH, 60)
                    lividDagger.baseStats.add(SStatType.CRIT_CHANCE, 100)
                    lividDagger.baseStats.add(SStatType.CRIT_DAMAGE, 50)
                    lividDagger.baseStats.add(SStatType.BONUS_ATTACK_SPEED, 50)
                    lividDagger.type = SItemType.SWORD
                    lividDagger.reforgeable = true
                    lividDagger.reforge = SReforge.DIRTY
                    lividDagger.stars = 5
                    lividDagger.hotPotatoBooks = 10
                    lividDagger.fumingPotatoBooks = 5
                    lividDagger.isDungeon = true
                    lividDagger.gemstoneSlots.add(SGemstoneSlotType.JASPER, true, SGemstoneType.FINE_JASPER)

                    lividDagger.updateItemStack()
                    inv.setStack(9 * 2 + 4, lividDagger.itemStack)

                    var hype = SItem("HYPERION", Items.IRON_SWORD)
                    hype.name = "Hyperion"
                    hype.rarity = SRarity.LEGENDARY
                    hype.type = SItemType.SWORD
                    hype.reforgeable = true
                    hype.baseStats.add(SStat(SStatType.DAMAGE, 260))
                    hype.baseStats.add(SStat(SStatType.GEAR_SCORE, 715))
                    hype.baseStats.add(SStat(SStatType.STRENGTH, 150))
                    hype.baseStats.add(SStat(SStatType.INTELLIGENCE, 350))
                    hype.baseStats.add(SStat(SStatType.FEROCITY, 30))
                    hype.reforge = SReforge.HEROIC
                    hype.gemstoneSlots.add(SGemstoneSlotType.SAPPHIRE, true, SGemstoneType.PERFECT_SAPPHIRE)
                    hype.gemstoneSlots.add(SGemstoneSlotType.SAPPHIRE, true, SGemstoneType.PERFECT_SAPPHIRE)
                    lividDagger.isDungeon = true

                    hype.updateItemStack()
                    inv.setStack(9 * 5 + 4, hype.itemStack)



                    player.openHandledScreen(screenHanderFactory)

                    1
                }.then(
                    CommandManager.argument("amount", IntegerArgumentType.integer(0, colors.size * 5))
                        .executes { context: CommandContext<ServerCommandSource?>? ->

                            if (context!!.source?.player == null) {
                                context.getSource()!!.sendFeedback({ Text.literal("You are not a player!") }, false)
                            }
                            val player = context.source?.player!!

                            val item = player.getStackInHand(Hand.MAIN_HAND)
                            val sItem = SItem(item)
                            sItem.stars = IntegerArgumentType.getInteger(context, "amount")
                            sItem.updateItemStack()
                            //[434] ♫ [VIP+] Rebbix is holding [Pitchin' Hellfire Rod ✪✪✪✪✪]
                            ///give @s minecraft:white_dye[minecraft:item_model="animated_java:blueprint/blueprint/base"]
                            /*
                            < 125 SkyBlock icons health.pngHP	10
< 165 SkyBlock icons health.pngHP	11
< 230 SkyBlock icons health.pngHP	12
< 300 SkyBlock icons health.pngHP	13
< 400 SkyBlock icons health.pngHP	14
< 500 SkyBlock icons health.pngHP	15
< 650 SkyBlock icons health.pngHP	16
< 800 SkyBlock icons health.pngHP	17
< 1,000 SkyBlock icons health.pngHP	18
< 1,250 SkyBlock icons health.pngHP	19
1,250+ SkyBlock icons health.pngHP	20
                             */
                            1
                        })
            )
        }

        CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, environment ->
            dispatcher.register(
                CommandManager.literal("dummy").executes { context: CommandContext<ServerCommandSource?>? ->
                    val player = context!!.source?.player!!

                    val dummy = SCombatEntity(
                        SEntityType.DUMMY,
                        player.world,
                        Text.literal("Dummy"),
                        position = player.pos
                    )
                    dummy._maxHealth = 1_500_000_000
                    dummy.health = 1_500_000_000
                    1
                }
            )
        }



        AttackEntityCallback.EVENT.register { player, world, hand, entity, _ ->
            val sEntity = entityMap[entity.uuid]
            val sItem = SItem(player.getStackInHand(hand))
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

        ServerTickEvents.END_SERVER_TICK.register { server ->
//            ServerPlayerEntity
        }

        UseItemCallback.EVENT.register { player, world, hand ->
            var pass = true // Event nicht abbrechen
            if (!world.isClient) {
                val itemStack = player.getStackInHand(hand)
                var sItem = SItem(itemStack)


//                if (sItem.isShortbow) {
//                    println("Short bow")
//                    (world as ServerWorld).server.execute {
//                        player.stopUsingItem() // Stops on server
//                        (player as ServerPlayerEntity).networkHandler.sendPacket(
//                            EntityStatusS2CPacket(player, 9.toByte())
//                        )
//
//                        world.server.playerManager.sendToAround(
//                            player,
//                            player.pos.x, player.pos.y, player.pos.z,
//                            64.0,
//                            player.world.registryKey,
//                            EntityStatusS2CPacket(player, 9.toByte())
//                        )
//                    }


//                    pass = false
//                }


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
                            player.x - 500.0,
                            player.y - 500.0,
                            player.z - 500.0,
                            player.x + 500.0,
                            player.y + 500.0,
                            player.z + 500.0
                        )
                    )

                    allEntities.forEach {
                        it.damage(serverWorld, player.damageSources.playerAttack(player), 1.0F)
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
        private val MOD_ID: String = "stars"
        private var test: Boolean = false
        var instance: Stars? = null
        var inventoryMap = mutableMapOf<Int, Inventory>()
        var entityMap = mutableMapOf<UUID, SLivingEntity>()
        val damageIndicatorHandler = DamageIndicatorHandler()
    }
}
