package com.cereadro.archive.rest;

import com.cereadro.archive.service.ArchiveService;
import com.cereadro.archive.service.Document;
import com.cereadro.archive.service.DocumentMetadata;
import org.apache.log4j.Logger;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping(value = "/archive")
public class ArchiveController {

    private static final Logger LOG = Logger.getLogger(ArchiveController.class);
    
    @Resource
    ArchiveService archiveService;

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public @ResponseBody
    DocumentMetadata handleFileUpload(
            @RequestParam(value="file", required=true) MultipartFile file ,
            @RequestParam(value="person", required=true) String person,
            @RequestParam(value="date", required=true) @DateTimeFormat(pattern="yyyy-MM-dd") Date date) {
        
        try {
            Document document = new Document(file.getBytes(), file.getOriginalFilename(), date, person );
            archiveService.save(document);
            return document.getMetadata();
        } catch (RuntimeException e) {
            LOG.error("Error while uploading.", e);
            throw e;
        } catch (Exception e) {
            LOG.error("Error while uploading.", e);
            throw new RuntimeException(e);
        }      
    }

    @RequestMapping(value = "/documents", method = RequestMethod.GET)
    public HttpEntity<List<DocumentMetadata>> findDocument(
            @RequestParam(value="person", required=false) String person,
            @RequestParam(value="date", required=false) @DateTimeFormat(pattern="yyyy-MM-dd") Date date) {
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity<>(archiveService.findDocuments(person,date), httpHeaders,HttpStatus.OK);
    }

    @RequestMapping(value = "/document/{id}", method = RequestMethod.GET)
    public HttpEntity<byte[]> getDocument(@PathVariable String id) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(archiveService.getDocumentFile(id), httpHeaders, HttpStatus.OK);
    }

}
