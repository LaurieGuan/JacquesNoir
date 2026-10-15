package com.example.jacquesnoir.Depot

import com.example.jacquesnoir.Modele.Carte
import com.example.jacquesnoir.Modele.Mains.MainCreee
import com.example.jacquesnoir.Modele.Mains.MainDepassee
import com.example.jacquesnoir.Modele.Mains.MainEngagee
import com.example.jacquesnoir.Modele.Mains.EtatMain
import com.example.jacquesnoir.Modele.Mains.MainInitialisee
import com.example.jacquesnoir.Modele.Mains.MainJacquesNoir
import com.example.jacquesnoir.Modele.Mains.MainModele
import com.example.jacquesnoir.Modele.Mains.MainTerminee
import com.example.jacquesnoir.Modele.Mains.meilleurTotal
import kotlinx.coroutines.delay

data class ResultatMainReglee(
    val numeroMain: Int,
    /** Montant rendu au solde, mise comprise. */
    val jetonsRetournes: Int
)

data class ResultatReglementManche(
    val resultatsMains: List<ResultatMainReglee>,
    val soldeFinal: Int
)

/** Orchestre les actions d'une partie en s'appuyant sur le dépôt pour persister l'état. */
class GestionnairePartie(private val depot: DepotJacquesNoir) {
    companion object {
        private const val CLE_MANCHE_REGLEE = "mancheReglee"
    }

    /** Crée au besoin un sabot vide, crée les mains puis distribue deux cartes à chacune. */
    suspend fun demarrerManche(
        mise: Int? = null,
        nombrePaquetsSiSabotVide: Int = 1
    ): List<MainModele> = depot.dansTransaction {
        check(depot.obtenirToutesLesMains().isEmpty()) {
            "Les mains de la manche précédente doivent être réglées et vidées avant d'en démarrer une autre."
        }

        val miseInitiale = mise
            ?: depot.obtenirParametreEntier(DepotJacquesNoir.CLE_MISE_PAR_DEFAUT)
            ?: error("La mise par défaut n'a pas été enregistrée.")
        require(miseInitiale > 0) { "La mise doit être positive." }

        if (depot.nombreCartesRestantes() == 0) {
            depot.initialiserSabot(nombrePaquetsSiSabotVide)
        }
        if (depot.nombreCartesRestantes() < 4) {
            depot.initialiserSabot(nombrePaquetsSiSabotVide)
        }

        val jetons = depot.obtenirParametreEntier(DepotJacquesNoir.CLE_JETONS_RESTANTS)
            ?: error("Le nombre de jetons restants n'a pas été enregistré.")
        check(jetons >= miseInitiale) { "Le solde ne permet pas de placer cette mise." }

        depot.definirPartieEnCours(true)

        val mainsInitiales = depot.sauvegarderMains(
            listOf(
                MainModele(0, true, MainCreee(mutableListOf(), miseInitiale)),
                // Le croupier n'engage pas de jetons; sa mise vaut zéro.
                MainModele(0, false, MainCreee(mutableListOf(), 0))
            )
        )
        depot.enregistrerParametreEntier(
            DepotJacquesNoir.CLE_JETONS_RESTANTS,
            jetons - miseInitiale
        )
        depot.enregistrerParametreBooleen(CLE_MANCHE_REGLEE, false)

        val mainJoueur = mainsInitiales[0]
        val mainCroupier = mainsInitiales[1]
        repeat(2) {
            depot.pigerCarte(mainJoueur.id)
            depot.pigerCarte(mainCroupier.id)
        }
        depot.obtenirToutesLesMains()
    }

    /** Indique si une partie est active, même entre deux manches sans mains en cours. */
    suspend fun partieEnCours(): Boolean {
        val partieEnCours = depot.obtenirPartieEnCours()
        if (!partieEnCours) {
            depot.definirPartieEnCours(false)
        }
        return partieEnCours
    }

    /** Termine la partie et archive les cartes jouées dans les statistiques. */
    suspend fun quitterPartie() = terminerPartie()

    suspend fun tirer(mainId: Int): Carte? = depot.dansTransaction {
        depot.pigerCarte(mainId)
    }

    suspend fun rester(mainId: Int): MainModele? = depot.dansTransaction {
        val main = depot.obtenirMain(mainId) ?: return@dansTransaction null
        depot.sauvegarderMains(listOf(main.rester())).single()
    }

