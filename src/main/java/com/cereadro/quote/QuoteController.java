package com.cereadro.quote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Ylar on 01/11/2017.
 */
@RestController
public class QuoteController {

    @Autowired
    private QuoteService quoteService;


    @RequestMapping(value="/quote/add", method=RequestMethod.POST)
    public ResponseEntity<Void> addQuote(@RequestBody Quote quote){

        //debug log, to be removed
        System.out.println("Creating quote with id: " + quote.getId());
        quoteService.save(quote);
        return return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
