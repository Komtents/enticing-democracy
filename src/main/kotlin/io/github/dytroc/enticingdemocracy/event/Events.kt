package io.github.dytroc.enticingdemocracy.event

import io.github.dytroc.enticingdemocracy.EnticingDemocracy.logger
import io.github.dytroc.enticingdemocracy.EnticingDemocracy.manager
import io.github.dytroc.enticingdemocracy.event.Event.Companion.create
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.TntEntity
import net.minecraft.entity.boss.dragon.EnderDragonEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffectUtil
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.predicate.entity.EntityPredicates
import net.minecraft.world.Difficulty
import kotlin.random.Random.Default.nextBoolean

object Events {

    fun initialize() {
        logger.info("Initialized events!")
        createNegativeEvents()
        createNeutralEvents()
        createPositiveEvents()
    }

    @JvmField
    val jumpingIncapability = create("jumping_incapability", EventDurationType.Temporary(20 * 180), EventEffectType.Negative)

    @JvmField
    val upsideDownScreen = create("upside_down_screen", EventDurationType.Temporary(20 * 30), EventEffectType.Negative)

    @JvmField
    val itemPickingIncapability = create("item_picking_incapability", EventDurationType.Temporary(20 * 150), EventEffectType.Negative)

    @JvmField
    val burnsInDaylight = create("burns_in_daylight", EventDurationType.Temporary(20 * 450  ), EventEffectType.Negative)

    @JvmField
    val weirdControls = create("weird_controls", EventDurationType.Temporary(20 * 90), EventEffectType.Negative)

    @JvmField
    val noMeat = create("no_meat", EventDurationType.Permanent, EventEffectType.Negative)

    @JvmField
    val hearingImpairment = create("hearing_impairment", EventDurationType.Temporary(20 * 900), EventEffectType.Negative)

    @JvmField
    val fast = create("fast", EventDurationType.Temporary(20 * 300), EventEffectType.Negative)

    @JvmField
    val insomnia = create("insomnia", EventDurationType.Permanent, EventEffectType.Negative)

    @JvmField
    val intenseMouseShaking = create("intense_mouse_shaking", EventDurationType.Temporary(20 * 10), EventEffectType.Negative)

    @JvmField
    val inkOverlay = create("ink_overlay", EventDurationType.Temporary(20 * 30), EventEffectType.Negative)

    @JvmField
    val noTrade = create("no_trade", EventDurationType.Permanent, EventEffectType.Negative)

    @JvmField
    val higherSpawnCap = create("higher_spawn_cap", EventDurationType.Permanent, EventEffectType.Negative)

    @JvmField
    val creeperDuplication = create("creeper_duplication", EventDurationType.Permanent, EventEffectType.Negative)

    @JvmField
    val noNaturalRegen = create("no_natural_regen", EventDurationType.Temporary(20 * 600), EventEffectType.Negative)

    @JvmField
    val tntRun = create("tnt_run", EventDurationType.Temporary(20 * 300) {
        it.affectedTNTRunBlocks.clear()
    }, EventEffectType.Negative)

    @JvmField
    val returnedDamage = create("damage_return", EventDurationType.Temporary(20 * 180), EventEffectType.Negative)

    @JvmField
    val movingIncapability = create("moving_incapability", EventDurationType.Temporary(20 * 5), EventEffectType.Negative)

    @JvmField
    val invertedControls = create("inverted_controls", EventDurationType.Temporary(20 * 300), EventEffectType.Negative)

    private fun createNegativeEvents() {
        create("shuffle_inventory", EventDurationType.Once, EventEffectType.Negative) {
            it.playerManager.playerList.forEach { player ->
                player.inventory.main.shuffle()
                player.inventory.armor.shuffle()
            }
        }

        create("drop_all", EventDurationType.Once, EventEffectType.Negative) { server ->
            server.playerManager.playerList.forEach { player ->
                player.inventory.dropAll()
            }
        }

        create("spawn_tnt", EventDurationType.Once, EventEffectType.Negative) { server ->
            server.playerManager.playerList.forEach { player ->
                player.world.spawnEntity(TntEntity(player.world, player.x, player.y, player.z, null))
            }
        }

        create("remove_items", EventDurationType.Once, EventEffectType.Negative) { server ->
            server.playerManager.playerList.forEach { player ->
                player.inventory.main.forEach { if (nextBoolean()) it.count = 0 }
                player.inventory.offHand.forEach { if (nextBoolean()) it.count = 0 }
                player.inventory.armor.forEach { if (nextBoolean()) it.count = 0 }
            }
        }

        create("send_to_the_sky", EventDurationType.Once, EventEffectType.Negative) { server ->
            server.playerManager.playerList.forEach { player ->
                player.teleport(player.x, 750.0, player.z)
            }
        }

        create("hunger", EventDurationType.Once, EventEffectType.Negative) { server ->
            server.playerManager.playerList.forEach { player ->
                player.hungerManager.apply {
                    foodLevel = 0
                    exhaustion = 40f
                    saturationLevel = 0f
                }
            }
        }

        create("weakness", EventDurationType.Once, EventEffectType.Negative) {
            it.playerManager.playerList.forEach { player ->
                player.addStatusEffect(StatusEffectInstance(StatusEffects.HASTE, 20 * 180, 2))
            }
        }

        create("slow_break_speed", EventDurationType.Once, EventEffectType.Negative) {
            it.playerManager.playerList.forEach { player ->
                player.addStatusEffect(StatusEffectInstance(StatusEffects.MINING_FATIGUE, 20 * 180, 2))
            }
        }
    }

