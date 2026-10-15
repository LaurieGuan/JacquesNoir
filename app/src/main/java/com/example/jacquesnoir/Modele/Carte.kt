package com.example.jacquesnoir.Modele

class Carte (val num: Numero, val couleur: Couleur)

enum class Couleur { TREFLE, CARREAU, COEUR, PIQUE }
enum class Numero {
    CARTE_2, CARTE_3, CARTE_4,
    CARTE_5, CARTE_6, CARTE_7,
    CARTE_8, CARTE_9, CARTE_10,
    CARTE_VALET, CARTE_DAME,
    CARTE_ROI, CARTE_AS
}

fun Numero.evaluer(): ValeurCarte {
    return when (this) {
        Numero.CARTE_VALET, Numero.CARTE_DAME, Numero.CARTE_ROI -> SimpleValeur(10)
        Numero.CARTE_AS -> DoubleValeur(1, 11)
        else -> SimpleValeur(this.ordinal + 2)
    }
}