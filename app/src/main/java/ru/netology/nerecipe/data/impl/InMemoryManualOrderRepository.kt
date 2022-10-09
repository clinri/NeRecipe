package ru.netology.nerecipe.data.impl

import ru.netology.nerecipe.data.OrderRepository
import ru.netology.nerecipe.dto.Order
import ru.netology.nerecipe.dto.Recipe

class InMemoryManualOrderRepository(data: List<Recipe>) : OrderRepository {

    private var savedOrder: MutableList<Order>

    override val order: List<Order>
        get() = savedOrder

    override fun sort() {
        savedOrder.sortBy {
            it.order
        }
    }

    init {
        savedOrder = data.map {
            Order(it.id, it.id)
        } as MutableList<Order>
    }

//    private fun delay() {
//        try {
//            Thread.sleep(2000)
//        } catch (ignored: InterruptedException) {
//        }
//    }

    override fun insert(id: Int) {
        savedOrder.add(0, Order(id, id))
    }

    override fun delete(id: Int) {
        savedOrder.forEachIndexed { index, order ->
            if (order.id == id)
                savedOrder.removeAt(index)
        }
    }

    override fun swapOrdersByIds(id1: Int, order1: Int, id2: Int, order2: Int) {
        savedOrder.forEachIndexed { index, order ->
            if (order.id == id1) {
                savedOrder.removeAt(index)
                savedOrder.add(index, Order(id1, order2))
            }
            if (order.id == id2) {
                savedOrder.removeAt(index)
                savedOrder.add(index, Order(id2, order1))
            }
        }
    }
}