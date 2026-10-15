package com.example.jacquesnoir.Modele.Mains

import com.example.jacquesnoir.Modele.Carte
import com.example.jacquesnoir.Modele.SimpleValeur
import com.example.jacquesnoir.Modele.ValeurCarte
import com.example.jacquesnoir.Modele.evaluer
import com.example.jacquesnoir.Modele.plus
import com.example.jacquesnoir.Modele.plusPetiteValeur

class MainEngagee(
    override val cartes: MutableList<Carte>,
    override val mise: Int
) : EtatMain {
    init {
        check(cartes.size >= 2)
        check(evaluer().plusPetiteValeur < 21)
    }

    override fun tirer(carte: Carte): EtatMain {
        val nouvellesCartes = cartes.toMutableList()
        nouvellesCartes.add(carte)

        var total: ValeurCarte = SimpleValeur(0)
        for (c in nouvellesCartes) { total += c.num.evaluer() }

        return when {
            total.plusPetiteValeur > 21 -> MainDepassee(nouvellesCartes, mise)
            total.plusPetiteValeur == 21 -> MainTerminee(nouvellesCartes, mise)
            else -> MainEngagee(nouvellesCartes, mise)
        }
    }

    override fun rester(): EtatMain = MainTerminee(cartes, mise)

    override fun separer(): List<EtatMain> {
        check(cartes.size == 2 && cartes[0].num == cartes[1].num)

        val premiereMain = mutableListOf(cartes[0])
        val deuxiemeMain = mutableListOf(cartes[1])

        return listOf(
            MainSeparee(premiereMain, mise),
            MainSeparee(deuxiemeMain, mise)
        )
    }
}
