package com.prj1.mysticdungeon.component

import com.github.quillraven.fleks.Entity

enum class LootType{
    UNDEFINED, COMMON, RARE, EPIC
}
class LootComponent {
    var lootType = LootType.UNDEFINED
}