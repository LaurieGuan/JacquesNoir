package com.example.jacquesnoir.Vue.EcranJeu

import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun PileJetons(montant: Int) {
    val pilesParDenomination = calculerJetonsPourMontant(montant)
        .groupingBy { it }
        .eachCount()
        .toList()

    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        pilesParDenomination.forEach { (ressource, quantite) ->
            val tailleJeton = 44.dp
            val pasVertical = if (quantite > 1) {
                minOf(5.dp, 54.dp / (quantite - 1).toFloat())
            } else {
                0.dp
            }
            val hauteurPile = tailleJeton + pasVertical * (quantite - 1).toFloat()

            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(hauteurPile)
            ) {
                repeat(quantite) { index ->
                    Image(
                        painter = painterResource(ressource),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = -(pasVertical * index.toFloat()))
                            .size(tailleJeton)
                    )
                }
            }
        }
    }

}
