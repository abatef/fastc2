package com.abatef.fastc2.security.auth;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "refresh_token")
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ColumnDefault("nextval('refresh_token_id_seq')")
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "token", nullable = false, length = Integer.MAX_VALUE)
    private String token;

    @Column(name = "username", length = Integer.MAX_VALUE)
    private String username;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "expiry_date")
    private Instant expiryDate;

    @ColumnDefault("false")
    @Column(name = "used")
    private Boolean used;
}
