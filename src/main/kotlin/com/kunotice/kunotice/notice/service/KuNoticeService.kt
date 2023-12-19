package com.kunotice.kunotice.notice.service

import com.kunotice.kunotice.common.service.ApiService
import com.kunotice.kunotice.notice.entity.Notice
import com.kunotice.kunotice.notice.enum.NoticeKind
import org.springframework.stereotype.Service

@Service
class KuNoticeService(
    private val apiService: ApiService
) : BaseNoticeService() {
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

    fun crawlAllKuNotice(kuNotices: List<Notice>): List<Notice> {
        val existNotices = HashSet<Notice>(kuNotices)
        val crawledNotices = HashSet<Notice>()

        for (query in queries) {
            val baseUrl = "https://www.konkuk.ac.kr/do/MessageBoard/ArticleList.do"
            run outForEach@{
                generateSequence(0) { it + 1 }.forEach { page ->
                    val response = apiService.get(baseUrl, query + pageQuery(page))
                    val html = response.body?.string() ?: return@outForEach
                    for (parsedNotice in parseNoticeList(html, NoticeKind.KU_NOTICE)) {
                        if (existNotices.contains(parsedNotice)) return@outForEach
                        if (!crawledNotices.add(parsedNotice)) return@outForEach
                    }
                }
            }
        }

        return crawledNotices.toList()
    }
}