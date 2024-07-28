package com.EduConnectB.app.services;

import com.EduConnectB.app.dao.ArchivoAsesorRepository;
import com.EduConnectB.app.models.ArchivoAsesor;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Service
public class ArchivoAsesorService {

    @Autowired
    private ArchivoAsesorRepository archivoAsesorRepository;

    public byte[] descargarArchivoComoPDF(Integer archivoId) throws IOException, DocumentException {
        ArchivoAsesor archivo = archivoAsesorRepository.findById(archivoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Archivo no encontrado"));

        URL url = new URL(archivo.getRutaArchivo());
        InputStream inputStream = url.openStream();
        byte[] rawBytes = inputStream.readAllBytes();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
        document.open();

        PdfReader reader = new PdfReader(rawBytes);
        PdfImportedPage page;
        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            page = writer.getImportedPage(reader, i);
            Image image = Image.getInstance(page);
            document.add(image);
        }

        document.close();
        return outputStream.toByteArray();
    }
}

