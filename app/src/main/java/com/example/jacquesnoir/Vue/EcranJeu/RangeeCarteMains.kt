package com.example.jacquesnoir.Vue.EcranJeu

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jacquesnoir.Modele.Carte

@Composable
fun RangeeCartesMain(
    cartes: List<Carte>,
    modifier: Modifier = Modifier
) {
    val largeurCarte = 72.dp

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(108.dp)
    ) {
        val pasEntreCartes = calculerPasEntreCartes(
            largeurDisponible = maxWidth,
            largeurCarte = largeurCarte,
            nombreCartes = cartes.size
        )

        val decalageInitial = calculerDecalageInitialCartes(
            largeurDisponible = maxWidth,
            largeurCarte = largeurCarte,
            pasEntreCartes = pasEntreCartes,
            nombreCartes = cartes.size
        )

        cartes.forEachIndexed { index, carte ->
            CarteJeu(
                carte = carte,
                description = "Carte ${index + 1}",
                modifier = Modifier
                    .offset(x = decalageInitial + pasEntreCartes * index.toFloat())
                    .width(largeurCarte)
            )
        }
    }
}
