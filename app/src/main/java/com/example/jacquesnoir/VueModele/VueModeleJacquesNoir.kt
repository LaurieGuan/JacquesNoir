package com.example.jacquesnoir.VueModele

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jacquesnoir.Depot.DepotJacquesNoir
import com.example.jacquesnoir.Depot.GestionnairePartie
import com.example.jacquesnoir.Modele.Mains.EtatMain
import com.example.jacquesnoir.Modele.Mains.MainDepassee
import com.example.jacquesnoir.Modele.Mains.MainEngagee
import com.example.jacquesnoir.Modele.Mains.MainInitialisee
import com.example.jacquesnoir.Modele.Mains.MainJacquesNoir
import com.example.jacquesnoir.Modele.Mains.MainModele
import com.example.jacquesnoir.Modele.Mains.MainSeparee
import com.example.jacquesnoir.Modele.Mains.MainTerminee
import com.example.jacquesnoir.Room.Entites.CarteDefausse
import com.example.jacquesnoir.Room.Entites.StatistiqueDB
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VueModeleJacquesNoir(
    private val depot: DepotJacquesNoir,
    private val gestionnairePartie: GestionnairePartie
) : ViewModel() {

    val jetonsDisponibles = depot.observerJetonsRestants()
        .map { it ?: DepotJacquesNoir.JETONS_INITIAUX }
        .stateIn(viewModelScope, SharingStarted.Eagerly, DepotJacquesNoir.JETONS_INITIAUX)

    val nombrePaquetsParDefaut = depot.observerNombrePaquetsParDefaut()
        .map { it ?: 1 }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 1)


    private val miseParDefaut = depot.observerMiseParDefaut()
        .map { it ?: 25 }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 25)

    val partieEnCours = depot.observerPartieEnCours()
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val estPauvre = depot.observerEstPauvre()
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val _confirmationFailliteVisible = MutableStateFlow(false)
    val confirmationFailliteVisible = _confirmationFailliteVisible.asStateFlow()

    val statistiques = depot.observerStatistiques()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList<StatistiqueDB>()
        )

    private val _texteMise = MutableStateFlow("25")
    val texteMise = _texteMise.asStateFlow()
    private var texteMiseModifie = false

    val miseSelectionnee = combine(_texteMise, miseParDefaut) { texte, miseDefaut ->
        texte.toIntOrNull() ?: miseDefaut
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 25)

    private val _erreur = MutableStateFlow<String?>(null)
    val erreur = _erreur.asStateFlow()

    private val _mains = MutableStateFlow<List<MainModele>>(emptyList())
    val mains = _mains.asStateFlow()

    private val _donneesChargees = MutableStateFlow(false)
    val donneesChargees = _donneesChargees.asStateFlow()

    private val _nombreCartesRestantes = MutableStateFlow(0)
    val nombreCartesRestantes = _nombreCartesRestantes.asStateFlow()

    val defausse = depot.observerDefausse()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList<CarteDefausse>())

    private val _identifiantMainActive = MutableStateFlow<Int?>(null)
    val identifiantMainActive = _identifiantMainActive.asStateFlow()

    private val _croupierRevele = MutableStateFlow(false)
    val croupierRevele = _croupierRevele.asStateFlow()

    private val _partieQuittee = MutableSharedFlow<Unit>()
    val partieQuittee = _partieQuittee.asSharedFlow()

    private val _actionEnCours = MutableStateFlow(false)
    val actionEnCours = _actionEnCours.asStateFlow()

    private var reglementEnCours = false

    init {
        viewModelScope.launch {
            try {
                depot.initialiserParametresParDefaut()
            } catch (exception: Exception) {
                _erreur.value = exception.message ?: "Impossible d’initialiser les paramètres."
            }
        }
        viewModelScope.launch {
            miseParDefaut.collect { mise ->
                if (!texteMiseModifie) _texteMise.value = mise.toString()
            }
        }
        viewModelScope.launch {
            depot.observerNombreCartesRestantes().collect { nombre ->
                _nombreCartesRestantes.value = nombre
            }
        }
        viewModelScope.launch {
            depot.observerMains().collect { mains ->
                _mains.value = mains
                _donneesChargees.value = true
                ajusterMainActive(mains)
                try {
                    val croupierNaturel = mains.firstOrNull { !it.estJoueur }
                        ?.etat is MainJacquesNoir
                    if (croupierNaturel) {
                        mains.filter { it.estJoueur && !it.etat.estTerminee() }
                            .forEach { gestionnairePartie.rester(it.id) }
                    }
                    val mainsPourFinalisation =
                        if (croupierNaturel) depot.obtenirToutesLesMains() else mains
                    // Ne pas bloquer la collecte Room pendant le tour du croupier :
                    // chaque tirage peut ainsi mettre à jour l'interface immédiatement.
                    viewModelScope.launch {
                        finaliserMancheSiNecessaire(mainsPourFinalisation)
                    }
                } catch (exception: Exception) {
                    _erreur.value = exception.message ?: "Impossible de mettre à jour la manche."
                }
            }
        }
    }

    fun modifierTexteMise(texte: String) {
        _texteMise.value = texte.filter(Char::isDigit)
        texteMiseModifie = true
        _erreur.value = null
    }

    fun choisirNombrePaquets(nombre: Int) {
        if (nombre !in 1..4) return
        viewModelScope.launch {
            depot.enregistrerParametreEntier(
                DepotJacquesNoir.CLE_NOMBRE_PAQUETS_SABOT,
                nombre
            )
        }
    }

    fun diminuerMise() {
        modifierTexteMise(((miseSelectionnee.value - 5).coerceAtLeast(5)).toString())
    }

    fun augmenterMise() {
        val montant = (miseSelectionnee.value.toLong() + 5L)
            .coerceAtMost(Int.MAX_VALUE.toLong())
        modifierTexteMise(montant.toString())
    }

    fun enregistrerMiseParDefaut() {
        viewModelScope.launch {
            val mise = obtenirMiseValide() ?: return@launch
            try {
                gestionnairePartie.changerMiseParDefaut(mise)
                _texteMise.value = mise.toString()
                texteMiseModifie = false
                _erreur.value = null
            } catch (exception: Exception) {
                _erreur.value = exception.message ?: "Impossible d’enregistrer la mise."
            }
        }
    }

    fun confirmerMiseEtContinuer() {
        executerAction {
            depot.initialiserParametresParDefaut()
            val mise = obtenirMiseValide() ?: return@executerAction
            gestionnairePartie.changerMiseParDefaut(mise)
            _texteMise.value = mise.toString()
            texteMiseModifie = false
            val mainsExistantes = depot.obtenirToutesLesMains()
            if (mainsExistantes.isNotEmpty()) {
                check(gestionnairePartie.mancheReglee()) {
                    "La manche en cours doit être terminée avant d'en démarrer une autre."
                }
                gestionnairePartie.archiverMancheTerminee()
            }
            gestionnairePartie.demarrerManche(
                mise = mise,
                nombrePaquetsSiSabotVide = nombrePaquetsParDefaut.value
            )
        }
    }

    fun selectionnerMain(mainId: Int) {
        if (_mains.value.any { it.id == mainId && it.estJoueur }) {
            _identifiantMainActive.value = mainId
        }
    }

    fun actionsDisponibles(mainId: Int): Boolean =
        !_actionEnCours.value && mainJouable(mainId)

    fun peutRester(mainId: Int): Boolean =
        mainJouable(mainId) && main(mainId)?.etat.let {
            it is MainInitialisee || it is MainEngagee || it is MainSeparee
        }

    fun peutDoubler(mainId: Int): Boolean {
        val main = main(mainId) ?: return false
        return mainJouable(mainId) &&
            (main.etat is MainInitialisee || main.etat is MainSeparee) &&
            jetonsDisponibles.value >= main.etat.mise
    }

    fun peutSeparer(mainId: Int): Boolean {
        val main = main(mainId) ?: return false
        return mainJouable(mainId) &&
            (main.etat is MainInitialisee || main.etat is MainEngagee || main.etat is MainSeparee) &&
            main.etat.cartes.size == 2 &&
            main.etat.cartes[0].num == main.etat.cartes[1].num &&
            _mains.value.count { it.estJoueur } < 4 &&
            jetonsDisponibles.value >= main.etat.mise
    }

    fun tirer(mainId: Int) {
        executerAction {
            check(mainJouable(mainId)) { "Cette main ne peut pas tirer." }
            check(gestionnairePartie.tirer(mainId) != null) { "Le sabot ne contient plus de carte." }
        }
    }

    fun rester(mainId: Int) {
        executerAction {
            check(mainJouable(mainId) && peutRester(mainId)) { "Cette main ne peut pas rester." }
            check(gestionnairePartie.rester(mainId) != null) { "La main est introuvable." }
        }
    }

    fun doubler(mainId: Int) {
        executerAction {
            check(mainJouable(mainId) && peutDoubler(mainId)) {
                "Le solde ou l'état de la main ne permet pas de doubler."
            }
            check(gestionnairePartie.doublerEtTirer(mainId) != null) { "La main est introuvable." }
        }
    }

    fun separer(mainId: Int) {
        executerAction {
            // L'éligibilité détaillée est revérifiée sur les données Room par le gestionnaire.
            // Ici, ne vérifier que le tour évite de refuser l'action sur un état Compose en retard.
            check(mainJouable(mainId)) { "Cette main ne peut pas être séparée maintenant." }
            check(gestionnairePartie.separer(mainId) != null) { "La main est introuvable." }
        }
    }

    fun verifierFailliteAvantPartie(): Boolean {
        val faillite = estPauvre.value ?: true
        _confirmationFailliteVisible.value = faillite
        return faillite
    }

    fun afficherConfirmationFaillite() {
        _confirmationFailliteVisible.value = true
    }

    fun annulerConfirmationFaillite() {
        _confirmationFailliteVisible.value = false
    }

    fun reinitialiserJetonsRestant(apresReinitialisation: () -> Unit = {}) {
        executerAction {
            depot.enregistrerJetonsRestants(DepotJacquesNoir.JETONS_INITIAUX)
            _confirmationFailliteVisible.value = false
            apresReinitialisation()
        }
    }

    fun quitterPartie() {
        viewModelScope.launch {
            try {
                gestionnairePartie.terminerPartie()
                _erreur.value = null
                _partieQuittee.emit(Unit)
            } catch (exception: Exception) {
                _erreur.value = exception.message ?: "Impossible de quitter la partie."
            }
        }
    }

    private fun executerAction(action: suspend () -> Unit) {
        if (_actionEnCours.value) return
        viewModelScope.launch {
            if (_actionEnCours.value) return@launch
            _actionEnCours.value = true
            try {
                _erreur.value = null
                action()
            } catch (exception: Exception) {
                _erreur.value = exception.message ?: "L’action n’a pas pu être exécutée."
            } finally {
                _actionEnCours.value = false
            }
        }
    }

    private fun mainJouable(mainId: Int): Boolean =
        !_croupierRevele.value &&
            _mains.value.none { !it.estJoueur && it.etat is MainJacquesNoir } &&
            _mains.value.any {
            it.id == mainId && it.estJoueur && it.etat.estJouable()
        }

    private suspend fun finaliserMancheSiNecessaire(mains: List<MainModele>) {
        val mainsJoueur = mains.filter { it.estJoueur }
        if (mainsJoueur.isEmpty() || !mainsJoueur.all { it.etat.estTerminee() }) return

        if (reglementEnCours) return
        reglementEnCours = true
        try {
            if (!gestionnairePartie.mancheReglee()) {
                gestionnairePartie.reglerManche()
            }
            _croupierRevele.value = true
        } catch (exception: Exception) {
            _erreur.value = exception.message ?: "Impossible de régler la manche."
        } finally {
            reglementEnCours = false
        }
    }

    private fun ajusterMainActive(mains: List<MainModele>) {
        val mainsJoueur = mains.filter { it.estJoueur }
        val mainSelectionnee = _identifiantMainActive.value
        val selectionToujoursJouable = mainsJoueur.any {
            it.id == mainSelectionnee && it.etat.estJouable()
        }
        if (!selectionToujoursJouable) {
            _identifiantMainActive.value = mainsJoueur.firstOrNull { it.etat.estJouable() }?.id
        }
        if (mainsJoueur.isEmpty()) _croupierRevele.value = false
    }

    private fun main(mainId: Int): MainModele? =
        _mains.value.firstOrNull { it.id == mainId && it.estJoueur }

    private fun obtenirMiseValide(): Int? {
        val mise = _texteMise.value.toIntOrNull()
        if (mise == null) {
            _erreur.value = "Entrez une mise valide."
            return null
        }
        if (mise <= 0) {
            _erreur.value = "La mise doit être supérieure à zéro."
            return null
        }
        if (mise > jetonsDisponibles.value) {
            _erreur.value = "Votre solde ne permet pas cette mise."
            return null
        }
        return mise
    }
}

private fun EtatMain.estJouable(): Boolean =
    this is MainInitialisee || this is MainEngagee || this is MainSeparee

private fun EtatMain.estTerminee(): Boolean =
    this is MainTerminee || this is MainDepassee || this is MainJacquesNoir
