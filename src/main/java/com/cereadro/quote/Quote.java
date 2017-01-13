package com.cereadro.quote;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Id;
import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;
import javax.persistence.ForeignKey;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Created by Ylar on 01/11/2017.
 */
@Entity
@Table(name = "bt.quote", uniqueConstraints = @UniqueConstraint(columnNames = { "id" }))
public class Quote {

    @Id
    @Setter
    @Getter
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Getter
    @Setter
    private Long fileId;

    @NotNull
    @Getter
    @Setter
    private Long userId;

    @NotNull
    @Size(max = 2000)
    @Getter
    @Setter
    private String content;

    @NotNull
    @Setter
    @Getter
    private LocalDateTime createdDtime;
}
