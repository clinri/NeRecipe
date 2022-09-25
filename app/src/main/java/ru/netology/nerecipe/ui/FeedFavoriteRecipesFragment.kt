package ru.netology.nerecipe.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nerecipe.R
import ru.netology.nerecipe.adapter.RecipesAdapter
import ru.netology.nerecipe.databinding.FragmentFeedFavoriteRecipesBinding
import ru.netology.nerecipe.viewModel.RecipeViewModel

class FeedFavoriteRecipesFragment : Fragment() {

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedFavoriteRecipesBinding.inflate(inflater, container, false)
        val viewModel by viewModels<RecipeViewModel>(ownerProducer = ::requireParentFragment)

        binding.feedLayout.test.text = "favorite"

        val adapter = RecipesAdapter(viewModel)
        binding.feedLayout.recipesRecyclerView.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) {
            viewModel.updateSortData()
            adapter.submitList(viewModel.sortedData)
            if (viewModel.sortedData.isEmpty()) {
                binding.feedLayout.emptyImage.setImageResource(R.raw.empty_board)
                binding.feedLayout.emptyImage.visibility = View.VISIBLE
                binding.feedLayout.recipesRecyclerView.visibility = View.GONE
            } else {
                binding.feedLayout.emptyImage.visibility = View.GONE
                binding.feedLayout.recipesRecyclerView.visibility = View.VISIBLE
            }
        }

        viewModel.navigateToNewRecipeFragment.observe(viewLifecycleOwner) {
            findNavController()
                .navigate(
                    R.id.action_feedFavoriteRecipesFragment_to_newRecipeFragment,
//                    Bundle().apply {
//                        textArg = it
//                    }
                )
        }

        binding.feedLayout.fab.setOnClickListener {
            viewModel.onAddClicked()
        }
        return binding.root
    }
}