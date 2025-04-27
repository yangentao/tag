@file:Suppress("LocalVariableName", "unused", "FunctionName")

package io.github.yangentao.tag

import io.github.yangentao.anno.Name

open class HtmlDoc(context: TagContext = DefaultTagContext()) : Tag(context, "html") {
    var lang: String by TagProp

    override fun toHtml(buf: Appendable, level: Int) {
        buf.appendLine("<!DOCTYPE HTML>")
        super.toHtml(buf, level)
    }
}

class HtmlHead(context: TagContext) : Tag(context, "head") {

}

fun HtmlDoc.head(block: HtmlHead.() -> Unit) {
    single<HtmlHead>(block)
}

class HtmlTitle(context: TagContext) : Tag(context, "title")

fun HtmlHead.title(block: HtmlTitle.() -> Unit) {
    single<HtmlTitle>(block)
}

fun HtmlHead.title(title: String) {
    title {
        this.children.clear()
        +title
    }
}

class HtmlBody(context: TagContext) : Tag(context, "body")

fun HtmlDoc.body(block: HtmlBody.() -> Unit) {
    single<HtmlBody>(block)
}

class HtmlAnchor(context: TagContext) : Tag(context, "a") {
    var href: String by TagProp
    var role: String by TagProp
}

fun Tag.anchor(vararg clses: String, block: HtmlAnchor.() -> Unit): HtmlAnchor {
    return append(*clses, block = block)
}

fun Tag.a(vararg classes: String, block: HtmlAnchor.() -> Unit): HtmlAnchor {
    return append(*classes, block = block)
}

class HtmlButton(context: TagContext) : Tag(context, "button") {
    var type: String by TagProp
    var role: String by TagProp
}

fun Tag.button(vararg classes: String, block: HtmlButton.() -> Unit): HtmlButton {
    return append(*classes) {
        "type" attr "button"
        block()
    }
}

fun Tag.submit(vararg classes: String, block: HtmlButton.() -> Unit): HtmlButton {
    return append(*classes) {
        "type" attr "submit"
        block()
    }
}

class HtmlLabel(context: TagContext) : Tag(context, "label") {
    @Name("for")
    var forID: String by TagProp

