@file:Suppress("unused")

package io.github.yangentao.tag.xml

import io.github.yangentao.tag.DefaultTagContext
import io.github.yangentao.tag.TAG_TEXT
import io.github.yangentao.tag.Tag
import io.github.yangentao.tag.TagContext

const val TAG_CDATA = "_CDATA"

open class XmlTag(context: TagContext, tagName: String) : Tag(context, tagName) {
    override fun add(name: String): XmlTag {
        return add(XmlTag(context, name))
    }

    fun element(name: String, block: XmlTag.() -> Unit): XmlTag {
        val a: XmlTag = add(XmlTag(context, name))
        a.block()
        return a
    }

    fun text(value: String): XmlText {
        return add(XmlText(context, value))
    }

    fun cdata(value: String): XmlCDATA {
        return add(XmlCDATA(context, value))
    }
}

open class XmlRoot(context: TagContext, tagName: String) : XmlTag(context, tagName) {
    override fun toString(): String {
        return """<?xml version="1.0" ?>""" + "\n" + super.toString()
    }
}

open class XmlText(context: TagContext, val value: String) : XmlTag(context, TAG_TEXT) {
    override fun toString(): String {
        return value
    }
}

open class XmlCDATA(context: TagContext, val value: String) : XmlTag(context, TAG_CDATA) {
    override fun toString(): String {
        return """
            <![CDATA[
                $value
            ]] >
            """.trimIndent()
    }
}

fun main() {
    val root = XmlRoot(DefaultTagContext(), "root")
    root.element("users") {
        element("entao") {
            "age" attr 99
            "son" attr "suo"
            add("suo").apply {
                "age" attr 14
            }
        }
        element("suo") {
            "age" attr 14
            element("desc") {
                text("I'm a student!")
            }
            element("data") {
                cdata("This is cdata.")
            }
        }
    }
    println(root)
}
