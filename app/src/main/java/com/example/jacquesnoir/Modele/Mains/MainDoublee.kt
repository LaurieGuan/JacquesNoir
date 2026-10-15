package com.example.jacquesnoir.Modele.Mains

import com.example.jacquesnoir.Modele.Carte
import com.example.jacquesnoir.Modele.SimpleValeur
import com.example.jacquesnoir.Modele.ValeurCarte
import com.example.jacquesnoir.Modele.evaluer
import com.example.jacquesnoir.Modele.plus
import com.example.jacquesnoir.Modele.plusPetiteValeur

class MainDoublee(
    override val cartes: MutableList<Carte>,
    override val mise: Int
) : EtatMain {
    override fun tirer(carte: Carte): EtatMain {
        val nouvellesCartes = cartes.toMutableList()
        nouvellesCartes.add(carte)

        var total: ValeurCarte = SimpleValeur(0)
        for (c in nouvellesCartes) { total += c.num.evaluer() }

        return if (total.plusPetiteValeur > 21) {
            MainDepassee(nouvellesCartes, mise)
        } else {
            MainTerminee(nouvellesCartes, mise)
        }
    }
}
