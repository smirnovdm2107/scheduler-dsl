package org.example.model

class Description {
    val description: StringBuilder = StringBuilder()

    override fun toString() : String = description.toString()

    operator fun String.unaryPlus() = description.append(this).append("\n")
}