package org.team2471.frc2022

import edu.wpi.first.wpilibj.DigitalInput
import org.team2471.frc.lib.actuators.MotorController
import org.team2471.frc.lib.actuators.TalonID
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.Subsystem

object Conveyor: Subsystem("Conveyor") {
    val conveyorSensor = DigitalInput(6)
    val conveyorMotor = MotorController(TalonID(Talons.BUNNY_CONVEYOR))

    val ballLoaded: Boolean
        get() =  !conveyorSensor.get()

    init {
        println("INSIDE CONVEYOR INIT!!!!!")
        conveyorMotor.config {
           brakeMode()

           inverted(true)
            burnSettings()
        }
    }

    override suspend fun default() {
        periodic {
            if (!ballLoaded || !Uptake.ballReady) {
                conveyorMotor.setPercentOutput(0.3)
            } else if (ballLoaded) {
                conveyorMotor.setPercentOutput(0.0)
            }
            println("in periodic")
        }
    }
}