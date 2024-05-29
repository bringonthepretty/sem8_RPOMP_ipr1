package com.wah.sem8_rpomp_ipr1.model

data class Note(val title: String, val text: String) {
    override fun toString(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append(title).append(": ").append(text)
        return stringBuilder.toString()
    }
}