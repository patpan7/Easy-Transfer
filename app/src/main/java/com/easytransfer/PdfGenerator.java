package com.easytransfer;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
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
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class PdfGenerator {

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static File generateVoucherPdf(Context context, Voucher voucher, Cursor settingsCursor) {
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

            // Λογότυπο (τοποθετείται στην κορυφή του εγγράφου)
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
                                .scaleToFit(120, 120) // μεγαλύτερη προσαρμοσμένη κλίμακα
                                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                                .setMarginTop(10)
                                .setMarginBottom(10);

                        document.add(logo);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Τίτλος
            Paragraph title = new Paragraph("Transfer Voucher")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setFontSize(18)
                    .setMarginBottom(15);
            document.add(title);

            String createdAt = convertDate(voucher.getCreatedAt());
            Paragraph dateParagraph = new Paragraph("Date: " + createdAt)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(10)
                    .setItalic()
                    .setMarginBottom(10);
            document.add(dateParagraph);

            // --- Εταιρικά Στοιχεία από Settings (αν υπάρχουν)
            if (settingsCursor != null && settingsCursor.moveToFirst()) {
                String compTitle = settingsCursor.getString(settingsCursor.getColumnIndexOrThrow("company_title"));
                String compName = settingsCursor.getString(settingsCursor.getColumnIndexOrThrow("company_name"));
                String afm = settingsCursor.getString(settingsCursor.getColumnIndexOrThrow("company_afm"));
                String address = settingsCursor.getString(settingsCursor.getColumnIndexOrThrow("company_address"));
                String city = settingsCursor.getString(settingsCursor.getColumnIndexOrThrow("company_city"));
                String postcode = settingsCursor.getString(settingsCursor.getColumnIndexOrThrow("company_postcode"));
                String email = settingsCursor.getString(settingsCursor.getColumnIndexOrThrow("company_email"));
                String mobile = settingsCursor.getString(settingsCursor.getColumnIndexOrThrow("company_mobile"));
                String phone = settingsCursor.getString(settingsCursor.getColumnIndexOrThrow("company_phone"));

                Paragraph companyParagraph = new Paragraph()
                        .setTextAlignment(TextAlignment.LEFT)
                        .setFontSize(12);

                if (compTitle != null && !compTitle.isEmpty())
                    companyParagraph.add(new Text(compTitle + "\n").setBold());

                if (compName != null && !compName.isEmpty())
                    companyParagraph.add(compName + "\n");

                if (address != null && !address.isEmpty())
                    companyParagraph.add(address + "\n");

                if ((postcode != null && !postcode.isEmpty()) || (city != null && !city.isEmpty()))
                    companyParagraph.add((postcode != null ? postcode + ", " : "") + (city != null ? city : "") + "\n");

                if ((mobile != null && !mobile.isEmpty()))
                    companyParagraph.add("Mobile: " + (mobile != null ? mobile : "") + "\n");

                if ((phone != null && !phone.isEmpty()))
                    companyParagraph.add("Phone: " + (phone != null ? phone : "") + "\n");

                if (email != null && !email.isEmpty())
                    companyParagraph.add("Email: " + email + "\n");

                if (afm != null && !afm.isEmpty())
                    companyParagraph.add("VAT: " + afm + "\n");

                document.add(companyParagraph);
                document.add(new Paragraph("\n"));
            }

            // Πίνακας στοιχείων Voucher
            Table table = new Table(UnitValue.createPercentArray(new float[]{3, 7}))
                    .useAllAvailableWidth()
                    .setFontSize(12) // Μεγαλύτερη γραμματοσειρά για καλύτερη αναγνωσιμότητα
                    .setMarginTop(20)  // Αύξηση του πάνω περιθωρίου για μεγαλύτερη αίσθηση
                    .setMarginBottom(20)
                    .setBorderBottom(new SolidBorder(ColorConstants.BLACK, 2)); // Παχύτερη γραμμή στον κάτω όριο

            // Στυλ για τη γραμματοσειρά
            font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            // Helper για τα rows
            String[][] rows = {
                    {"Name", voucher.getName()},
                    {"Email", voucher.getEmail()},
                    {"Passengers", voucher.getAdults() + " adults, " + voucher.getChildren() + " children"},
                    {"Transfer Type", voucher.getType()},
                    {"Pickup & Drop off", voucher.getPickupLocation() + " to " + voucher.getDropoffLocation()},
                    {"Date & Time", voucher.getDate() + " " + voucher.getTime()},
                    {"Notes", voucher.getNotes()}
            };

            // Δημιουργία της κεφαλίδας
            Cell headerCell = new Cell(1, 2)
                    .add(new Paragraph("Voucher Details").setFont(font).setFontSize(14).setTextAlignment(TextAlignment.CENTER))
                    .setBackgroundColor(ColorConstants.WHITE)
                    .setPadding(10)
                    .setBorder(Border.NO_BORDER);
            table.addCell(headerCell);

            // Δημιουργία των σειρών του πίνακα
            for (int i = 0; i < rows.length; i++) {
                String label = rows[i][0];
                String value = rows[i][1];

                // Κελιά για το label
                Cell labelCell = new Cell()
                        .add(new Paragraph(label).setFont(font).setBold())
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                        .setPadding(10)
                        .setBorder(new SolidBorder(ColorConstants.BLACK, 1))
                        .setTextAlignment(TextAlignment.RIGHT); // Ευθυγράμμιση κειμένου στα δεξιά

                // Κελιά για την τιμή
                Cell valueCell = new Cell()
                        .add(new Paragraph(value != null ? value : "").setFont(normalFont))
                        .setPadding(10)
                        .setBorder(new SolidBorder(ColorConstants.BLACK, 1));

                table.addCell(labelCell);
                table.addCell(valueCell);
            }

            document.add(table);
            //document.add(new Paragraph("\n"));

            // Υποσημείωση
            document.add(new LineSeparator(new SolidLine()));
            document.add(new Paragraph("Thank you for choosing us!")
                    .setItalic()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(12)
                    .setMarginTop(10));

            document.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String convertDate(String input) {
        String[] patterns = {
                "yyyy/MM/dd HH:mm",
                "yyyy-MM-dd HH:mm:ss",
                "dd/MM/yyyy HH:mm",
                "yyyy/MM/dd"
        };
        for (String pattern : patterns) {
            try {
                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(pattern);
                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                LocalDateTime dateTime = LocalDateTime.parse(input, inputFormatter);
                return outputFormatter.format(dateTime);
            } catch (Exception ignored) { }
        }
        return input;
    }
}

