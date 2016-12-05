package com.cereadro.archive.rest;

import com.cereadro.archive.service.ArchiveService;
import com.cereadro.archive.service.DocumentFile;
import com.cereadro.archive.service.DocumentMetadata;
import com.cereadro.archive.service.FileMetadata;
import com.cereadro.user.User;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.fit.pdfdom.PDFDomTree;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping(value = "/archive")
public class ArchiveController {

    private static final Logger LOG = Logger.getLogger(ArchiveController.class);
    
    @Resource
    ArchiveService archiveService;

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public @ResponseBody DocumentMetadata handleFileUpload(
            @RequestParam(value="file", required=true) MultipartFile file) {
        
        try {
            if (!"application/pdf".equals(file.getContentType())) {
                throw new Exception("Wrong file type. Only PDFs currently allowed.");
            }
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getDetails();
            DocumentFile document = new DocumentFile(file.getBytes(), file.getOriginalFilename(), LocalDate.now(), user.getFirstName());
            archiveService.saveFile(file);
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

    @RequestMapping(value = "/files", method = RequestMethod.GET)
    public HttpEntity<List<FileMetadata>> findAllFilesForUser() {
        HttpHeaders httpHeaders = new HttpHeaders();
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getDetails();
        return new ResponseEntity<>(archiveService.findFileListByUserId(user.getId()), httpHeaders,HttpStatus.OK);
    }

    @RequestMapping(value = "/document/{id}", method = RequestMethod.GET)
    public HttpEntity<byte[]> getDocument(@PathVariable String id) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(archiveService.getDocumentFile(id), httpHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/file/{id}", method = RequestMethod.GET)
    public HttpEntity<String> getFile(@PathVariable Long id) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.TEXT_HTML);
        return new ResponseEntity<>(archiveService.getFileByteArrayByFileId(id), httpHeaders, HttpStatus.OK);
    }

}
