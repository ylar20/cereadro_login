package com.cereadro.archive.service;

import com.cereadro.archive.dao.IDocumentDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Service
public class ArchiveService implements IArchiveService, Serializable {

    private static final long serialVersionUID = 8119784722798361327L;
    
    @Resource
    private IDocumentDao documentDao;

    @Override
    public DocumentMetadata save(Document document) {
        documentDao.insert(document);
        return document.getMetadata();
    }

    @Override
    public List<DocumentMetadata> findDocuments(String personName, Date date) {
        return documentDao.findByPersonNameDate(personName, date);
    }

    @Override
    public byte[] getDocumentFile(String id) {
        Document document = documentDao.load(id);
        if(document!=null) {
            return document.getFileData();
        } else {
            return null;
        }
    }


}
