package com.kunotice.kunotice.notice.service

import com.kunotice.kunotice.common.service.ApiService
import com.kunotice.kunotice.notice.entity.Notice
import com.kunotice.kunotice.notice.enum.NoticeKind
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.springframework.stereotype.Service

@Service
class InnovationSupportNoticeService(
    private val apiService: ApiService
) {
    fun crawlAllInnovationSupportNotice(innovationSupportNotices: List<Notice>): List<Notice> {
        val existNotices = HashSet<Notice>(innovationSupportNotices)
        val crawledNotices = HashSet<Notice>()

        val baseUrl =
            "http://ui.konkuk.ac.kr/web/community/notice.php"
        run outForEach@{
            generateSequence(1) { it + 1 }.forEach { page ->
                try {
                    val response = apiService.get(baseUrl, pageQuery(page))
                    val html = response.body?.string() ?: return@outForEach
                    val parsedNotices = parseNoticeList(html)

                    if (parsedNotices.isEmpty()) return@outForEach
                    for (parsedNotice in parsedNotices) {
                        if (existNotices.contains(parsedNotice)) return@outForEach
                        if (!crawledNotices.add(parsedNotice)) return@outForEach
                    }
                } catch (e: Exception) {
                    println(e)
                    return@outForEach
                }
            }
        }

        return crawledNotices.toList()
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
        val url = "http://ui.konkuk.ac.kr${noticeTag.attr("href")}"
        val id = "no=(\\d+)".toRegex().find(url)?.destructured?.component1() ?: title
        val isImportant = element.selectFirst("td.w_cell > span.ico") != null
        return Notice(
            noticeId = id,
            title = title,
            url = url,
            isImportant = isImportant,
            kind = NoticeKind.INNOVATION_SUPPORT_NOTICE
        )
    }

    private fun pageQuery(page: Int): String {
        return "&page=$page"
    }
}