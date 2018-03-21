package lv.tti.nonogramm.api.util

import java.util.*

object RandomChooser {
	private val rand = Random()

	fun isChosen(chance: Float = 0.5f): Boolean = rand.nextFloat() <= chance

	fun choose(max: Double): Double = rand.nextDouble() * max

	fun choose(max: Int): Int = rand.nextInt(max)
}
