/*
 * MIT License
 *
 * Copyright (c) 2021 A M Satrio <ams64.digital@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.ams64.anakayam


import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport

open class GameLoad(spriteBatch: SpriteBatch) : Disposable {
    private val viewPort: Viewport

    var stage: Stage
    private var table: Table

    private var loadLabel: Label
    private var emptyLabel: Label

    private var loadProgress: Float = 0f

    private var bitmapFont1: BitmapFont = BitmapFont()
    private var bitmapFont2: BitmapFont = BitmapFont()

    //Asset Manager
    private var assetManager: AssetManager = AssetManager()


    override fun dispose() {
        stage.dispose()
        assetManager.dispose()
    }

    fun loadAsset() {
        assetManager.load("textures/obstacle.png", Texture::class.java)
        assetManager.load("textures/ground.png", Texture::class.java)
        assetManager.load("textures/cloud.png", Texture::class.java)
        assetManager.load("sounds/bump_short.wav", Sound::class.java)
        assetManager.load("sounds/jump.wav", Sound::class.java)
    }

    fun getAssetManager(): AssetManager {
        return assetManager
    }

    fun setLoadProgress(value: Float){
        loadProgress = value
        loadLabel.setText("Load: ${(loadProgress*100).toInt()}%")
    }


    init {

        //mod Font
        bitmapFont1.data.setScale(1.5f)
        bitmapFont2.data.setScale(1.8f)

        //viewport
        viewPort = FitViewport(AnakAyam(null).screenWidth.toFloat()/AnakAyam(null).scalePixel.toFloat(), AnakAyam(null).screenHeight.toFloat()/AnakAyam(null).scalePixel.toFloat(), OrthographicCamera())

        //label
        loadLabel = Label("Load: ${(loadProgress*100).toInt()}%", Label.LabelStyle(bitmapFont1, Color.WHITE))
        emptyLabel = Label("", Label.LabelStyle(bitmapFont1, Color.WHITE))

        //initialize Stage and Table Menu
        stage = Stage(viewPort, spriteBatch)
        table = Table()
        table.top
        table.setFillParent(true)

        //add label to table
        table.add(loadLabel).expandX().padTop((10 / 1).toFloat())
        table.row()
        table.add(emptyLabel).expandX().padTop((10 / 1).toFloat())

        //add table to stage
        stage.addActor(table)

        // display loading information
        loadProgress = assetManager.progress


    }
}