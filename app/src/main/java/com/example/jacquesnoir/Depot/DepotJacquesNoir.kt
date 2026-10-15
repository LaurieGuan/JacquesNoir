package com.example.jacquesnoir.Depot

import androidx.lifecycle.flowWithLifecycle
import androidx.room.withTransaction
import com.example.jacquesnoir.Depot.DepotJacquesNoir.Companion.CLE_JETONS_RESTANTS
import com.example.jacquesnoir.Depot.DepotJacquesNoir.Companion.CLE_MISE_PAR_DEFAUT
import com.example.jacquesnoir.Modele.Carte
import com.example.jacquesnoir.Modele.Paquet
import com.example.jacquesnoir.Modele.creerPaquet
import com.example.jacquesnoir.Modele.Mains.MainModele
import com.example.jacquesnoir.Room.BDJacquesNoir
import com.example.jacquesnoir.Room.Entites.CarteMain
import com.example.jacquesnoir.Room.Entites.CarteSabot
import com.example.jacquesnoir.Room.Entites.CarteDefausse
import com.example.jacquesnoir.Room.Entites.StatistiqueDB
import com.example.jacquesnoir.Room.Entites.enCarteSabot
import com.example.jacquesnoir.Room.Entites.Parametre
import com.example.jacquesnoir.Room.Jointures.MainAvecCartes
import com.example.jacquesnoir.Room.Jointures.enMainsAvecCartes
import com.example.jacquesnoir.Room.Jointures.enMainDB
import com.example.jacquesnoir.Room.Jointures.enMainModele
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single

/** Accès aux données persistées et opérations atomiques de sauvegarde. */
class DepotJacquesNoir(private val bd: BDJacquesNoir) {
    companion object {
        const val CLE_MISE_PAR_DEFAUT = "miseParDefaut"
        const val CLE_JETONS_RESTANTS = "jetonsRestants"
        const val CLE_PARTIE_EN_COURS = "partieEnCours"
        const val CLE_NOMBRE_PAQUETS_SABOT = "nombrePaquetsSabot"
        const val JETONS_INITIAUX = 1_000
    }

    private val sabot get() = bd.sabotDAO()
    private val mains get() = bd.mainsDAO()
    private val defausse get() = bd.defausseDAO()
    private val statistiques get() = bd.statistiquesDAO()
    private val parametres get() = bd.parametresDAO()

    suspend fun viderSabot() = sabot.vider()

    suspend fun initialiserSabot(nombrePaquets: Int) {
        val paquet = Paquet().apply { creerPaquet(nombrePaquets) }
        bd.withTransaction {
            sabot.vider()
            sabot.insererCartes(paquet.cartes.mapIndexed { index, carte ->
                carte.enCarteSabot(index)
            })
        }
    }

    fun observerEstPauvre(): Flow<Boolean?> = observerJetonsRestants().map {it ->
        (it ?: 0) <= 0
    }
    suspend fun nombreCartesRestantes(): Int = sabot.nombreCartesRestantes()

    suspend fun obtenirCartesNonPigees(): List<CarteSabot> = sabot.obtenirCartesNonPigees()

    /** Retire du sabot la carte piochée en l'associant immédiatement à la main. */
    suspend fun pigerCarte(mainId: Int): Carte? = bd.withTransaction {
        if (mains.obtenirMain(mainId) == null) return@withTransaction null
        val carteDB = sabot.obtenirCarteDuDessus() ?: return@withTransaction null
        val main = mains.obtenirMainAvecCartes(mainId) ?: return@withTransaction null
        val liens = mains.obtenirLiens(mainId)
        mains.ajouterCartesMain(listOf(CarteMain(mainId, carteDB.id, liens.size)))
        val modele = main.enMainModele().tirer(carteDB.enCarteEngin())
        mains.modifierMain(modele.enMainDB())
        carteDB.enCarteEngin()
    }

    suspend fun obtenirMain(mainId: Int): MainModele? =
        mains.obtenirMainAvecCartes(mainId)?.enMainModele()

    suspend fun obtenirToutesLesMains(): List<MainModele> =
        mains.obtenirToutesLesMainsAvecCartes().map(MainAvecCartes::enMainModele)


    suspend fun supprimerToutesLesCartesDeMain(mainId: Int) = mains.supprimerCartesDeMain(mainId)

    /** Permet au gestionnaire de garder plusieurs opérations de dépôt atomiques. */
    suspend fun <T> dansTransaction(operation: suspend () -> T): T =
        bd.withTransaction { operation() }

    /** Enregistre plusieurs mains modifiées dans une transaction, en préservant les IDs des cartes. */
    suspend fun sauvegarderMains(mainsModifiees: List<MainModele>): List<MainModele> =
        bd.withTransaction {
            val sabotComplet = sabot.obtenirToutesLesCartes()
            val identifiantsModifies =
                mainsModifiees.mapNotNull { it.id.takeIf { id -> id != 0 } }.toSet()
            val idsDejaUtilises = mains.obtenirToutesLesMains()
                .filter { it.id !in identifiantsModifies }
                .flatMap { mains.obtenirLiens(it.id) }
                .map { it.carteId }
                .toMutableSet()

            val mainsEnregistrees = mutableListOf<MainModele>()
            for (modele in mainsModifiees) {
                val mainId = if (modele.id == 0) {
                    mains.insererMain(modele.enMainDB().copy(id = 0)).toInt()
                } else {
                    check(mains.modifierMain(modele.enMainDB()) == 1) { "Main ${modele.id} introuvable." }
                    modele.id
                }
                val anciensLiens = if (modele.id == 0) emptyList() else mains.obtenirLiens(mainId)
                val liensExistants = anciensLiens.toMutableList()
                val nouveauxLiens = modele.etat.cartes.mapIndexed { index, carte ->
                    val carteDB =
                        trouverCarteDisponible(carte, sabotComplet, idsDejaUtilises, liensExistants)
                    idsDejaUtilises.add(carteDB.id)
                    liensExistants.removeAll { it.carteId == carteDB.id }
                    CarteMain(mainId, carteDB.id, index)
                }
                mains.supprimerCartesDeMain(mainId)
                if (nouveauxLiens.isNotEmpty()) mains.ajouterCartesMain(nouveauxLiens)
                mainsEnregistrees.add(modele.copy(id = mainId))
            }
            mainsEnregistrees
        }

