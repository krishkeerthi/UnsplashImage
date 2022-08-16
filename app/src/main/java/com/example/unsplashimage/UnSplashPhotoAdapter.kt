package com.example.unsplashimage

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.unsplashimage.data.UnsplashPhoto
import com.example.unsplashimage.databinding.UnsplashPhotoBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UnSplashPhotoAdapter(
    private val itemClick: (UnsplashPhoto?) -> Unit
) :
    PagingDataAdapter<UnsplashPhoto, UnSplashPhotoAdapter.PhotoViewHolder>(PHOTO_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = UnsplashPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        Log.d(TAG, "onCreateViewHolder: called ${itemCount}")

        return PhotoViewHolder(binding).apply {
            binding.unsplashPhoto.setOnClickListener{
                itemClick(getItem(absoluteAdapterPosition))
            }
        }
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val currentItem = getItem(position)
        Log.d(TAG, "onBindViewHolder: ${itemCount}")
        if(currentItem != null)
        holder.bind(currentItem)
    }

    inner class PhotoViewHolder(private val binding: UnsplashPhotoBinding)
        : RecyclerView.ViewHolder(binding.root){

        fun bind(photo: UnsplashPhoto) {
            CoroutineScope(Dispatchers.IO).launch {
                Log.i(TAG, "Current thread ${Thread.currentThread().name}")
                val bitmap = downloadBitmap(photo.urls.regular)
                withContext(Dispatchers.Main) {
                    Log.i(TAG, "Current thread in the main dispatcher: ${Thread.currentThread().name}")
                    binding.unsplashPhoto.setImageBitmap(bitmap)
                }
            }
//
//            binding.apply {
//                Glide.with(itemView)
//                    .load(photo.urls.regular)
//                    .centerCrop()
//                    .transition(DrawableTransitionOptions.withCrossFade())
//                    .error(R.drawable.ic_baseline_close_24)
//                    .into(unsplashPhoto)
//            }
        }
    }

    companion object{
        private val PHOTO_COMPARATOR = object : DiffUtil.ItemCallback<UnsplashPhoto>() {
            override fun areItemsTheSame(oldItem: UnsplashPhoto, newItem: UnsplashPhoto): Boolean {
                Log.d(TAG, "areItemsTheSame: inside diffutil")
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: UnsplashPhoto,
                newItem: UnsplashPhoto
            ): Boolean {
                Log.d(TAG, "areItemsTheSame: inside diffutil")
                return oldItem == newItem
            }
        }
    }
}