package com.kunotice.kunotice.email

import com.kunotice.kunotice.notice.entity.Notice
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service

@Service
class EmailService(
    private val emailRepository: EmailRepository,
    private val javaMailSender: JavaMailSender
) {
    fun sendAll(kuNotices: List<Notice>, engineeringEducationNotices: List<Notice>) {
        val noticeCount = kuNotices.count() + engineeringEducationNotices.count()
        if (noticeCount == 0) return

        val message = javaMailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true, "UTF-8")

        val kuText =
            "<H2>건국대학교 공지사항</H2>" + kuNotices.joinToString(separator = "<br><br>") {
                "<a href=\"${it.url}\">${it.title}</a>"
            }

        val engineeringEducationText =
            "<H2>건국대학교 공학교육혁신사업단 공지사항</H2>" + engineeringEducationNotices.joinToString(separator = "<br><br>") {
                "<a href=\"${it.url}\">${it.title}</a>"
            }

        val text = "$kuText<br><br>$engineeringEducationText"

        for (email in emailRepository.findAll()) {
            helper.setTo(email.address)
            helper.setSubject("새로운 공지사항이 ${noticeCount}개 등록되었습니다.")
            helper.setText(text, true)
            javaMailSender.send(message)
        }
    }
}