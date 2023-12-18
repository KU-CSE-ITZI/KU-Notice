package com.kunotice.kunotice.notice.entity

import com.kunotice.kunotice.common.entity.BaseEntity
import com.kunotice.kunotice.notice.enum.NoticeKind
import jakarta.persistence.Entity

@Entity
class Notice(
    var noticeId: String,
    var title: String,
    var url: String,
    var isImportant: Boolean,
    var kind: NoticeKind
) : BaseEntity() {
    override fun hashCode(): Int {
        return noticeId.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Notice) return false
        return noticeId == other.noticeId
    }
}