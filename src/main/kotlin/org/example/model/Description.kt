package org.example.model

class Description {
    private val description: StringBuilder = StringBuilder()

    override fun toString() : String = description.toString()

    operator fun String.unaryPlus(): StringBuilder = description.append(this).append("\n")
}