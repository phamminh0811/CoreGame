package com.prj1.mysticdungeon.component

import kotlin.random.Random

enum class ItemType{
    UNDEFINED, HELMET, ARMOR,  SHIELD, SWORD, GAUNTLET, SHOES, PANTS;

    companion object{
        private val VALUES = values()

        fun random() = VALUES[Random.nextInt(1, VALUES.size -1)]

    }
}

class ItemComponent {
    var itemType = ItemType.UNDEFINED
    var amount = 1
}