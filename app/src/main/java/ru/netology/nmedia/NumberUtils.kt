package ru.netology.nmedia

import android.annotation.SuppressLint

@SuppressLint("DefaultLocale")
fun formatNumber(number: Int): String {
    return when {
        number < 1000 -> number.toString()
        number < 10_000 -> {
            val decimalPart = (number % 1000) / 100
            val integerPart = number / 1000
            if (decimalPart == 0) {
                String.format("%dK", integerPart)
            } else {
                String.format("%d,%dK", integerPart, decimalPart)
            }
        }

        number < 1_000_000 -> {
            val decimalPart = (number % 1000) / 100
            val integerPart = number / 1000
            if (decimalPart == 0) {
                String.format("%dK", integerPart)
            } else {
                String.format("%dK", integerPart, decimalPart)
            }
        }

        number < 100_000_000 -> {
            val decimalPart = (number % 1_000_000) / 100_000
            val integerPart = number / 1_000_000
            if (decimalPart == 0) {
                String.format("%dM", integerPart)
            } else {
                String.format("%d,%dM", integerPart, decimalPart)
            }
        }

        else -> String.format("%.1fM", number / 1_000_000.0)
    }
}