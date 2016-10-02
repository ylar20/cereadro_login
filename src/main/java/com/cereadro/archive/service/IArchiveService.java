package com.cereadro.archive.service;

import java.util.Date;
import java.util.List;

public interface IArchiveService {

    DocumentMetadata save(DocumentFile document);

    List<DocumentMetadata> findDocuments(String personName, Date date);

    byte[] getDocumentFile(String id);
}
