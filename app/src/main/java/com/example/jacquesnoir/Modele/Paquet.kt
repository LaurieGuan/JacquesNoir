package com.example.jacquesnoir.Modele

class Paquet {
    /** Note :
     * Pour les besoins de la gestion de la pile de cartes, nous utilisons
     * une liste. Cependant, nous la traitons en jeu comme une pile, donc,
     * on ajoute ou on retire UNIQUEMENT depuis la fin du paquet de cartes.
     */
    val cartes = mutableListOf<Carte>()
}

fun Paquet.creerPaquet(nombrePaquets: Int) {
    require(nombrePaquets > 0) {
        "Le sabot nécessite un nombre de paquets positif."
    }
    for (i in 0..< nombrePaquets) {
        for (numero in Numero.values()) {
            for (couleur in Couleur.values()) {
                cartes.add(Carte(numero, couleur))
            }
        }
    }
    cartes.shuffle()
}

fun Paquet.pigerCarte(): Carte {
    return cartes.removeAt(index = cartes.lastIndex)
}