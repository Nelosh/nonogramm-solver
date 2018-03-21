package lv.tti.nonogramm.api.genetic

import kotlinx.coroutines.experimental.async
import lv.tti.nonogramm.api.util.RandomChooser
import lv.tti.nonogramm.api.util.RandomChooser.isChosen
import lv.tti.nonogramm.api.util.crossbreed

class Population(private var population: List<Chromosome>, private val hint: Hint) {
	private val size = population.size

	fun crossover(chance: Float) {
		val parents = population

		var result = emptyList<Chromosome>()

		for (i in 0 until parents.size) {
				val father = parents[i]
				for (mother in parents.drop(i + 1)) {
					if (father == mother)
						continue
					if (!RandomChooser.isChosen(chance))
						continue
					result += father.crossbreed(mother).toList()
				}
				if (result.size > population.size * 3)
					break
		}
		population += result
	}


	fun mutate(chance: Float) {
		val sorted = population.sortedBy { it.score }
		val eliteSize = 1

		population = population.map { it.mutated(chance) } +
				sorted.takeLast(eliteSize)
	}

	fun selection() {
		val eliteSize = 1

		val sorted = population.sortedBy { it.score }
		var challengers = sorted.filterIndexed { i, p -> i < population.size - eliteSize }

		while (challengers.size > size - eliteSize) {
			val firstFighter = RandomChooser.choose(challengers.size)
			var secondFighter = RandomChooser.choose(challengers.size)
			while (secondFighter == firstFighter)
				secondFighter = RandomChooser.choose(challengers.size)

			challengers -= if (challengers[firstFighter].score >= challengers[secondFighter].score) {
				challengers[secondFighter]
			} else {
				challengers[firstFighter]
			}
		}
		population = sorted.takeLast(eliteSize) + challengers
	}


	fun bestChromosome(): Chromosome = population.maxBy{it.score}!!
	fun averageScore(): Double = population.map { it.score }.sum() / population.size.toDouble()

	companion object {
		fun generate(populationSize: Int, hint: Hint): Population {
			return Population(List(populationSize) { Chromosome.random(hint) }, hint)
		}
	}
}