package com.cereadro.quote;

import com.cereadro.archive.service.File;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Ylar on 01/11/2017.
 */
public interface QuoteDao extends CrudRepository<Quote, Long> {;

    List<Quote> findQuoteByFileId(Long id);

    List<File> findFileByUserId(Long userId);
}
