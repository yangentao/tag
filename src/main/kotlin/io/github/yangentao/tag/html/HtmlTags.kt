@file:Suppress("LocalVariableName", "unused", "FunctionName")

package io.github.yangentao.tag.html

import io.github.yangentao.anno.Name
import io.github.yangentao.tag.DefaultTagContext
import io.github.yangentao.tag.TagBlock
import io.github.yangentao.tag.TagContext
import io.github.yangentao.tag.TagProp

open class HtmlDoc(context: TagContext = DefaultTagContext()) : HtmlTag(context, "html") {
    var lang: String by TagProp

    override fun toHtml(buf: Appendable, level: Int) {
        buf.appendLine("<!DOCTYPE HTML>")
        super.toHtml(buf, level)
    }
}

class HtmlHead(context: TagContext) : HtmlTag(context, "head") {

}

fun HtmlDoc.head(block: HtmlHead.() -> Unit) {
    single<HtmlHead>(block)
}

class HtmlTitle(context: TagContext) : HtmlTag(context, "title")

fun HtmlHead.title(block: HtmlTitle.() -> Unit) {
    single<HtmlTitle>(block)
}

fun HtmlHead.title(title: String) {
    title {
        this.children.clear()
        +title
    }
}

class HtmlBody(context: TagContext) : HtmlTag(context, "body")

fun HtmlDoc.body(block: HtmlBody.() -> Unit) {
    single<HtmlBody>(block)
}

class HtmlAnchor(context: TagContext) : HtmlTag(context, "a") {
    var href: String by TagProp
    var role: String by TagProp
}

fun HtmlTag.anchor(vararg clses: String, block: HtmlAnchor.() -> Unit): HtmlAnchor {
    return append(*clses, block = block)
}

fun HtmlTag.a(vararg classes: String, block: HtmlAnchor.() -> Unit): HtmlAnchor {
    return append(*classes, block = block)
}

class HtmlButton(context: TagContext) : HtmlTag(context, "button") {
    var type: String by TagProp
    var role: String by TagProp
}

fun HtmlTag.button(vararg classes: String, block: HtmlButton.() -> Unit): HtmlButton {
    return append(*classes) {
        "type" attr "button"
        block()
    }
}

fun HtmlTag.submit(vararg classes: String, block: HtmlButton.() -> Unit): HtmlButton {
    return append(*classes) {
        "type" attr "submit"
        block()
    }
}

class HtmlLabel(context: TagContext) : HtmlTag(context, "label") {
    @Name("for")
    var forID: String by TagProp

    fun forInputPre() {
        val ls = parentHtml?.childrenHtml ?: return
        val n = ls.indexOf(this)
        if (n - 1 >= 0) {
            val ch = ls[n - 1]
            if (ch.tagName == "input") {
                forID = ch.idx
                return
            }
        }
    }
}

fun HtmlTag.label(vararg classes: String, block: HtmlLabel.() -> Unit): HtmlLabel {
    return append(*classes, block = block)
}

fun HtmlTag.label(text: String): HtmlLabel {
    return label {
        +text
    }
}

class HtmlInput(context: TagContext) : HtmlTag(context, "input") {
    var type: String by TagProp
    var value: String by TagProp
    var placeholder: String by TagProp
    var step: String by TagProp
    var pattern: String by TagProp
}

fun HtmlTag.input(vararg classes: String, block: HtmlInput.() -> Unit): HtmlInput {
    return append(*classes, block = block)
}

fun HtmlTag.date(vararg classes: String, block: HtmlInput.() -> Unit): HtmlInput {
    return input(*classes) {
        type = "date"
        this.block()
    }
}

fun HtmlTag.time(vararg classes: String, block: HtmlInput.() -> Unit): HtmlInput {
    return input(*classes) {
        type = "time"
        this.block()
    }
}

fun HtmlTag.datetime(vararg classes: String, block: HtmlInput.() -> Unit): HtmlInput {
    return input(*classes) {
        type = "datetime"
        this.block()
    }
}

fun HtmlTag.file(vararg classes: String, block: HtmlInput.() -> Unit): HtmlInput {
    return input(*classes) {
        type = "file"
        this.block()
    }
}

fun HtmlTag.password(vararg classes: String, block: HtmlInput.() -> Unit): HtmlInput {
    return input(*classes) {
        type = "password"
        this.block()
    }
}

fun HtmlTag.email(vararg classes: String, block: HtmlInput.() -> Unit): HtmlInput {
    return input(*classes) {
        type = "email"
        this.block()
    }
}

