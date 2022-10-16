package ru.netology.nerecipe.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.activity.viewModels
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
        //binding.test.text = "filters"
        val viewModel by requireActivity().viewModels<RecipeViewModel>()
        viewModel.optionMenuIsHidden(true)

        val listCategory = KitchenCategory.values().asList()
        listCategoryLayout = binding.listCategory
        showListCategory(listCategory)
        return binding.root
    }

    private fun showListCategory(list: List<KitchenCategory>) {
        list.forEach { kitchenCategory ->
            val layoutId = R.layout.filter_item
            val view = LayoutInflater.from(context).inflate(layoutId, listCategoryLayout, false)
            val checkBoxName = view.findViewById<CheckBox>(R.id.check_box)
            checkBoxName.text = context?.let { kitchenCategory.getLabel(it) }
            listCategoryLayout.addView(view)
        }
    }
}