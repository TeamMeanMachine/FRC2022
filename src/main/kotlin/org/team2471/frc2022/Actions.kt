package org.team2471.frc2022

import edu.wpi.first.wpilibj2.command.CommandGroupBase.parallel
import org.team2471.frc.lib.coroutines.delay
import org.team2471.frc.lib.coroutines.parallel
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.coroutines.suspendUntil
import org.team2471.frc.lib.framework.use
import org.team2471.frc.lib.util.Timer
import kotlin.math.absoluteValue

//Intake


suspend fun intake() = use(Intake) {
//    Intake.resetPivotOffset()
    Feeder.autoFeedMode = true
    Intake.setIntakePower(Intake.INTAKE_POWER)
    Intake.intakeState = Intake.Mode.INTAKE
    Intake.changeAngle(Intake.PIVOT_INTAKE)
    Intake.changeAngle(Intake.PIVOT_INTAKE)
    Climb.climbMode = false
    Climb.climbIsPrepped = false
}

suspend fun catch() = use(Intake) {
//    Intake.resetPivotOffset()
    Feeder.autoFeedMode = true
    Intake.setIntakePower(0.0)
    Intake.intakeState = Intake.Mode.CATCH
    Intake.changeAngle(Intake.PIVOT_CATCH)
    Climb.climbMode = false
    Climb.climbIsPrepped = false
}

suspend fun armUp() = use(Intake) {
//    Intake.resetPivotOffset()
    Feeder.autoFeedMode = false
    Intake.setIntakePower(0.0)
    Intake.intakeState = Intake.Mode.STOW
    Intake.changeAngle(Intake.PIVOT_STORE)
    Climb.climbMode = false
    Climb.climbIsPrepped = false
}

suspend fun powerSave() = use(Intake) {
    if (!Feeder.isAuto) Feeder.autoFeedMode = false
    Intake.setIntakePower(0.0)
    Intake.intakeState = Intake.Mode.POWERSAVE
    Intake.resetPivotOffset()
    Intake.changeAngle(Intake.PIVOT_BOTTOM)
    Climb.climbMode = false
    Climb.climbIsPrepped = false
    Intake.resetPivotOffset()
}

suspend fun feedUntilCargo() = use(Intake, Feeder) {
    periodic {
        if (Shooter.cargoIsStaged) {
            Feeder.setShooterFeedPower(0.0)
            println("Shooter Staged")
            if (Feeder.cargoIsStaged) {
                Intake.setIntakePower(0.0)
                println("Intake Staged")
            } else {
                Intake.setIntakePower(Intake.INTAKE_POWER)
                println("Intake Powering - waiting for 2nd cargo")
            }
        } else {
            Feeder.setShooterFeedPower(0.9)
            Intake.setIntakePower(Intake.INTAKE_POWER)
            println("Feeder Power")
        }
    }
}

suspend fun shootMode() = use(Shooter) {
    println("shoot mode has been called. Shootmode = ${Shooter.shootMode}")
    Shooter.shootMode = !Shooter.shootMode
    Limelight.backLedEnabled = (Shooter.shootMode && !Limelight.useFrontLimelight)
    Limelight.frontLedEnabled = (Shooter.shootMode && Limelight.useFrontLimelight)
}

