package ru.netology.nerecipe.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nerecipe.R
import ru.netology.nerecipe.databinding.FragmentListFilterBinding
import ru.netology.nerecipe.dto.KitchenCategory
import ru.netology.nerecipe.viewModel.RecipeViewModel


class ListFilterFragment : Fragment() {

    lateinit var binding: FragmentListFilterBinding
    lateinit var listCategoryLayout: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListFilterBinding.inflate(inflater, container, false)
        val viewModel by requireActivity().viewModels<RecipeViewModel>()
        viewModel.optionMenuIsHidden(true)

        val listCategory = KitchenCategory.values().asList()
        listCategoryLayout = binding.listCategory
        showListCategory(listCategory,viewModel)
        binding.update.setOnClickListener {
            viewModel.onUpdateButtonClicked()
            findNavController().navigateUp()
        }
        return binding.root
    }

    private fun showListCategory(list: List<KitchenCategory>, viewModel: RecipeViewModel) {
        list.forEachIndexed {index, kitchenCategory ->
            val layoutId = R.layout.filter_item
            val view = LayoutInflater.from(context).inflate(layoutId, listCategoryLayout, false)
            val checkBox = view.findViewById<CheckBox>(R.id.check_box)
            checkBox.text = context?.let { kitchenCategory.getLabel(it) }
            checkBox.isChecked = viewModel.filter[index]
            checkBox.setOnClickListener {
                viewModel.onItemFilterCategoryClicked(index)
            }
            listCategoryLayout.addView(view)
        }
    }
}