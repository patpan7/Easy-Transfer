package com.easytransfer;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.*;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class PdfGenerator {

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static File generateVoucherPdf(Context context, Voucher voucher) {
        try {
            File file = new File(context.getExternalFilesDir(null), "voucher_" + voucher.getName() + ".pdf");
            PdfWriter writer = new PdfWriter(new FileOutputStream(file));
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Ενσωματωμένη γραμματοσειρά (για ελληνικά)
            InputStream fontStream = context.getResources().openRawResource(R.raw.arial);
            PdfFont font = PdfFontFactory.createFont(
                    fontStream.readAllBytes(),
                    PdfEncodings.IDENTITY_H,
                    PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED
            );
            document.setFont(font);

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
                        document.add(new Paragraph("\n"));
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
                    .useAllAvailableWidth();

            table.addCell(new Cell().add(new Paragraph("Όνομα").setBold()));
            table.addCell(new Cell().add(new Paragraph(voucher.getName())));
            table.addCell(new Cell().add(new Paragraph("Email").setBold()));
            table.addCell(new Cell().add(new Paragraph(voucher.getEmail())));
            table.addCell(new Cell().add(new Paragraph("Ενήλικες").setBold()));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(voucher.getAdults()))));
            table.addCell(new Cell().add(new Paragraph("Παιδιά").setBold()));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(voucher.getChildren()))));
            table.addCell(new Cell().add(new Paragraph("Τύπος Μεταφοράς").setBold()));
            table.addCell(new Cell().add(new Paragraph(voucher.getType())));
            table.addCell(new Cell().add(new Paragraph("Ημερομηνία").setBold()));
            table.addCell(new Cell().add(new Paragraph(voucher.getDate())));
            table.addCell(new Cell().add(new Paragraph("Ώρα").setBold()));
            table.addCell(new Cell().add(new Paragraph(voucher.getTime())));

            document.add(table);
            document.add(new Paragraph("\n"));

            // Διαχωριστικό
            LineSeparator separator = new LineSeparator(new SolidLine());
            separator.setMarginTop(10);
            separator.setMarginBottom(10);
            document.add(separator);

            // Εταιρικά στοιχεία
            Paragraph company = new Paragraph()
                    .add("Εταιρεία: ΓΚΟΥΜΑΣ ΔΗΜΗΤΡΙΟΣ\n")
                    .add("Email: et@dgou.gr | Τηλ: 2241036750\n")
                    .add("ΑΦΜ: 054909468 | ΔΟΥ: ΡΟΔΟΥ")
                    .setFontSize(10)
                    .setMarginTop(10)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(company);

            // Υπογραφή / Ευχαριστήριο
            document.add(new Paragraph("\n"));
            document.add(new LineSeparator(new SolidLine()));
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