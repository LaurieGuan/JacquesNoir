package com.example.jacquesnoir.Vue.EcranJeu

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun calculerPasEntreCartes(
    largeurDisponible: Dp,
    largeurCarte: Dp,
    nombreCartes: Int
): Dp {
    if (nombreCartes <= 1) return 1.dp

    return ((largeurDisponible - largeurCarte) / (nombreCartes - 1).toFloat())
        .coerceIn(0.dp, largeurCarte)
}

fun calculerDecalageInitialCartes(
    largeurDisponible: Dp,
    largeurCarte: Dp,
    pasEntreCartes: Dp,
    nombreCartes: Int
): Dp {
    val largeurTotale =
        largeurCarte + pasEntreCartes * (nombreCartes - 1).toFloat()

    return ((largeurDisponible - largeurTotale) / 2).coerceAtLeast(0.dp)
}