package com.kunotice.kunotice.notice.service

import com.kunotice.kunotice.discord.DiscordService
import com.kunotice.kunotice.email.EmailService
import com.kunotice.kunotice.notice.entity.Notice
import com.kunotice.kunotice.notice.enum.NoticeKind
import com.kunotice.kunotice.notice.repository.NoticeRepository
import org.springframework.stereotype.Service

@Service
class NoticeService(
    private val noticeRepository: NoticeRepository,
    private val kuVolunteerNoticeService: KuVolunteerNoticeService,
    private val cossNoticeService: CossNoticeService,
    private val emailService: EmailService,
    private val discordService: DiscordService
) {
    fun crawlAllKuNotice() {
        val notices = mutableListOf<Notice>()

        val kuVolunteerNotices =
            noticeRepository.findAllByKind(NoticeKind.KU_VOLUNTEER_NOTICE)
        notices.addAll(kuVolunteerNoticeService.crawlAllKuVolunteerNotice(kuVolunteerNotices))

        val cossNotices = noticeRepository.findAllByKind(NoticeKind.COSS_NOTICE)
        notices.addAll(cossNoticeService.crawlAllCossNotice(cossNotices))

        emailService.sendAll(notices)
        discordService.sendAll(notices)

        noticeRepository.saveAll(notices)
    }

    fun resetAllNotices() {
        noticeRepository.deleteAll()
    }

    fun sendSavedNotices() {
        val notices = noticeRepository.findAll()
        emailService.sendAll(notices.toMutableList())
    }
}