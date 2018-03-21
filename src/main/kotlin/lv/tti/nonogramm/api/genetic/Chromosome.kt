package lv.tti.nonogramm.api.genetic

import com.sun.org.apache.xalan.internal.lib.ExsltMath.power
import lv.tti.nonogramm.api.util.RandomChooser.choose
import lv.tti.nonogramm.api.util.RandomChooser.isChosen
import lv.tti.nonogramm.api.util.map
import lv.tti.nonogramm.api.util.reverse
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

class Chromosome(val matrix: List<List<Boolean>>, private val hint: Hint) {
	private val height = hint.row.size
	private val width = hint.column.size

	val score: Double = score(hint)

	fun crossbreed(chromosome: Chromosome): Pair<Chromosome, Chromosome> =
			matrix.zip(chromosome.matrix).map {
				it.first.zip(it.second).map {
					if (isChosen()) it.reverse() else it
				}.unzip()
			}.unzip().map { Chromosome(it, hint) }

	fun mutated(chance: Float): Chromosome = Chromosome(matrix.map { it.map { isChosen(chance) xor it } }, hint)

	private fun score(hint: Hint): Double {
		val rowScore = MutableList(height) { emptyList<Int>() }
		val columnScore = MutableList(width) { emptyList<Int>() }

		var rowStreak = 0
		var columnStreak = 0

		for (i in 0 until height) {
			for (j in 0 until width) {
				val res = countHint(rowScore[i], matrix[i][j], rowStreak)
				rowStreak = res.first
				rowScore[i] = res.second
			}
			val res = countHint(rowScore[i], false, rowStreak)
			rowStreak = res.first
			rowScore[i] = res.second
		}

		for (i in 0 until width) {
			for (j in 0 until height) {
				val res = countHint(columnScore[i], matrix[j][i], columnStreak)
				columnStreak = res.first
				columnScore[i] = res.second
			}
			val res = countHint(columnScore[i], false, columnStreak)
			columnStreak = res.first
			columnScore[i] = res.second
		}

		for (i in 0 until height) {
			if (rowScore[i].isEmpty())
				rowScore[i] = rowScore[i] + 0
		}
		for (i in 0 until width) {
			if (columnScore[i].isEmpty())
				columnScore[i] = columnScore[i] + 0
		}


		val error = compare(hint.row, rowScore) + compare(hint.column, columnScore)
		return 1 / (1.0 + error / 100.0)
	}

	private fun compare(expected: List<List<Int>>, real: List<List<Int>>) =
			expected.zip(real).map {
				val lineSize = max(it.first.size, it.second.size)
				(it.first + List(lineSize - it.first.size) { 0 })
						.zip(it.second + List(lineSize - it.second.size) { 0 })
						.map { abs(it.first - it.second) }
						.sum()
			}.sum()


	private fun countHint(scorePoint: List<Int>, trigger: Boolean, streak: Int): Pair<Int, List<Int>> {
		if (trigger) return Pair(streak + 1, scorePoint)
		if (streak == 0) return Pair(streak, scorePoint)
		return Pair(0, scorePoint + streak)
	}

	companion object {
		fun random(hint: Hint): Chromosome = Chromosome(List(hint.row.size) { List(hint.column.size) { isChosen(choose(1.0).toFloat()) } }, hint)
	}
}