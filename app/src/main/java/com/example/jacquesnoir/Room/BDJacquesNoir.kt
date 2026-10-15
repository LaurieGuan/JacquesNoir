package com.example.jacquesnoir.Room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.jacquesnoir.Room.DAO.DefausseDAO
import com.example.jacquesnoir.Room.DAO.MainsDAO
import com.example.jacquesnoir.Room.DAO.ParametresDAO
import com.example.jacquesnoir.Room.DAO.SabotDAO
import com.example.jacquesnoir.Room.DAO.StatistiquesDAO
import com.example.jacquesnoir.Room.Entites.CarteDefausse
import com.example.jacquesnoir.Room.Entites.CarteSabot
import com.example.jacquesnoir.Room.Entites.MainDB
import com.example.jacquesnoir.Room.Entites.StatistiqueDB
import com.example.jacquesnoir.Room.Entites.CarteMain
import com.example.jacquesnoir.Room.Entites.Parametre

@Database(
    entities = [
        CarteDefausse::class,
        CarteSabot::class,
        MainDB::class,
        StatistiqueDB::class,
        CarteMain::class,
        Parametre::class
    ],
    version = 2
)
abstract class BDJacquesNoir : RoomDatabase() {
    abstract fun sabotDAO(): SabotDAO
    abstract fun mainsDAO(): MainsDAO
    abstract fun defausseDAO(): DefausseDAO
    abstract fun statistiquesDAO(): StatistiquesDAO
    abstract fun parametresDAO(): ParametresDAO

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE Mains ADD COLUMN mise INTEGER NOT NULL DEFAULT 0"
                )
            }
        }
    }
}
