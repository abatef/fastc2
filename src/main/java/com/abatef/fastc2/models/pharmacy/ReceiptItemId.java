package com.abatef.fastc2.models.pharmacy;

import jakarta.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Embeddable
public class ReceiptItemId implements Serializable {
    private static final long serialVersionUID = -4513323556034627185L;
}