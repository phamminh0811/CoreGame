package com.prj1.mysticdungeon.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.prj1.mysticdungeon.MysticDungeon
import kotlinx.coroutines.launch
import ktx.actors.plus
import ktx.actors.plusAssign
import ktx.app.KtxScreen
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.scene2d.actors
import ktx.scene2d.label
import ktx.scene2d.stack
import ktx.scene2d.table


private const val ACTOR_FADE_IN_TIME = 0.5f
private const val ACTOR_FADE_OUT_TIME = 1f

class LoadingScreen(
    private val game: MysticDungeon,
    private val introStage: Stage = game.introStage,
    private val assets: AssetStorage = game.assets
): KtxScreen {
    private lateinit var touchToBeginLabel: Label

    override fun show() {
        setupUI()
        KtxAsync.launch {
            assetsLoaded()
        }
    }

    override fun hide() {
        introStage.clear()
    }
    private fun setupUI() {
        introStage.actors {
            table {
                defaults().fillX().expandX()
                label("Loading game","gradient"){
                    wrap = true
                    setAlignment(Align.center)
                }
                row()

                touchToBeginLabel = label("Touch To Begin","default"){
                    wrap = true
                    setAlignment(Align.center)
                    color.a = 0f
                }
                row()

                stack { cell ->
                    label("Loading...", "default"){
                        setAlignment(Align.center)
                    }
                    cell.padLeft(5f).padRight(5f)
                }
                setFillParent(true)
                pack()
            }
        }
    }


    private fun assetsLoaded() {
        game.addScreen(GameScreen())
        touchToBeginLabel += forever(sequence(fadeIn(ACTOR_FADE_IN_TIME) + fadeOut(ACTOR_FADE_OUT_TIME)))
    }

    override fun resize(width: Int, height: Int) {
        introStage.viewport.update(width, height, true)
    }

    override fun render(delta: Float) {
        if(assets.progress.isFinished && Gdx.input.justTouched() ){
            game.removeScreen(LoadingScreen::class.java)
            dispose()
            game.setScreen<GameScreen>()
        }
        introStage.run {
            viewport.apply()
            act()
            draw()
        }
    }
}