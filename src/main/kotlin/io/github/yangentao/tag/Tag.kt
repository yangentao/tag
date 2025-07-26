@file:Suppress("FunctionName", "MemberVisibilityCanBePrivate", "unused", "PropertyName")

package io.github.yangentao.tag

import kotlin.reflect.full.primaryConstructor

@DslMarker
annotation class TagMarker

const val TAGNAME = "tagName"
const val TAG_TEXT = "_text"
typealias TagBlock = Tag.() -> Unit
typealias TagAttr = Pair<String, String>

/**
 * tag context
 */
fun interface TagContext {
    fun paramValue(key: String): String?
}

class DefaultTagContext : TagContext {
    override fun paramValue(key: String): String? {
        return null
    }

}

open class GenericTag(context: TagContext, tagName: String) : Tag(context, tagName)

class RawTextTag(context: TagContext, var value: String) : Tag(context, TAG_TEXT) {
    override fun toString(): String {
        return value
    }
}

/**
 * common tag, userd for html or xml.
 */
@TagMarker
abstract class Tag(val context: TagContext, val tagName: String) {
    val attrMap: TagPropMap = TagPropMap()
    val children = ArrayList<Tag>(8)
    var parent: Tag? = null
    val root: Tag get() = this.parent?.root ?: this

    fun parent(block: (Tag) -> Boolean): Tag? {
        val p = this.parent ?: return null
        if (block(p)) {
            return p
        }
        return p.parent(block)
    }

    fun parent(attr: TagAttr, vararg vs: TagAttr): Tag? {
        val p = this.parent ?: return null
        if (p.match(attr, *vs)) {
            return p
        }
        return p.parent(attr, *vs)
    }

    fun removeFromParent() {
        this.parent?.children?.remove(this)
    }

    fun removeChild(tag: Tag) {
        children.remove(tag)
    }

    fun cleanChildren() {
        children.clear()
    }

    private fun filterTo(ls: ArrayList<Tag>, attr: TagAttr, vararg vs: TagAttr): List<Tag> {
        for (c in this.children) {
            if (c.match(attr, *vs)) {
                ls += c
            }
            c.filterTo(ls, attr, *vs)
        }
        return ls
    }

    fun filter(attr: TagAttr, vararg vs: TagAttr): List<Tag> {
        val ls = ArrayList<Tag>()
        return filterTo(ls, attr, *vs)
    }

    fun first(attr: TagAttr, vararg vs: TagAttr): Tag? {
        for (c in this.children) {
            if (c.match(attr, *vs)) {
                return c
            }
            val t = c.first(attr, *vs)
            if (t != null) {
                return t
            }
        }
        return null
    }

    fun first(acceptor: (Tag) -> Boolean): Tag? {
        val t = children.find(acceptor)
        if (t != null) {
            return t
        }
        children.forEach {
            val tt = it.first(acceptor)
            if (tt != null) {
                return tt
            }
        }
        return null
    }

    open fun match(vararg vs: TagAttr): Boolean {
        for (a in vs) {
            val c = when (a.first) {
                TAGNAME -> this.tagName.equals(a.second, true)
                "tag" -> this.tagName == a.second
                else -> this.getAttr(a.first) == a.second
            }
            if (!c) {
                return false
            }
        }
        return true
    }

    fun bringToFirst() {
        val ls = parent?.children ?: return
        ls.remove(this)
        ls.add(0, this)
    }

    fun allAttrs(): Map<String, String> {
        return this.attrMap
    }

    fun attrRemove(attr: String) {
        attrMap.remove(attr)
    }

    fun <T : Tag> add(tag: T): T {
        children += tag
        tag.parent = this
        return tag
    }

    open fun add(name: String): Tag {
        return add(GenericTag(context, name))
    }

    fun single(tagname: String): Tag {
        for (c in this.children) {
            if (c.tagName == tagname) {
                return c
            }
        }
        return this.add(tagname)
    }

    inline fun <reified T : Tag> single(block: T.() -> Unit) {
        val a: Tag = children.firstOrNull { it::class == T::class } ?: add<T>(T::class.primaryConstructor!!.call(context))
        (a as T).block()
    }

    fun removeAttr(key: String) {
        attrMap.remove(key)
    }

    fun setAttr(key: String, value: String) {
        this.attrMap[key] = value
    }

    fun getAttr(key: String): String {
        return this.attrMap[key] ?: ""
    }

    fun hasAttr(key: String): Boolean {
        return this.attrMap.containsKey(key)
    }

    infix fun String.attr(value: Any) {
        attrMap[this] = value.toString()
    }

    protected fun Appendable.newLine(ident: Int): Appendable {
        this.appendLine().ident(ident)
        return this
    }

    override fun toString(): String {
        val buf = StringBuilder(2048)

        buf.append("<").append(this.tagName)
        for ((k, v) in attrMap) {
            buf.append(' ')
            buf.append(k).append("=").append("\"$v\"")
        }
        buf.append(">")
        for (ch in this.children) {
            buf.append(ch.toString())
        }
        buf.append("</").append(tagName).append(">")
        return buf.toString()
    }

}
