package com.ahmed.redditdemo.commonadapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.size
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ahmed.redditdemo.databinding.PostsListItemBinding
import com.ahmed.redditdemo.utils.toDp
import com.ahmed.redditmodellayer.models.AwardIcon
import com.ahmed.redditmodellayer.models.Post
import com.bumptech.glide.Glide

class PostsListAdapter : RecyclerView.Adapter<PostsListAdapter.PostViewHolder>() {

    private var postsList = arrayListOf<Post>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = PostsListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postsList[position]
        holder.binding.headerTitle.text = "u/${post.author} \u2022 14h \u2022 ${post.domain}"
        holder.binding.numOfAwards.text = "${post.totalAwardsReceived}"
        holder.binding.numOfVotesTv.text = "${post.upVotes}"
        holder.binding.numCommentsTv.text = "${post.numComments}"
        holder.binding.titleTv.text = post.title
        when (post.postHint) {
            "hosted:video", "rich:video" -> {
                holder.binding.playVideoIcon.visibility = View.VISIBLE
                Glide.with(holder.binding.root).load(post.thumbnail).into(holder.binding.mediaImageView)
                renderIcons(holder.binding.awardsContainer, post.awardIcons)
            }
            "image" -> {
                holder.binding.playVideoIcon.visibility = View.GONE
                Glide.with(holder.binding.root).load(post.thumbnailFullUrl).into(holder.binding.mediaImageView)
                renderIcons(holder.binding.awardsContainer, post.awardIcons)
            }

            "link" -> {
                holder.binding.playVideoIcon.visibility = View.GONE
                Glide.with(holder.binding.root).load(post.thumbnail).into(holder.binding.mediaImageView)
                renderIcons(holder.binding.awardsContainer, post.awardIcons)
            }
        }
    }

    override fun getItemCount(): Int {
        return postsList.size
    }

    fun addNewData(newPostsList: List<Post>) {
        val oldPostsList = ArrayList(postsList)
        postsList.addAll(newPostsList)
        val diffUtil = PostsListDiffUtil(oldPostsList, postsList)
        val diffResult = DiffUtil.calculateDiff(diffUtil, false)
        diffResult.dispatchUpdatesTo(this)
    }

    private fun renderIcons(viewGroup: ViewGroup, icons: List<AwardIcon>) {
        icons.forEachIndexed outer@{ index, awardIcon ->
            if (index < 4) {
                if (viewGroup.size == 5) {
                    viewGroup.removeViews(0, 4)
                }
                val imageView = ImageView(viewGroup.context)
                val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams(16.toDp(viewGroup.context), 16.toDp(viewGroup.context)))
                imageView.layoutParams = layoutParams
                Glide.with(viewGroup).load(awardIcon.iconUrl).into(imageView)
                viewGroup.addView(imageView, 0)
            } else {
                return@outer
            }
        }
    }

    class PostViewHolder(val binding: PostsListItemBinding) : RecyclerView.ViewHolder(binding.root)
}

class PostsListDiffUtil(
    private val olderList: List<Post>,
    private val newList: List<Post>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return olderList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        olderList[oldItemPosition].id == newList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            olderList[oldItemPosition].id == newList[newItemPosition].id -> true
            // other contents

            else -> false
        }
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }

}