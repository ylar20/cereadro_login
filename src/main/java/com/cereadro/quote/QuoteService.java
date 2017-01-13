package com.cereadro.quote;

import com.cereadro.archive.service.DocumentFile;
import com.cereadro.archive.service.DocumentMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Created by Ylar on 01/11/2017.
 */
@Service
public class QuoteService {

    private static final long serialVersionUID = 8119784722798361327L;

    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Autowired
    private QuoteDao quoteDao;



    public Quote save(Quote quote) {
        return quoteDao.save(quote);
    }

    public void save(List<Quote> quoteList) {
        quoteDao.save(quoteList);
    }

    public void deleteQuote(Long id) {
        quoteDao.delete(id);
    }


}
