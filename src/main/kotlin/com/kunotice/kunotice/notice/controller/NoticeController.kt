package com.kunotice.kunotice.notice.controller

import com.kunotice.kunotice.notice.service.NoticeService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class NoticeController(
    private val noticeService: NoticeService
) {
    @GetMapping("/notice")
    fun crawlAllKuNotice() {
        noticeService.crawlAllKuNotice()
    }

    @GetMapping("/reset")
    fun resetAllNotices() {
        noticeService.resetAllNotices()
    }

    @GetMapping("/saved-notices")
    fun sendSavedNotices() {
        noticeService.sendSavedNotices()
    }
}