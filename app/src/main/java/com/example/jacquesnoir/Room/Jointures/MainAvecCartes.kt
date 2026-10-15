package com.example.jacquesnoir.Room.Jointures

import com.example.jacquesnoir.Modele.Mains.MainDepassee
import com.example.jacquesnoir.Modele.Mains.MainDoublee
import com.example.jacquesnoir.Modele.Mains.MainCreee
import com.example.jacquesnoir.Modele.Mains.MainEngagee
import com.example.jacquesnoir.Modele.Mains.MainInitialisee
import com.example.jacquesnoir.Modele.Mains.MainJacquesNoir
import com.example.jacquesnoir.Modele.Mains.MainModele
import com.example.jacquesnoir.Modele.Mains.MainSeparee
import com.example.jacquesnoir.Modele.Mains.MainTerminee
import com.example.jacquesnoir.Room.Entites.CarteSabot
import com.example.jacquesnoir.Room.Entites.MainDB

/** Résultat de lecture réunissant une main et ses cartes dans l'ordre de la main. */
data class MainAvecCartes(
    val main: MainDB,
    val cartes: List<CarteSabot>
)

fun List<MainAvecCarteLigne>.enMainsAvecCartes(): List<MainAvecCartes> =
    groupBy { it.main.id }.values.map { lignes ->
        MainAvecCartes(
            main = lignes.first().main,
            cartes = lignes.mapNotNull { it.carte }
        )
    }

fun MainAvecCartes.enMainModele(): MainModele {
    val cartes = cartes.map { carte -> carte.enCarteEngin() }.toMutableList()
    val etat = when (main.etat) {
        "CREEE" -> MainCreee(cartes, main.mise)
        "INITIALISEE" -> MainInitialisee(cartes, main.mise)
        "ENGAGEE" -> MainEngagee(cartes, main.mise)
        "SEPAREE" -> MainSeparee(cartes, main.mise)
        "DOUBLEE" -> MainDoublee(cartes, main.mise)
        "TERMINEE" -> MainTerminee(cartes, main.mise)
        "JACQUES_NOIR" -> MainJacquesNoir(cartes, main.mise)
        "DEPASSEE" -> MainDepassee(cartes, main.mise)
        else -> error("État de main inconnu : ${main.etat}")
    }
    return MainModele(
        numero = main.numero,
        estJoueur = main.estJoueur == 1,
        etat = etat,
        id = main.id
    )
}

fun List<MainAvecCartes>.enMainsModele(): List<MainModele> = map { it.enMainModele() }

fun MainModele.enMainDB(): MainDB = MainDB(
    id = id,
    etat = when (etat) {
        is MainCreee -> "CREEE"
        is MainInitialisee -> "INITIALISEE"
        is MainEngagee -> "ENGAGEE"
        is MainSeparee -> "SEPAREE"
        is MainDoublee -> "DOUBLEE"
        is MainTerminee -> "TERMINEE"
        is MainJacquesNoir -> "JACQUES_NOIR"
        is MainDepassee -> "DEPASSEE"
    },
    numero = numero,
    estJoueur = if (estJoueur) 1 else 0,
    mise = etat.mise
)
