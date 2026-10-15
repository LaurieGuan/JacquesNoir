package com.example.jacquesnoir.Modele.Mains

import com.example.jacquesnoir.Modele.Carte

class MainTerminee(
    override val cartes: MutableList<Carte>,
    override val mise: Int
) : EtatMain
