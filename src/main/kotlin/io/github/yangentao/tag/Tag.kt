@file:Suppress("FunctionName", "MemberVisibilityCanBePrivate", "unused", "PropertyName")

package io.github.yangentao.tag

import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

@DslMarker
annotation class TagMarker

const val TAGNAME = "tagName"
typealias TagBlock = Tag.() -> Unit
typealias TagAttr = Pair<String, String>

interface TagContext {
    fun paramValue(key: String): String?
}

class DefaultTagContext : TagContext {
    override fun paramValue(key: String): String? {
        return null
    }

}

class GenericTag(context: TagContext, tagName: String) : Tag(context, tagName)

@TagMarker
abstract class Tag(val context: TagContext, val tagName: String) {
    private val attrMap: TagPropMap = TagPropMap()
    val children = ArrayList<Tag>(8)
    val classList: ArrayList<String> = ArrayList()
    var parent: Tag? = null
    var id: String by TagProp
    var name: String by TagProp
    var style: String by TagProp
    var onclick: String by TagProp
    val root: Tag get() = this.parent?.root ?: this

    fun classAppend(vararg clses: String) {
        for (s in clses) {
            classList += s.split(' ').map { it.trim() }.filter { it.isNotEmpty() }
        }
    }

    fun classPush(cls: String) {
        classList.remove(cls)
        classList.add(0, cls)
    }

    fun classHas(cls: String): Boolean {
        return cls in classList
    }

    fun classRemove(cls: String) {
        classList.remove(cls)
    }

    fun classBringFirst(cls: String) {
        classList.remove(cls)
        classList.add(0, cls)
    }

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

    fun match(vararg vs: TagAttr): Boolean {
        for (a in vs) {
            val c = when (a.first) {
                TAGNAME -> this.tagName.equals(a.second, true)
                "tag" -> this.tagName == a.second
                "class" -> this.classHas(a.second)
                else -> this.getAttr(a.first) == a.second
            }
            if (!c) {
                return false
            }
        }
        return true
    }

    val idx: String
        get() {
            return requireID()
        }

    fun requireID(): String {
        if (this.id.isEmpty()) {
            this.id = makeID(tagName)
        }
        return this.id
    }

    fun required() {
        setAttr("required", "required")
    }

    fun readonly() {
        "readonly" attr "true"
    }

    fun disabled() {
        "disabled" attr "true"
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

    fun add(name: String): Tag {
        val t = create(this.context, name)
        return add(t)
    }

    fun <T : Tag> add(tag: T): T {
        children += tag
        tag.parent = this
        return tag
    }

    inline fun <reified T : Tag> append(vararg clses: String, block: T.() -> Unit): T {
        val t: T = add(T::class.primaryConstructor!!.call(context))
        t.classAppend(*clses)
        t.block()
        return t
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

    //==textEscaped
    // +"text"
    operator fun String?.unaryPlus() {
        text(this)
    }

    // !"text"
    operator fun String?.not() {
        unsafe(this)
    }

    fun data(name: String): String {
        return if (name.startsWith("data-")) {
            this.getAttr(name)
        } else {
            this.getAttr("data-$name")
        }
    }

    fun data(name: String, value: String) {
        if (name.startsWith("data-")) {
            this.setAttr(name, value)
        } else {
            this.setAttr("data-$name", value)
        }
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

    infix fun String.attr(value: String) {
        attrMap[this] = value
    }

    fun valueFromContext() {
        val k = this.name.ifEmpty { return }
        val v = context.paramValue(k) ?: return
        "value" attr v
    }

    open fun toHtml(buf: Appendable, level: Int = 0) {
        val multiLine: Boolean = htmlMultiLine()
        val parentMultiLine = parent?.htmlMultiLine() == true

        if (this.classList.isNotEmpty()) {
            this.attrMap["class"] = this.classList.joinToString(" ")
        }
        if (parentMultiLine) {
            buf.newLine(level)
        }
        buf.append("<").append(this.tagName)
        if (this.attrMap.isNotEmpty()) {
            buf.append(' ')
            val s: String = this.attrMap.map { e ->
                htmlAttrPair(e.key, e.value)
            }.filter { it.isNotEmpty() }.joinToString(" ")
            buf.append(s)
        }
        if (this.children.isEmpty()) {
            if (this.tagName in mustBlockTags) {
                buf.append("></").append(this.tagName).append(">")
            } else {
                buf.append("/>")
            }
            return
        }
        buf.append(">")
        children.forEach { tag ->
            tag.toHtml(buf, level + 1)
        }
        if (multiLine) {
            buf.newLine(level)
        }
        buf.append("</").append(tagName).append(">")
    }

    protected fun Appendable.newLine(ident: Int): Appendable {
        this.appendLine().ident(ident)
        return this
    }

    open fun toHtml(): String {
        val buf = StringBuilder(2048)
        toHtml(buf, 0)
        return buf.toString()
    }

    override fun toString(): String {
        return toHtml()
    }

    protected fun htmlAttrPair(key: String, value: String): String {
        if (value.isEmpty()) {
            if (!htmlAllowEmptyAttrValue(key)) return ""
        }
        if (key in singleAttr) {
            return if (key == value || value == "true" || value == "yes" || value == "on" || value == "1") {
                key
            } else {
                ""
            }
        }
        return key + "=" + attrVal(value)
    }

    protected fun htmlAllowEmptyAttrValue(key: String): Boolean {
        if (this.tagName == "col" && key == "width") return true
        if (this.tagName == "option" && key == "value") return true
        return false
    }

    open fun htmlMultiLine(): Boolean {
        return when (children.size) {
            0 -> false
            1 -> children.first().htmlMultiLine()
            else -> true
        }
    }

    companion object {
        internal val tagMap: HashMap<String, KClass<*>> = HashMap()
        protected val singleAttr = setOf("required", "novalidate", "checked", "disabled", "multiple", "readonly", "selected")
        protected val mustBlockTags = setOf("script", "div", "p", "ul", "ol", "span", "datalist", "option", "button", "textarea", "label", "select", "a")
        protected val keepEmptyAttr: Set<Pair<String, String>> = setOf("col" to "width", "option" to "value")
        private var eleId: Int = 0

        init {
            registerAll(htmlTagMap)
        }

        fun create(context: TagContext, tagName: String): Tag {
            printX("create tag: ", tagName)
            val cls: KClass<*> = tagMap.get(tagName) ?: GenericTag::class
            return cls.primaryConstructor?.invokeMap(nameMap = mapOf("tagName" to tagName), typeList = listOf(context)) as Tag
        }

        fun create(cls: KClass<*>, context: TagContext, tagName: String): Tag {
            return cls.primaryConstructor?.invokeMap(nameMap = mapOf("tagName" to tagName), typeList = listOf(context)) as Tag
        }

        fun register(tagName: String, cls: KClass<*>) {
            tagMap[tagName] = cls
        }

        fun registerAll(map: Map<String, KClass<*>>) {
            tagMap.putAll(map)
        }

        @Synchronized
        fun makeID(prefix: String = "e"): String {
            eleId += 1
            if (eleId > 1_000_000) eleId = 1
            return "$prefix$eleId"
        }

        const val TEXT_TAG = "text"
        val trueSet: Set<String> = setOf("on", "1", "true", "yes")

    }
}
