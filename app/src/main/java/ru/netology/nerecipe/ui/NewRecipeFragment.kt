package ru.netology.nerecipe.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.netology.nerecipe.R
import ru.netology.nerecipe.databinding.NewRecipeFragmentBinding
import ru.netology.nerecipe.dto.KitchenCategory
import ru.netology.nerecipe.dto.Recipe
import ru.netology.nerecipe.ui.FeedRecipesFragment.Companion.intArg
import ru.netology.nerecipe.viewModel.RecipeViewModel

class NewRecipeFragment : Fragment() {

    private lateinit var binding: NewRecipeFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = NewRecipeFragmentBinding.inflate(inflater, container, false)
        val viewModel by requireActivity().viewModels<RecipeViewModel>()
        viewModel.optionMenuIsHidden(true)

        val id = arguments?.intArg
        val recipe = if (id != 0 && id != null) {
            binding.ok.setImageResource(R.drawable.ic_baseline_update_24dp)
            viewModel.getRecipeById(id)
        } else {
            null
        }

        recipe?.let(::bind) ?: bindSpinner(null)
        binding.title.requestFocus()

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            onBackPressedCallback)

        binding.ok.setOnClickListener {
            viewModel.onSaveButtonClicked(getEditRecipe(recipe?.id ?: 0))
            findNavController().navigateUp()
        }
        return binding.root
    }

    private fun getEditRecipe(id: Int): Recipe {
        return with(binding) {
            val categoryEnumString =
                getStringEnumKitchenCategory(requireContext(), category.text.toString())
            val categoryRecipe = KitchenCategory.valueOf(categoryEnumString)
            val authorRecipe = author.text.toString()
            val titleRecipe = title.text.toString()
            Recipe(id, id, categoryRecipe, authorRecipe, titleRecipe)
        }
    }

    private fun getStringEnumKitchenCategory(context: Context, string: String): String {
        val enumValue: String
        val listEnums = KitchenCategory.values()
        listEnums.map {
            it.getLabel(context)
        }.also {
            enumValue = listEnums[it.indexOf(string)].toString()
        }
        return enumValue
    }

    private fun bind(recipe: Recipe) {
        with(binding) {
            nameCategory.text = root.context.getString(R.string.kitchen_category)
            nameAuthor.text = root.context.getString(R.string.author_name)
            category.text = recipe.kitchenCategory.getLabel(binding.root.context)
            author.setText(recipe.author)
            title.setText(recipe.title)
            bindSpinner(recipe.kitchenCategory.getLabel(requireContext()))
        }
    }

    private fun bindSpinner(kitchenCategory: String?) {
        with(binding) {
            val list = KitchenCategory.values().map {
                it.getLabel(requireContext())
            }
            val adapterCategory = ArrayAdapter(
                requireContext(), R.layout.spinner_category, list
            )
            adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categorySpinner.adapter = adapterCategory
            categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    p3: Long,
                ) {
                    category.text = parent?.getItemAtPosition(position).toString()
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {
                    category.text = getString(R.string.select_kitchen_category)
                }
            }
            val position: Int = if (kitchenCategory != null) {
                adapterCategory.getPosition(kitchenCategory)
            } else 5 // Russian category
            position.let(categorySpinner::setSelection)
        }
    }
}