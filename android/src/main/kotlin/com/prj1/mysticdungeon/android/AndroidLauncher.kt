package com.prj1.mysticdungeon.android

import android.os.Bundle

import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.prj1.mysticdungeon.MysticDungeon
import com.prj1.mysticdungeon.android.screen.GameScreen

/** Launches the Android application. */
class AndroidLauncher : AndroidApplication() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize(MysticDungeon(), AndroidApplicationConfiguration().apply {
            // Configure your application here
        })
    }
}
