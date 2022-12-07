package org.team2471.frc2022

import edu.wpi.first.wpilibj.AnalogInput
import org.team2471.frc.lib.framework.Subsystem
import edu.wpi.first.wpilibj.Servo
import org.team2471.frc.lib.actuators.MotorController
import org.team2471.frc.lib.actuators.SparkMaxID
import org.team2471.frc.lib.actuators.TalonID
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.units.Angle
import org.team2471.frc.lib.units.degrees


object Bunny : Subsystem("Bunny") {
    //motors
    val leftPinchMotor = Servo(PWMServos.BUNNY_PINCH_LEFT)
    val rightPinchMotor = Servo(PWMServos.BUNNY_PINCH_RIGHT)
    val bunnyExtend = MotorController(SparkMaxID(Sparks.BUNNY_EXTEND))

    //sensors
    val bunnyExtendSensor = AnalogInput(AnalogSensors.BUNNY_EXTEND)
//    lift encoder
    val angleOffset = 0.0.degrees
    val analogAngle: Angle
        get() = -(((bunnyExtendSensor.voltage - 0.2) / 4.6 * 360.0).degrees) + angleOffset

    init {
        bunnyExtend.config(20) {
            feedbackCoefficient =
                (1.0 / 20.0) * (360.0 / 42.0) // degrees per tick
            setRawOffsetConfig(analogAngle)
            currentLimit(15, 20, 1)
        }
    }
    override suspend fun default() {
        println("starting periodic")
        periodic {
            leftPinchMotor.set(if (OI.operatorController.leftBumper) 0.5 else 1.0)
            rightPinchMotor.set(if (OI.operatorController.rightBumper) 0.0 else 0.5)
            //debug//println(leftPinchMotor.get())
            println("Bunny Angle: $analogAngle")
        }
    }

}
