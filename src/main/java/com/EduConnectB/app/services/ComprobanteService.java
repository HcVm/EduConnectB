package com.EduConnectB.app.services;

import com.EduConnectB.app.models.Membresia;
import com.EduConnectB.app.models.Pago;
import com.EduConnectB.app.models.Usuario;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
public class ComprobanteService {

    public byte[] generarComprobantePDF(Membresia membresia, Pago pago) throws DocumentException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        document.open();

        // Información del emisor osea nosotros xd
        Paragraph emisor = new Paragraph("EduConnect", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
        emisor.setAlignment(Element.ALIGN_CENTER);
        document.add(emisor);

        Usuario usuario = membresia.getUsuario();
        Paragraph cliente = new Paragraph("Cliente: " + usuario.getNombre(), FontFactory.getFont(FontFactory.HELVETICA, 12));
        document.add(cliente);
        Paragraph correo = new Paragraph("Correo electrónico: " + usuario.getCorreoElectronico(), FontFactory.getFont(FontFactory.HELVETICA, 12));
        document.add(correo);

        document.add(new Paragraph("Detalles de la membresía:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
        PdfPTable tablaMembresia = new PdfPTable(2);
        tablaMembresia.addCell("Tipo de membresía:");
        tablaMembresia.addCell(membresia.getTipoMembresia().toString());
        tablaMembresia.addCell("Fecha de inicio:");
        tablaMembresia.addCell(membresia.getFechaInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        tablaMembresia.addCell("Fecha de fin:");
        tablaMembresia.addCell(membresia.getFechaFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        document.add(tablaMembresia);

        document.add(new Paragraph("Detalles del pago:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
        PdfPTable tablaPago = new PdfPTable(2);
        tablaPago.addCell("ID de pago:");
        tablaPago.addCell(pago.getIdPago().toString());
        tablaPago.addCell("Monto:");
        tablaPago.addCell(pago.getMonto().toString());
        tablaPago.addCell("Fecha:");
        tablaPago.addCell(pago.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        document.add(tablaPago);

        document.close();
        return outputStream.toByteArray();
    }
}
