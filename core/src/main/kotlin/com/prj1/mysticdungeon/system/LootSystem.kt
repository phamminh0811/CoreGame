package com.prj1.mysticdungeon.system

import com.github.quillraven.fleks.*
import com.prj1.mysticdungeon.component.*
import ktx.collections.getOrPut
import ktx.log.logger
import kotlin.random.Random

@AllOf([PlayerComponent::class, LootComponent::class, BagComponent::class])
class LootSystem(
    private val playerCmps: ComponentMapper<PlayerComponent>,
    private val lootCmps: ComponentMapper<LootComponent>,
    private val bagCmps: ComponentMapper<BagComponent>,
    private val itemCmps: ComponentMapper<ItemComponent>
) : EntityListener, IteratingSystem(){

    override fun onTickEntity(entity: Entity) {
        when (lootCmps[entity].lootType){
            LootType.COMMON -> {
                addPlayerLoot(entity, 25)
            }
            LootType.RARE -> {
                addPlayerLoot(entity, 50)
            }
            LootType.EPIC -> {
                addPlayerLoot(entity, 100, Random.nextInt(1, 3))
            }
            else -> {

            }
        }

        lootCmps[entity].lootType = LootType.UNDEFINED
    }

    private fun addPlayerLoot(entity: Entity, itemChance: Int, numItems: Int = 1) {
        with(bagCmps[entity]) {
            if (itemChance >= 100 || Random.nextInt(1, 101) <= itemChance) {
                for (i in 0 until numItems) {
                    createItemForBag(entity)
                }
            }
        }
    }

    private fun createItemForBag(entity: Entity) {
        val type = ItemType.random()

        if (type in bagCmps[entity].items.keys()) {
            val amount = bagCmps[entity].items.get(type)
            bagCmps[entity].items.put(type, amount+1)
        } else {
            bagCmps[entity].items.put(type, 1)
        }

        LOG.debug { "Added item of type '$type' to bag: ${bagCmps[entity].items.keys()}" }
    }

    companion object {
        private val LOG = logger<LootSystem>()
    }
}