package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nerecipe.R
import ru.netology.nerecipe.adapter.RecipesAdapter
import ru.netology.nerecipe.databinding.ActivityAppBinding
import ru.netology.nerecipe.databinding.FeedRecipesFragmentBinding
import ru.netology.nerecipe.util.StringArg
import ru.netology.nerecipe.util.hideKeyboard
import ru.netology.nerecipe.viewModel.RecipeViewModel

class FeedRecipesFragment : Fragment() {

    private lateinit var binding: FeedRecipesFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FeedRecipesFragmentBinding.inflate(inflater, container, false)
        val viewModel by viewModels<RecipeViewModel>(ownerProducer = ::requireParentFragment)

//        arguments?.textArg?.let(binding.test::setText)
        //binding.searchView.setIconifiedByDefault(false)
        binding.test.text = "feed"
        val adapter = RecipesAdapter(viewModel)
        binding.recipesRecyclerView.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) { recipes ->
            adapter.submitList(recipes)
            if (viewModel.data.value?.isEmpty()!!) {
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
        return binding.root
    }

    companion object {
        var Bundle.textArg: String? by StringArg
    }
}