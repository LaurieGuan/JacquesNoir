package com.example.jacquesnoir.Modele.Mains

import com.example.jacquesnoir.Modele.Carte
import com.example.jacquesnoir.Modele.SimpleValeur
import com.example.jacquesnoir.Modele.ValeurCarte
import com.example.jacquesnoir.Modele.evaluer
import com.example.jacquesnoir.Modele.plus
import com.example.jacquesnoir.Modele.plusPetiteValeur

class MainInitialisee(
    cartesInitiales: MutableList<Carte>,
    override val mise: Int
) : EtatMain {
    override val cartes: MutableList<Carte> = cartesInitiales.toMutableList()

    init {
        var total: ValeurCarte = SimpleValeur(0)
        for (carte in cartes) { total += carte.num.evaluer() }

        require(total.plusPetiteValeur <= 21)
        require(mise >= 0)
    }

    override fun tirer(carte: Carte): EtatMain {
        val nouvellesCartes = cartes.toMutableList()
        nouvellesCartes.add(carte)

        var total: ValeurCarte = SimpleValeur(0)
        for (c in nouvellesCartes) { total += c.num.evaluer() }

        return when {
            total.plusPetiteValeur < 21 -> MainEngagee(nouvellesCartes, mise)
            total.plusPetiteValeur == 21 -> MainTerminee(nouvellesCartes, mise)
            else -> MainDepassee(nouvellesCartes, mise)
        }
    }

    override fun rester(): EtatMain = MainTerminee(cartes, mise)

    override fun doubler(): EtatMain = MainDoublee(cartes, mise * 2)

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