suspend fun autoShootv2(shotCount: Int = 2, maxWait: Double = 2.5, minWait: Double = 0.0) = use(Shooter, Feeder, Drive) {
    var doneShooting = false
    Shooter.pastMinWait = false  //pizza (parameter too)
    Feeder.autoFeedMode = true
    Shooter.rpmSetpoint = 3000.0
    Shooter.shootMode = true
    val t = Timer()
    t.start()
    parallel({
        println("autoshooting   usingFrontLL ${Limelight.useFrontLimelight} distance ${Limelight.distance}")
        suspendUntil { Shooter.allGood || doneShooting }  // Limelight.aimError.absoluteValue < Shooter.aimMaxError && Shooter.rpmError.absoluteValue < Shooter.rpmMaxError || doneShooting }
        suspendUntil { doneShooting }
        Shooter.shootMode = false
    }, {
        periodic {
            Drive.autoSteer()
//            println("rpm ${Shooter.rpm.roundToInt()}     rpmSetpoint ${Shooter.rpmSetpoint.roundToInt()}    pitch ${Shooter.pitch.roundToInt()}       pitchSetpoint ${Shooter.pitchSetpoint.roundToInt()}")
            if (doneShooting) {
                stop()
            }
        }
        println("aimError = ${Limelight.aimError}")
    }, {
        Feeder.autoCargoShot = 0
//        suspendUntil { Feeder.autoCargoShot > 0 || doneShooting}
//        var startWait = t.get()
//        Feeder.waitASecond = true
//        suspendUntil { t.get() - startWait > 0.5 }
//        Feeder.waitASecond = false
        suspendUntil { Feeder.autoCargoShot >= shotCount || doneShooting }
        delay(0.1)
        if (!doneShooting) {
            println("doneShooting after ${Feeder.autoCargoShot} cargo in ${t.get()} seconds")
        }
        doneShooting = true
    }, {
        periodic {
            if (!doneShooting && t.get() > minWait) Shooter.pastMinWait = true //pizza
            if (!doneShooting && t.get() > maxWait) {
                println("failed shoot allGood: ${Shooter.allGood} rpmGood ${Shooter.rpmGood} pitchGood ${Shooter.pitchGood} aimGood ${Shooter.aimGood} ")
                doneShooting = true
                println("doneShooting after $maxWait sec")
            } else if (doneShooting) {
                stop()
            }
        }
    })
    Shooter.shootMode = false
    Shooter.pastMinWait = false  //pizza
}

suspend fun autoShoot() = use(Shooter, Feeder, Drive) {
    Feeder.setShooterFeedPower(0.0)
    Shooter.shootMode = true
    var doneShooting = false
    var t = Timer()
    t.start()
    parallel({
        println("autoshooting   usingFrontLL ${Limelight.useFrontLimelight} distance ${Limelight.distance}")
        Feeder.autoFeedMode = true
        Feeder.setBedFeedPower(Feeder.BED_FEED_POWER)
        delay(1.0)
        suspendUntil { Limelight.aimError.absoluteValue < 4.0 || doneShooting }
        println("aimError: ${Limelight.aimError}      doneShooting? $doneShooting")
        Feeder.setShooterFeedPower(0.8)
        suspendUntil { doneShooting }
        Feeder.setShooterFeedPower(0.0)
        Shooter.shootMode = false
        Feeder.autoFeedMode = true
    }, {
        periodic {
            Drive.autoSteer()
//            println("rpm ${Shooter.rpm.roundToInt()}     rpmSetpoint ${Shooter.rpmSetpoint.roundToInt()}    pitch ${Shooter.pitch.roundToInt()}       pitchSetpoint ${Shooter.pitchSetpoint.roundToInt()}")
            if (doneShooting) {
                stop()
            }
        }
        println("aimError = ${Limelight.aimError}")
    }, {
        suspendUntil { Shooter.cargoIsStaged || doneShooting }
        suspendUntil { !Shooter.cargoIsStaged || doneShooting }
        suspendUntil { Shooter.cargoIsStaged || doneShooting }
        suspendUntil { !Shooter.cargoIsStaged || doneShooting }
        delay(0.1)
        if (!doneShooting) {
            println("doneShooting after 2 cargo")
        }
        doneShooting = true
    }, {
        periodic {
            if (!doneShooting && t.get() > 2.5) {
                doneShooting = true
                println("doneShooting after 2.5 sec")
            } else if (doneShooting) {
                stop()
            }
        }
    })
}

