package com.example.mindcheck.screening

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.example.mindcheck.R

/**
 * Step 7 (OOP: polymorphism): how each level the model returns is shown to the student.
 * Each level is its own object that supplies its own name, message, icon and colours,
 * so the Result screen just asks "the level" for them without any if/else.
 *
 * Mapping from the ML team's guide: OKAY = Low, MILD = Moderate, MODERATE_OR_WORSE = High.
 */
sealed class RiskLevel(
    val emoji: String,
    @StringRes val titleRes: Int,
    @StringRes val messageRes: Int,
    @ColorRes val containerColorRes: Int,
    @ColorRes val onContainerColorRes: Int,
) {
    /** Whether the Result screen should push the student towards a counsellor first. */
    open val urgeCounsellor: Boolean = false

    data object Low : RiskLevel(
        "🌤️", R.string.level_low_title, R.string.level_low_message,
        R.color.level_low_container, R.color.level_low_on_container,
    )

    data object Moderate : RiskLevel(
        "🌥️", R.string.level_moderate_title, R.string.level_moderate_message,
        R.color.level_moderate_container, R.color.level_moderate_on_container,
    )

    data object High : RiskLevel(
        "🤝", R.string.level_high_title, R.string.level_high_message,
        R.color.level_high_container, R.color.level_high_on_container,
    ) {
        override val urgeCounsellor = true
    }

    companion object {
        /** Turns the model's [Level] into how we show it. */
        fun from(level: Level): RiskLevel = when (level) {
            Level.OKAY -> Low
            Level.MILD -> Moderate
            Level.MODERATE_OR_WORSE -> High
        }
    }
}

/** The outcome of a check-in. [needsUrgentSupport] is true when the safety question was answered above "Not at all". */
data class ScreeningResult(val riskLevel: RiskLevel, val needsUrgentSupport: Boolean)
