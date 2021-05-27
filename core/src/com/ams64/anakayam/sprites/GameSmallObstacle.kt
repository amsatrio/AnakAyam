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
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.physics.box2d.*

class GameSmallObstacle(private val gamePlayScreen: GamePlayScreen, private var positionX: Float, private var positionY: Float){
    //private var texture: Texture = Texture("textures/obstacle.png")
    private val scaleObjectXY: Float = 3.6f

    private var textureRegion: TextureRegion = TextureRegion(gamePlayScreen.obstacleTexture,gamePlayScreen.obstacleTexture.width/3*(Math.random()*2).toInt(),0,gamePlayScreen.obstacleTexture.width/3,gamePlayScreen.obstacleTexture.height)

    private var world: World = gamePlayScreen.getWorld()
    var body: Body

    private var bodyDef: BodyDef = BodyDef()

    init {
        bodyDef.position.set(((positionX+textureRegion.regionWidth/2f*scaleObjectXY* gamePlayScreen.getScalePixel().toFloat())), ((positionY+textureRegion.regionHeight/2*scaleObjectXY* gamePlayScreen.getScalePixel().toFloat())))
        bodyDef.type = BodyDef.BodyType.StaticBody

        body = world.createBody(bodyDef)

        val fixtureDef = FixtureDef()
        val polygonShape = PolygonShape()
        polygonShape.setAsBox(((textureRegion.regionWidth/2.5f)*scaleObjectXY* gamePlayScreen.getScalePixel().toFloat()), ((textureRegion.regionHeight/2.5f)*scaleObjectXY* gamePlayScreen.getScalePixel().toFloat()))
        fixtureDef.filter.categoryBits = gamePlayScreen.game.gameObstacleBit

        fixtureDef.shape = polygonShape
        body.createFixture(fixtureDef)
    }


    fun render (batch: SpriteBatch){
        bodyDef.position.set(positionX, positionY)
        batch.draw(textureRegion,positionX,positionY,textureRegion.regionWidth*scaleObjectXY* gamePlayScreen.getScalePixel().toFloat(),textureRegion.regionHeight*scaleObjectXY* gamePlayScreen.getScalePixel().toFloat())
    }


    fun getWidth(): Float {
        return textureRegion.regionWidth* gamePlayScreen.getScalePixel().toFloat()
    }

    fun dispose() {
        gamePlayScreen.obstacleTexture.dispose()
    }

}