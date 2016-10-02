package com.cereadro.archive.service;

import com.cereadro.archive.dao.FileDao;
import com.cereadro.archive.dao.IDocumentDao;
import com.cereadro.user.User;
import com.snowtide.PDF;
import com.snowtide.pdf.Document;
import com.snowtide.pdf.VisualOutputTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pdfts.examples.GoogleHTMLOutputHandler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ArchiveService implements IArchiveService, Serializable {

    private static final long serialVersionUID = 8119784722798361327L;

    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    @Autowired
    private IDocumentDao documentDao;

    @Autowired
    private FileDao fileDao;

    @Override
    public DocumentMetadata save(DocumentFile document) {
        documentDao.insert(document);
        return document.getMetadata();
    }

    @Override
    public List<DocumentMetadata> findDocuments(String personName, Date date) {
        return documentDao.findByPersonNameDate(personName, date);
    }

    @Override
    public byte[] getDocumentFile(String id) {
        DocumentFile document = documentDao.load(id);
        if(document!=null) {
            return document.getFileData();
        }
        return null;
    }

    public void saveFile(MultipartFile uploadedFile) throws IOException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getDetails();
        File file = new File();
        file.setUserId(user.getId());
        file.setContent(uploadedFile.getBytes());
        file.setFileName(uploadedFile.getOriginalFilename());
        file.setCreatedDtime(LocalDateTime.now());
        fileDao.save(file);
    }

    public File getFileByFilename(String filename) {
        return fileDao.findByFileName(filename);
    }

    public byte[] getFileByteArrayByFileId(Long id) {
        File file = fileDao.findFileById(id);
        parsePdfFile(file);
        if(file != null) {
            return file.getContent();
        }
        return null;
    }

    public List<FileMetadata> findFileListByUserId(Long userId) {
        List<File> files = fileDao.findFileByUserId(userId);
        return getMetadataForFiles(files);
    }

    private List<FileMetadata> getMetadataForFiles(List<File> files) {
        List<FileMetadata> metadataList = new ArrayList<>();
        files.forEach(file -> {
            FileMetadata fileMetadata = new FileMetadata();
            fileMetadata.setId(file.getId());
            fileMetadata.setCreatedDtime(file.getCreatedDtime().format(dateFormatter));
            fileMetadata.setFileName(file.getFileName());
            metadataList.add(fileMetadata);
        });
        return metadataList;
    }

    public String parsePdfFile(File file) {
        String content = "";
        try {
            java.io.File tempFile = java.io.File.createTempFile(file.getFileName(), null, null);
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(file.getContent());
            Document document = PDF.open(tempFile);
            StringWriter buffer = new StringWriter();
            GoogleHTMLOutputHandler googleHTMLOutputHandler = new GoogleHTMLOutputHandler();
            document.pipe(new VisualOutputTarget(buffer));
            content = buffer.toString();
            org.w3c.dom.Document doc = googleHTMLOutputHandler.getHTMLDocument();
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

}
