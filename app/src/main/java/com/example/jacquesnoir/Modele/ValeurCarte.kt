package com.example.jacquesnoir.Modele

sealed class ValeurCarte
data class SimpleValeur(val val1: Int): ValeurCarte()
data class DoubleValeur(val val1: Int, val val2: Int): ValeurCarte()

val ValeurCarte.plusPetiteValeur: Int
    get() = when (this) {
        is SimpleValeur -> val1
        is DoubleValeur -> minOf(val1, val2)
}

operator fun SimpleValeur.plus(terme: SimpleValeur): SimpleValeur =
    SimpleValeur( this.val1 + terme.val1 )

operator fun SimpleValeur.plus(terme: DoubleValeur): DoubleValeur =
    DoubleValeur( this.val1 + terme.val1, this.val1 + terme.val2 )

operator fun DoubleValeur.plus(terme: DoubleValeur): DoubleValeur =
    DoubleValeur(
        this.plusPetiteValeur + terme.val1,
        this.plusPetiteValeur + terme.val2
    )

operator fun DoubleValeur.plus(terme: SimpleValeur): DoubleValeur =
    DoubleValeur( this.val1 + terme.val1, this.val2 + terme.val1 )

operator fun ValeurCarte.plus(terme: ValeurCarte): ValeurCarte {
    return when (this) {
        is SimpleValeur -> when (terme) {
            is SimpleValeur -> this + terme
            is DoubleValeur -> this + terme
        }
        is DoubleValeur -> when (terme) {
            is SimpleValeur -> this + terme
            is DoubleValeur -> this + terme
        }
    }
}
