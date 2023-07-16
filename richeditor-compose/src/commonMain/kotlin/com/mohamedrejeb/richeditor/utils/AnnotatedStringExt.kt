package com.mohamedrejeb.richeditor.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.withStyle
import com.mohamedrejeb.richeditor.model.RichSpan
import com.mohamedrejeb.richeditor.model.RichSpanStyle
import kotlin.math.max
import kotlin.math.min

internal fun AnnotatedString.Builder.append(
    richSpanList: List<RichSpan>,
    startIndex: Int,
    text: String,
    selection: TextRange,
    onStyledRichSpan: (RichSpan) -> Unit,
): Int {
    var index = startIndex
    richSpanList.fastForEach { richSpan ->
        index = append(
            richSpan = richSpan,
            startIndex = index,
            text = text,
            selection = selection,
            onStyledRichSpan = onStyledRichSpan,
        )
    }
    return index
}

internal fun AnnotatedString.Builder.append(
    richSpanList: List<RichSpan>,
    startIndex: Int,
    selection: TextRange,
): Int {
    var index = startIndex
    richSpanList.fastForEach { richSpan ->
        index = append(
            richSpan = richSpan,
            startIndex = index,
            selection = selection,
        )
    }
    return index
}

internal fun AnnotatedString.Builder.append(
    richSpanList: List<RichSpan>,
    startIndex: Int,
): Int {
    var index = startIndex
    richSpanList.fastForEach { richSpan ->
        index = append(
            richSpan = richSpan,
            startIndex = index,
        )
    }
    return index
}

internal fun AnnotatedString.Builder.append(
    richSpan: RichSpan,
    startIndex: Int,
    text: String,
    selection: TextRange,
    onStyledRichSpan: (RichSpan) -> Unit,
): Int {
    var index = startIndex

    withStyle(richSpan.spanStyle.merge(richSpan.style.spanStyle)) {
        val newText = text.substring(index, index + richSpan.text.length)
        richSpan.text = newText
        richSpan.textRange = TextRange(index, index + richSpan.text.length)
        if (
            !selection.collapsed &&
            selection.min < index + richSpan.text.length &&
            selection.max > index
        ) {
            val beforeSelection =
                if (selection.min > index) richSpan.text.substring(0, selection.min - index)
                else ""
            val selectedText =
                richSpan.text.substring(max(0, selection.min - index), min(selection.max - index, richSpan.text.length))
            val afterSelection =
                if (selection.max - index < richSpan.text.length) richSpan.text.substring(selection.max - index)
                else ""

            append(beforeSelection)
            withStyle(SpanStyle(background = Color.Transparent)) {
                append(selectedText)
            }
            append(afterSelection)
        } else {
            append(newText)
        }

        if (richSpan.style !is RichSpanStyle.Default) {
            onStyledRichSpan(richSpan)
        }

        index += richSpan.text.length
        richSpan.children.fastForEach { richSpan ->
            index = append(
                richSpan = richSpan,
                startIndex = index,
                text = text,
                selection = selection,
                onStyledRichSpan = onStyledRichSpan,
            )
        }
    }
    return index
}

internal fun AnnotatedString.Builder.append(
    richSpan: RichSpan,
    startIndex: Int,
    selection: TextRange,
): Int {
    var index = startIndex

    withStyle(richSpan.spanStyle.merge(richSpan.style.spanStyle)) {
        richSpan.textRange = TextRange(index, index + richSpan.text.length)
        if (
            !selection.collapsed &&
            selection.min < index + richSpan.text.length &&
            selection.max > index
        ) {
            val selectedText = richSpan.text.substring(
                max(0, selection.min - index),
                min(selection.max - index, richSpan.text.length)
            )
            append(selectedText)
        }
        index += richSpan.text.length
        richSpan.children.fastForEach { richSpan ->
            index = append(
                richSpan = richSpan,
                startIndex = index,
                selection = selection,
            )
        }
    }
    return index
}

internal fun AnnotatedString.Builder.append(
    richSpan: RichSpan,
    startIndex: Int,
): Int {
    var index = startIndex

    withStyle(richSpan.spanStyle.merge(richSpan.style.spanStyle)) {
        richSpan.textRange = TextRange(index, index + richSpan.text.length)
        append(richSpan.text)
        index += richSpan.text.length
        richSpan.children.fastForEach { richSpan ->
            index = append(
                richSpan = richSpan,
                startIndex = index,
            )
        }
    }
    return index
}