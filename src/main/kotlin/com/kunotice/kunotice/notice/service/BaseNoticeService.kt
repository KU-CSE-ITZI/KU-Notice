package com.kunotice.kunotice.notice.service

import com.kunotice.kunotice.notice.entity.Notice
import com.kunotice.kunotice.notice.enum.NoticeKind
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

open class BaseNoticeService {
    protected fun parseNoticeList(html: String, kind: NoticeKind): List<Notice> {
        val doc = Jsoup.parse(html)
        val noticeTables = doc.select("tbody > tr")
        return noticeTables.mapNotNull { parseNotice(it, kind) }
    }

    private fun parseNotice(element: Element?, kind: NoticeKind): Notice? {
        if (element == null) return null
        val noticeTag = element.selectFirst("td.subject > a") ?: return null

        val title = noticeTag.text()
        val url = "https://www.konkuk.ac.kr/do/MessageBoard/${noticeTag.attr("href")}"
        val id = url.split("id=").last()
        val isImportant = element.selectFirst("td > img") != null
        return Notice(
            noticeId = id,
            title = title,
            url = url,
            isImportant = isImportant,
            kind = kind
        )
    }

    protected fun pageQuery(page: Int): String {
        return "&p=$page"
    }
}