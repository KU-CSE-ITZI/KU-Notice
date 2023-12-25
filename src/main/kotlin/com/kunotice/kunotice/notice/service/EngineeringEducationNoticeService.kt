package com.kunotice.kunotice.notice.service

import com.kunotice.kunotice.common.service.ApiService
import com.kunotice.kunotice.notice.entity.Notice
import com.kunotice.kunotice.notice.enum.NoticeKind
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.springframework.stereotype.Service

@Service
class EngineeringEducationNoticeService(
    private val apiService: ApiService
) {
    fun crawlAllEngineeringEducationNotice(engineeringEducationNotices: List<Notice>): List<Notice> {
        val existNotices = HashSet<Notice>(engineeringEducationNotices)
        val crawledNotices = HashSet<Notice>()

        val baseUrl =
            "https://ceei.konkuk.ac.kr/noticeList.do?siteId=CEEI&boardSeq=1227&menuSeq=8579"
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
        val noticeTables = doc.select("tbody#noticeList > tr")
        noticeTables.addAll(doc.select("tbody#dispList > tr"))

        return noticeTables.mapNotNull { parseNotice(it) }
    }

    private fun parseNotice(element: Element?): Notice? {
        if (element == null) return null
        val noticeTag = element.selectFirst("td.subject > a") ?: return null

        val title = noticeTag.text()
        val id = noticeTag.attr("data-itsp-view-link")
        val url =
            "https://ceei.konkuk.ac.kr/noticeView.do?siteId=CEEI&boardSeq=1227&menuSeq=8579&seq=$id"
        val isImportant = element.selectFirst("td")?.text() == "[공지]"
        val date = element.select("td").eq(3).text()
        return Notice(
            noticeId = id,
            title = title,
            url = url,
            isImportant = isImportant,
            kind = NoticeKind.ENGINEERING_EDUCATION_NOTICE,
            date = date
        )
    }

    private fun pageQuery(page: Int): String {
        return "&pageNum=$page"
    }
}