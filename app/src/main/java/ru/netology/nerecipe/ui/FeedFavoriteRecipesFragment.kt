package ru.netology.nerecipe.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nerecipe.R
import ru.netology.nerecipe.adapter.RecipesAdapter
import ru.netology.nerecipe.databinding.FragmentFeedFavoriteRecipesBinding
import ru.netology.nerecipe.ui.FeedRecipesFragment.Companion.textArg
import ru.netology.nerecipe.viewModel.FavoriteRecipeViewModel

class FeedFavoriteRecipesFragment : Fragment() {

    val viewModel by viewModels<FavoriteRecipeViewModel>(ownerProducer = ::requireParentFragment)

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedFavoriteRecipesBinding.inflate(inflater, container, false)

        binding.feedLayout.test.text = "favorite"
        binding.feedLayout.test.showSoftInputOnFocus = false

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
                    Bundle().apply {
                        textArg = it
                    }
                )
        }
        binding.feedLayout.fab.setOnClickListener {
            viewModel.onAddClicked()
        }

        //        setupMoveAndSwipeListener
        val recyclerViewRecipes = binding.feedLayout.recipesRecyclerView
        setupMoveAndSwipeListener(recyclerViewRecipes)
        return binding.root
    }

    private fun setupMoveAndSwipeListener(recyclerViewRecipes: RecyclerView) {
        val callback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val item = viewHolder.absoluteAdapterPosition
                val itemTarget = target.absoluteAdapterPosition
                Log.d("move", "move from $item to $itemTarget")
                recyclerView.adapter?.notifyItemMoved(item, itemTarget)
                viewModel.moveTo(item, itemTarget)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val item = viewHolder.layoutPosition
                viewModel.onRemoveClicked(viewModel.sortedData[item])
            }
        }
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerViewRecipes)
    }
}