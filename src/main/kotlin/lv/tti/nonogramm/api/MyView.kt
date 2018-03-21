package lv.tti.nonogramm.api

import javafx.beans.property.*
import lv.tti.nonogramm.api.genetic.Hint
import lv.tti.nonogramm.api.stats.StatCollector
import tornadofx.*
import java.io.File
import kotlin.concurrent.thread


class MyView : View() {

	var hint: Hint? = null
	var disabled: BooleanProperty = SimpleBooleanProperty(true)
	var populationSize: IntegerProperty = SimpleIntegerProperty(0)
	var crossoverChance: FloatProperty = SimpleFloatProperty(0.01f)
	var mutationChance: FloatProperty = SimpleFloatProperty(0.001f)
	var iterationLimit: IntegerProperty = SimpleIntegerProperty(1000)

	override val root = vbox {
		hbox {
			button("Choose nonogram definition") {
				setOnAction {
					val files: List<File> = chooseFile(null, emptyArray()) {
						this.initialDirectory = File(".").absoluteFile
					}
					if (files.isNotEmpty()) {
						try {
							hint = Hint.fromString(files[0].bufferedReader().use { it.readText() })
							populationSize.set(hint!!.row.size * hint!!.column.size)
							disabled.set(false)
						} catch (e: Throwable) {
							println(e.message)
							hint = null
							disabled.set(true)
						}
					}
				}
			}
		}
		hbox {
			vbox {
				label("Population size")
				textfield(populationSize.get().toString()).bind(populationSize)
			}
			vbox {
				label("Crossover chance")
				textfield(crossoverChance.get().toString()).bind(crossoverChance)
			}
			vbox {
				label("Mutation chance")
				textfield(mutationChance.get().toString()).bind(mutationChance)
			}
			vbox {
				label("Iteration limit")
				textfield(iterationLimit.get().toString()).bind(iterationLimit)
			}
		}
		hbox {
			button("Solve") {
				this.disableProperty().bind(disabled)
				action {
					StatCollector.clean()
					thread {
						Solver.solve(hint!!, populationSize.get(), crossoverChance.get(), mutationChance.get(), iterationLimit.get())
					}
				}
			}
		}
	}
}
