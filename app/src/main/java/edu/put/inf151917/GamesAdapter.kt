package edu.put.inf151917

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import edu.put.inf151917.databinding.ItemViewBinding

class GamesAdapter(private val Games: List<Game> ):
    RecyclerView.Adapter<GamesAdapter.GameViewHolder>(){
    inner class GameViewHolder(binding: ItemViewBinding ): RecyclerView.ViewHolder(binding.root){
        val img = binding.imageView
        val GameName= binding.GameName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemViewBinding.inflate(layoutInflater, parent, false)
        return GameViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return Games.size
    }
    fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
    override fun onBindViewHolder(holder: GamesAdapter.GameViewHolder, position: Int) {
        val game = Games[position]
        val gameViewHolder = holder
        gameViewHolder.GameName.text = game.name

        val image = byteArrayToBitmap(game.image)
        gameViewHolder.img.setImageBitmap(image)
    }
}