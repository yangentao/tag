import io.github.yangentao.tag.HtmlDoc
import io.github.yangentao.tag.base
import io.github.yangentao.tag.body
import io.github.yangentao.tag.div
import io.github.yangentao.tag.h1
import io.github.yangentao.tag.head
import io.github.yangentao.tag.header
import io.github.yangentao.tag.printX
import io.github.yangentao.tag.title

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