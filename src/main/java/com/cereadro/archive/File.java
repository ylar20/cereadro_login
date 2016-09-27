package com.cereadro.archive;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Created by ylaraasmae on 18/09/16.
 */
@Entity
@Table(name = "file")
public class File {

    @Id
    @Setter
    @Getter
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Getter
    @Setter
    private Long userId;

    @NotNull
    @Size(min = 4, max = 50)
    @Setter
    @Getter
    private String fileName;

    @NotNull
    @Setter
    @Getter
    private Byte content;

    @NotNull
    @Setter
    @Getter
    private LocalDateTime createdDtime;


}
