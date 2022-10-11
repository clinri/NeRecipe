package ru.netology.nerecipe.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import ru.netology.nerecipe.databinding.FragmentListFilterBinding
import ru.netology.nerecipe.viewModel.RecipeViewModel


class ListFilterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentListFilterBinding.inflate(inflater, container, false)
        binding.test.text = "filters"
        val viewModel by requireActivity().viewModels<RecipeViewModel>()
        viewModel.optionMenuIsHidden(true)
        return binding.root
    }

}