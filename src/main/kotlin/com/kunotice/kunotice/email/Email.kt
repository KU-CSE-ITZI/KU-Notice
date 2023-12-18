package com.kunotice.kunotice.email

import com.kunotice.kunotice.common.entity.BaseEntity
import jakarta.persistence.Entity

@Entity
class Email(
    var address: String
) : BaseEntity()