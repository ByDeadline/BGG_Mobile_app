package edu.put.inf151917

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.provider.BaseColumns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.concurrent.thread
import android.text.Html
import edu.put.inf151917.databinding.ActivityGamesBinding
import edu.put.inf151917.databinding.ActivityMainBinding
import java.net.URL
import kotlin.io.readBytes

class MainActivity : AppCompatActivity() {

    companion object {
        // 0 - no data
        // 1 - data fetched
        // 2 - invalid username
        var globalVar = 0
        var body = ""
        var games = mutableListOf<Game>()
    }
    private fun downloadImage(url: String): ByteArray {
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val image = URL(url).readBytes()
        return image
    }
    private fun fetchUserData(UserN: String): Thread
    {
        return thread{
            var Games = mutableListOf<Game>()
            val client = OkHttpClient()
            val request = Request.Builder()
                .method("GET", null)
                .url("https://www.boardgamegeek.com/xmlapi2/collection?username=$UserN")
                .build()
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()
            body = responseBody.toString()
            if (response.isSuccessful) {

                if (responseBody!!.lines()[3] == "\t\t<message>Invalid username specified</message>"){
                    globalVar = 2
                } else {
                    globalVar = 1

                }
            }

        }
    }

    object TableInfo: BaseColumns {
        val TABLE_NAME = "Games"
        val TABLE_COLUMN_ID = "id"
        val TABLE_COLUMN_NAME = "name"
        val TABLE_COLUMN_YEAR = "year"
        val TABLE_COLUMN_THUMBNAIL = "thumbnail"
        val TABLE_COLUMN_IMAGE = "image"

    }

    object BasicCommand{
        val SQL_CREATE_TABLE: String =
            "CREATE TABLE ${TableInfo.TABLE_NAME} (" +
            "${TableInfo.TABLE_COLUMN_ID} TEXT PRIMARY KEY," +
            "${TableInfo.TABLE_COLUMN_NAME} TEXT," +
            "${TableInfo.TABLE_COLUMN_YEAR} TEXT," +
            "${TableInfo.TABLE_COLUMN_THUMBNAIL} BLOB," +
            "${TableInfo.TABLE_COLUMN_IMAGE} BLOB)"
        val SQL_DELETE_TABLE: String =
            "DROP TABLE IF EXISTS ${TableInfo.TABLE_NAME}"
    }

    class DataBaseHelper(context: Context): SQLiteOpenHelper(context, TableInfo.TABLE_NAME, null, 1){
        override fun onCreate(db: SQLiteDatabase?) {
            db?.execSQL(BasicCommand.SQL_CREATE_TABLE)
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db?.execSQL(BasicCommand.SQL_DELETE_TABLE)
            onCreate(db)
        }
    }

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        /*val dataAccess = DataAccess(applicationContext)
        if (dataAccess != null) {
            val intent = Intent(this, GamesActivity::class.java)
            startActivity(intent)
        }*/

        setContentView(R.layout.activity_main)
        val Search = findViewById<Button>(R.id.Search_button)


        Search.setOnClickListener {
            val UserName = findViewById<EditText>(R.id.User_Name_Inp).text.toString()
            println("abc")
            var LOGGEDINUSER = UserInfo()
            LOGGEDINUSER.username = UserName

            fetchUserData("minmyska").join() //change to UserName
            var responseBody = body
            var i = 1
            var j = 0
            // 1 game has 6 lines
            while(i< responseBody.lines().size-3){
                if (responseBody.lines()[i+4].contains("own=\"0\"") || !(responseBody.lines()[i].contains("subtype=\"boardgame\""))){
                    i+=6
                    continue
                } else {
                    games.add(Game())
                    games[j].id =
                        responseBody.lines()[i].substringAfter("objectid=\"").substringBefore("\"")
                    i++
                    games[j].name =
                        responseBody.lines()[i].substringAfter(">").substringBefore("</name>")
                    games[j].name = Html.fromHtml(games[j].name).toString()
                    i++
                    games[j].year =
                        responseBody.lines()[i].substringAfter("<yearpublished>")
                            .substringBefore("</yearpublished>")
                    games[j].image = downloadImage(
                        responseBody.lines()[i].substringAfter("<image>")
                        .substringBefore("</image>"))

                    i += 1
                    games[j].thumbnail = downloadImage(
                        responseBody.lines()[i].substringAfter("<thumbnail>")
                        .substringBefore("</thumbnail>"))
                    i+= 3

                    j++}



            }

            val dataAccess = DataAccess(this)
            dataAccess.insertData(games)

            val intent = Intent(this, GamesActivity::class.java)
            intent.putExtra("Size", games.size)

         startActivity(intent)
        }

        fetchUserData("minmyska").join() //change to UserName
        val UserName = "minmyska"
        println("abc")
        var LOGGEDINUSER = UserInfo()
        LOGGEDINUSER.username = UserName
        var responseBody = body
        var i = 1
        var j = 0
        // 1 game has 6 lines
        while(i< responseBody.lines().size-3){
            if (responseBody.lines()[i+4].contains("own=\"0\"") || !(responseBody.lines()[i].contains("subtype=\"boardgame\""))){
                i+=6
                continue
            } else {
                games.add(Game())
                games[j].id =
                    responseBody.lines()[i].substringAfter("objectid=\"").substringBefore("\"")
                i++
                games[j].name =
                    responseBody.lines()[i].substringAfter(">").substringBefore("</name>")
                games[j].name = Html.fromHtml(games[j].name).toString()
                i++
                games[j].year =
                    responseBody.lines()[i].substringAfter("<yearpublished>")
                        .substringBefore("</yearpublished>")
                games[j].image = downloadImage(
                    responseBody.lines()[i].substringAfter("<image>")
                    .substringBefore("</image>"))

                i += 1
                games[j].thumbnail = downloadImage(
                    responseBody.lines()[i].substringAfter("<thumbnail>")
                    .substringBefore("</thumbnail>"))
                i+= 3

                j++}



        }



        val dataAccess = DataAccess(this)
        dataAccess.dropTables()
        dataAccess.insertData(games)

        if (dataAccess.CreateUser(LOGGEDINUSER.username)){
            println("User created")
        } else {
            println("User already exists")
        }
    }
}

