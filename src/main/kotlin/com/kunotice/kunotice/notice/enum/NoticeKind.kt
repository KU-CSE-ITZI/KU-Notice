package com.kunotice.kunotice.notice.enum

enum class NoticeKind {
    KU_NOTICE,
    ENGINEERING_EDUCATION_NOTICE,
    KU_VOLUNTEER_NOTICE,
    INNOVATION_SUPPORT_NOTICE,
    COSS_NOTICE,
    KU_SCHOLARSHIP_NOTICE;

    companion object {
        private val titleMap = mapOf(
            KU_NOTICE to "건국대학교 공지사항",
            ENGINEERING_EDUCATION_NOTICE to "건국대학교 공학교육혁신사업단 공지사항",
            KU_VOLUNTEER_NOTICE to "건국대학교 사회봉사센터 공지사항",
            INNOVATION_SUPPORT_NOTICE to "건국대학교 대학혁신지원사업 공지사항",
            COSS_NOTICE to "실감미디어 혁신공유대학사업단 공지사항",
            KU_SCHOLARSHIP_NOTICE to "건국대학교 장학공지사항",
        )

        fun getTitle(kind: NoticeKind): String {
            return titleMap[kind] ?: ""
        }
    }
}

