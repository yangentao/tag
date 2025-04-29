@file:Suppress("unused")

package io.github.yangentao.tag.html

import io.github.yangentao.tag.TAGNAME
import io.github.yangentao.tag.TAG_TEXT
import io.github.yangentao.tag.TagContext
import io.github.yangentao.tag.escapeHtml

fun HtmlTag.appendText(text: String?): HtmlText? {
    if (text != null) {
        return add(HtmlText(context, text))
    }
    return null
}

fun HtmlTag.text(text: String?): HtmlText? {
    if (text != null) {
        return add(HtmlText(context, text))
    }
    return null
}

fun HtmlTag.unsafe(text: String?) {
    if (text != null) {
        add(HtmlText(context, text, unsafe = true))
    }
}

fun HtmlTag.unsafe(block: () -> String) {
    unsafe(block())
}

fun HtmlTag.text(text: String, block: HtmlText.() -> Unit): HtmlText {
    return add(HtmlText(context, text)).apply(block)
}

fun HtmlTag.pArticle(text: String) {
    val textList = text.split("\n")
    for (s in textList) {
        this.p {
            "style" attr "text-indent:2em"
            text(s)?.forView = true
        }
    }
}

class HtmlText(context: TagContext, var text: String = "", var unsafe: Boolean = false) : HtmlTag(context, TAG_TEXT) {
    var formatOutput = true
    var forView = true

    override fun toHtml(buf: Appendable, level: Int) {
        val multiLine: Boolean = htmlMultiLine()
        val parentMultiLine = parentHtml?.htmlMultiLine() == true

        val s = if (unsafe) this.text else this.text.escapeHtml(forView)
        if (!formatOutput || parent?.tagName in listOf("pre", "code", "textarea")) {
            buf.append(s)
            return
        }
        val lines = s.lines()
        for (i in lines.indices) {
            if (multiLine || parentMultiLine) {
                buf.newLine(level)
            }
            buf.append(lines[i])
        }
    }

    override fun htmlMultiLine(): Boolean {
        return '\n' in text || '\r' in text
    }

}

fun HtmlTag.scriptsToBottom() {
    val ls = this.filter(TAGNAME to "script")
    for (a in ls) {
        a.removeFromParent()
        this.add(a)
    }
}