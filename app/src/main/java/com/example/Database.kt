package com.example

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val mbtiResult: String? = null,
    val sunSign: String? = null,
    val moonSign: String? = null,
    val risingSign: String? = null,
    val isDarkMode: Boolean = true,
    val favoriteSeries: String = "Blank: The Series",
    val favoriteCouple: String = "FayeYoko",
    val favoriteActress: String = "Faye Peraya",
    val bias: String = "Yoko Apasra",
    val isAdmin: Boolean = false,
    val isModerator: Boolean = false,
    val subscriptionTier: String = "Free"
)

@Entity(tableName = "tarot_readings")
data class TarotReading(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cards: String,
    val reading: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_media_lists")
data class SavedMediaList(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val itemsJson: String, // Comma-separated or serialized
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface ProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getProfile(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProfile(profile: UserProfile)
    
    @Query("SELECT * FROM tarot_readings ORDER BY timestamp DESC")
    fun getTarotReadings(): Flow<List<TarotReading>>
    
    @Insert
    suspend fun saveTarotReading(reading: TarotReading)

    @Query("SELECT * FROM saved_media_lists ORDER BY timestamp DESC")
    fun getSavedMediaLists(): Flow<List<SavedMediaList>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMediaList(mediaList: SavedMediaList)

    @Query("DELETE FROM saved_media_lists WHERE id = :id")
    suspend fun deleteSavedMediaList(id: Int)
}

@Database(entities = [UserProfile::class, TarotReading::class, SavedMediaList::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao

    companion object {
        @Volatile private var instance: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "app_database")
                    .fallbackToDestructiveMigration(true)
                    .build().also { instance = it }
            }
        }
    }
}
