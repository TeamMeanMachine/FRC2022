package org.team2471.frc2022

import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.PowerDistribution
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.Subsystem

object PowerDistribution : Subsystem("PowerDistribution") {
    var PDH = PowerDistribution()
    val table = NetworkTableInstance.getDefault().getTable(name)
    val totalPower = table.getEntry("Power")
    val totalCurrent = table.getEntry("Current")
    val totalEnergy = table.getEntry("Energy")
    init {
        GlobalScope.launch {
            println("setting power distribution info")
            periodic {
                try {
                    totalCurrent.setDouble(PDH.totalCurrent)
                    totalPower.setDouble(PDH.totalPower)
                    totalEnergy.setDouble(PDH.totalEnergy)
                } catch (ex: Exception) {
                    println("Exception when reading power: ${ex.message}")
                }
                for (i in 0..23) {
                    try {
                        val entry = table.getEntry("port_$i")
                        entry.setDouble(PDH.getCurrent(i))
                        val x = 42
                    } catch (ex: Exception) {
                        println("port $i couldn't be read")
                    }
                }
            }
        }
    }

    override suspend fun default() {
        super.default()
        periodic {
            if (DriverStation.isEnabled()) {
                try {
                    totalEnergy.setDouble(PDH.totalEnergy)
                } catch (ex: Exception) {
                    println("Exception in default when reading energy: ${ex.message}")
                }
            }
        }
    }
}