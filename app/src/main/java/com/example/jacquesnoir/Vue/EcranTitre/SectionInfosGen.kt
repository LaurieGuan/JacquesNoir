package com.example.jacquesnoir.Vue.EcranTitre

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.MaterialTheme

@Composable
fun SectionInfosGen(
    soldeDisponible: Int,
    dernierePartie: String,
    statutDossier: String
) {
    Column {
        EnteteSection(
            texte = "SITUATION ACTUELLE",
            taille = 30.sp
        )

        Spacer(Modifier.height(12.dp))

        Spacer(
            Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(MaterialTheme.colorScheme.outline)
        )

        ElementInfoGenerale(
            libelle = "SOLDE DISPONIBLE",
            valeur = soldeDisponible.toString()
        )

        ElementInfoGenerale(
            libelle = "DERNIÈRE PARTIE",
            valeur = dernierePartie
        )

        ElementInfoGenerale(
            libelle = "STATUT",
            valeur = statutDossier,
            afficherMarqueur = true
        )
    }
}
