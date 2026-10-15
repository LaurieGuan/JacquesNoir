package com.example.jacquesnoir.Modele.Mains

import com.example.jacquesnoir.Modele.Carte
import com.example.jacquesnoir.Modele.SimpleValeur
import com.example.jacquesnoir.Modele.ValeurCarte
import com.example.jacquesnoir.Modele.Numero
import com.example.jacquesnoir.Modele.evaluer
import com.example.jacquesnoir.Modele.plus

sealed interface EtatMain {
    val cartes: List<Carte>
    val mise: Int

    fun tirer(carte: Carte): EtatMain {
        throw IllegalStateException("Cette main ne peut pas tirer.")
    }

    fun rester(): EtatMain {
        throw IllegalStateException("Cette main ne peut pas rester.")
    }

    fun doubler(): EtatMain {
        throw IllegalStateException("Cette main ne peut pas doubler.")
    }

    fun separer(): List<EtatMain> {
        throw IllegalStateException("Cette main ne peut pas être séparée.")
    }

    fun evaluer(): ValeurCarte {
        var total: ValeurCarte = SimpleValeur(0)
        for (carte in cartes) { total += carte.num.evaluer() }
        return total
    }
}

/** Calcule le meilleur total de la main sans dépasser 21 lorsque c'est possible. */
fun EtatMain.meilleurTotal(): Int {
    var total = 0
    var asSouples = 0
    for (carte in cartes) {
        when (carte.num) {
            Numero.CARTE_AS -> {
                total += 11
                asSouples += 1
            }
            Numero.CARTE_VALET, Numero.CARTE_DAME, Numero.CARTE_ROI -> total += 10
            else -> total += carte.num.ordinal + 2
        }
    }
    while (total > 21 && asSouples > 0) {
        total -= 10
        asSouples -= 1
    }
    return total
}
