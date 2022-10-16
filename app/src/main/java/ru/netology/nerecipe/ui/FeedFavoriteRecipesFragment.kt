package ru.netology.nerecipe.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nerecipe.R
import ru.netology.nerecipe.adapter.RecipesAdapter
import ru.netology.nerecipe.databinding.FragmentFeedFavoriteRecipesBinding
import ru.netology.nerecipe.viewModel.RecipeViewModel

class FeedFavoriteRecipesFragment : FeedRecipesFragment() {

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentFeedFavoriteRecipesBinding.inflate(inflater, container, false)
        binding.feedLayout.test.text = "favorite"

        val viewModel by requireActivity().viewModels<RecipeViewModel>()
        viewModel.optionMenuIsHidden(false)
        viewModel.onFavoriteTabClicked()
        val recipesAdapter = RecipesAdapter(viewModel)
        var f = 1
        binding.feedLayout.recipesRecyclerView.adapter = recipesAdapter
        viewModel.data.observe(viewLifecycleOwner) {
            println("FAV:${f++}")
            recipesAdapter.submitList(it)
            println(it)
            if (viewModel.data.value?.isEmpty() == true) {
                binding.feedLayout.emptyImage.setImageResource(R.raw.empty_board)
                binding.feedLayout.emptyImage.visibility = View.VISIBLE
                binding.feedLayout.recipesRecyclerView.visibility = View.GONE
            } else {
                binding.feedLayout.emptyImage.visibility = View.GONE
                binding.feedLayout.recipesRecyclerView.visibility = View.VISIBLE
            }
        }
        viewModel.activateSearching.observe(viewLifecycleOwner) {
            println("observe activate searching")
            viewModel.data.observe(viewLifecycleOwner) {
                println("FAV:${f++}")
                recipesAdapter.submitList(it)
                println(it)
                if (viewModel.data.value?.isEmpty() == true) {
                    binding.feedLayout.emptyImage.setImageResource(R.raw.empty_board)
                    binding.feedLayout.emptyImage.visibility = View.VISIBLE
                    binding.feedLayout.recipesRecyclerView.visibility = View.GONE
                } else {
                    binding.feedLayout.emptyImage.visibility = View.GONE
                    binding.feedLayout.recipesRecyclerView.visibility = View.VISIBLE
                }
            }
        }
        viewModel.activateSearching.observe(viewLifecycleOwner) {
            println("observe activate searching on Favorite tab")
            viewModel.data.observe(viewLifecycleOwner) {
                recipesAdapter.submitList(it)
                println(it)
            }
        }

        viewModel.navigateToNewRecipeFragment.observe(viewLifecycleOwner) {
            findNavController()
                .navigate(
                    R.id.action_feedFavoriteRecipesFragment_to_newRecipeFragment,
                    Bundle().apply {
                        intArg = it
                    }
                )
        }
        binding.feedLayout.fab.setOnClickListener {
            viewModel.onAddClicked()
        }

        // setupMoveAndSwipeListener
        val recyclerViewRecipes = binding.feedLayout.recipesRecyclerView
        setupMoveAndSwipeListener(recyclerViewRecipes, viewModel)
        viewModel.activateFilterFragment.observe(viewLifecycleOwner) {
            startFilterFragment(R.id.action_feedFavoriteRecipesFragment_to_listFilterFragment)
        }
        return binding.root
    }
}