    fun forInputPre() {
        val ls = parent?.children ?: return
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

fun Tag.label(vararg classes: String, block: HtmlLabel.() -> Unit): HtmlLabel {
    return append(*classes, block = block)
}

fun Tag.label(text: String): HtmlLabel {
    return label {
        +text
    }
}

class HtmlInput(context: TagContext) : Tag(context, "input") {
    var type: String by TagProp
    var value: String by TagProp
    var placeholder: String by TagProp
    var step: String by TagProp
    var pattern: String by TagProp
}

fun Tag.input(vararg classes: String, block: HtmlInput.() -> Unit): HtmlInput {
    return append(*classes, block = block)
}

fun Tag.date(vararg classes: String, block: HtmlInput.() -> Unit): HtmlInput {
    return input(*classes) {
        type = "date"
        this.block()
    }
}

fun Tag.time(vararg classes: String, block: HtmlInput.() -> Unit): HtmlInput {
    return input(*classes) {
        type = "time"
        this.block()
    }
}

fun Tag.datetime(vararg classes: String, block: HtmlInput.() -> Unit): HtmlInput {
    return input(*classes) {
        type = "datetime"
        this.block()
    }
}

fun Tag.file(vararg classes: String, block: HtmlInput.() -> Unit): HtmlInput {
    return input(*classes) {
        type = "file"
        this.block()
    }
}

fun Tag.password(vararg classes: String, block: HtmlInput.() -> Unit): HtmlInput {
    return input(*classes) {
        type = "password"
        this.block()
    }
}

fun Tag.email(vararg classes: String, block: HtmlInput.() -> Unit): HtmlInput {
    return input(*classes) {
        type = "email"
        this.block()
    }
}

fun Tag.hidden(vararg classes: String, block: HtmlInput.() -> Unit): HtmlInput {
    return input(*classes) {
        type = "hidden"
        this.block()
    }
}

fun Tag.hidden(hiddenName: String, hiddenValue: Any?): HtmlInput {
    return this.hidden {
        name = hiddenName
        value = hiddenValue?.toString() ?: ""
    }
}

fun Tag.radio(vararg classes: String, block: HtmlInput.() -> Unit): HtmlInput {
    return input(*classes) {
        type = "radio"
        this.block()
    }
}

fun Tag.checkbox(vararg classes: String, block: HtmlInput.() -> Unit): HtmlInput {
    return input(*classes) {
        type = "checkbox"
        this.block()
    }
}

class HtmlTextarea(context: TagContext) : Tag(context, "textarea") {
    var rows: Int by TagProp
    var placeholder: String by TagProp
}

fun Tag.textarea(vararg classes: String, block: HtmlTextarea.() -> Unit): HtmlTextarea {
    return append(*classes) {
        rows = 3
        block()
    }
}

class HtmlForm(context: TagContext) : Tag(context, "form") {
    var action: String by TagProp
    var method: String by TagProp
}

fun Tag.form(vararg classes: String, block: HtmlForm.() -> Unit): HtmlForm {
    return append(*classes, block = block)
}

class HtmlScript(context: TagContext) : Tag(context, "script") {
    var src: String by TagProp
    var type: String by TagProp

    init {
        type = "text/javascript"
    }

    override fun htmlMultiLine(): Boolean {
        return this.children.isNotEmpty()
    }
}

fun Tag.script(src: String) {
    append<HtmlScript> {
        this.src = src
    }
}

fun Tag.script(block: () -> String) {
    append<HtmlScript> {
        unsafe(block())
    }
}

class HtmlLink(context: TagContext) : Tag(context, "link") {
    var rel: String by TagProp
    var href: String by TagProp
}

fun Tag.link(
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

fun Tag.link(vararg classes: String, block: HtmlLink.() -> Unit): HtmlLink {
    return append(*classes, block = block)
}

fun Tag.linkCSS(href: String): HtmlLink {
    return link {
        this.href = href
        this.rel = "stylesheet"
    }
}

fun Tag.stylesheet(url: String) {
    link {
        rel = "stylesheet"
        href = url
    }
}

class HtmlOption(context: TagContext) : Tag(context, "option") {
    var value: String by TagProp
}

fun Tag.option(vararg classes: String, block: HtmlOption.() -> Unit): HtmlOption {
    return append(*classes, block = block)
}

class HtmlMeta(context: TagContext) : Tag(context, "meta") {
    var content: String by TagProp
    var charset: String by TagProp

}

fun Tag.meta(charset: String? = null, content: String? = null, http_equiv: String? = null, name: String? = null, scheme: String? = null): HtmlMeta {
    return append {
        charset?.also { setAttr("charset", it) }
        content?.also { setAttr("content", it) }
        name?.also { setAttr("name", it) }
        scheme?.also { setAttr("scheme", it) }
        http_equiv?.also { setAttr("http-equiv", it) }
    }
}

fun Tag.meta(vararg classes: String, block: HtmlMeta.() -> Unit): HtmlMeta {
    return append(*classes, block = block)
}

fun Tag.keywords(ws: List<String>): HtmlMeta {
    return meta {
        name = "keywords"
        content = ws.joinToString(",")
    }
}

class HtmlImage(context: TagContext) : Tag(context, "img") {
    var src: String by TagProp
}

fun Tag.img(vararg classes: String, block: HtmlImage.() -> Unit): HtmlImage {
    return append(*classes, block = block)
}

open class HtmlDiv(context: TagContext) : Tag(context, "div") {
    var role: String by TagProp
}

fun Tag.div(vararg classes: String, block: HtmlDiv.() -> Unit): HtmlDiv {
    return append(*classes, block = block)
}

class HtmlBase(context: TagContext) : Tag(context, "base") {
    var href: String by TagProp
}

fun Tag.base(block: HtmlBase.() -> Unit): HtmlBase {
    return append(block = block)
}

open class HtmlHeader(context: TagContext) : Tag(context, "header")

fun Tag.header(vararg classes: String, block: HtmlHeader.() -> Unit): HtmlHeader {
    return append(*classes, block = block)
}

class HtmlMain(context: TagContext) : Tag(context, "main")

fun Tag.main_(vararg classes: String, block: HtmlMain.() -> Unit): HtmlMain {
    return append(*classes, block = block)
}

class HtmlSelect(context: TagContext) : Tag(context, "select")

fun Tag.select(vararg classes: String, block: HtmlSelect.() -> Unit): HtmlSelect {
    return append(*classes, block = block)
}

open class HtmlNav(context: TagContext) : Tag(context, "nav")

fun Tag.nav(vararg classes: String, block: HtmlNav.() -> Unit): HtmlNav {
    return append(*classes, block = block)
}

class HtmlSpan(context: TagContext) : Tag(context, "span")

fun Tag.span(vararg classes: String, block: HtmlSpan.() -> Unit): HtmlSpan {
    return append(*classes, block = block)
}

class HtmlHr(context: TagContext) : Tag(context, "hr")

fun Tag.hr(vararg classes: String, block: HtmlHr.() -> Unit): HtmlHr {
    return append(*classes, block = block)
}

fun Tag.hr(): HtmlHr {
    return this.hr {}
}

class HtmlPre(context: TagContext) : Tag(context, "pre")

fun Tag.pre(vararg classes: String, block: HtmlPre.() -> Unit): HtmlPre {
    return append(*classes, block = block)
}

class HtmlCode(context: TagContext) : Tag(context, "code")

fun Tag.code(vararg classes: String, block: HtmlCode.() -> Unit): HtmlCode {
    return append(*classes, block = block)
}

class HtmlOl(context: TagContext) : Tag(context, "ol")

fun Tag.ol(vararg classes: String, block: HtmlOl.() -> Unit): HtmlOl {
    return append(*classes, block = block)
}

class HtmlUl(context: TagContext) : Tag(context, "ul")

fun Tag.ul(vararg classes: String, block: HtmlUl.() -> Unit): HtmlUl {
    return append(*classes, block = block)
}

class HtmlLi(context: TagContext) : Tag(context, "li")

fun Tag.li(vararg classes: String, block: HtmlLi.() -> Unit): HtmlLi {
    return append(*classes, block = block)
}

class HtmlH1(context: TagContext) : Tag(context, "h1")

fun Tag.h1(vararg classes: String, block: HtmlH1.() -> Unit): HtmlH1 {
    return append(*classes, block = block)
}

class HtmlH2(context: TagContext) : Tag(context, "h2")

fun Tag.h2(vararg classes: String, block: HtmlH2.() -> Unit): HtmlH2 {
    return append(*classes, block = block)
}

class HtmlH3(context: TagContext) : Tag(context, "h3")

fun Tag.h3(vararg classes: String, block: HtmlH3.() -> Unit): HtmlH3 {
    return append(*classes, block = block)
}

class HtmlH4(context: TagContext) : Tag(context, "h4")

fun Tag.h4(vararg classes: String, block: HtmlH4.() -> Unit): HtmlH4 {
    return append(*classes, block = block)
}

class HtmlH5(context: TagContext) : Tag(context, "h5")

fun Tag.h5(vararg classes: String, block: HtmlH5.() -> Unit): HtmlH5 {
    return append(*classes, block = block)
}

class HtmlH6(context: TagContext) : Tag(context, "h6")

fun Tag.h6(vararg classes: String, block: HtmlH6.() -> Unit): HtmlH6 {
    return append(*classes, block = block)
}

class HtmlP(context: TagContext) : Tag(context, "p")

fun Tag.p(vararg classes: String, block: HtmlP.() -> Unit): HtmlP {
    return append(*classes, block = block)
}

class HtmlDl(context: TagContext) : Tag(context, "dl")

fun Tag.dl(vararg classes: String, block: HtmlDl.() -> Unit): HtmlDl {
    return append(*classes, block = block)
}

class HtmlDt(context: TagContext) : Tag(context, "dt")

fun Tag.dt(vararg classes: String, block: HtmlDt.() -> Unit): HtmlDt {
    return append(*classes, block = block)
}

class HtmlDd(context: TagContext) : Tag(context, "dd")

fun Tag.dd(vararg classes: String, block: HtmlDd.() -> Unit): HtmlDd {
    return append(*classes, block = block)
}

open class HtmlTable(context: TagContext) : Tag(context, "table")

fun Tag.table(vararg classes: String, block: HtmlTable.() -> Unit): HtmlTable {
    return append(*classes, block = block)
}

class HtmlTHead(context: TagContext) : Tag(context, "thead")

fun Tag.thead(vararg classes: String, block: HtmlTHead.() -> Unit): HtmlTHead {
    return append(*classes, block = block)
}

class HtmlTBody(context: TagContext) : Tag(context, "tbody")

fun Tag.tbody(vararg classes: String, block: HtmlTBody.() -> Unit): HtmlTBody {
    return append(*classes, block = block)
}

class HtmlTh(context: TagContext) : Tag(context, "th") {
    var scope: String by TagProp
}

fun Tag.th(vararg classes: String, block: HtmlTh.() -> Unit): HtmlTh {
    return append(*classes, block = block)
}

class HtmlTd(context: TagContext) : Tag(context, "td")

fun Tag.td(vararg classes: String, block: HtmlTd.() -> Unit): HtmlTd {
    return append(*classes, block = block)
}

class HtmlTr(context: TagContext) : Tag(context, "tr")

fun Tag.tr(vararg classes: String, block: HtmlTr.() -> Unit): HtmlTr {
    return append(*classes, block = block)
}

class HtmlCol(context: TagContext) : Tag(context, "col")

fun Tag.col(vararg classes: String, block: HtmlCol.() -> Unit): HtmlCol {
    return append(*classes, block = block)
}

class HtmlColGroup(context: TagContext) : Tag(context, "colgroup")

fun Tag.colgroup(vararg classes: String, block: HtmlColGroup.() -> Unit): HtmlColGroup {
    return append(*classes, block = block)
}

class HtmlWell(context: TagContext) : Tag(context, "well")

fun Tag.well(vararg classes: String, block: HtmlWell.() -> Unit): HtmlWell {
    return append(*classes, block = block)
}

class HtmlStrong(context: TagContext) : Tag(context, "strong")

fun Tag.strong(vararg classes: String, block: HtmlStrong.() -> Unit): HtmlStrong {
    return append(*classes, block = block)
}

class HtmlFont(context: TagContext) : Tag(context, "font")

fun Tag.font(vararg classes: String, block: HtmlFont.() -> Unit): HtmlFont {
    return append(*classes, block = block)
}

fun Tag.font(size: Int, color: String, block: TagBlock): HtmlFont {
    return font {
        "size" attr size.toString()
        "color" attr color
        this.block()
    }
}

class HtmlSmall(context: TagContext) : Tag(context, "small")

fun Tag.small(vararg classes: String, block: HtmlSmall.() -> Unit): HtmlSmall {
    return append(*classes, block = block)
}

class HtmlDatalist(context: TagContext) : Tag(context, "datalist")

fun Tag.datalist(vararg classes: String, block: HtmlDatalist.() -> Unit): HtmlDatalist {
    return append(*classes, block = block)
}

class HtmlFooter(context: TagContext) : Tag(context, "footer")

fun Tag.footer(vararg classes: String, block: HtmlFooter.() -> Unit): HtmlFooter {
    return append(*classes, block = block)
}

class HtmlArtical(context: TagContext) : Tag(context, "article")

fun Tag.article(vararg classes: String, block: HtmlArtical.() -> Unit): HtmlArtical {
    return append(*classes, block = block)
}

class HtmlStyle(context: TagContext) : Tag(context, "style")

fun Tag.style(block: () -> String): HtmlStyle {
    return append {
        unsafe(block)
    }
}

class HtmlBr(context: TagContext) : Tag(context, "br")

fun Tag.br(): HtmlBr {
    return append(block = {})
}