    @JvmField
    val slipperyBlocks = create("slippery_blocks", EventDurationType.Temporary(20 * 450), EventEffectType.Neutral)

    @JvmField
    val hideChoiceNames = create("hide_choice_names", EventDurationType.Temporary(20 * 600), EventEffectType.Neutral)

    @JvmField
    val jumpHigh = create("jump_high", EventDurationType.Temporary(20 * 120), EventEffectType.Neutral)

    @JvmField
    val itemMagnet = create("item_magnet", EventDurationType.Temporary(20 * 200), EventEffectType.Neutral)

    @JvmField
    val explosiveArrows = create("explosive_arrows", EventDurationType.Permanent, EventEffectType.Neutral)

    private fun createNeutralEvents() {
        create("remove_random_event", EventDurationType.Once, EventEffectType.Neutral) { _ ->
            manager?.let {
                (it.appliedTemporaryEvents + it.appliedPermanentEvents).randomOrNull()?.let { chosen ->
                    if (chosen.durationType == EventDurationType.Permanent) {
                        it.appliedPermanentEvents.remove(chosen)
                    } else {
                        it.appliedTemporaryEvents.remove(chosen)
                    }
                }
            }
        }

        create("extinction", EventDurationType.Once, EventEffectType.Neutral) {
            it.playerManager.playerList.forEach { player ->
                player.world.getOtherEntities(player, player.boundingBox.expand(25.0), EntityPredicates.VALID_LIVING_ENTITY).forEach { other ->
                    if (!(other.type == EntityType.PLAYER || other.type == EntityType.ENDER_DRAGON)) other.remove(Entity.RemovalReason.DISCARDED)
                }
            }
        }

        create("heal_or_damage", EventDurationType.Once, EventEffectType.Neutral) {
            it.playerManager.playerList.forEach { player ->
                if (nextBoolean()) player.heal(5f) else player.damage(DamageSource.GENERIC, 5f)
            }
        }

        create("clear_potion_effects", EventDurationType.Once, EventEffectType.Neutral) {
            it.playerManager.playerList.forEach { player ->
                player.clearStatusEffects()
            }
        }

        create("random_event", EventDurationType.Once, EventEffectType.Neutral)

        create("hit_all_close_entities", EventDurationType.Once, EventEffectType.Neutral) {
            it.playerManager.playerList.forEach { player ->
                player.world.getOtherEntities(player, player.boundingBox.expand(8.0), EntityPredicates.VALID_LIVING_ENTITY).forEach { other ->
                    other.damage(DamageSource.player(player), 15f)
                }
            }
        }
    }

    @JvmField
    val highAttackSpeed = create("high_attack_speed", EventDurationType.Permanent, EventEffectType.Positive)

    @JvmField
    val notTargeted = create("not_targeted", EventDurationType.Temporary(20 * 600), EventEffectType.Positive)

    @JvmField
    val noFallDamage = create("no_fall_damage", EventDurationType.Temporary(20 * 600), EventEffectType.Positive)

    @JvmField
    val vampire = create("vampire", EventDurationType.Temporary(20 * 300), EventEffectType.Positive)

    private fun createPositiveEvents() {
        create("heal", EventDurationType.Once, EventEffectType.Positive) {
            it.playerManager.playerList.forEach { player -> player.heal(100f) }
        }

        create("full_hunger", EventDurationType.Once, EventEffectType.Positive) {
            it.playerManager.playerList.forEach { player ->
                player.hungerManager.apply {
                    foodLevel = 20
                    exhaustion = 0f
                    saturationLevel = 6f
                }
            }
        }

        create("diamonds_for_you", EventDurationType.Once, EventEffectType.Positive) {
            it.playerManager.playerList.forEach { player ->
                player.inventory.offer(ItemStack(Items.DIAMOND, 8), true)
            }
        }

        create("fire_resistance", EventDurationType.Once, EventEffectType.Positive) {
            it.playerManager.playerList.forEach { player ->
                player.addStatusEffect(StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 20 * 180, 0))
            }
        }

        create("fast_mining", EventDurationType.Once, EventEffectType.Positive) {
            it.playerManager.playerList.forEach { player ->
                player.addStatusEffect(StatusEffectInstance(StatusEffects.HASTE, 20 * 180, 2))
            }
        }

        create("strength", EventDurationType.Once, EventEffectType.Positive) {
            it.playerManager.playerList.forEach { player ->
                player.addStatusEffect(StatusEffectInstance(StatusEffects.STRENGTH, 20 * 180, 1))
            }
        }

        create("totem_of_undying", EventDurationType.Once, EventEffectType.Positive) {
            it.playerManager.playerList.forEach { player ->
                player.inventory.offer(ItemStack(Items.TOTEM_OF_UNDYING), true)
            }
        }

        create("saturation", EventDurationType.Once, EventEffectType.Positive) {
            it.playerManager.playerList.forEach { player ->
                player.addStatusEffect(StatusEffectInstance(StatusEffects.SATURATION, 20 * 180, 1))
            }
        }
    }
}