package com.prj1.mysticdungeon.component

import java.util.*


enum class StatsType{
    DEFEND, ATTACK, SPEED, NONE
}
class BuffStatsComponent {
    val stats = EnumMap<StatsType, Int>(StatsType::class.java)
}