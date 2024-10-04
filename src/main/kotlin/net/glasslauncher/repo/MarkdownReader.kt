package net.glasslauncher.repo

import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

class MarkdownReader {
    companion object {
        private val parser = Parser.builder().
        extensions(
            listOf(
                AutolinkExtension.create(),
                StrikethroughExtension.builder().requireTwoTildes(true).build(),
                TablesExtension.create(),
                HeadingAnchorExtension.builder().idPrefix("mod_").build(), // TODO: Document this shit for users
                )
        ).build()
        private val renderer = HtmlRenderer.builder()
            .softbreak("<br/>")
            .build()

        fun renderMarkdown(md: String): String {
            return renderer.render(parser.parse(md))
        }
    }
}

fun String.renderToHtml(): String {
    return MarkdownReader.renderMarkdown(this)
}