fun zeroIntakePivot() {
//    try {
//        println("reinitializing pivot motor position")
//        periodic {
//            Intake.setIntakePivotPower(0.2)
//            println("${Intake.intakePivotMotor.current}")
//            if (Intake.intakePivotMotor.current > 60.0) {
//                stop()
//            }
//        }
//    }
//    finally {
//        println("finally has been reached")
    Intake.setIntakePivotPower(0.0)
//        Intake.pivotAngle = 95.0
//    }
}

suspend fun shootTest2() = use(Shooter, Feeder) {
    println("Got into shoot test")
    Feeder.setShooterFeedPower(0.9)
//    Intake.setIntakePower(0.8)
    periodic {
        Shooter.rpm = Shooter.rpmSetpoint
        println("in shooter test. Hi.")
    }
}

suspend fun goToPose(targetPose: Pose, fullCurve: Boolean = false, minTime: Double = 0.0) = use(Climb) {
    val time = if (fullCurve) {
        maxOf(minTime, Climb.heightChangeTime(Climb.height1, targetPose.height1), Climb.heightChangeTime(Climb.height2, targetPose.height2))
    } else {
        minTime
    }
    println("Pose Values: $time ${targetPose.height1} ${targetPose.height2}")
    parallel({
        Climb.changeHeight1(targetPose.height1, time)
    },
    {
        Climb.changeHeight2(targetPose.height2, time)
    })
}

suspend fun climbPrep() = use(Climb) {
    println("trying to climbprep")

    Feeder.autoFeedMode = false
//    if (Climb.climbIsPrepped){
//        Climb.climbIsPrepped = false
//        println("FUN")
//        delay(0.5)
//    } else {
        Climb.climbMode = true
        climbPrepOther()
        goToPose(Pose.CLIMB_PREP)
        Climb.climbIsPrepped = true
        println("climb is prepped")
        suspendUntil { OI.operatorController.rightBumper || !Climb.climbIsPrepped }
        if (Climb.climbIsPrepped) {
            performClimb()
        }
//    }
}

suspend fun climbPrepOther () = use(Shooter, Intake) {
    parallel({
        Intake.setIntakePower(0.0)
        Intake.changeAngle(Intake.PIVOT_TOP)
    }, {
        Shooter.changeAngle(Shooter.PITCH_LOW)
    })
}


suspend fun performClimb() {
    println("trying to start climb")
    if (Climb.climbIsPrepped) {
        //println("Climb stage executing: ${Climb.climbStage} roll: ${Climb.roll}")
        OI.operatorController.rumble = 0.5
        var lasTroll = Climb.roll
        Climb.climbStage = 0
        while (Climb.climbStage < 3 && Climb.climbIsPrepped) {
            if (OI.operatorController.rightBumper) {  //rightBumper instead of leftTrigger
                println("Trigger climb stage ${Climb.climbStage}, roll is ${Climb.roll}")
                when (Climb.climbStage) {
                    0 -> {
                        goToPose(Pose.CLIMB_LEFT_DOWN)
                        delay(2.3)
                    }
                    1 -> {
                        goToPose(Pose.CLIMB_RIGHT_DOWN)
                        delay(3.0) //1.8
                    }
                    2 -> {
                        goToPose(Pose.CLIMB_LEFT_DOWN)
                    }
                    else -> println("Climb Stage Complete")
                }
            } else {
                println("rightBumper let go")
            }
            Climb.climbStage += 1
        }
    }
    OI.operatorController.rumble = 0.0
    println("done with start climb")
}


//suspend fun midClimb()

suspend fun clearFeeder() = use(Feeder) {
    println("clearing out feeder and Intake")
    //val currFeedMode = Feeder.autoFeedMode
    // Feeder.autoFeedMode = false
    Feeder.isClearing = true
//    Feeder.setBedFeedPower(-Feeder.BED_FEED_POWER)
//    Feeder.setShooterFeedPower(-Feeder.SHOOTER_FEED_POWER)
    delay(0.5)
    //Feeder.autoFeedMode = currFeedMode
    Feeder.isClearing = false
}
