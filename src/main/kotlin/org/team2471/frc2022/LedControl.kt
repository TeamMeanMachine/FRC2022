package org.team2471.frc2022

import edu.wpi.first.wpilibj.AddressableLED
import edu.wpi.first.wpilibj.AddressableLEDBuffer
import org.team2471.frc.lib.framework.Subsystem

object LedControl : Subsystem ("LedControl"){
    val led = AddressableLED(Leds.LED_PORT)
    val ledBuffer = AddressableLEDBuffer(Leds.LED_LENGTH)

    init {
        println("LEDLEDLED")
        led.setLength(ledBuffer.length)

        for (i in 0 until ledBuffer.length) {
            ledBuffer.setHSV(i, i, 100, 100)
        }

        led.setData(ledBuffer)

        led.start()
    }
}