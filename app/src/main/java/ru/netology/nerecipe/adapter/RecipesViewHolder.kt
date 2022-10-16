package ru.netology.nerecipe.adapter

import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nerecipe.R
import ru.netology.nerecipe.databinding.RecipeItemBinding
import ru.netology.nerecipe.dto.Recipe

class RecipesViewHolder(
    private val binding: RecipeItemBinding,
    listener: RecipesInteractionListener
):RecyclerView.ViewHolder(binding.root) {
    private lateinit var recipe: Recipe
    private val popupMenu by lazy {
        PopupMenu(itemView.context, binding.menu).apply {
            inflate(R.menu.options_recipe)
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.remove -> {
                        listener.onRemoveClicked(recipe)
                        true
                    }
                    R.id.edit -> {
                        listener.onEditClicked(recipe)
                        true
                    }
                    else -> false
                }
            }
        }
    }

    init {
        binding.favoriteToggle.setOnClickListener {
            listener.onFavoriteClicked(recipe)
        }
    }

    fun bind(recipe: Recipe) {
        this.recipe = recipe
        with(binding) {
            nameCategory.text = binding.root.context.getString(R.string.kitchen_category)
            nameAuthor.text = binding.root.context.getString(R.string.author_name)
            category.text = recipe.kitchenCategory.getLabel(binding.root.context)
            author.text = recipe.author
            titleRecipe.text = recipe.title
            favoriteToggle.isChecked = recipe.favorite
            menu.setOnClickListener { popupMenu.show() }
        }
    }
}