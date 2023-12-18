package com.kunotice.kunotice.common.schedule

import com.kunotice.kunotice.notice.service.NoticeService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class Scheduler(
    private val noticeService: NoticeService
) {
    @Scheduled(cron = "0 0 * * * *", zone = "Asia/Seoul")
    fun updateCompany() {
        noticeService.crawlAllKuNotice()
    }
}