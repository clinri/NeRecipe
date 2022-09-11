package ru.netology.nmedia.util

import android.os.Bundle
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

object IdArg : ReadWriteProperty<Bundle, Int> {
    override fun setValue(thisRef: Bundle, property: KProperty<*>, value: Int) {
        thisRef.putInt(property.name,value)
    }

    override fun getValue(thisRef: Bundle, property: KProperty<*>): Int {
        return thisRef.getInt(property.name)
    }
}