package org.team2471.frc2022

import com.ctre.phoenix.motorcontrol.StatusFrame
import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.wpilibj.Timer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.team2471.frc.lib.actuators.FalconID
import org.team2471.frc.lib.actuators.MotorController
import org.team2471.frc.lib.control.PDController
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.Subsystem
import org.team2471.frc.lib.motion_profiling.MotionCurve
import org.team2471.frc.lib.units.degrees
import org.team2471.frc.lib.units.radians
import kotlin.math.absoluteValue

object Climb : Subsystem("Climb") {
    val heightMotor1 = MotorController(FalconID(Falcons.CLIMB))
    val heightMotor2 = MotorController(FalconID(Falcons.CLIMB_TWO))
    private val table = NetworkTableInstance.getDefault().getTable(name)

    val heightEntry1 = table.getEntry("Height 1")
    val heightEntry2 = table.getEntry("Height 2")
    val heightSetpointEntry1 = table.getEntry("Height Setpoint 1")
    val heightSetpointEntry2 = table.getEntry("Height Setpoint 2")
    val robotRollEntry = table.getEntry("Roll")
    val heightMotorOutput1 = table.getEntry("Height Output 1")
    val heightMotorOutput2 = table.getEntry("Height Output 2")

    var climbIsPrepped = false
    var climbStage = 0
    var climbMode = false
    var bungeeTakeOver = false
    val height1: Double
        get() = heightMotor1.position
    var heightSetpoint1 = height1
        get() = heightSetpointEntry1.getDouble(height1)
        set(value) {
            field = value.coerceIn(HEIGHT_BOTTOM, HEIGHT_TOP)
            heightSetpointEntry1.setDouble(field)
        }
    val height2: Double
        get() = heightMotor2.position
    var heightSetpoint2 = height2
        get() = heightSetpointEntry2.getDouble(height2)
        set(value) {
            field = value.coerceIn(HEIGHT_BOTTOM, HEIGHT_TOP)
            heightSetpointEntry2.setDouble(field)
        }

    val tuningMode = false

    const val HEIGHT_TOP = 30.5
    const val HEIGHT_BOTTOM = 0.0

    val roll : Double
        get() = Drive.gyro.getRoll()

    init {
        heightMotor1.config {
            brakeMode()
            inverted(true)
            feedbackCoefficient = 3.14 / 2048.0 / 9.38 * 28.5 / 25.5
            pid {
                p(0.00000002)
            }
        }

        heightMotor2.config {
            brakeMode()
            inverted(false)
            feedbackCoefficient = 3.14 / 2048.0 / 9.38 * 28.5 / 25.5
            pid {
                p(0.00000002)
            }
        }

        heightMotor1.position = 1.0
        heightMotor2.position = 1.0
        heightSetpointEntry1.setDouble(height1)
        heightSetpointEntry2.setDouble(height2)
        GlobalScope.launch {
                periodic {
                    heightEntry1.setDouble(heightMotor1.position)
                    heightEntry2.setDouble(heightMotor2.position)
                    robotRollEntry.setDouble(roll)
                    heightMotorOutput1.setDouble(heightMotor1.output)
                    heightMotorOutput2.setDouble(heightMotor2.output)

                    if (OI.operatorLeftY.absoluteValue > 0.1 && climbMode) heightSetpoint1 -= OI.operatorLeftY * 0.45
                    if ((OI.operatorLeftTrigger > 0.1 || OI.operatorRightTrigger > 0.1)) {  //!climbMode commented out to allow for zeroing in climbMode
                        setPower((OI.operatorLeftTrigger - OI.operatorRightTrigger) * 0.5)
                    } else {
                        if (heightSetpoint1 > 1.5 && !climbMode) {
                            heightSetpoint1 -= 0.05
                        } else if (heightSetpoint1 < 1.0 && !climbMode) {
                            heightSetpoint1 = 1.0
                        }
                        heightMotor1.setPositionSetpoint(heightSetpoint1)
                    }
                }
        }
    }
    override fun postEnable() {
        heightSetpoint1 = height1
        climbMode = false
    }

    fun setPower(power: Double) {
        heightMotor1.setPercentOutput(power)
    }

    fun zeroClimb() {
        heightMotor1.setRawOffset(0.0.radians)
        heightSetpoint1 = 0.0
    }

    suspend fun changePosition(current: Double, target: Double, time : Double, function: (value : Double) -> (Unit)) {
        val curve = MotionCurve()
        curve.storeValue(0.0, current)
        curve.storeValue(time, target)
        val timer = Timer()
        timer.start()
        periodic {
            val t = timer.get()
            function(curve.getValue(t))
            if (t >= curve.length) {
                stop()
            }
        }
    }

    fun heightChangeTime(height: Double, target: Double) : Double {
        val distance = (height - target)
        val rate = if (distance < 0.0) 40.0 else 20.0  // inches per sec
        return distance.absoluteValue / rate
    }

    suspend fun changeHeight1(target: Double, minTime: Double = 0.0) {
        var time = heightChangeTime(height1, target)
        if (minTime > time) {
            println("Time extended for changeHeight using minTime: $minTime")
            time = minTime
        }
        changePosition(height1, target, time) { value: Double ->
            heightSetpoint1 = value
            updatePositions()
        }
    }

    suspend fun changeHeight2(target: Double, minTime: Double = 0.0) {
        var time = heightChangeTime(height2, target)
        if (minTime > time) {
            println("Time extended for changeHeight using minTime: $minTime")
            time = minTime
        }
        changePosition(height2, target, time) { value: Double ->
            heightSetpoint2 = value
            updatePositions()
        }
    }

    fun updatePositions() {
        heightMotor1.setPositionSetpoint(heightSetpoint1)
        heightMotor2.setPositionSetpoint(heightSetpoint2)
    }

    override suspend fun default() {
        periodic {
            if (tuningMode) {
                println("is tuning mode")
//                updatePositions()
            } else if (OI.operatorLeftY.absoluteValue > 0.1 || OI.operatorRightY.absoluteValue > 0.1) {
//                heightSetpoint -= OI.operatorLeftY * 0.45
//                angleSetpoint += OI.operatorRightY * 0.2
//                heightMotor.setPositionSetpoint(heightSetpoint)
            }

        }
    }


}