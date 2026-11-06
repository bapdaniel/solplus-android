package com.solplus.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.solplus.app.data.local.dao.AuditLogDao
import com.solplus.app.data.local.dao.DoseOccurrenceDao
import com.solplus.app.data.local.dao.MedicationDao
import com.solplus.app.data.local.entity.AuditLogEntity
import com.solplus.app.data.local.entity.DoseOccurrenceEntity
import com.solplus.app.data.local.entity.MedicationEntity
import com.solplus.app.domain.model.MedicationType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

@Database(
    entities = [
        MedicationEntity::class,
        DoseOccurrenceEntity::class,
        AuditLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SolPlusDatabase : RoomDatabase() {
    abstract fun medicationDao(): MedicationDao
    abstract fun doseOccurrenceDao(): DoseOccurrenceDao
    abstract fun auditLogDao(): AuditLogDao

    companion object {
        @Volatile
        private var INSTANCE: SolPlusDatabase? = null

        fun getInstance(context: Context): SolPlusDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SolPlusDatabase::class.java,
                    "solplus_database.db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Pré-seeding dos 3 tratamentos iniciais
                            CoroutineScope(Dispatchers.IO).launch {
                                getInstance(context).seedInitialData()
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    suspend fun seedInitialData() {
        val medDao = medicationDao()
        val doseDao = doseOccurrenceDao()

        if (medDao.count() == 0) {
            val med1 = MedicationEntity(
                name = "Remédio da pressão",
                type = MedicationType.ORAL,
                scheduledTime = LocalTime.of(8, 0),
                dosage = "1 comprimido",
                instructions = "Tomar pela manhã com um copo d'água",
                period = "MANHÃ"
            )
            val med2 = MedicationEntity(
                name = "Colírio",
                type = MedicationType.EYE_DROP,
                scheduledTime = LocalTime.of(8, 0),
                dosage = "1 gota em cada olho",
                instructions = "Aplicar nos dois olhos pela manhã",
                period = "MANHÃ"
            )
            val med3 = MedicationEntity(
                name = "Colírio",
                type = MedicationType.EYE_DROP,
                scheduledTime = LocalTime.of(20, 0),
                dosage = "1 gota em cada olho",
                instructions = "Aplicar nos dois olhos à noite",
                period = "NOITE"
            )

            val id1 = medDao.insert(med1)
            val id2 = medDao.insert(med2)
            val id3 = medDao.insert(med3)

            // Gerar ocorrências para o dia de hoje
            val today = LocalDate.now()
            doseDao.insertAll(
                listOf(
                    DoseOccurrenceEntity(medicationId = id1, scheduledDate = today, scheduledTime = LocalTime.of(8, 0)),
                    DoseOccurrenceEntity(medicationId = id2, scheduledDate = today, scheduledTime = LocalTime.of(8, 0)),
                    DoseOccurrenceEntity(medicationId = id3, scheduledDate = today, scheduledTime = LocalTime.of(20, 0))
                )
            )
        }
    }
}
