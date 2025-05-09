package io.github.yangentao.tag.html

import kotlin.reflect.KClass

internal val htmlTagMap: Map<String, KClass<out HtmlTag>> = mapOf(
    "main" to HtmlMain::class,
    "header" to HtmlHeader::class,
    "head" to HtmlHead::class,
    "body" to HtmlBody::class,
    "div" to HtmlDiv::class,
    "a" to HtmlAnchor::class,
    "button" to HtmlButton::class,
    "link" to HtmlLink::class,
    "meta" to HtmlMeta::class,
    "select" to HtmlSelect::class,
    "option" to HtmlOption::class,
    "nav" to HtmlNav::class,
    "form" to HtmlForm::class,
    "script" to HtmlScript::class,
    "label" to HtmlLabel::class,
    "img" to HtmlImage::class,
    "span" to HtmlSpan::class,
    "hr" to HtmlHr::class,
    "pre" to HtmlPre::class,
    "code" to HtmlCode::class,
    "ol" to HtmlOl::class,
    "ul" to HtmlUl::class,
    "li" to HtmlLi::class,
    "h1" to HtmlH1::class,
    "h2" to HtmlH2::class,
    "h3" to HtmlH3::class,
    "h4" to HtmlH4::class,
    "h5" to HtmlH5::class,
    "h6" to HtmlH6::class,
    "p" to HtmlP::class,
    "dl" to HtmlDl::class,
    "dt" to HtmlDt::class,
    "dd" to HtmlDd::class,
    "table" to HtmlTable::class,
    "thead" to HtmlTHead::class,
    "tbody" to HtmlTBody::class,
    "th" to HtmlTh::class,
    "tr" to HtmlTr::class,
    "td" to HtmlTd::class,
    "col" to HtmlCol::class,
    "colgroup" to HtmlColGroup::class,
    "well" to HtmlWell::class,
    "small" to HtmlSmall::class,
    "font" to HtmlFont::class,
    "strong" to HtmlStrong::class,
    "textarea" to HtmlTextarea::class,
    "input" to HtmlInput::class,
    "datalist" to HtmlDatalist::class,
    "footer" to HtmlFooter::class,
    "article" to HtmlArtical::class,
    "base" to HtmlBase::class,
    "style" to HtmlStyle::class,
    "br" to HtmlBr::class,
)