fun HtmlTag.hidden(vararg classes: String, block: HtmlInput.() -> Unit): HtmlInput {
    return input(*classes) {
        type = "hidden"
        this.block()
    }
}

fun HtmlTag.hidden(hiddenName: String, hiddenValue: Any?): HtmlInput {
    return this.hidden {
        name = hiddenName
        value = hiddenValue?.toString() ?: ""
    }
}

fun HtmlTag.radio(vararg classes: String, block: HtmlInput.() -> Unit): HtmlInput {
    return input(*classes) {
        type = "radio"
        this.block()
    }
}

fun HtmlTag.checkbox(vararg classes: String, block: HtmlInput.() -> Unit): HtmlInput {
    return input(*classes) {
        type = "checkbox"
        this.block()
    }
}

class HtmlTextarea(context: TagContext) : HtmlTag(context, "textarea") {
    var rows: Int by TagProp
    var placeholder: String by TagProp
}

fun HtmlTag.textarea(vararg classes: String, block: HtmlTextarea.() -> Unit): HtmlTextarea {
    return append(*classes) {
        rows = 3
        block()
    }
}

class HtmlForm(context: TagContext) : HtmlTag(context, "form") {
    var action: String by TagProp
    var method: String by TagProp
}

fun HtmlTag.form(vararg classes: String, block: HtmlForm.() -> Unit): HtmlForm {
    return append(*classes, block = block)
}

class HtmlScript(context: TagContext) : HtmlTag(context, "script") {
    var src: String by TagProp
    var type: String by TagProp

    init {
        type = "text/javascript"
    }

    override fun htmlMultiLine(): Boolean {
        return this.children.isNotEmpty()
    }
}

fun HtmlTag.script(src: String) {
    append<HtmlScript> {
        this.src = src
    }
}

fun HtmlTag.script(block: () -> String) {
    append<HtmlScript> {
        unsafe(block())
    }
}

class HtmlLink(context: TagContext) : HtmlTag(context, "link") {
    var rel: String by TagProp
    var href: String by TagProp
}

fun HtmlTag.link(
    charset: String? = null, href: String? = null, hreflang: String? = null,
    media: String? = null, referrerpolicy: String? = null, rel: String? = null, rev: String? = null,
    sizes: String? = null, target: String? = null, type: String? = null
): HtmlLink {
    return append {
        charset?.also { setAttr("charset", it) }
        href?.also { setAttr("href", it) }
        hreflang?.also { setAttr("hreflang", it) }
        media?.also { setAttr("media", it) }
        referrerpolicy?.also { setAttr("referrerpolicy", it) }
        rel?.also { setAttr("rel", it) }
        rev?.also { setAttr("rev", it) }
        sizes?.also { setAttr("sizes", it) }
        target?.also { setAttr("target", it) }
        type?.also { setAttr("type", it) }
    }
}

fun HtmlTag.link(vararg classes: String, block: HtmlLink.() -> Unit): HtmlLink {
    return append(*classes, block = block)
}

fun HtmlTag.linkCSS(href: String): HtmlLink {
    return link {
        this.href = href
        this.rel = "stylesheet"
    }
}

fun HtmlTag.stylesheet(url: String) {
    link {
        rel = "stylesheet"
        href = url
    }
}

class HtmlOption(context: TagContext) : HtmlTag(context, "option") {
    var value: String by TagProp
}

fun HtmlTag.option(vararg classes: String, block: HtmlOption.() -> Unit): HtmlOption {
    return append(*classes, block = block)
}

class HtmlMeta(context: TagContext) : HtmlTag(context, "meta") {
    var content: String by TagProp
    var charset: String by TagProp

}

fun HtmlTag.meta(charset: String? = null, content: String? = null, http_equiv: String? = null, name: String? = null, scheme: String? = null): HtmlMeta {
    return append {
        charset?.also { setAttr("charset", it) }
        content?.also { setAttr("content", it) }
        name?.also { setAttr("name", it) }
        scheme?.also { setAttr("scheme", it) }
        http_equiv?.also { setAttr("http-equiv", it) }
    }
}

fun HtmlTag.meta(vararg classes: String, block: HtmlMeta.() -> Unit): HtmlMeta {
    return append(*classes, block = block)
}

fun HtmlTag.keywords(ws: List<String>): HtmlMeta {
    return meta {
        name = "keywords"
        content = ws.joinToString(",")
    }
}

class HtmlImage(context: TagContext) : HtmlTag(context, "img") {
    var src: String by TagProp
}

fun HtmlTag.img(vararg classes: String, block: HtmlImage.() -> Unit): HtmlImage {
    return append(*classes, block = block)
}

