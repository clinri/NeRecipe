package ru.netology.nerecipe.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import ru.netology.nerecipe.R
import ru.netology.nerecipe.adapter.RecipesAdapter
import ru.netology.nerecipe.databinding.FeedRecipesFragmentBinding
import ru.netology.nerecipe.databinding.TabsFragmentBinding
import ru.netology.nerecipe.ui.FeedRecipesFragment.Companion.textArg
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewModel.RecipeViewModel

class TabsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = TabsFragmentBinding.inflate(inflater, container, false)
        binding.navigationMenu.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.all_recipes -> {
                    loadFragment(binding.containerActivityApp.id, "all")
                    return@setOnItemSelectedListener true
                }
                R.id.favorite_recipes -> {
                    loadFragment(binding.containerActivityApp.id, "favorite")
                    return@setOnItemSelectedListener true
                }
                else -> return@setOnItemSelectedListener false
            }
        }
        return binding.root
    }

    private fun loadFragment(containerId: Int, text: String) {
        val fragment = FeedRecipesFragment().apply {
            arguments = Bundle().apply {
                textArg = text
            }
        }
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(containerId, fragment)
            .commit()
    }

    companion object {
        var Bundle.textArg: String? by StringArg
    }
}