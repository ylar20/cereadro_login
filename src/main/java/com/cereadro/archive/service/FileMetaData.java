package com.cereadro.archive.service;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by ylaraasmae on 18/09/16.
 */
public class FileMetadata implements Serializable {

    @Setter
    @Getter
    private Long id;

    @NotNull
    @Size(min = 4, max = 50)
    @Setter
    @Getter
    private String fileName;

    @NotNull
    @Setter
    @Getter
    private LocalDateTime createdDtime;


}
