package com.easytransfer;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.*;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class PdfGenerator {

    public static File generateVoucherPdf(Context context, Voucher voucher, String[] companyInfo) {
        try {
            File file = new File(context.getExternalFilesDir(null), "voucher_" + voucher.getName() + ".pdf");
            PdfWriter writer = new PdfWriter(new FileOutputStream(file));
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Προσθήκη λογοτύπου (αν υπάρχει)
            SharedPreferences prefs = context.getSharedPreferences("company_info", Context.MODE_PRIVATE);
            String logoUriStr = prefs.getString("logo_uri", null);
            if (logoUriStr != null) {
                try {
                    Uri logoUri = Uri.parse(logoUriStr);
                    InputStream inputStream = context.getContentResolver().openInputStream(logoUri);
                    if (inputStream != null) {
                        byte[] logoBytes = new byte[inputStream.available()];
                        inputStream.read(logoBytes);
                        inputStream.close();

                        ImageData imageData = ImageDataFactory.create(logoBytes);
                        Image logo = new Image(imageData)
                                .scaleToFit(100, 100)
                                .setHorizontalAlignment(HorizontalAlignment.CENTER);
                        document.add(logo);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Τίτλος
            Paragraph title = new Paragraph("VOUCHER ΜΕΤΑΦΟΡΑΣ")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setFontSize(18)
                    .setMarginBottom(20);
            document.add(title);

            // Πίνακας με στοιχεία πελάτη
            Table table = new Table(UnitValue.createPercentArray(new float[]{3, 7}))
                    .useAllAvailableWidth()
                    .setBorder(new SolidBorder(ColorConstants.GRAY, 1));

            table.addCell(new Cell().add("Όνομα").setBold());
            table.addCell(voucher.getName());
            table.addCell(new Cell().add("Email").setBold());
            table.addCell(voucher.getEmail());
            table.addCell(new Cell().add("Ενήλικες").setBold());
            table.addCell(String.valueOf(voucher.getAdults()));
            table.addCell(new Cell().add("Παιδιά").setBold());
            table.addCell(String.valueOf(voucher.getChildren()));
            table.addCell(new Cell().add("Τύπος Μεταφοράς").setBold());
            table.addCell(voucher.getType());
            table.addCell(new Cell().add("Ημερομηνία").setBold());
            table.addCell(voucher.getDate());
            table.addCell(new Cell().add("Ώρα").setBold());
            table.addCell(voucher.getTime());

            document.add(table);
            document.add(new Paragraph("\n"));

            // Διαχωριστικό
            document.add(new LineSeparator());

            // Εταιρικά στοιχεία
            Paragraph company = new Paragraph()
                    .add("Εταιρεία: " + companyInfo[0] + "\n")
                    .add("Email: " + companyInfo[1] + " | Τηλ: " + companyInfo[2] + "\n")
                    .add("ΑΦΜ: " + companyInfo[3] + " | ΔΟΥ: " + companyInfo[4])
                    .setFontSize(10)
                    .setMarginTop(10);
            document.add(company);

            // Υπογραφή / Ευχαριστήριο
            document.add(new Paragraph("\n"));
            document.add(new LineSeparator());
            document.add(new Paragraph("Σας ευχαριστούμε για την προτίμησή σας!")
                    .setItalic()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(11));

            document.close();
            return file;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
