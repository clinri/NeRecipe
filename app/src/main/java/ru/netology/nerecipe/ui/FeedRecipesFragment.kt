package ru.netology.nerecipe.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nerecipe.R
import ru.netology.nerecipe.adapter.RecipesAdapter
import ru.netology.nerecipe.databinding.FeedRecipesFragmentBinding
import ru.netology.nerecipe.util.StringArg
import ru.netology.nerecipe.viewModel.RecipeViewModel

open class FeedRecipesFragment : Fragment() {

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FeedRecipesFragmentBinding.inflate(inflater, container, false)
        binding.test.text = "feed"

        val viewModel by requireActivity().viewModels<RecipeViewModel>()
        viewModel.optionMenuIsHidden(false)
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
        setupMoveAndSwipeListener(recyclerViewRecipes, viewModel)
        viewModel.activateFilterFragment.observe(viewLifecycleOwner){
            startFilterFragment(R.id.action_feedRecipesFragment_to_listFilterFragment)
        }

        return binding.root
    }

    internal fun Fragment.startFilterFragment(action: Int) {
        findNavController().navigate(action)
    }

    internal fun setupMoveAndSwipeListener(
        recyclerViewRecipes: RecyclerView,
        viewModel: RecipeViewModel
    ) {
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
                if (item == itemTarget) return false
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