package com.example.kadai09_pi12a_36

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AchievementAdapter(
    private val achievements: List<Achievement>
) : RecyclerView.Adapter<AchievementAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val container: LinearLayout = view.findViewById(R.id.achievementContainer)
        val tvIcon: TextView = view.findViewById(R.id.tvIcon)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val ivUnlocked: ImageView = view.findViewById(R.id.ivUnlocked)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_achievement, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val achievement = achievements[position]

        holder.tvIcon.text = achievement.icon
        holder.tvTitle.text = achievement.title
        holder.tvDescription.text = achievement.description

        if (achievement.isUnlocked) {
            holder.ivUnlocked.visibility = View.VISIBLE
            holder.container.alpha = 1f
            holder.tvIcon.alpha = 1f
        } else {
            holder.ivUnlocked.visibility = View.GONE
            holder.container.alpha = 0.5f
            holder.tvIcon.alpha = 0.3f
        }
    }

    override fun getItemCount() = achievements.size
}
