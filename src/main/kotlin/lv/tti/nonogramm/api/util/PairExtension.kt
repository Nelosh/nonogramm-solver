package lv.tti.nonogramm.api.util

import lv.tti.nonogramm.api.genetic.Chromosome

fun <T, R> Pair<T, T>.map(transform: (T) -> R) = this.toList().map(transform).zipWithNext()[0]

fun <T> Pair<T, T>.reverse() = Pair(this.second, this.first)

fun Pair<Chromosome, Chromosome>.crossbreed() = this.first.crossbreed(this.second)