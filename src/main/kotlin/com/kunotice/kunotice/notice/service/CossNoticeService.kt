package com.kunotice.kunotice.notice.service

import com.kunotice.kunotice.common.service.ApiService
import com.kunotice.kunotice.notice.entity.Notice
import com.kunotice.kunotice.notice.enum.NoticeKind
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.springframework.stereotype.Service

@Service
class CossNoticeService(
    private val apiService: ApiService
) {
    fun crawlAllCossNotice(cossNotices: List<Notice>): List<Notice> {
        val existNotices = HashSet<Notice>(cossNotices)
        val crawledNotices = HashSet<Notice>()

        val baseUrl =
            "https://www.xr.ac.kr/ko/board/notice/list/"
        run outForEach@{
            generateSequence(1) { it + 1 }.forEach { page ->
                try {
                    val response = apiService.get("$baseUrl$page")
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
        val noticeTables = doc.select("ul.black > li.tbody")
        return noticeTables.mapNotNull { parseNotice(it) }
    }

    private fun parseNotice(element: Element?): Notice? {
        if (element == null) return null
        val noticeTag = element.selectFirst("span.title > a") ?: return null

        val title = noticeTag.text()
        val url = "https://www.xr.ac.kr${noticeTag.attr("href")}"
        val id = "view/(\\d+)".toRegex().find(url)?.destructured?.component1() ?: title
        val isImportant = element.selectFirst("span.loopnum")?.text() == "공지"
        val date = element.selectFirst("span.reg_date > time")?.text() ?: ""
        return Notice(
            noticeId = id,
            title = title,
            url = url,
            isImportant = isImportant,
            kind = NoticeKind.COSS_NOTICE,
            date = date
        )
    }
}