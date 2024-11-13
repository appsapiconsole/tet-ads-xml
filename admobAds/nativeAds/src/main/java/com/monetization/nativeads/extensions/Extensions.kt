package com.monetization.nativeads.extensions

import com.monetization.nativeads.R


fun Int.getDrawableOfRange(): Int {
    return when {
        this == 0 -> {
            R.drawable.cta_rounded_0
        }

        this in 1..10 -> {
            R.drawable.cta_rounded_1
        }

        this in 11..20 -> {
            R.drawable.cta_rounded_2
        }

        this in 21..30 -> {
            R.drawable.cta_rounded_3
        }

        this in 31..40 -> {
            R.drawable.cta_rounded_4
        }

        this in 41..50 -> {
            R.drawable.cta_rounded_5
        }

        this in 51..60 -> {
            R.drawable.cta_rounded_6
        }

        this in 61..70 -> {
            R.drawable.cta_rounded_7
        }

        this in 71..80 -> {
            R.drawable.cta_rounded_8
        }

        this in 81..90 -> {
            R.drawable.cta_rounded_9
        }

        this in 91..100 -> {
            R.drawable.cta_rounded_10
        }

        this in 101..150 -> {
            R.drawable.cta_rounded_15
        }

        this in 151..200 -> {
            R.drawable.cta_rounded_20
        }

        else -> {
            R.drawable.cta_rounded_full
        }
    }
}