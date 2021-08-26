package com.mattg.pickem.ui.news.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mattg.pickem.databinding.RssRvItemBinding
import com.mattg.pickem.utils.startCustomTab
import com.prof.rssparser.Article

class RSSAdapter(private val context: Context, private val newsItems: ArrayList<Article>) :
    RecyclerView.Adapter<RSSViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RSSViewHolder {
        return RSSViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RSSViewHolder, position: Int) {
        holder.bind(newsItems[position], context)
    }

    override fun getItemCount(): Int {
        return newsItems.size
    }
}

class RSSViewHolder private constructor(private val binding: RssRvItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun from(parent: ViewGroup): RSSViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = RssRvItemBinding.inflate(layoutInflater, parent, false)
            return RSSViewHolder(binding)
        }
    }

    fun bind(item: Article, context: Context) {
        binding.newsItem = item
        binding.rssItemCl.setOnClickListener {
            item.link?.let { startCustomTab(it, context) }
        }
        val image = item.image
        if (image != null) {
            binding.ivNewsImage.visibility = View.VISIBLE
            Glide.with(context)
                .load(image)
                .into(binding.ivNewsImage)
        }
    }
}

