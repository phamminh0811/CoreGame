@file:JvmName("Lwjgl3Launcher")

package com.prj1.mysticdungeon.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.prj1.mysticdungeon.MysticDungeon

/** Launches the desktop (LWJGL3) application. */
fun main() {
    Lwjgl3Application(MysticDungeon(), Lwjgl3ApplicationConfiguration().apply {
        setTitle("MysticDungeon")
        setWindowedMode(732, 412)
        setWindowIcon(*(arrayOf(128, 64, 32, 16).map { "libgdx$it.png" }.toTypedArray()))
    })
}
