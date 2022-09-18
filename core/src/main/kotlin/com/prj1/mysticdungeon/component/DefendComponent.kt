package com.prj1.mysticdungeon.component

data class DefendComponent (
    var shieldDef: Int = 0,
    var isShielding: Boolean = false,
    var def: Int = 0
) {
    val totalShield: Int
        get() = if (isShielding) def + shieldDef else def
}