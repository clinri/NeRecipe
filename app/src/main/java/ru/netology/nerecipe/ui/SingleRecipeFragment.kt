package ru.netology.nerecipe.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nerecipe.R
import ru.netology.nerecipe.databinding.FragmentSingleRecipeBinding
import ru.netology.nerecipe.dto.Recipe
import ru.netology.nerecipe.ui.FeedRecipesFragment.Companion.intArg
import ru.netology.nerecipe.viewModel.RecipeViewModel

@AndroidEntryPoint
class SingleRecipeFragment : Fragment() {
    private lateinit var binding: FragmentSingleRecipeBinding
    private lateinit var popupMenu: PopupMenu
    private val viewModel: RecipeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel.optionMenuIsHidden(true)
        binding = FragmentSingleRecipeBinding.inflate(inflater, container, false)
        val id = requireArguments().intArg
        val recipe = viewModel.getRecipeById(id)
        val indexRecipe = viewModel.data.value!!.indexOf(recipe)
        viewModel.data.observe(viewLifecycleOwner) {
            Log.d("update single recipe", it[indexRecipe].toString())
            it[indexRecipe].let(::bind)
        }

        binding.recipeLayout.favoriteToggle.setOnClickListener {
            viewModel.onFavoriteClicked(recipe)
        }

        popupMenu = PopupMenu(context, binding.recipeLayout.menu).apply {
            inflate(R.menu.options_recipe)
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.remove -> {
                        viewModel.onRemoveClicked(recipe)
                        findNavController().navigateUp()
                        true
                    }

                    R.id.edit -> {
                        viewModel.onEditClicked(recipe)
                        true
                    }

                    else -> false
                }
            }
        }
        viewModel.navigateToNewRecipeFragment.observe(viewLifecycleOwner) {
            findNavController()
                .navigate(
                    R.id.action_singleRecipeFragment_to_newRecipeFragment,
                    Bundle().apply {
                        intArg = it
                    }
                )
        }
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun bind(recipe: Recipe) {
        with(binding.recipeLayout) {
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