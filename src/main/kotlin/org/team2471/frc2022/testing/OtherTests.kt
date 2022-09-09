package org.team2471.frc2022.testing

import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX
import org.team2471.frc.lib.coroutines.delay
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.use
import org.team2471.frc.lib.input.Controller
import org.team2471.frc.lib.math.round
import org.team2471.frc2022.Drive
import org.team2471.frc2022.OI


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

suspend fun canTest() {
    val basicMotor = WPI_TalonFX(6,"TestCanivore")
    basicMotor.set(0.25)
    delay(0.5)
    basicMotor.set(0.0)
}
