package ru.netology.nerecipe.ui

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

    private lateinit var binding: FeedRecipesFragmentBinding
    private lateinit var recipesAdapter: RecipesAdapter
    val viewModel by viewModels<RecipeViewModel>(ownerProducer = ::requireParentFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FeedRecipesFragmentBinding.inflate(inflater, container, false)


//        arguments?.textArg?.let(binding.test::setText)

        binding.test.text = "feed"
        recipesAdapter = RecipesAdapter(viewModel)
        binding.recipesRecyclerView.adapter = recipesAdapter
        viewModel.sortedData.observe(viewLifecycleOwner) {
            recipesAdapter.submitList(viewModel.sortedData.value)
            if (viewModel.sortedData.value?.isEmpty()!!) {
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
//                    Bundle().apply {
//                        textArg = it
//                    }
                )
        }
        binding.fab.setOnClickListener {
            viewModel.onAddClicked()
        }
        setupLongClickListener()
        val recyclerViewRecipes = binding.recipesRecyclerView
        setupSwipeListener(recyclerViewRecipes)
        return binding.root
    }

    private fun setupLongClickListener() {
        recipesAdapter.onRecipeItemLongClickListener = {
            Log.d("Long click", it.id.toString())
        }
    }

    private fun setupSwipeListener(recyclerViewRecipes: RecyclerView) {
        val callback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val item = recipesAdapter.currentList[viewHolder.adapterPosition].id
                val itemTarget = recipesAdapter.currentList[target.adapterPosition].id
                viewModel.moveTo(item,itemTarget)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val item = recipesAdapter.currentList[viewHolder.adapterPosition]
                viewModel.onRemoveClicked(item)
            }
        }
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerViewRecipes)
    }

    companion object {
        var Bundle.textArg: String? by StringArg
    }
}