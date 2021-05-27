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

package com.ams64.anakayam.sprites

import com.ams64.anakayam.screen.GamePlayScreen
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape

class GameGround(private var gamePlayScreen: GamePlayScreen, private var positionX: Float, private var positionY: Float){

    var body: Body

    private var bodyDef: BodyDef = BodyDef()
    private val scaleObjectY: Float = 5f
    private var scaleObjectX: Float = 7f

    init {

        //define
        bodyDef.position.set((positionX+gamePlayScreen.groundTexture.width.toFloat()/2f*gamePlayScreen.getScalePixel().toFloat()), (positionY+2*gamePlayScreen.groundTexture.height.toFloat()/2*gamePlayScreen.getScalePixel().toFloat()))
        bodyDef.type = BodyDef.BodyType.StaticBody

        body = gamePlayScreen.getWorld().createBody(bodyDef)

        val fixtureDef = FixtureDef()
        val polygonShape = PolygonShape()
        polygonShape.setAsBox((scaleObjectX*gamePlayScreen.groundTexture.width/2)*gamePlayScreen.getScalePixel().toFloat(),(scaleObjectY*gamePlayScreen.groundTexture.height/1.5f)*gamePlayScreen.getScalePixel().toFloat())

        fixtureDef.shape = polygonShape
        body.createFixture(fixtureDef)

    }

    fun render (batch: SpriteBatch){
        bodyDef.position.set(positionX, positionY)
        batch.draw(gamePlayScreen.groundTexture,positionX,positionY,scaleObjectY*gamePlayScreen.groundTexture.width*gamePlayScreen.getScalePixel().toFloat(),scaleObjectY*gamePlayScreen.groundTexture.height*gamePlayScreen.getScalePixel().toFloat())
    }

    fun getWidth(): Float {
        return gamePlayScreen.groundTexture.width*gamePlayScreen.getScalePixel().toFloat()
    }

    fun getHeight(): Float {
        return gamePlayScreen.groundTexture.height*gamePlayScreen.getScalePixel().toFloat()
    }
    fun dispose(){
        gamePlayScreen.groundTexture.dispose()
    }

}