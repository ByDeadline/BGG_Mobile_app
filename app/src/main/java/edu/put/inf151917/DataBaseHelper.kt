package edu.put.inf151917
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns


object TableInfo: BaseColumns {
    val TABLE_GAME_NAME = "Games"
    val TABLE_GAME_COLUMN_ID = "id"
    val TABLE_GAME_COLUMN_NAME = "name"
    val TABLE_GAME_COLUMN_YEAR = "year"
    val TABLE_GAME_COLUMN_THUMBNAIL = "thumbnail"
    val TABLE_GAME_COLUMN_IMAGE = "image"
    val TABLE_USER_NAME = "User"
    val TABLE_USER_COLUMN_USERNAME = "username"
    val TABLE_USER_COLUMN_CREATED_AT = "created_at"
    val TABLE_USER_COLUMN_UPDATED_AT = "updated_at"

}

object BasicCommand{
    val SQL_CREATE_TABLE_USER: String =
        "CREATE TABLE ${TableInfo.TABLE_USER_NAME} (" +
                "${TableInfo.TABLE_USER_COLUMN_USERNAME} TEXT PRIMARY KEY," +
                "${TableInfo.TABLE_USER_COLUMN_CREATED_AT} TIMESTAMP DEFAULT CURRENT_TIMESTAMP ," +
                "${TableInfo.TABLE_USER_COLUMN_UPDATED_AT} TIMESTAMP DEFAULT CURRENT_TIMESTAMP)"


    val SQL_CREATE_TABLE_GAMES: String =
        "CREATE TABLE ${TableInfo.TABLE_GAME_NAME} (" +
                "${TableInfo.TABLE_GAME_COLUMN_ID} INT PRIMARY KEY," +
                "${TableInfo.TABLE_GAME_COLUMN_NAME} VARCHAR(50)," +
                "${TableInfo.TABLE_GAME_COLUMN_YEAR} INT," +
                "${TableInfo.TABLE_GAME_COLUMN_THUMBNAIL} BLOB," +
                "${TableInfo.TABLE_GAME_COLUMN_IMAGE} BLOB);"
    val SQL_DELETE_TABLE: String =
        "DROP TABLE IF EXISTS ${TableInfo.TABLE_GAME_NAME};"+
        "DROP TABLE IF EXISTS ${TableInfo.TABLE_USER_NAME}"
}

class DataBaseHelper(context: Context): SQLiteOpenHelper(context, TableInfo.TABLE_GAME_NAME, null, 1){
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(BasicCommand.SQL_CREATE_TABLE_GAMES)
        db?.execSQL(BasicCommand.SQL_CREATE_TABLE_USER)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(BasicCommand.SQL_DELETE_TABLE)
        onCreate(db)
    }
}
class DataAccess(private val context: Context) {
    private val databaseHelper = DataBaseHelper(context)
    private val database: SQLiteDatabase = databaseHelper.writableDatabase
    fun dropTables(){
        database.execSQL(BasicCommand.SQL_DELETE_TABLE)
    }
    fun insertData(Games: List<Game>){

        val db = MainActivity.DataBaseHelper(context)
        val dbWrite = db.writableDatabase
        try {dbWrite.execSQL(BasicCommand.SQL_CREATE_TABLE_GAMES)}
        catch (e: android.database.sqlite.SQLiteException){
            println("Database already exists")
        }
        val value = ContentValues()
        for (game in Games){
            try {
                value.put(TableInfo.TABLE_GAME_COLUMN_ID, game.id)
                value.put(TableInfo.TABLE_GAME_COLUMN_NAME, game.name)
                value.put(TableInfo.TABLE_GAME_COLUMN_YEAR, game.year)
                value.put(TableInfo.TABLE_GAME_COLUMN_THUMBNAIL, game.thumbnail)
                value.put(TableInfo.TABLE_GAME_COLUMN_IMAGE, game.image)
                dbWrite.insertOrThrow(TableInfo.TABLE_GAME_NAME, null, value)
            } catch (e: android.database.sqlite.SQLiteConstraintException){
                println("Game already exists in database")
            }

        }
    }
    fun getSize(): Int{
        val size = database.rawQuery("SELECT COUNT(*) FROM Games", null)
        if (size.moveToFirst()) {
            return size.getInt(0)
        }
        size.close()
        return 0
    }
    fun retrieveData(offset:Int ): Cursor {
        val limit = 1
        return database.rawQuery("SELECT * FROM Games LIMIT $limit OFFSET $offset", null)
    }
    fun CreateUser(username: String): Boolean {

        val db = MainActivity.DataBaseHelper(context)

        val dbWrite = db.writableDatabase
        try {dbWrite.execSQL(BasicCommand.SQL_CREATE_TABLE_USER)}
        catch (e: android.database.sqlite.SQLiteException){
            println("Table already exists")
        }
        val info = GetUserInfo()
        info.use{
            if (info.moveToFirst()){
                if (it.getString(it.getColumnIndexOrThrow("username")) == username){
                    return false
                } else{
                    dbWrite.execSQL("UPDATE USER SET username = '$username',updated_at = CURRENT_TIMESTAMP, created_at = CURRENT_TIMESTAMP WHERE username = '${it.getString(it.getColumnIndexOrThrow("username"))}'")
                    return true
                }
            }
            val value = ContentValues()
            value.put("username", username)
            dbWrite.insertOrThrow("USER", null, value)
            return true
        }
        val value = ContentValues()
        value.put("username", username)
        dbWrite.insertOrThrow("USER", null, value)
        return true
    }
    fun GetUserInfo(): Cursor {
        return database.rawQuery("SELECT * FROM USER", null)
    }
}
