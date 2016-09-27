package com.cereadro.archive.service;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.Properties;

public class Document extends DocumentMetadata implements Serializable {

    private static final long serialVersionUID = 2004955454853853315L;

    @Getter
    @Setter
    private byte[] fileData;
    
    public Document( byte[] fileData, String fileName, Date documentDate, String personName) {
        super(fileName, documentDate, personName);
        this.fileData = fileData;
    }

    public Document(Properties properties) {
        super(properties);
    }
    
    public Document(DocumentMetadata metadata) {
        super(metadata.getUuid(), metadata.getFileName(), metadata.getDocumentDate(), metadata.getPersonName());
    }

    public DocumentMetadata getMetadata() {
        return new DocumentMetadata(getUuid(), getFileName(), getDocumentDate(), getPersonName());
    }
    
}
