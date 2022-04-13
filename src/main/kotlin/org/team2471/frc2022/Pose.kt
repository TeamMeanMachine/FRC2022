package org.team2471.frc2022

data class Pose(val height1: Double, val height2: Double) {

    companion object {
        val current: Pose
            get() = Pose(Climb.height1, Climb.height2)

        val CLIMB_START = Pose(Climb.HEIGHT_BOTTOM, Climb.HEIGHT_BOTTOM)
        val CLIMB_PREP = Pose(Climb.HEIGHT_TOP, Climb.HEIGHT_BOTTOM)
        val CLIMB_HIGH_BAR = Pose(Climb.HEIGHT_BOTTOM, Climb.HEIGHT_TOP)
        val CLIMB_TRAVERSE = Pose(Climb.HEIGHT_TOP, Climb.HEIGHT_BOTTOM)
    }
}
