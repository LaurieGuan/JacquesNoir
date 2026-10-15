package com.example.jacquesnoir.Vue.EcranJeu

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.jacquesnoir.Modele.Carte
import com.example.jacquesnoir.Modele.Couleur
import com.example.jacquesnoir.Modele.Numero
import com.example.jacquesnoir.R

internal const val RATIO_CARTE = 361f / 540f

@Composable
fun CarteJeu(
    ressource: Int,
    description: String,
    modifier: Modifier = Modifier,
    echelleContenu: ContentScale = ContentScale.Fit
) {
    Image(
        painter = painterResource(ressource),
        contentDescription = description,
        modifier = modifier.aspectRatio(RATIO_CARTE),
        contentScale = echelleContenu
    )
}

@Composable
fun CarteJeu(
    carte: Carte,
    description: String,
    modifier: Modifier = Modifier
) {
    CarteJeu(
        ressource = carte.ressourceCarte(),
        description = description,
        modifier = modifier
    )
}
private val ressourcesParCouleur = mapOf(
    Couleur.TREFLE to listOf(
        R.drawable.carte_2_trefle, R.drawable.carte_3_trefle,
        R.drawable.carte_4_trefle, R.drawable.carte_5_trefle,
        R.drawable.carte_6_trefle, R.drawable.carte_7_trefle,
        R.drawable.carte_8_trefle, R.drawable.carte_9_trefle,
        R.drawable.carte_10_trefle, R.drawable.carte_valet_trefle,
        R.drawable.carte_dame_trefle, R.drawable.carte_roi_trefle,
        R.drawable.carte_as_trefle
    ),
    Couleur.CARREAU to listOf(
        R.drawable.carte_2_carreau, R.drawable.carte_3_carreau,
        R.drawable.carte_4_carreau, R.drawable.carte_5_carreau,
        R.drawable.carte_6_carreau, R.drawable.carte_7_carreau,
        R.drawable.carte_8_carreau, R.drawable.carte_9_carreau,
        R.drawable.carte_10_carreau, R.drawable.carte_valet_carreau,
        R.drawable.carte_dame_carreau, R.drawable.carte_roi_carreau,
        R.drawable.carte_as_carreau
    ),
    Couleur.COEUR to listOf(
        R.drawable.carte_2_coeur, R.drawable.carte_3_coeur,
        R.drawable.carte_4_coeur, R.drawable.carte_5_coeur,
        R.drawable.carte_6_coeur, R.drawable.carte_7_coeur,
        R.drawable.carte_8_coeur, R.drawable.carte_9_coeur,
        R.drawable.carte_10_coeur, R.drawable.carte_valet_coeur,
        R.drawable.carte_dame_coeur, R.drawable.carte_roi_coeur,
        R.drawable.carte_as_coeur
    ),
    Couleur.PIQUE to listOf(
        R.drawable.carte_2_pique, R.drawable.carte_3_pique,
        R.drawable.carte_4_pique, R.drawable.carte_5_pique,
        R.drawable.carte_6_pique, R.drawable.carte_7_pique,
        R.drawable.carte_8_pique, R.drawable.carte_9_pique,
        R.drawable.carte_10_pique, R.drawable.carte_valet_pique,
        R.drawable.carte_dame_pique, R.drawable.carte_roi_pique,
        R.drawable.carte_as_pique
    )
)

private fun Carte.ressourceCarte(): Int =
    ressourcesParCouleur.getValue(couleur)[num.ordinal]
