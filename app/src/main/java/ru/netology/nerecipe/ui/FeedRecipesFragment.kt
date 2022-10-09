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
import ru.netology.nerecipe.databinding.FeedRecipesFragmentBinding
import ru.netology.nerecipe.util.StringArg
import ru.netology.nerecipe.viewModel.RecipeViewModel

class FeedRecipesFragment : Fragment() {

    val viewModel by viewModels<RecipeViewModel>(ownerProducer = ::requireParentFragment)

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FeedRecipesFragmentBinding.inflate(inflater, container, false)

        binding.test.text = "feed"
        binding.test.showSoftInputOnFocus = false

        viewModel.onAllTabClicked()

        val recipesAdapter = RecipesAdapter(viewModel)
        binding.recipesRecyclerView.adapter = recipesAdapter
        viewModel.data.observe(viewLifecycleOwner) {
            recipesAdapter.submitList(viewModel.data.value)
            if (viewModel.data.value?.isEmpty() == true) {
                binding.emptyImage.setImageResource(R.raw.empty_plate)
                binding.emptyImage.visibility = View.VISIBLE
                binding.recipesRecyclerView.visibility = View.GONE
            } else {
                binding.emptyImage.visibility = View.GONE
                binding.recipesRecyclerView.visibility = View.VISIBLE
            }
        }

        viewModel.navigateToNewRecipeFragment.observe(viewLifecycleOwner) {
            findNavController()
                .navigate(
                    R.id.action_feedRecipesFragment_to_newRecipeFragment,
                    Bundle().apply {
                        textArg = it
                    }
                )
        }
        binding.fab.setOnClickListener {
            viewModel.onAddClicked()
        }
//        setupMoveAndSwipeListener
        val recyclerViewRecipes = binding.recipesRecyclerView
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
                viewModel.onRemoveClicked(viewModel.data.value!![item])
            }
        }
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerViewRecipes)
    }

    companion object {
        var Bundle.textArg: String? by StringArg
    }
}