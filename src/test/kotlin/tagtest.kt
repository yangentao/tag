import io.github.yangentao.tag.html.HtmlDoc
import io.github.yangentao.tag.html.base
import io.github.yangentao.tag.html.body
import io.github.yangentao.tag.html.div
import io.github.yangentao.tag.html.h1
import io.github.yangentao.tag.html.head
import io.github.yangentao.tag.html.header
import io.github.yangentao.tag.printX
import io.github.yangentao.tag.html.title

fun main() {
    val h = testHtml()
    printX(h.toHtml())
}

fun testHtml(): HtmlDoc {
    return HtmlDoc().apply {
        head {
            title("Title")
        }
        body {
            header {

            }
            base {

            }
            header {

            }
            div {
                h1 {
                    +"Hello Entao"
                }
            }
        }
    }
}