    suspend fun doubler(mainId: Int): MainModele? = depot.dansTransaction {
        val main = depot.obtenirMain(mainId) ?: return@dansTransaction null
        check(main.estJoueur) { "Seule une main du joueur peut doubler." }
        retirerJetons(main.etat.mise)
        depot.sauvegarderMains(listOf(main.doubler())).single()
    }

    /** Double la mise, tire la carte obligatoire et termine l'action atomiquement. */
    suspend fun doublerEtTirer(mainId: Int): MainModele? = depot.dansTransaction {
        val main = depot.obtenirMain(mainId) ?: return@dansTransaction null
        check(main.estJoueur) { "Seule une main du joueur peut doubler." }
        retirerJetons(main.etat.mise)
        val mainDoublee = depot.sauvegarderMains(listOf(main.doubler())).single()
        check(depot.pigerCarte(mainDoublee.id) != null) {
            "Le sabot ne contient plus de carte pour doubler."
        }
        depot.obtenirMain(mainDoublee.id)
    }

    suspend fun separer(mainId: Int): List<MainModele>? = depot.dansTransaction {
        val toutesLesMains = depot.obtenirToutesLesMains()
        val main = toutesLesMains.firstOrNull { it.id == mainId }
            ?: return@dansTransaction null
        check(main.estJoueur) { "Seule une main du joueur peut être séparée." }
        check(toutesLesMains.count { it.estJoueur } < 4) {
            "Une manche ne peut pas contenir plus de quatre mains du joueur."
        }
        check(depot.nombreCartesRestantes() >= 2) {
            "Le sabot ne contient pas assez de cartes pour séparer cette main."
        }
        val mainsSeparees = main.separer(toutesLesMains)
        retirerJetons(main.etat.mise)
        val mainsEnregistrees = depot.sauvegarderMains(mainsSeparees)
        mainsEnregistrees.forEach { mainSeparee ->
            check(depot.pigerCarte(mainSeparee.id) != null) {
                "Le sabot ne contient pas assez de cartes pour distribuer après la séparation."
            }
        }
        mainsEnregistrees.mapNotNull { depot.obtenirMain(it.id) }
    }

    /** Fait tirer le croupier jusqu'à 17, puis marque sa main comme terminée. */
    suspend fun faireJouerCroupier(): MainModele? {
        var croupier = depot.obtenirToutesLesMains().firstOrNull { !it.estJoueur }
            ?: return null

        while (croupier.etat is MainInitialisee || croupier.etat is MainEngagee) {
            if (meilleurTotal(croupier) >= 17) break
            delay(500)
            if (depot.pigerCarte(croupier.id) == null) break
            croupier = depot.obtenirMain(croupier.id) ?: break
        }

        if (croupier.etat is MainInitialisee || croupier.etat is MainEngagee) {
            croupier = depot.dansTransaction {
                val mainActuelle = depot.obtenirMain(croupier.id) ?: return@dansTransaction null
                if (mainActuelle.etat is MainInitialisee || mainActuelle.etat is MainEngagee) {
                    depot.sauvegarderMains(listOf(mainActuelle.rester())).single()
                } else {
                    mainActuelle
                }
            } ?: return null
        }
        return croupier
    }

    /** Joue le croupier, règle les mains terminées et crédite le solde. */
    suspend fun reglerManche(): ResultatReglementManche {
        check(depot.obtenirParametreBooleen(CLE_MANCHE_REGLEE) != true) {
            "Cette manche a déjà été réglée."
        }
        val croupier = faireJouerCroupier()
            ?: error("La main du croupier est introuvable.")

        return depot.dansTransaction {
            check(depot.obtenirParametreBooleen(CLE_MANCHE_REGLEE) != true) {
                "Cette manche a déjà été réglée."
            }
            val mains = depot.obtenirToutesLesMains()
            check(mains.filter { it.estJoueur }.isNotEmpty()) { "Aucune main de joueur à régler." }
            check(mains.all { it.etat.estTerminee() }) {
                "Toutes les mains du joueur et du croupier doivent être terminées avant le règlement."
            }

            val solde = depot.obtenirParametreEntier(DepotJacquesNoir.CLE_JETONS_RESTANTS)
                ?: error("Le nombre de jetons restants n'a pas été enregistré.")
            val resultats = mains.filter { it.estJoueur }.map { main ->
                ResultatMainReglee(
                    numeroMain = main.numero,
                    jetonsRetournes = calculerRetour(main, croupier)
                )
            }
            val soldeFinalLong = solde.toLong() + resultats.sumOf { it.jetonsRetournes.toLong() }
            check(soldeFinalLong <= Int.MAX_VALUE) { "Le solde dépasse la valeur maximale permise." }
            val soldeFinal = soldeFinalLong.toInt()
            depot.enregistrerParametreEntier(DepotJacquesNoir.CLE_JETONS_RESTANTS, soldeFinal)
            depot.enregistrerParametreBooleen(CLE_MANCHE_REGLEE, true)
            ResultatReglementManche(resultats, soldeFinal)
        }
    }

