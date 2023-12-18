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
    fun sendAll(notices: List<Notice>) {
        val message = javaMailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true, "UTF-8")
        val text = notices.joinToString(separator = "<br><br>") {
            "<a href=\"${it.url}\">${it.title}</a>"
        }

        for (email in emailRepository.findAll()) {
            helper.setTo(email.address)
            helper.setSubject("새로운 공지사항이 ${notices.count()}개 등록되었습니다.")
            helper.setText(text, true)
            javaMailSender.send(message)
        }
    }

    fun saveEmail(email: Email) {
        emailRepository.save(email)
    }
}