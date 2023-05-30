package edu.put.inf151917

import android.os.Bundle
import android.os.StrictMode
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import edu.put.inf151917.databinding.ActivityGamesBinding
import java.net.URL
import com.gu.toolargetool.TooLargeTool
import edu.put.inf151917.DataAccess
class GamesActivity: AppCompatActivity() {

private lateinit var binding: ActivityGamesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val size: Int = intent.getIntExtra("size", 0)
        var GamesDisp : MutableList<Game> = MutableList(size) { Game() }
        val dataAccess = DataAccess(applicationContext)
        val sizeDB = dataAccess.getSize()
        var I = 0
        while (I <= sizeDB && sizeDB >0){

            val retrieved = dataAccess.retrieveData(I)
            retrieved.use{
                while (it.moveToNext()) {
                    val game = Game()
                    game.id = it.getString(it.getColumnIndexOrThrow("id"))
                    game.name = it.getString(it.getColumnIndexOrThrow("name"))
                    game.year = it.getString(it.getColumnIndexOrThrow("year"))
                    game.thumbnail = it.getBlob(it.getColumnIndexOrThrow("thumbnail"))
                    game.image = it.getBlob(it.getColumnIndexOrThrow("image"))
                    GamesDisp.add(game)
                }
                retrieved.close()
            I+=1
            }
        }
        binding = ActivityGamesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val adapter = GamesAdapter(GamesDisp)
        binding.gamesView.layoutManager = LinearLayoutManager(applicationContext)
        binding.gamesView.adapter = adapter
    }

}