package com.example.jacquesnoir.Modele.Mains

import com.example.jacquesnoir.Modele.Carte
import com.example.jacquesnoir.Modele.Numero
import com.example.jacquesnoir.Modele.SimpleValeur
import com.example.jacquesnoir.Modele.ValeurCarte
import com.example.jacquesnoir.Modele.evaluer
import com.example.jacquesnoir.Modele.plus
import com.example.jacquesnoir.Modele.plusPetiteValeur

class MainCreee(
    override val cartes: MutableList<Carte>,
    override val mise: Int
) : EtatMain {
    init {
        check(cartes.size < 2)
    }

    override fun tirer(carte: Carte): EtatMain {
        check(cartes.size < 2)

        val nouvellesCartes = cartes.toMutableList()
        nouvellesCartes.add(carte)

        if (nouvellesCartes.size < 2) return MainCreee(nouvellesCartes, mise)

        val contientAs = nouvellesCartes.any { it.num == Numero.CARTE_AS }
        val contientDix = nouvellesCartes.any {
            it.num == Numero.CARTE_10 ||
                it.num == Numero.CARTE_VALET ||
                it.num == Numero.CARTE_DAME ||
                it.num == Numero.CARTE_ROI
        }
        if (contientAs && contientDix) {
            return MainJacquesNoir(nouvellesCartes, mise)
        }

        var total: ValeurCarte = SimpleValeur(0)
        for (c in nouvellesCartes) { total += c.num.evaluer() }

        return when {
            total.plusPetiteValeur > 21 -> MainDepassee(nouvellesCartes, mise)
            total.plusPetiteValeur == 21 -> MainTerminee(nouvellesCartes, mise)
            else -> MainInitialisee(nouvellesCartes, mise)
        }
    }
}
