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
    fun sendAll(kuNotices: List<Notice>) {
        val message = javaMailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true, "UTF-8")

        val kuText =
            "<H2>건국대학교 공지사항</H2>" + kuNotices.joinToString(separator = "<br><br>") {
                "<a href=\"${it.url}\">${it.title}</a>"
            }

        for (email in emailRepository.findAll()) {
            helper.setTo(email.address)
            helper.setSubject("새로운 공지사항이 ${kuNotices.count()}개 등록되었습니다.")
            helper.setText(kuText, true)
            javaMailSender.send(message)
        }
    }

    fun saveEmail(email: Email) {
        emailRepository.save(email)
    }
}