open class HtmlDiv(context: TagContext) : HtmlTag(context, "div") {
    var role: String by TagProp
}

fun HtmlTag.div(vararg classes: String, block: HtmlDiv.() -> Unit): HtmlDiv {
    return append(*classes, block = block)
}

class HtmlBase(context: TagContext) : HtmlTag(context, "base") {
    var href: String by TagProp
}

fun HtmlTag.base(block: HtmlBase.() -> Unit): HtmlBase {
    return append(block = block)
}

open class HtmlHeader(context: TagContext) : HtmlTag(context, "header")

fun HtmlTag.header(vararg classes: String, block: HtmlHeader.() -> Unit): HtmlHeader {
    return append(*classes, block = block)
}

class HtmlMain(context: TagContext) : HtmlTag(context, "main")

fun HtmlTag.main_(vararg classes: String, block: HtmlMain.() -> Unit): HtmlMain {
    return append(*classes, block = block)
}

class HtmlSelect(context: TagContext) : HtmlTag(context, "select")

fun HtmlTag.select(vararg classes: String, block: HtmlSelect.() -> Unit): HtmlSelect {
    return append(*classes, block = block)
}

open class HtmlNav(context: TagContext) : HtmlTag(context, "nav")

fun HtmlTag.nav(vararg classes: String, block: HtmlNav.() -> Unit): HtmlNav {
    return append(*classes, block = block)
}

class HtmlSpan(context: TagContext) : HtmlTag(context, "span")

fun HtmlTag.span(vararg classes: String, block: HtmlSpan.() -> Unit): HtmlSpan {
    return append(*classes, block = block)
}

class HtmlHr(context: TagContext) : HtmlTag(context, "hr")

fun HtmlTag.hr(vararg classes: String, block: HtmlHr.() -> Unit): HtmlHr {
    return append(*classes, block = block)
}

fun HtmlTag.hr(): HtmlHr {
    return this.hr {}
}

class HtmlPre(context: TagContext) : HtmlTag(context, "pre")

fun HtmlTag.pre(vararg classes: String, block: HtmlPre.() -> Unit): HtmlPre {
    return append(*classes, block = block)
}

class HtmlCode(context: TagContext) : HtmlTag(context, "code")

fun HtmlTag.code(vararg classes: String, block: HtmlCode.() -> Unit): HtmlCode {
    return append(*classes, block = block)
}

class HtmlOl(context: TagContext) : HtmlTag(context, "ol")

fun HtmlTag.ol(vararg classes: String, block: HtmlOl.() -> Unit): HtmlOl {
    return append(*classes, block = block)
}

class HtmlUl(context: TagContext) : HtmlTag(context, "ul")

fun HtmlTag.ul(vararg classes: String, block: HtmlUl.() -> Unit): HtmlUl {
    return append(*classes, block = block)
}

class HtmlLi(context: TagContext) : HtmlTag(context, "li")

fun HtmlTag.li(vararg classes: String, block: HtmlLi.() -> Unit): HtmlLi {
    return append(*classes, block = block)
}

class HtmlH1(context: TagContext) : HtmlTag(context, "h1")

fun HtmlTag.h1(vararg classes: String, block: HtmlH1.() -> Unit): HtmlH1 {
    return append(*classes, block = block)
}

class HtmlH2(context: TagContext) : HtmlTag(context, "h2")

fun HtmlTag.h2(vararg classes: String, block: HtmlH2.() -> Unit): HtmlH2 {
    return append(*classes, block = block)
}

class HtmlH3(context: TagContext) : HtmlTag(context, "h3")

fun HtmlTag.h3(vararg classes: String, block: HtmlH3.() -> Unit): HtmlH3 {
    return append(*classes, block = block)
}

class HtmlH4(context: TagContext) : HtmlTag(context, "h4")

fun HtmlTag.h4(vararg classes: String, block: HtmlH4.() -> Unit): HtmlH4 {
    return append(*classes, block = block)
}

class HtmlH5(context: TagContext) : HtmlTag(context, "h5")

fun HtmlTag.h5(vararg classes: String, block: HtmlH5.() -> Unit): HtmlH5 {
    return append(*classes, block = block)
}

class HtmlH6(context: TagContext) : HtmlTag(context, "h6")

fun HtmlTag.h6(vararg classes: String, block: HtmlH6.() -> Unit): HtmlH6 {
    return append(*classes, block = block)
}

class HtmlP(context: TagContext) : HtmlTag(context, "p")

fun HtmlTag.p(vararg classes: String, block: HtmlP.() -> Unit): HtmlP {
    return append(*classes, block = block)
}

class HtmlDl(context: TagContext) : HtmlTag(context, "dl")

