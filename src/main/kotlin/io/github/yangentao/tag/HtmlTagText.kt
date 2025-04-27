@file:Suppress("unused")

package io.github.yangentao.tag



fun Tag.appendText(text: String?): TextTag? {
    if (text != null) {
        return add(TextTag(context, text))
    }
    return null
}

fun Tag.text(text: String?): TextTag? {
    if (text != null) {
        return add(TextTag(context, text))
    }
    return null
}

fun Tag.unsafe(text: String?) {
    if (text != null) {
        add(TextTag(context, text, unsafe = true))
    }
}

fun Tag.unsafe(block: () -> String) {
    unsafe(block())
}

fun Tag.text(text: String, block: TextTag.() -> Unit): TextTag {
    return add(TextTag(context, text)).apply(block)
}

fun Tag.pArticle(text: String) {
    val textList = text.split("\n")
    for (s in textList) {
        this.p {
            "style" attr "text-indent:2em"
            text(s)?.forView = true
        }
    }
}

class TextTag(context: TagContext, var text: String = "", var unsafe: Boolean = false) : Tag(context, TEXT_TAG) {
    var formatOutput = true
    var forView = true

    override fun toHtml(buf: Appendable, level: Int) {
        val multiLine: Boolean = htmlMultiLine()
        val parentMultiLine = parent?.htmlMultiLine() == true

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

fun Tag.scriptsToBottom() {
    val ls = this.filter(TAGNAME to "script")
    for (a in ls) {
        a.removeFromParent()
        this.add(a)
    }
}