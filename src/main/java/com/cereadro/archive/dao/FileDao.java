package com.cereadro.archive.dao;

import com.cereadro.archive.service.File;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by ylaraasmae on 28/09/16.
 */
public interface FileDao extends CrudRepository<File, Long> {

    File findByFileName(String fileName);

    File findFileById(Long id);

    List<File> findFileByUserId(Long userId);

}
