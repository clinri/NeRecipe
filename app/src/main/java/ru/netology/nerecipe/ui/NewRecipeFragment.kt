package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nerecipe.databinding.NewRecipeFragmentBinding
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewModel.RecipeViewModel

class NewRecipeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = NewRecipeFragmentBinding.inflate(inflater, container, false)
        val viewModel by viewModels<RecipeViewModel>()

        arguments?.textArg.let(binding.edit::setText)
        binding.edit.requestFocus()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        binding.ok.setOnClickListener {
            val recipeContent = binding.edit.text.toString()
            if (!recipeContent.isNullOrBlank()) {
                viewModel.onSaveButtonClicked(recipeContent)
            }
            findNavController().navigateUp()
        }
        return binding.root
    }

    companion object {
        var Bundle.textArg: String? by StringArg
    }
}