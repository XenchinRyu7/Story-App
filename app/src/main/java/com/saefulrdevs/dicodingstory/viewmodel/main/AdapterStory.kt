package com.saefulrdevs.dicodingstory.viewmodel.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.saefulrdevs.dicodingstory.data.remote.response.Story
import com.saefulrdevs.dicodingstory.databinding.ItemStoryBinding

class AdapterStory(private val onItemClick: ((String?) -> Unit)? = null) :
    ListAdapter<Story, AdapterStory.StoryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding =
            ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class StoryViewHolder(
        private val binding: ItemStoryBinding,
        private val onItemClick: ((String?) -> Unit)?
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: Story) {
            binding.username.text = story.name
            Glide.with(binding.imageStory.context)
                .load(story.photoUrl)
                .into(binding.imageStory)

            binding.root.setOnClickListener {
                onItemClick?.invoke(story.id)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Story> =
            object : DiffUtil.ItemCallback<Story>() {
                override fun areItemsTheSame(
                    oldItem: Story,
                    newItem: Story
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: Story,
                    newItem: Story
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}