    /** Archive les cartes et cumule les statistiques après la fin de toutes les mains. */
    suspend fun archiverMancheTerminee() = depot.dansTransaction {
        val mains = depot.obtenirToutesLesMains()
        if (mains.isEmpty()) return@dansTransaction
        check(depot.obtenirParametreBooleen(CLE_MANCHE_REGLEE) == true) {
            "La manche doit être réglée avant l'archivage."
        }
        check(mains.all {
            it.etat is MainTerminee ||
                it.etat is MainDepassee ||
                it.etat is MainJacquesNoir
        }) { "Toutes les mains doivent être terminées avant l'archivage." }
        depot.viderMainsDansDefausse()
    }

    suspend fun terminerPartie() = depot.dansTransaction {
        depot.viderMainsDansDefausse()
        depot.comptabiliserDefausse()
        depot.viderSabot()
        depot.definirPartieEnCours(false)
    }

    suspend fun mancheReglee(): Boolean =
        depot.obtenirParametreBooleen(CLE_MANCHE_REGLEE) == true

    suspend fun changerMiseParDefaut(mise: Int) {
        require(mise > 0)
        depot.enregistrerParametreEntier(DepotJacquesNoir.CLE_MISE_PAR_DEFAUT, mise)
    }

    suspend fun definirJetonsRestants(jetons: Int) {
        require(jetons >= 0)
        depot.enregistrerParametreEntier(DepotJacquesNoir.CLE_JETONS_RESTANTS, jetons)
    }

    private suspend fun retirerJetons(montant: Int) {
        val solde = depot.obtenirParametreEntier(DepotJacquesNoir.CLE_JETONS_RESTANTS)
            ?: error("Le nombre de jetons restants n'a pas été enregistré.")
        check(montant >= 0 && solde >= montant) {
            "Le solde ne permet pas de couvrir cette mise supplémentaire."
        }
        depot.enregistrerParametreEntier(
            DepotJacquesNoir.CLE_JETONS_RESTANTS,
            solde - montant
        )
    }

    private fun calculerRetour(joueur: MainModele, croupier: MainModele): Int {
        val mise = joueur.etat.mise.toLong()
        val totalJoueur = meilleurTotal(joueur)
        val totalCroupier = meilleurTotal(croupier)
        val blackjackCroupier = croupier.etat is MainJacquesNoir
        val blackjackJoueur = joueur.etat is MainJacquesNoir
        val croupierDepasse = croupier.etat is MainDepassee || totalCroupier > 21

        val retour = when {
            blackjackCroupier -> if (blackjackJoueur) mise else 0L
            joueur.etat is MainDepassee || totalJoueur > 21 -> 0L
            blackjackJoueur -> mise * 5 / 2 // Retour total : mise plus gain de 3:2.
            croupierDepasse || totalJoueur > totalCroupier -> mise * 2
            totalJoueur == totalCroupier -> mise
            else -> 0L
        }
        check(retour <= Int.MAX_VALUE) { "Le retour de mise dépasse la valeur maximale permise." }
        return retour.toInt()
    }

    private fun EtatMain.estTerminee(): Boolean =
        this is MainTerminee || this is MainDepassee || this is MainJacquesNoir

    /** Évalue les As à 11 si cela ne fait pas dépasser 21, sinon à 1. */
    private fun meilleurTotal(main: MainModele): Int {
        return main.etat.meilleurTotal()
    }
}
