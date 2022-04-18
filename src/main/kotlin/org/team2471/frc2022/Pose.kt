package org.team2471.frc2022

data class Pose(val height1: Double, val height2: Double) {

    companion object {
        val current: Pose
            get() = Pose(Climb.height1, Climb.height2)

        val CLIMB_PREP = Pose(Climb.HEIGHT_TOP, Climb.HEIGHT_TOP)
        val CLIMB_LEFT_DOWN = Pose(Climb.HEIGHT_BOTTOM, Climb.HEIGHT_TOP)
        val CLIMB_RIGHT_DOWN = Pose(Climb.HEIGHT_TOP, Climb.HEIGHT_BOTTOM)

    }
}
