package com.kunotice.kunotice.notice.service

import com.kunotice.kunotice.common.service.ApiService
import com.kunotice.kunotice.notice.entity.Notice
import com.kunotice.kunotice.notice.enum.NoticeKind
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.springframework.stereotype.Service

@Service
class KuNoticeService(
    private val apiService: ApiService
) {
    val queries = arrayOf(
        "?forum=notice&cat=0000300001",
        "?forum=11688412",
        "?forum=11731332",
        "?forum=notice&cat=0000300002",
        "?forum=notice&cat=0000300003",
        "?forum=65659&cat=0010300001",
        "?forum=notice&cat=0000300006",
        "?forum=11869309"
    )

    fun crawlAllKuNotice(latestKuNotice: Notice?): List<Notice> {
        val notices = HashSet<Notice>()
        if (latestKuNotice != null) notices.add(latestKuNotice)

        for (query in queries) {
            val baseUrl = "https://www.konkuk.ac.kr/do/MessageBoard/ArticleList.do"
            run outForEach@{
                generateSequence(0) { it + 1 }.forEach { page ->
                    val response = apiService.get(baseUrl, query + pageQuery(page))
                    val html = response.body?.string() ?: return@outForEach
                    for (parsedNotice in parseNoticeList(html)) if (!notices.add(parsedNotice)) return@outForEach
                }
            }
        }

        notices.remove(latestKuNotice)
        return notices.toList()
    }

    private fun parseNoticeList(html: String): List<Notice> {
        val doc = Jsoup.parse(html)
        val noticeTables = doc.select("tbody > tr")
        return noticeTables.mapNotNull { parseNotice(it) }
    }

    private fun parseNotice(element: Element?): Notice? {
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
            kind = NoticeKind.KU_NOTICE
        )
    }

    private fun pageQuery(page: Int): String {
        return "&p=$page"
    }
}