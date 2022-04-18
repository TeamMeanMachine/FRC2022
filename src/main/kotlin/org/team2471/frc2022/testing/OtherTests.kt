package org.team2471.frc2022.testing

import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.use
import org.team2471.frc.lib.input.Controller
import org.team2471.frc.lib.math.round
import org.team2471.frc2022.*
import kotlin.math.absoluteValue

suspend fun Feeder.motorTest() = use(this) {
    periodic {
//        setShooterFeedPower(0.4)
        setBedFeedPower(0.8)
    }
}
suspend fun Shooter.pitchTest() = use(this) {
    periodic {
//        println("${(org.team2471.frc2022.OI.operatorRightTrigger - org.team2471.frc2022.OI.operatorLeftTrigger) * 0.5}")
        pitchSetPower((OI.operatorRightTrigger - OI.operatorLeftTrigger) * 0.5)
    }
}

suspend fun Climb.motorTest() = use(this) {
    var power1 = OI.operatorLeftY * 0.2
    var power2 = OI.operatorRightY * 0.2
    periodic {
        power1 = -OI.operatorLeftY
        power2 = -OI.operatorRightY
        println("power1: $power1       power2: $power2")
        setPower1(power1)
        setPower2(power2)
    }
}


suspend fun Intake.pivotTest() = use(this) {
    periodic {
        var power = (OI.driveRightTrigger - OI.driveLeftTrigger)
        intakePivotMotor.setPercentOutput(power)
        if (power > 0.1) println("pivotTest active    $power")
    }
}

suspend fun Climb.adjustmentTest() = use(this) {
    periodic {
        heightSetpoint1 -= OI.operatorController.leftThumbstickY * (12.0 / 50.0)
        heightMotor1.setPositionSetpoint(heightSetpoint1)

    }
}

suspend fun Drive.autoSteerTest() = use(Drive){
    Limelight.backLedEnabled = true
    periodic {
        autoSteer()
    }
}

suspend fun Drive.currentTest() = use(this) {
    var power = 0.0
    var upPressed = false
    var downPressed = false
    periodic {
        if (OI.driverController.dPad == Controller.Direction.UP) {
            upPressed = true
        } else if (OI.driverController.dPad == Controller.Direction.DOWN) {
            downPressed = true
        }
        if (OI.driverController.dPad != Controller.Direction.UP && upPressed) {
            upPressed = false
            power += 0.01
        }
        if (OI.driverController.dPad != Controller.Direction.DOWN && downPressed) {
            downPressed = false
            power -= 0.01
        }
//        for (moduleCount in 0..3) {
//            val module = modules[moduleCount] as Drive.Module
//        }
//        println()
//        println("power: $power")
        val currModule = modules[1] as Drive.Module
        currModule.driveMotor.setPercentOutput(power)
        currModule.turnMotor.setPositionSetpoint(0.0)
        println("current: ${round(currModule.driveCurrent, 2)}  power: $power")
//        drive(
//            Vector2(0.0, power),
//            0.0,
//            false
//        )
    }
}

suspend fun systemsCheck() = use(Intake, Feeder, Shooter, Climb, Limelight) {
}

suspend fun Intake.pivotFeedForwardTest() {
    var f = 0.0
    var upPressed = false
    var downPressed = false
    periodic {
        if (OI.driverController.dPad == Controller.Direction.UP) {
            upPressed = true
        } else if (OI.driverController.dPad == Controller.Direction.DOWN) {
            downPressed = true
        }
        if (OI.driverController.dPad != Controller.Direction.UP && upPressed) {
            upPressed = false
            f += 0.01
        }
        if (OI.driverController.dPad != Controller.Direction.DOWN && downPressed) {
            downPressed = false
            f -= 0.01
        }
//        for (moduleCount in 0..3) {
//            val module = modules[moduleCount] as Drive.Module
//        }
//        println()
//        println("power: $power")
        intakePivotMotor.setPositionSetpoint(5.0, f)
        println("current: ${round(intakePivotMotor.current, 2)}  f: $f")
//        drive(
//            Vector2(0.0, power),
//            0.0,
//            false
//        )
    }
}