package com.cereadro.archive.service;

import com.cereadro.archive.dao.FileDao;
import com.cereadro.archive.dao.IDocumentDao;
import com.cereadro.user.User;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.io.RandomAccess;
import org.apache.pdfbox.io.ScratchFile;
import org.fit.pdfdom.PDFDomTreeConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import pdfts.examples.GoogleHTMLOutputHandler;
import org.fit.pdfdom.PDFDomTree;
import org.fit.pdfdom.PDFToHTML;
import org.apache.pdfbox.pdmodel.PDDocument;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
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

    public String getFileByteArrayByFileId(Long id) {
        File file = fileDao.findFileById(id);
        if(file != null) {
            return parsePdfFile(file);
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
        PDDocument pdDocument = null;
        try {
            pdDocument = PDDocument.load(new ByteArrayInputStream(file.getContent()));
            PDFDomTree parser = new PDFDomTree(PDFDomTreeConfig.createDefaultConfig());
            Document pdfDoc =  parser.createDOM(pdDocument);
            content = getStringFromDocument(pdfDoc);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException pe) {
            pe.printStackTrace();
        }
        return content;
    }

    public String getStringFromDocument(Document doc)
    {
        try
        {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            return writer.toString();
        }
        catch(TransformerException ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

}