    suspend fun viderMainsDansDefausse() = defausse.viderMainsDansDefausse()

    suspend fun comptabiliserDefausse() = statistiques.comptabiliserDefausse()

    suspend fun obtenirParametreTexte(cle: String): String? =
        parametres.obtenir(cle)
            ?.let { Json.parseToJsonElement(it.valeur).jsonPrimitive.contentOrNull }

    suspend fun obtenirParametreEntier(cle: String): Int? =
        parametres.obtenir(cle)?.let { Json.parseToJsonElement(it.valeur).jsonPrimitive.intOrNull }

    suspend fun obtenirParametreBooleen(cle: String): Boolean? =
        parametres.obtenir(cle)
            ?.let { Json.parseToJsonElement(it.valeur).jsonPrimitive.booleanOrNull }

    suspend fun initialiserParametresParDefaut() {
        if (obtenirParametreEntier(CLE_JETONS_RESTANTS) == null) {
            enregistrerParametreEntier(CLE_JETONS_RESTANTS, JETONS_INITIAUX)
        }
        if (obtenirParametreEntier(CLE_MISE_PAR_DEFAUT) == null) {
            enregistrerParametreEntier(CLE_MISE_PAR_DEFAUT, 25)
        }
        if (obtenirParametreBooleen(CLE_PARTIE_EN_COURS) == null) {
            enregistrerParametreBooleen(CLE_PARTIE_EN_COURS, false)
        }
    }

    suspend fun enregistrerJetonsRestants(jetons: Int) {
        require(jetons > 0) { "Le nombre de jetons doit être positif." }
        enregistrerParametreEntier(CLE_JETONS_RESTANTS, jetons)

    }

    suspend fun obtenirPartieEnCours(): Boolean =
        obtenirParametreBooleen(CLE_PARTIE_EN_COURS) ?: false

    suspend fun definirPartieEnCours(partieEnCours: Boolean) =
        enregistrerParametreBooleen(CLE_PARTIE_EN_COURS, partieEnCours)

    suspend fun enregistrerParametreTexte(cle: String, valeur: String) =
        parametres.enregistrer(Parametre(cle, JsonPrimitive(valeur).toString()))

    suspend fun enregistrerParametreEntier(cle: String, valeur: Int) =
        parametres.enregistrer(Parametre(cle, JsonPrimitive(valeur).toString()))

    suspend fun enregistrerParametreBooleen(cle: String, valeur: Boolean) =
        parametres.enregistrer(Parametre(cle, JsonPrimitive(valeur).toString()))

    private fun trouverCarteDisponible(
        carte: Carte,
        toutesLesCartes: List<CarteSabot>,
        idsUtilises: Set<Int>,
        anciensLiens: List<CarteMain>
    ): CarteSabot {
        val idAncien = anciensLiens.firstOrNull { lien ->
            toutesLesCartes.firstOrNull { it.id == lien.carteId }?.correspondA(carte) == true
        }?.carteId
        return toutesLesCartes.firstOrNull { candidate ->
            candidate.id == idAncien || (candidate.id !in idsUtilises && candidate.correspondA(carte))
        } ?: error("Aucune carte disponible dans le sabot ne correspond à $carte.")
    }

    private fun CarteSabot.correspondA(carte: Carte): Boolean =
        couleur == carte.couleur.name && numero == carte.num.ordinal + 2

    // Méthodes pour l'interface
    /** Observe aussi les changements des liens et des cartes associées aux mains. */
    fun observerMains(): Flow<List<MainModele>> =
        mains.observerMainsAvecCartes().map { lignes ->
            lignes.enMainsAvecCartes().map(MainAvecCartes::enMainModele)
        }

    fun observerNombreCartesRestantes(): Flow<Int> = sabot.observerNombreCartesRestantes()

    fun observerMiseParDefaut(): Flow<Int?> = observerParametreEntier(CLE_MISE_PAR_DEFAUT)

    fun observerJetonsRestants(): Flow<Int?> = observerParametreEntier(CLE_JETONS_RESTANTS)

    fun observerNombrePaquetsParDefaut(): Flow<Int?> = observerParametreEntier(CLE_NOMBRE_PAQUETS_SABOT)

    fun observerPartieEnCours(): Flow<Boolean> =
        observerParametreBooleen(CLE_PARTIE_EN_COURS).map { it ?: false }

    fun observerDefausse(): Flow<List<CarteDefausse>> = defausse.observerToutesLesCartes()

    fun observerStatistiques(): Flow<List<StatistiqueDB>> = statistiques.observerToutes()

    fun observerParametreEntier(cle: String): Flow<Int?> =
        parametres.observer(cle).map { parametre ->
            parametre?.let { Json.parseToJsonElement(it.valeur).jsonPrimitive.intOrNull }
        }

    fun observerParametreBooleen(cle: String): Flow<Boolean?> =
        parametres.observer(cle).map { parametre ->
            parametre?.let { Json.parseToJsonElement(it.valeur).jsonPrimitive.booleanOrNull }
        }
}
