package org.team2471.frc2022

import edu.wpi.first.wpilibj.DigitalInput
import org.team2471.frc.lib.actuators.MotorController
import org.team2471.frc.lib.actuators.TalonID
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.Subsystem

object Uptake: Subsystem("Hopper") {

    val uptakeSensor = DigitalInput(5)
    val uptakeMotor = MotorController(TalonID(Talons.BUNNY_UPTAKE))

    val ballReady: Boolean
        get() = !uptakeSensor.get()

    init {
        uptakeMotor.config {
            coastMode()
            inverted(true)
            burnSettings()
        }
    }

    override suspend fun default() {
        periodic {
//            println("ballReady ${ballReady}")
            if (!ballReady) {
                uptakeMotor.setPercentOutput(0.3)
//                println("BALL NOT READY!!!!!!!!")
            } else {
                uptakeMotor.setPercentOutput(0.0)
//                println("ball ready")
            }
        }
    }
}