fun HtmlTag.dl(vararg classes: String, block: HtmlDl.() -> Unit): HtmlDl {
    return append(*classes, block = block)
}

class HtmlDt(context: TagContext) : HtmlTag(context, "dt")

fun HtmlTag.dt(vararg classes: String, block: HtmlDt.() -> Unit): HtmlDt {
    return append(*classes, block = block)
}

class HtmlDd(context: TagContext) : HtmlTag(context, "dd")

fun HtmlTag.dd(vararg classes: String, block: HtmlDd.() -> Unit): HtmlDd {
    return append(*classes, block = block)
}

open class HtmlTable(context: TagContext) : HtmlTag(context, "table")

fun HtmlTag.table(vararg classes: String, block: HtmlTable.() -> Unit): HtmlTable {
    return append(*classes, block = block)
}

class HtmlTHead(context: TagContext) : HtmlTag(context, "thead")

fun HtmlTag.thead(vararg classes: String, block: HtmlTHead.() -> Unit): HtmlTHead {
    return append(*classes, block = block)
}

class HtmlTBody(context: TagContext) : HtmlTag(context, "tbody")

fun HtmlTag.tbody(vararg classes: String, block: HtmlTBody.() -> Unit): HtmlTBody {
    return append(*classes, block = block)
}

class HtmlTh(context: TagContext) : HtmlTag(context, "th") {
    var scope: String by TagProp
}

fun HtmlTag.th(vararg classes: String, block: HtmlTh.() -> Unit): HtmlTh {
    return append(*classes, block = block)
}

class HtmlTd(context: TagContext) : HtmlTag(context, "td")

fun HtmlTag.td(vararg classes: String, block: HtmlTd.() -> Unit): HtmlTd {
    return append(*classes, block = block)
}

class HtmlTr(context: TagContext) : HtmlTag(context, "tr")

fun HtmlTag.tr(vararg classes: String, block: HtmlTr.() -> Unit): HtmlTr {
    return append(*classes, block = block)
}

class HtmlCol(context: TagContext) : HtmlTag(context, "col")

fun HtmlTag.col(vararg classes: String, block: HtmlCol.() -> Unit): HtmlCol {
    return append(*classes, block = block)
}

class HtmlColGroup(context: TagContext) : HtmlTag(context, "colgroup")

fun HtmlTag.colgroup(vararg classes: String, block: HtmlColGroup.() -> Unit): HtmlColGroup {
    return append(*classes, block = block)
}

class HtmlWell(context: TagContext) : HtmlTag(context, "well")

fun HtmlTag.well(vararg classes: String, block: HtmlWell.() -> Unit): HtmlWell {
    return append(*classes, block = block)
}

class HtmlStrong(context: TagContext) : HtmlTag(context, "strong")

fun HtmlTag.strong(vararg classes: String, block: HtmlStrong.() -> Unit): HtmlStrong {
    return append(*classes, block = block)
}

class HtmlFont(context: TagContext) : HtmlTag(context, "font")

fun HtmlTag.font(vararg classes: String, block: HtmlFont.() -> Unit): HtmlFont {
    return append(*classes, block = block)
}

fun HtmlTag.font(size: Int, color: String, block: TagBlock): HtmlFont {
    return font {
        "size" attr size.toString()
        "color" attr color
        this.block()
    }
}

class HtmlSmall(context: TagContext) : HtmlTag(context, "small")

fun HtmlTag.small(vararg classes: String, block: HtmlSmall.() -> Unit): HtmlSmall {
    return append(*classes, block = block)
}

class HtmlDatalist(context: TagContext) : HtmlTag(context, "datalist")

fun HtmlTag.datalist(vararg classes: String, block: HtmlDatalist.() -> Unit): HtmlDatalist {
    return append(*classes, block = block)
}

class HtmlFooter(context: TagContext) : HtmlTag(context, "footer")

fun HtmlTag.footer(vararg classes: String, block: HtmlFooter.() -> Unit): HtmlFooter {
    return append(*classes, block = block)
}

class HtmlArtical(context: TagContext) : HtmlTag(context, "article")

fun HtmlTag.article(vararg classes: String, block: HtmlArtical.() -> Unit): HtmlArtical {
    return append(*classes, block = block)
}

class HtmlStyle(context: TagContext) : HtmlTag(context, "style")

fun HtmlTag.style(block: () -> String): HtmlStyle {
    return append {
        unsafe(block)
    }
}

class HtmlBr(context: TagContext) : HtmlTag(context, "br")

fun HtmlTag.br(): HtmlBr {
    return append(block = {})
}
