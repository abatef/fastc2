package com.abatef.fastc2.dtos.drug;

import com.abatef.fastc2.models.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DrugInfo {
    private Integer id;
    private String name;
    private String form;
    private Integer createdBy;
    private Instant createdAt;
    private Instant updatedAt;
}
