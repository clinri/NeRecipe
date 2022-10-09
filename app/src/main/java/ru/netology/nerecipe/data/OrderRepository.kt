package ru.netology.nerecipe.data

import androidx.lifecycle.LiveData
import ru.netology.nerecipe.dto.Order
import ru.netology.nerecipe.dto.Recipe

interface OrderRepository {
    val order: List<Order>
    fun sort()
    fun delete(id: Int)
    fun insert(id: Int)
    fun swapOrdersByIds(id1: Int, order1: Int, id2: Int, order2: Int)
}