package lv.tti.nonogramm.api.genetic

class Hint(val row: List<List<Int>>, val column: List<List<Int>>) {
	companion object {
		fun fromString(hintString: String): Hint {
			val hints = hintString.split("\r\n\r\n").map {
				it.split("\r\n").map { it.split(" ").map { it.toInt() } }
			}

			return Hint(hints[0], hints[1])
		}
	}
}
