package lv.tti.nonogramm.api.stats

import lv.tti.nonogramm.api.genetic.Chromosome

object StatCollector {

	var scores: List<Double> = emptyList()
	var matrix: List<List<Boolean>>? = null

	var averageScores: List<Double> = emptyList()

	fun save(chromosome: Chromosome, average: Double) {
		scores += chromosome.score
		matrix = chromosome.matrix
		averageScores += average
	}

	var max
		get() = if (scores.isEmpty()) 0.0 else scores.last()
		set(value) {}

	var average
		get() = if (averageScores.isEmpty()) 0.0 else averageScores.last()
		set(value) {}

	var iteration
		get() = scores.size
		set(value) {}

	fun clean() {
		scores = emptyList()
		matrix = null
		averageScores = emptyList()
	}
}