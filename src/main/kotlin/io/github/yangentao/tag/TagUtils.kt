@file:Suppress("MayBeConstant")

package io.github.yangentao.tag

import io.github.yangentao.anno.Name
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf

internal typealias TagPropMap = LinkedHashMap<String, String>

object TagProp {
    operator fun <T> setValue(thisRef: Tag, property: KProperty<*>, value: T) {
        val k = property.userName
        when (value) {
            is String -> thisRef.setAttr(k, value)
            is Boolean -> {
                if (value) {
                    thisRef.setAttr(k, k)
                } else {
                    thisRef.removeAttr(k)
                }
            }

            else -> thisRef.setAttr(k, value.toString())
        }
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T> getValue(thisRef: Tag, property: KProperty<*>): T {
        val pname = property.userName
        val v = thisRef.getAttr(pname)
        return when (property.returnType.classifier) {
            String::class -> v as T
            Boolean::class -> (v == pname) as T
            Int::class -> {
                if (v.isEmpty()) {
                    0 as T
                } else {
                    v.toInt() as T
                }
            }

            else -> throw IllegalArgumentException("不支持的类型$property")
        }
    }
}

internal const val IDENT_HTML = "    "
internal const val QUOT = "\""
internal fun attrVal(value: String): String {
    val s = value.replace(QUOT, "&quot;")
    return QUOT + s + QUOT
}

internal fun Appendable.ident(n: Int): Appendable {
    if (n > 0) {
        for (i in 0 until n) {
            this.append(IDENT_HTML)
        }
    }
    return this
}

fun String.escapeHtml(forView: Boolean): String {
    if (!forView) {
        return this.escapeHtml()
    }
    val sb = StringBuffer((this.length * 1.1).toInt())
    var i = 0
    val CR = 13.toChar()
    val LF = 10.toChar()
    val SP = ' '
    val BR = "<br/>"
    while (i < this.length) {
        val c = this[i]
        when (c) {
            '<' -> sb.append("&lt;")
            '>' -> sb.append("&gt;")
            '"' -> sb.append("&quot;")
            '\'' -> sb.append("&#x27;")
            '&' -> sb.append("&amp;")
            '/' -> sb.append("&#x2F;")
            SP -> {
                sb.append("&nbsp;")
            }

            CR -> {
                val nextChar: Char? = if (i + 1 < this.length) this[i + 1] else null
                if (nextChar != LF) {
                    sb.append(BR)
                }
            }

            LF -> {
                sb.append(BR)
            }

            else -> sb.append(c)
        }
        ++i
    }

    return sb.toString()
}

internal fun String.escapeHtml(): String {
    val sb = StringBuffer((this.length * 1.1).toInt())
    this.forEach {
        when (it) {
            '<' -> sb.append("&lt;")
            '>' -> sb.append("&gt;")
            '"' -> sb.append("&quot;")
            '\'' -> sb.append("&#x27;")
            '&' -> sb.append("&amp;")
            '/' -> sb.append("&#x2F;")
            else -> sb.append(it)
        }
    }
    return sb.toString()
}

internal fun printX(vararg vs: Any?) {
    val s = vs.joinToString(" ") {
        it?.toString() ?: "null"
    }
    println(s)
}

internal val KProperty<*>.userName: String
    get() {
        return this.findAnnotation<Name>()?.value ?: this.name
    }

internal fun KFunction<*>.invokeMap(inst: Any? = null, nameMap: Map<String, Any?> = emptyMap(), typeMap: Map<KClass<*>, Any?> = emptyMap(), typeList: List<Any> = emptyList()): Any? {
    fun valueParam(paramMap: HashMap<KParameter, Any?>, p: KParameter, value: Any?) {
        if (value == null) {
            if (p.type.isMarkedNullable) {
                paramMap[p] = null
                return
            }
            if (p.isOptional) return
            error("value is null : $p")
        }
        paramMap[p] = value
    }

    val typeListMap: Map<KClass<*>, Any?> = if (typeList.isEmpty()) {
        typeMap
    } else {
        val map = LinkedHashMap<KClass<*>, Any?>()
        map.putAll(typeMap)
        for (a in typeList) {
            map[a::class] = a
        }
        map
    }

    val paramMap = HashMap<KParameter, Any?>()
    forParams@
    for (p in this.parameters) {
        when (p.kind) {
            KParameter.Kind.INSTANCE -> paramMap[p] = inst
            KParameter.Kind.EXTENSION_RECEIVER -> paramMap[p] = inst
            KParameter.Kind.VALUE -> {
                //先根据参数名匹配
                val nv = nameMap[p.name!!]
                if (nv != null) {
                    valueParam(paramMap, p, nv)
                    continue
                }
                val pclass = p.type.classifier as KClass<*>
                //精确匹配类型
                val tv = typeListMap[pclass]
                if (tv != null) {
                    paramMap[p] = tv
                    continue
                }
                //子类
                for (e in typeListMap.entries) {
                    if (e.key.isSubclassOf(pclass)) {
                        paramMap[p] = e.value
                        continue@forParams
                    }
                }

                valueParam(paramMap, p, null)
            }
        }
    }
    return this.callBy(paramMap)
}