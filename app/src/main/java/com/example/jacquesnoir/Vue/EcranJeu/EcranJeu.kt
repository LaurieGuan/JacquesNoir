package com.example.jacquesnoir.Vue.EcranJeu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jacquesnoir.Modele.Carte
import com.example.jacquesnoir.Modele.Couleur
import com.example.jacquesnoir.Modele.Numero
import com.example.jacquesnoir.VueModele.VueModeleJacquesNoir

@Composable
fun EcranJeu(vueModele: VueModeleJacquesNoir) {
    val mains by vueModele.mains.collectAsStateWithLifecycle()
    val donneesChargees by vueModele.donneesChargees.collectAsStateWithLifecycle()
    val mainActiveId by vueModele.identifiantMainActive.collectAsStateWithLifecycle()
    val croupierRevele by vueModele.croupierRevele.collectAsStateWithLifecycle()
    val cartesRestantes by vueModele.nombreCartesRestantes.collectAsStateWithLifecycle()
    val defausse by vueModele.defausse.collectAsStateWithLifecycle()
    val partieEnCours by vueModele.partieEnCours.collectAsStateWithLifecycle()
    val miseProchaine by vueModele.miseSelectionnee.collectAsStateWithLifecycle()
    val texteMise by vueModele.texteMise.collectAsStateWithLifecycle()
    val erreur by vueModele.erreur.collectAsStateWithLifecycle()
    val actionEnCours by vueModele.actionEnCours.collectAsStateWithLifecycle()

    val mainsJoueur = mains.filter { it.estJoueur }.sortedBy { it.numero }
    val mainCroupier = mains.firstOrNull { !it.estJoueur }
    val derniereCarteDefaussee = defausse.lastOrNull()?.let { carte ->
        Carte(
            num = Numero.entries[carte.numero - 2],
            couleur = Couleur.valueOf(carte.couleur)
        )
    }
    val mainSelectionnee = mainsJoueur.firstOrNull { it.id == mainActiveId }
        ?: mainsJoueur.firstOrNull()
    val miseEnCours = mainSelectionnee?.etat?.mise ?: 0
    val mancheEnCours = mainsJoueur.isNotEmpty() && !croupierRevele
    val etatPages = rememberPagerState(pageCount = { mainsJoueur.size.coerceAtLeast(1) })

    var preparationNouvelleManche by remember { mutableStateOf(false) }
    var modeModificationMise by remember { mutableStateOf(false) }
    val dialogueMiseVisible = donneesChargees &&
        ((!partieEnCours && mainsJoueur.isEmpty()) || preparationNouvelleManche)

    LaunchedEffect(mainsJoueur) {
        if (mainsJoueur.isNotEmpty()) preparationNouvelleManche = false
    }

    LaunchedEffect(mainActiveId, mainsJoueur) {
        val index = mainsJoueur.indexOfFirst { it.id == mainActiveId }
        if (index >= 0 && etatPages.currentPage != index) {
            etatPages.animateScrollToPage(index)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ZoneCroupier(
            mainCroupier = mainCroupier,
            carteCacheeRevelee = croupierRevele,
            modifier = Modifier.weight(2f)
        )

        ZoneSabotDefausse(
            nombreCartesRestantes = cartesRestantes,
            derniereCarteDefaussee = derniereCarteDefaussee,
            modifier = Modifier.weight(1.7f)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(3.2f)
        ) {
            ZoneMise(
                miseEnCours = miseEnCours,
                miseProchaine = miseProchaine,
                mancheEnCours = mancheEnCours,
                auClic = {
                    modeModificationMise = true
                    preparationNouvelleManche = true
                },
                modifier = Modifier.weight(0.9f)
            )

            ZoneMainsJoueur(
                etatPages = etatPages,
                mains = mainsJoueur,
                mainActiveId = mainActiveId,
                auClicMain = vueModele::selectionnerMain,
                modifier = Modifier.weight(2.3f)
            )
        }

        erreur?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        CommandesMain(
            commandesActives = !actionEnCours &&
                mainSelectionnee?.let { vueModele.actionsDisponibles(it.id) } == true,
            resterDisponible = !actionEnCours &&
                mainSelectionnee?.let { vueModele.peutRester(it.id) } == true,
            doublerDisponible = !actionEnCours &&
                mainSelectionnee?.let { vueModele.peutDoubler(it.id) } == true,
            separerDisponible = !actionEnCours &&
                mainSelectionnee?.let { vueModele.peutSeparer(it.id) } == true,
            croupierRevele = croupierRevele,
            auClicTirer = { mainSelectionnee?.let { vueModele.tirer(it.id) } },
            auClicRester = { mainSelectionnee?.let { vueModele.rester(it.id) } },
            auClicDoubler = { mainSelectionnee?.let { vueModele.doubler(it.id) } },
            auClicSeparer = { mainSelectionnee?.let { vueModele.separer(it.id) } },
            auClicChangerMise = {
                modeModificationMise = true
                preparationNouvelleManche = true
            },
            auClicNouvelleManche = {
                modeModificationMise = false
                vueModele.confirmerMiseEtContinuer()
            },
            modifier = Modifier.height(56.dp)
        )
    }

    DialoguePreparationMise(
        visible = dialogueMiseVisible,
        modeModification = modeModificationMise,
        mancheEnCours = mancheEnCours,
        miseProchaine = miseProchaine,
        texteMise = texteMise,
        erreur = erreur,
        actionEnCours = actionEnCours,
        auChangementTexteMise = vueModele::modifierTexteMise,
        auClicMoins = vueModele::diminuerMise,
        auClicPlus = vueModele::augmenterMise,
        auClicModifier = { modeModificationMise = true },
        auClicContinuer = vueModele::confirmerMiseEtContinuer,
        auClicValider = {
            vueModele.enregistrerMiseParDefaut()
            modeModificationMise = false
            preparationNouvelleManche = false
        },
        auClicAnnuler = {
            modeModificationMise = false
            preparationNouvelleManche = false
        }
    )
}
