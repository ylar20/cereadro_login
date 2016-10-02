package com.cereadro.archive.service;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Properties;

public class DocumentFile extends DocumentMetadata implements Serializable {

    private static final long serialVersionUID = 2004955454853853315L;

    @Getter
    @Setter
    private byte[] fileData;
    
    public DocumentFile(byte[] fileData, String fileName, LocalDate documentDate, String personName) {
        super(fileName, documentDate, personName);
        this.fileData = fileData;
    }

    public DocumentFile(Properties properties) {
        super(properties);
    }
    
    public DocumentFile(DocumentMetadata metadata) {
        super(metadata.getUuid(), metadata.getFileName(), metadata.getDocumentDate(), metadata.getPersonName());
    }

    public DocumentMetadata getMetadata() {
        return new DocumentMetadata(getUuid(), getFileName(), getDocumentDate(), getPersonName());
    }
    
}
