package com.prj1.mysticdungeon


import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import com.prj1.mysticdungeon.asset.BitmapFontAsset
import com.prj1.mysticdungeon.asset.TextureAtlasAsset
import com.prj1.mysticdungeon.screen.GameScreen
import com.prj1.mysticdungeon.screen.LoadingScreen
import com.prj1.mysticdungeon.ui.createSkin
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.assets.async.AssetStorage
import ktx.assets.disposeSafely
import ktx.async.KtxAsync
import ktx.collections.gdxArrayOf

/** [com.badlogic.gdx.ApplicationListener] implementation shared by all platforms. */
class MysticDungeon : KtxGame<KtxScreen>(){
    val introStage: Stage by lazy {
        val result = Stage(FitViewport(248f, 135f) )
        Gdx.input.inputProcessor = result
        result
    }
    val assets: AssetStorage by lazy {
        KtxAsync.initiate()
        AssetStorage()
    }
    override fun create() {
        Gdx.app.logLevel = Application.LOG_DEBUG

        val assetRefs = gdxArrayOf(
            TextureAtlasAsset.values().map { assets.loadAsync(it.descriptor) },
            BitmapFontAsset.values().map { assets.loadAsync(it.descriptor) }
        ).flatten()

        KtxAsync.launch {
            assetRefs.joinAll()
            createSkin(assets)
            addScreen(LoadingScreen(this@MysticDungeon))
            setScreen<LoadingScreen>()
        }
    }

    override fun dispose() {
        introStage.disposeSafely()
    }
    companion object{
        const val UNIT_SCALE = 1/16f
    }
}
