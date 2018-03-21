package lv.tti.nonogramm.api


import lv.tti.nonogramm.api.genetic.Hint
import lv.tti.nonogramm.api.genetic.Population
import lv.tti.nonogramm.api.stats.StatCollector
import lv.tti.nonogramm.api.ui.Window
import kotlin.concurrent.thread
import kotlin.math.abs

object Solver {

	fun solve(hint: Hint, populationSize: Int, crossoverChance: Float, mutationChance: Float, iterations: Int) {
		val population = Population.generate(populationSize, hint)

		val thread = thread { Window("Stats") }

		var previousBest = 0.0
		var mutationChanceAdjusted = mutationChance

		for (i in 1..iterations) {
			population.crossover(crossoverChance)
			population.mutate(mutationChance)
			population.selection()

			val best = population.bestChromosome()
			val bestScore = best.score
			StatCollector.save(best, population.averageScore())

			if (1 - bestScore < 0.00000001)
				return

			if (!thread.isAlive)
				return

			if (abs(bestScore - previousBest) < 0.00001) {
				mutationChanceAdjusted += 0.0001f
			} else {
				previousBest = bestScore
				mutationChanceAdjusted = mutationChance
			}

			if (1 - mutationChanceAdjusted < mutationChance) {
				mutationChanceAdjusted = mutationChance
			}
		}

	}
}
