package com.example.view;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

public void genererPDF() {
    try {
        String filePath = "bulletin_etudiant.pdf";

        PdfWriter writer = new PdfWriter(filePath);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Exemple contenu
        document.add(new Paragraph("Bulletin de l'étudiant"));
        document.add(new Paragraph("Nom: Dupont"));
        document.add(new Paragraph("Math: 15/20"));
        document.add(new Paragraph("Physique: 14/20"));

        document.close();

        System.out.println("PDF généré !");
    } catch (Exception e) {
        e.printStackTrace();
    }
}
