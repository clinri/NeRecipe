package ru.netology.nerecipe.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import ru.netology.nerecipe.databinding.RecipeItemBinding
import ru.netology.nerecipe.dto.Recipe

class RecipesAdapter(
    private val interactionListener: RecipesInteractionListener
) : ListAdapter<Recipe, RecipesViewHolder>(DiffCallback) {

    private var onRecipeItemLongClickListener: ((Recipe) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecipeItemBinding.inflate(inflater, parent, false)
        return RecipesViewHolder(binding, interactionListener)
    }

    override fun onBindViewHolder(holder: RecipesViewHolder, position: Int) {
        val recipeItem = getItem(position)
        holder.bind(recipeItem)
        holder.itemView.setOnLongClickListener {
            onRecipeItemLongClickListener?.invoke(recipeItem)
            true
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Recipe>() {
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe) =
            oldItem == newItem
    }
}