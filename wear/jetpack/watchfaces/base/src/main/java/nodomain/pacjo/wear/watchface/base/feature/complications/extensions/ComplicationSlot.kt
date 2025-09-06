package nodomain.pacjo.wear.watchface.base.feature.complications.extensions

import androidx.wear.watchface.complications.rendering.ComplicationStyle

// I'd like to do this:
// complicationDrawable.activeStyle = definition.complicationStyle
// but 'var cannot be re-assigned', so:
fun ComplicationStyle.set(complicationStyle: ComplicationStyle) {
    backgroundColor = complicationStyle.backgroundColor
    backgroundDrawable = complicationStyle.backgroundDrawable
    textColor = complicationStyle.textColor
    titleColor = complicationStyle.titleColor
    setTextTypeface(complicationStyle.textTypeface)
    setTitleTypeface(complicationStyle.titleTypeface)
    imageColorFilter = complicationStyle.imageColorFilter
    iconColor = complicationStyle.iconColor
    textSize = complicationStyle.textSize
    titleSize = complicationStyle.titleSize
    borderColor = complicationStyle.borderColor
    borderStyle = complicationStyle.borderStyle
    borderDashWidth = complicationStyle.borderDashWidth
    borderDashGap = complicationStyle.borderDashGap
    borderRadius = complicationStyle.borderRadius
    rangedValueRingWidth = complicationStyle.rangedValueRingWidth
    rangedValuePrimaryColor = complicationStyle.rangedValuePrimaryColor
    rangedValueSecondaryColor = complicationStyle.rangedValueSecondaryColor
    highlightColor = complicationStyle.highlightColor
}

fun ComplicationStyle.copy(): ComplicationStyle {
    return ComplicationStyle(this)
}