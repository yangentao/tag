package io.github.yangentao.tag.html

import io.github.yangentao.tag.*
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

open class HtmlTag(context: TagContext, tagName: String) : Tag(context, tagName) {
    @Suppress("UNCHECKED_CAST")
    val childrenHtml: List<HtmlTag> get() = children as List<HtmlTag>
    val parentHtml: HtmlTag? get() = parent as? HtmlTag

    val classList: ArrayList<String> = ArrayList()
    var id: String by TagProp
    var name: String by TagProp
    var style: String by TagProp
    var onclick: String by TagProp

    fun valueFromContext() {
        val k = this.name.ifEmpty { return }
        val v = context.paramValue(k) ?: return
        "value" attr v
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

    override fun match(vararg vs: TagAttr): Boolean {
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

    inline fun <reified T : HtmlTag> append(vararg clses: String, block: T.() -> Unit): T {
        val t: T = add(T::class.primaryConstructor!!.call(context))
        t.classAppend(*clses)
        t.block()
        return t
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

    //==textEscaped
    // +"text"
    operator fun String?.unaryPlus() {
        text(this)
    }

    // !"text"
    operator fun String?.not() {
        unsafe(this)
    }

    override fun add(name: String): HtmlTag {
        val t = create(this.context, name)
        return add(t)
    }

    open fun toHtml(buf: Appendable, level: Int = 0) {
        val multiLine: Boolean = htmlMultiLine()
        val parentMultiLine = parentHtml?.htmlMultiLine() == true

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
        childrenHtml.forEach { tag ->
            tag.toHtml(buf, level + 1)
        }
        if (multiLine) {
            buf.newLine(level)
        }
        buf.append("</").append(tagName).append(">")
    }

    open fun toHtml(): String {
        val buf = StringBuilder(2048)
        toHtml(buf, 0)
        return buf.toString()
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
            1 -> childrenHtml.first().htmlMultiLine()
            else -> true
        }
    }

    companion object {
        internal val tagMap: HashMap<String, KClass<out HtmlTag>> = HashMap()
        protected val singleAttr = setOf("required", "novalidate", "checked", "disabled", "multiple", "readonly", "selected")
        protected val mustBlockTags = setOf("script", "div", "p", "ul", "ol", "span", "datalist", "option", "button", "textarea", "label", "select", "a")
        protected val keepEmptyAttr: Set<Pair<String, String>> = setOf("col" to "width", "option" to "value")
        private var eleId: Int = 0

        init {
            registerAll(htmlTagMap)
        }

        fun create(context: TagContext, tagName: String): HtmlTag {
            printX("create tag: ", tagName)
            val cls: KClass<out HtmlTag> = tagMap.get(tagName) ?: HtmlTag::class
            return create(cls, context, tagName)
        }

        fun create(cls: KClass<out HtmlTag>, context: TagContext, tagName: String): HtmlTag {
            return cls.primaryConstructor?.invokeMap(nameMap = mapOf(TAGNAME to tagName), typeList = listOf(context)) as HtmlTag
        }

        fun register(tagName: String, cls: KClass<out HtmlTag>) {
            tagMap[tagName] = cls
        }

        fun registerAll(map: Map<String, KClass<out HtmlTag>>) {
            tagMap.putAll(map)
        }

        @Synchronized
        fun makeID(prefix: String = "e"): String {
            eleId += 1
            if (eleId > 1_000_000) eleId = 1
            return "$prefix$eleId"
        }

        val trueSet: Set<String> = setOf("on", "1", "true", "yes")

    }
}