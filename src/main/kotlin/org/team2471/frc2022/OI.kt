package org.team2471.frc2022

import org.team2471.frc.lib.input.*
import org.team2471.frc.lib.math.Vector2
import org.team2471.frc.lib.math.cube
import org.team2471.frc.lib.math.deadband
import org.team2471.frc.lib.math.squareWithSign

object OI {
    val driverController = XboxController(0)
    val operatorController = XboxController(1)

    private val deadBandDriver = 0.1
    private val deadBandOperator = 0.1


    private val driveTranslationX: Double
        get() = driverController.leftThumbstickX.deadband(deadBandDriver).squareWithSign()

    private val driveTranslationY: Double
        get() = -driverController.leftThumbstickY.deadband(deadBandDriver).squareWithSign()

    val driveTranslation: Vector2
        get() = Vector2(driveTranslationX, driveTranslationY) //does owen want this cubed?

    val driveRotation: Double
        get() = (driverController.rightThumbstickX.deadband(deadBandDriver)).cube() // * 0.6

    val driveLeftTrigger: Double
        get() = driverController.leftTrigger

    val driveRightTrigger: Double
        get() = driverController.rightTrigger

    val operatorLeftTrigger: Double
        get() = operatorController.leftTrigger

    val operatorLeftY: Double
        get() = operatorController.leftThumbstickY.deadband(0.2)

    val operatorLeftX: Double
        get() = operatorController.leftThumbstickX.deadband(0.2)

    val operatorRightTrigger: Double
        get() = operatorController.rightTrigger

    val operatorRightX: Double
        get() = operatorController.rightThumbstickX.deadband(0.25)

    val operatorRightY: Double
        get() = operatorController.rightThumbstickY.deadband(0.25)

    init {
        driverController::back.whenTrue { Drive.zeroGyro(); Drive.initializeSteeringMotors() }
        driverController::leftBumper.whenTrue { shootMode() }
        driverController::x.whenTrue { Drive.position = Vector2(0.0, -12.0) }

        operatorController::start.whenTrue { climbPrep() }
        operatorController::b.whenTrue { intake() }
        operatorController::a.whenTrue { catch() }
        operatorController::y.whenTrue { armUp() }
        operatorController::x.whenTrue { powerSave() }
        operatorController::back.whenTrue { Climb.zeroClimb() }
        operatorController::rightBumper.whenTrue { clearFeeder() }
    }
}
