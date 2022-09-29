package com.prj1.mysticdungeon.component

data class LifeComponent (
    var life: Float = 50f,
    var max:Float = 50f,
    var regeneration : Float = 0f,
    var takeDamage: Float = 0f,
){
    val isDead: Boolean
        get() = life <= 0f
}