package com.example.jacquesnoir.Modele.Mains

import com.example.jacquesnoir.Modele.Carte

/** Associe un numéro stable de manche à l'état métier de la main. */
data class MainModele(
    val numero: Int,
    val estJoueur: Boolean,
    val etat: EtatMain,
    /** Identifiant Room; 0 indique que la main n'est pas encore insérée. */
    val id: Int = 0
) {
    init {
        require(numero in 0..3)
        require(id >= 0)
    }

    fun tirer(carte: Carte): MainModele = copy(etat = etat.tirer(carte))

    fun rester(): MainModele = copy(etat = etat.rester())

    fun doubler(): MainModele = copy(etat = etat.doubler())

    /**
     * Sépare l'état métier et attribue à la seconde main le premier numéro
     * encore libre parmi toutes les mains déjà chargées pour ce joueur ou le
     * croupier. Les mains terminées restent incluses jusqu'à la fin de manche.
     */
    fun separer(mainsDeLaManche: Collection<MainModele>): List<MainModele> {
        val numeroNouveau = prochainNumeroMain(mainsDeLaManche + this, estJoueur)
            ?: throw IllegalStateException("La manche contient déjà quatre mains.")

        val nouvellesEtats = etat.separer()
        return listOf(
            copy(etat = nouvellesEtats[0]),
            MainModele(numeroNouveau, estJoueur, nouvellesEtats[1])
        )
    }
}

/** Prochain numéro disponible après le rechargement des mains depuis Room. */
fun prochainNumeroMain(mains: Collection<MainModele>, estJoueur: Boolean): Int? {
    val numerosUtilises = mains
        .asSequence()
        .filter { it.estJoueur == estJoueur }
        .map { it.numero }
        .toSet()

    return (0..3).firstOrNull { it !in numerosUtilises }
}
