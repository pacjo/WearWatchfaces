package nodomain.pacjo.wear.watchface.base.feature.complications.extensions

import android.annotation.SuppressLint
import androidx.wear.watchface.complications.data.ComplicationType

/**
 * Creates a list of common complication types for general-purpose slots.
 */
@SuppressLint("NewApi")     // unsupported types are ignored on older platforms
fun ComplicationType.Companion.general(): List<ComplicationType> = listOf(
    ComplicationType.SHORT_TEXT,
    ComplicationType.SMALL_IMAGE,
    ComplicationType.MONOCHROMATIC_IMAGE,
    ComplicationType.GOAL_PROGRESS,
    ComplicationType.WEIGHTED_ELEMENTS,
    ComplicationType.RANGED_VALUE,
    ComplicationType.LONG_TEXT,
    ComplicationType.EMPTY
)

/**
 * Creates a list of complication types suitable for small/corner slots.
 */
@SuppressLint("NewApi")     // unsupported types are ignored on older platforms
fun ComplicationType.Companion.compact(): List<ComplicationType> = listOf(
    ComplicationType.SHORT_TEXT,
    ComplicationType.RANGED_VALUE,
    ComplicationType.MONOCHROMATIC_IMAGE,
    ComplicationType.GOAL_PROGRESS,
    ComplicationType.WEIGHTED_ELEMENTS,
    ComplicationType.EMPTY
)

/**
 * Creates a list of complication types for text-focused slots.
 */
fun ComplicationType.Companion.textOnly(): List<ComplicationType> = listOf(
    ComplicationType.SHORT_TEXT,
    ComplicationType.LONG_TEXT,
    ComplicationType.EMPTY
)