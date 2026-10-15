package com.example.jacquesnoir.Room.Entites

fun validerCarte(couleur: String, numero: Int) {
    check(
        couleur == "PIQUE" ||
        couleur == "COEUR" ||
        couleur == "TREFLE" ||
        couleur == "CARREAU"
    )
    check(numero in 2..14)
}
