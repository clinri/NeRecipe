package ru.netology.nerecipe.dto

import android.content.Context
import ru.netology.nerecipe.R

enum class KitchenCategory(private val labelId: Int) {
    EUROPEAN(R.string.european), //Европейская
    ASIAN(R.string.asian), //Азиатская
    PAN_ASIAN(R.string.pan_asian), //Паназиатская
    EASTERN(R.string.eastern), //Восточная
    AMERICAN(R.string.american), //Американская
    RUSSIAN(R.string.russian), //Русская
    MEDITERRANEAN(R.string.mediterranean); //Средиземноморская

    fun getLabel(context: Context) =
        context.getString(labelId)


}