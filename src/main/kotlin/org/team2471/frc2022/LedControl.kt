package org.team2471.frc2022

import edu.wpi.first.wpilibj.AddressableLED
import edu.wpi.first.wpilibj.AddressableLEDBuffer
import edu.wpi.first.wpilibj.util.Color
import org.team2471.frc.lib.framework.Subsystem
import org.team2471.frc.lib.math.linearMap

object LedControl : Subsystem ("LedControl"){
    val led = AddressableLED(Leds.LED_PORT)
    val ledBuffer = AddressableLEDBuffer(Leds.LED_LENGTH)

    init {
        println("LEDLEDLED")
        led.setLength(ledBuffer.length)

        //rainbow()
        setDotted(Color.kRed, 2)

        led.setData(ledBuffer)

        led.start()
    }

    fun rainbow(brightness: Int = 255) {
        for (i in 0 until ledBuffer.length) {
            ledBuffer.setHSV(i, linearMap(0.0, ledBuffer.length.toDouble(), 0.0, 180.0, i.toDouble()).toInt(), 255, brightness)
//            ledBuffer.setRGB(i, 255, 0, 0)
        }
        led.setData(ledBuffer)
    }

    fun incrementRainbow() {

    }

    fun setSolid(color: Color) {
        for (i in 0 until ledBuffer.length) {
            //println(color.red)
            ledBuffer.setRGB(i, color.red.toInt() * 255, color.green.toInt() * 255, color.blue.toInt() * 255)
        }
        led.setData(ledBuffer)
    }

    fun setDotted(color: Color, spacing: Int) {
        for (i in 0 until ledBuffer.length) {
            if (i % spacing == 1) {
                ledBuffer.setRGB(i, color.red.toInt() * 255, color.green.toInt() * 255, color.blue.toInt() * 255)
                println("dot")
            }
            else {
                ledBuffer.setRGB(i, 0, 0, 0)
                println("no dot")
            }
        }
        led.setData(ledBuffer)
    }
}