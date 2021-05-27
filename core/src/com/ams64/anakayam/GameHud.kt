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

import com.ams64.anakayam.screen.GamePlayScreen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport

class GameHud(gamePlayScreen: GamePlayScreen,spriteBatch: SpriteBatch) : Disposable {
    var stage: Stage
    private val viewport: Viewport

    private var scoreLabel: Label
    private var emptyLabel: Label

    private var currentScore = 0
    override fun dispose() {
        stage.dispose()
    }

    fun addScore(value: Int) {
        currentScore += value
        scoreLabel.setText(String.format("%07d", currentScore))
    }

    fun getCurrentScore(): Int {
        return currentScore
    }

    init {
        viewport = FitViewport(gamePlayScreen.game.screenWidth.toFloat()/gamePlayScreen.game.scalePixel.toFloat(), gamePlayScreen.game.screenHeight.toFloat()/gamePlayScreen.game.scalePixel.toFloat(), OrthographicCamera())
        stage = Stage(viewport, spriteBatch)

        val table = Table()
        table.top()
        table.setFillParent(true)

        val bitmapFont = BitmapFont()
        bitmapFont.data.setScale(gamePlayScreen.game.fontSize2)
        scoreLabel = Label(String.format("%09d", currentScore), LabelStyle(bitmapFont, Color.WHITE))
        emptyLabel = Label("",LabelStyle(BitmapFont(), Color.WHITE))

        table.add(emptyLabel).expandX().padTop((10).toFloat())
        table.add(emptyLabel).expandX().padTop((10).toFloat())
        table.add(scoreLabel).expandX().padTop((20).toFloat())
        stage.addActor(table)
    }
}
