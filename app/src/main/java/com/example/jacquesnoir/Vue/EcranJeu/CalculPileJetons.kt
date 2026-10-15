package com.example.jacquesnoir.Vue.EcranJeu

import com.example.jacquesnoir.R

fun calculerJetonsPourMontant(montant: Int): List<Int> {
    val denominations = listOf(
        100 to R.drawable.jeton_100,
        25 to R.drawable.jeton_25,
        10 to R.drawable.jeton_10,
        5 to R.drawable.jeton_5,
        1 to R.drawable.jeton_1
    )

    var restant = montant.coerceAtLeast(0)

    return buildList {
        denominations.forEach { (valeur, ressource) ->
            repeat(restant / valeur) {
                add(ressource)
            }
            restant %= valeur
        }
    }
}