package com.kunotice.kunotice.discord

import com.kunotice.kunotice.common.service.ApiService
import com.kunotice.kunotice.notice.entity.Notice
import com.kunotice.kunotice.notice.enum.NoticeKind
import okhttp3.FormBody
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class DiscordService(
    private val apiService: ApiService,
    @Value("\${discord.webhook-url}")
    private val discordWebhookUrl: String
) {
    fun sendAll(notices: MutableList<Notice>) {
        if (notices.isEmpty()) return

        val text = StringBuilder("# 새로운 공지사항이 ${notices.size}개 등록되었습니다.\n\n")
        notices
            .sortedByDescending { it.date }
            .groupBy { it.kind }
            .forEach { (kind, notices) ->
                text.append("\n\n## ${NoticeKind.getTitle(kind)}\n" + notices.joinToString(separator = "\n") {
                    "[${it.date}] ${if (it.isImportant) "[중요] " else ""} [${it.title}](${it.url})"
                })
            }

        val body = FormBody.Builder()
            .add("content", text.toString())
            .build()

        apiService.post(discordWebhookUrl, body)
    }
}