package com.tienda.tiendavirtual.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.tienda.tiendavirtual.model.DetallePedido;
import com.tienda.tiendavirtual.model.Factura;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] generarPdf(Factura factura) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        // Encabezado de la empresa
        doc.add(new Paragraph("TIENDA VIRTUAL S.A.")
                .setFontSize(18).setBold().setTextAlignment(TextAlignment.CENTER));
        doc.add(new Paragraph("RUC: 20123456789")
                .setFontSize(10).setTextAlignment(TextAlignment.CENTER));
        doc.add(new Paragraph("FACTURA ELECTRÓNICA")
                .setFontSize(14).setBold().setTextAlignment(TextAlignment.CENTER));
        doc.add(new Paragraph(factura.getNumero())
                .setFontSize(12).setBold().setTextAlignment(TextAlignment.CENTER));
        doc.add(new Paragraph("Fecha: " + factura.getFecha().format(FORMATO_FECHA))
                .setFontSize(10));

        doc.add(new Paragraph("\n"));

        // Datos del cliente
        var cliente = factura.getPedido().getUsuario();
        doc.add(new Paragraph("Cliente: " + cliente.getNombre() + " " + cliente.getApellido()));
        doc.add(new Paragraph("Email: " + cliente.getEmail()));
        if (cliente.getDireccion() != null) {
            doc.add(new Paragraph("Dirección: " + cliente.getDireccion()));
        }

        doc.add(new Paragraph("\n"));

        // Tabla de productos
        Table tabla = new Table(new float[]{3, 1, 1, 1});
        tabla.setWidth(UnitValue.createPercentValue(100));

        tabla.addHeaderCell(new Cell().add(new Paragraph("Producto").setBold()));
        tabla.addHeaderCell(new Cell().add(new Paragraph("Cant.").setBold()));
        tabla.addHeaderCell(new Cell().add(new Paragraph("P. Unit.").setBold()));
        tabla.addHeaderCell(new Cell().add(new Paragraph("Subtotal").setBold()));

        for (DetallePedido detalle : factura.getPedido().getDetalles()) {
            tabla.addCell(detalle.getProducto().getNombre());
            tabla.addCell(String.valueOf(detalle.getCantidad()));
            tabla.addCell("S/. " + String.format("%.2f", detalle.getPrecioUnitario()));
            tabla.addCell("S/. " + String.format("%.2f", detalle.getSubtotal()));
        }

        doc.add(tabla);
        doc.add(new Paragraph("\n"));

        // Totales alineados a la derecha
        doc.add(new Paragraph("Subtotal: S/. " + String.format("%.2f", factura.getSubtotal()))
                .setTextAlignment(TextAlignment.RIGHT));
        doc.add(new Paragraph("IGV (18%): S/. " + String.format("%.2f", factura.getIgv()))
                .setTextAlignment(TextAlignment.RIGHT));
        doc.add(new Paragraph("TOTAL: S/. " + String.format("%.2f", factura.getTotal()))
                .setBold().setFontSize(14).setTextAlignment(TextAlignment.RIGHT));

        doc.close();
        return baos.toByteArray();
    }
}