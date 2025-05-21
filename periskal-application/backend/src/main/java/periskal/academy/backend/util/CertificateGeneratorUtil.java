package periskal.academy.backend.util;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream; 
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;


public final class CertificateGeneratorUtil {

    private CertificateGeneratorUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // --- Page Dimensions ---
    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth(); 
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight(); 
    private static final float MARGIN_LEFT = 50;
    private static final float MARGIN_RIGHT = 50;
    private static final float MARGIN_TOP = 50;
    private static final float MARGIN_BOTTOM = 50;

    // --- Colors ---
    private static final Color COLOR_PERISKAL_BLACK = new Color(0, 0, 0); 
    private static final Color COLOR_PERISKAL_GRAY = new Color(130, 130, 130);
    private static final Color COLOR_TITLE_GOLD = new Color(191, 144, 0); 
    private static final Color COLOR_PRODUCT_RED = new Color(192, 0, 0);
    private static final Color COLOR_BODY_TEXT = new Color(64, 64, 64); 
    private static final Color COLOR_DECO_YELLOW = new Color(255, 215, 0); 
    private static final Color COLOR_DECO_LIGHT_GRAY = new Color(217, 217, 217);
    private static final Color COLOR_DECO_DARK_GRAY = new Color(166, 166, 166);
    private static final Color COLOR_CIRCLE_GRAY = new Color(128, 128, 128);


    // --- Fonts ---
    private static final String FONT_BOLD_PATH = "/fonts/YourSansBold.ttf"; 
    private static final String FONT_REGULAR_PATH = "/fonts/YourSansRegular.ttf"; 
    private static final String FONT_SCRIPT_PATH = "/fonts/YourScript.ttf"; 

    // --- Logo ---
    private static final String LOGO_IMAGE_PATH = "/periskal_logo_globe.png"; // Logo in resources folder
    private static final float LOGO_IMG_X = MARGIN_LEFT + 20;
    private static final float LOGO_IMG_Y = PAGE_HEIGHT - MARGIN_TOP - 85; 
    private static final float LOGO_IMG_WIDTH = 80;
    private static final float LOGO_IMG_HEIGHT = 80;
    private static final float LOGO_TEXT_PERISKAL_X = LOGO_IMG_X + LOGO_IMG_WIDTH + 15;
    private static final float LOGO_TEXT_PERISKAL_Y = LOGO_IMG_Y + 45;
    private static final float LOGO_TEXT_GROUP_X = LOGO_TEXT_PERISKAL_X;
    private static final float LOGO_TEXT_GROUP_Y = LOGO_TEXT_PERISKAL_Y - 25;

    // --- Title ---
    private static final float TITLE_Y_START = LOGO_IMG_Y - 80; 
    private static final float TITLE_LINE_HEIGHT = 30;

    // --- Product ---
    private static final float PRODUCT_X = MARGIN_LEFT + 40;

    // Add more space below the title
    private static final float PRODUCT_Y = TITLE_Y_START - 120; 
    private static final float PRODUCT_LINE_HEIGHT = 18; 

    // --- Body Text ---
    private static final float BODY_X = MARGIN_LEFT + 40;
    private static final float BODY_Y = PRODUCT_Y - 90; 
    private static final float BODY_LINE_HEIGHT = 15f;

    // --- Crew Names ---
    private static final float CREW_COL1_X = MARGIN_LEFT + 40;
    private static final float CREW_COL2_X = PAGE_WIDTH / 2 + 20;
    private static final float CREW_START_Y = BODY_Y - 110; 
    private static final float CREW_SPACING_Y = 28; 

    // --- Signatory ---
    private static final float SIGNATORY_TEXT_X = MARGIN_LEFT + 40;
    private static final float SIGNATORY_TEXT_Y = MARGIN_BOTTOM + 110; 

    // --- Signature Image ---
    // User should provide an image,  150px wide, 50px high. Adjust these defaults as needed.
    private static final float SIGNATURE_IMAGE_WIDTH = 120f;
    private static final float SIGNATURE_IMAGE_HEIGHT = 50f;
    private static final float SIGNATURE_IMAGE_X = SIGNATORY_TEXT_X; // Align with signatory text block
    // Place the bottom of the signature image slightly above the signatory text
    private static final float PADDING_BETWEEN_SIGNATURE_AND_TEXT = 5f; // Space between text and signature image bottom
    private static final float SIGNATURE_IMAGE_Y = SIGNATORY_TEXT_Y + PADDING_BETWEEN_SIGNATURE_AND_TEXT; // Y for bottom-left of signature
    private static final String SIGNATURE_IMAGE_PATH = "/signature.png"; // Signature image in resources folder

    // --- Footer ---
    private static final float FOOTER_X = MARGIN_LEFT + 40;
    private static final float FOOTER_Y = MARGIN_BOTTOM + 30; 
    private static final float FOOTER_LINE_HEIGHT = 11f; 

    // --- Decorative Elements ---
    private static final float DECO_BAR_WIDTH = 40;
    private static final float DECO_BAR_X = PAGE_WIDTH - MARGIN_RIGHT - DECO_BAR_WIDTH;
    private static final float DECO_LINE_WIDTH = 0.5f;
    private static final float DECO_CIRCLE_CENTER_X = PAGE_WIDTH - MARGIN_RIGHT - DECO_BAR_WIDTH / 2;
    private static final float DECO_CIRCLE_CENTER_Y = SIGNATORY_TEXT_Y - 80; 
    private static final float DECO_CIRCLE_RADIUS = 45; 


    public static byte[] generateCertificatePdf(
            String productName,
            String productdetails,
            String shipName,
            String trainingDate,
            List<String> crewNames,
            String signerName,
            String signerTitle,
            String companyName,
            String companyAddress,
            String companyPhone) throws IOException {

        try (PDDocument document = new PDDocument(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            // --- Load Fonts ---
            PDFont fontBold = loadFont(document, FONT_BOLD_PATH, PDType1Font.HELVETICA_BOLD);
            PDFont fontRegular = loadFont(document, FONT_REGULAR_PATH, PDType1Font.HELVETICA);
            PDFont fontScript = loadFont(document, FONT_SCRIPT_PATH, PDType1Font.TIMES_ITALIC); // Fallback scriptish

            // --- Load Logo Image ---
            PDImageXObject pdLogoImage = null;
            try (InputStream logoStream = CertificateGeneratorUtil.class.getResourceAsStream(LOGO_IMAGE_PATH)) {
                if (logoStream != null) {
                    pdLogoImage = PDImageXObject.createFromByteArray(document, logoStream.readAllBytes(), "logo");
                } else {
                    System.err.println("Warning: Logo image not found at " + LOGO_IMAGE_PATH);
                }
            } catch (Exception e) {
                System.err.println("Warning: Could not load logo image: " + e.getMessage());
            }

            PDImageXObject pdSignatureImage = null;
            try (InputStream pdsignatureImage = CertificateGeneratorUtil.class.getResourceAsStream(SIGNATURE_IMAGE_PATH)) {
                if (pdsignatureImage != null) {
                    pdSignatureImage = PDImageXObject.createFromByteArray(document, pdsignatureImage.readAllBytes(), "signature");
                } else {
                    System.err.println("Warning: SignatureImage image not found at " + SIGNATURE_IMAGE_PATH);
                }
            } catch (Exception e) {
                System.err.println("Warning: Could not load logo image: " + e.getMessage());
            }



            // --- Start Drawing ---
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {

                // 1. Draw Decorative Right Bar
                drawDecorativeBar(contentStream);

                // 2. Draw Decorative Circle (Placeholder)
                drawDecorativeCircle(contentStream);

                // 3. Draw Logo (Image + Text)
                drawLogo(contentStream, pdLogoImage, fontBold, fontRegular);

                // 4. Draw Title
                drawTitle(contentStream, fontBold); 

                // 5. Draw Product Info
                drawProductInfo(contentStream, fontBold, fontRegular, productName);

                // 6. Draw Body Text
                drawBodyText(contentStream, fontRegular, signerName, shipName, productdetails ,trainingDate);

                // 7. Draw Crew Names
                drawCrewNames(contentStream, fontScript, crewNames); 

                // 8. Draw Signatory
                drawSignatory(contentStream, fontRegular, signerName, signerTitle, pdSignatureImage);

                // 9. Draw Footer
                drawFooter(contentStream, fontRegular, companyName, companyAddress, companyPhone);

            } // contentStream closed automatically

            document.save(baos);
            return baos.toByteArray();

        } // document closed automatically
    }

    // --- Helper Drawing Methods ---

    private static void drawDecorativeBar(PDPageContentStream contentStream) throws IOException {
        // Yellow background
        contentStream.setNonStrokingColor(COLOR_DECO_YELLOW);
        contentStream.addRect(DECO_BAR_X, 0, DECO_BAR_WIDTH, PAGE_HEIGHT);
        contentStream.fill();

        // Gray lines (thin)
        contentStream.setStrokingColor(COLOR_DECO_LIGHT_GRAY);
        contentStream.setLineWidth(DECO_LINE_WIDTH);
        // Left line
        contentStream.moveTo(DECO_BAR_X, 0);
        contentStream.lineTo(DECO_BAR_X, PAGE_HEIGHT);
        contentStream.stroke();
        // Right line
        contentStream.moveTo(DECO_BAR_X + DECO_BAR_WIDTH, 0);
        contentStream.lineTo(DECO_BAR_X + DECO_BAR_WIDTH, PAGE_HEIGHT);
        contentStream.stroke();

        // Slightly thicker gray line inside left
         contentStream.setStrokingColor(COLOR_DECO_DARK_GRAY);
         contentStream.setLineWidth(DECO_LINE_WIDTH * 2);
         float innerLineOffset = 3f; // Adjust spacing
         contentStream.moveTo(DECO_BAR_X + innerLineOffset, 0);
         contentStream.lineTo(DECO_BAR_X + innerLineOffset, PAGE_HEIGHT);
         contentStream.stroke();

    }

    private static void drawDecorativeCircle(PDPageContentStream contentStream) throws IOException {
        // Draw a proper circle using Bezier curves
        contentStream.setNonStrokingColor(COLOR_CIRCLE_GRAY);
        
        // Approximation factor for circular Bezier curves
        float kappa = 0.552284749831f;
        
        // Calculate control points
        float centerX = DECO_CIRCLE_CENTER_X;
        float centerY = DECO_CIRCLE_CENTER_Y;
        float radius = DECO_CIRCLE_RADIUS;
        
        // Start path
        contentStream.moveTo(centerX + radius, centerY);
        
        // Draw four Bezier curves to form a circle
        // Top right quadrant
        contentStream.curveTo(
            centerX + radius, centerY + kappa * radius, 
            centerX + kappa * radius, centerY + radius, 
            centerX, centerY + radius
        );
        
        // Top left quadrant
        contentStream.curveTo(
            centerX - kappa * radius, centerY + radius, 
            centerX - radius, centerY + kappa * radius, 
            centerX - radius, centerY
        );
        
        // Bottom left quadrant
        contentStream.curveTo(
            centerX - radius, centerY - kappa * radius, 
            centerX - kappa * radius, centerY - radius, 
            centerX, centerY - radius
        );
        
        // Bottom right quadrant
        contentStream.curveTo(
            centerX + kappa * radius, centerY - radius, 
            centerX + radius, centerY - kappa * radius, 
            centerX + radius, centerY
        );
        
        // Fill the circle
        contentStream.fill();
    }

    private static void drawLogo(PDPageContentStream cs, PDImageXObject logoImg, PDFont fontBold, PDFont fontRegular) throws IOException {
        // Draw Image
        if (logoImg != null) {
            cs.drawImage(logoImg, LOGO_IMG_X, LOGO_IMG_Y, LOGO_IMG_WIDTH, LOGO_IMG_HEIGHT);
        }

        // Draw Text "PERISKAL"
        cs.beginText();
        cs.setFont(fontBold, 30); // Large Bold
        cs.setNonStrokingColor(COLOR_PERISKAL_BLACK);
        cs.newLineAtOffset(LOGO_TEXT_PERISKAL_X, LOGO_TEXT_PERISKAL_Y);
        cs.showText("PERISKAL");
        cs.endText();

        // Draw Text "GROUP"
        cs.beginText();
        cs.setFont(fontRegular, 18); // Smaller Regular
        cs.setNonStrokingColor(COLOR_PERISKAL_GRAY);
        cs.newLineAtOffset(LOGO_TEXT_GROUP_X, LOGO_TEXT_GROUP_Y);
        cs.showText("GROUP");
        cs.endText();
    }

    private static void drawTitle(PDPageContentStream cs, PDFont font) throws IOException {
        float fontSize1 = 24;
        float fontSize2 = 24;
        String title1 = "CERTIFICATE";
        String title2 = "OF QUALIFICATION";

        // Calculate width for centering
        float title1Width = font.getStringWidth(title1) / 1000 * fontSize1;
        float title2Width = font.getStringWidth(title2) / 1000 * fontSize2;

        float title1X = (PAGE_WIDTH - title1Width) / 2;
        float title2X = (PAGE_WIDTH - title2Width) / 2;

        cs.beginText();
        cs.setFont(font, fontSize1);
        cs.setNonStrokingColor(COLOR_TITLE_GOLD);
        cs.newLineAtOffset(title1X, TITLE_Y_START);
        cs.showText(title1);
        cs.newLineAtOffset(title2X - title1X, -TITLE_LINE_HEIGHT); 
        cs.setFont(font, fontSize2); 
        cs.showText(title2);
        cs.endText();
    }

    private static void drawProductInfo(PDPageContentStream cs, PDFont fontBold, PDFont fontRegular, String productName) throws IOException {
        float fontSizeLabel = 14;
    float fontSizeValue = 14; 
    float labelIndent = 0; 
    float valueIndent = 10; 
    float currentX = PRODUCT_X + labelIndent;
    float currentY = PRODUCT_Y;

    // --- 1. Draw the Label ---
    cs.beginText();
    cs.setFont(fontBold, fontSizeLabel);
    cs.setNonStrokingColor(COLOR_PRODUCT_RED);
    cs.newLineAtOffset(currentX, currentY);
    cs.showText("PRODUCT : ");
    cs.endText();

    // Move Y down for the first line of the product name
    currentY -= PRODUCT_LINE_HEIGHT;
    // Set X for the indented product name value
    currentX = PRODUCT_X + valueIndent;


    float availableWidth = PAGE_WIDTH - MARGIN_RIGHT - currentX - 5; // Added 5pt safety margin

    // --- 3. Prepare and Wrap the Product Name Text ---
    PDFont valueFont = fontBold; 
    List<String> lines = new ArrayList<>();
    String[] words = productName.trim().split("\\s+"); 
    StringBuilder lineBuilder = new StringBuilder();

    for (String word : words) {
        // Calculate width of potential new line
        String testLine = (lineBuilder.length() == 0) ? word : lineBuilder + " " + word;
        float testWidth = 0;
        try {
             testWidth = valueFont.getStringWidth(testLine) / 1000f * fontSizeValue;
        } catch (IOException e) {
             System.err.println("Warning: Could not get string width. " + e.getMessage());
             testWidth = (testLine.length() * fontSizeValue * 0.6f); 
        }


        if (testWidth < availableWidth) {
            // Word fits, add it to the current line
            if (lineBuilder.length() > 0) {
                lineBuilder.append(" ");
            }
            lineBuilder.append(word);
        } else {
            // Word doesn't fit, finalize the previous line (if any)
            if (lineBuilder.length() > 0) {
                lines.add(lineBuilder.toString());
                lineBuilder.setLength(0); // Reset builder
            }
            // Start the new line with the current word
            // Handle case where a single word is longer than availableWidth
             float wordWidth = 0;
             try {
                 wordWidth = valueFont.getStringWidth(word) / 1000f * fontSizeValue;
             } catch (IOException e) {
                 wordWidth = (word.length() * fontSizeValue * 0.6f);
             }

            if (wordWidth >= availableWidth) {
                 // Single word is too long, add it on its own line (will overflow)
                 lines.add(word);
            } else {
                 // Start new line with this word
                 lineBuilder.append(word);
            }
        }
    }
    // Add the last remaining line
    if (lineBuilder.length() > 0) {
        lines.add(lineBuilder.toString());
    }

    // --- 4. Draw the Wrapped Product Name Lines ---
    cs.beginText();
    cs.setFont(valueFont, fontSizeValue); 
    cs.setNonStrokingColor(COLOR_PRODUCT_RED); 
    // Set initial position for the first line of the value
    cs.newLineAtOffset(currentX, currentY);

    for (int i = 0; i < lines.size(); i++) {
        if (i > 0) {
            // Move down for subsequent lines relative to the previous one
            // Use negative offset because Y=0 is bottom in PDFBox
            cs.newLineAtOffset(0, -PRODUCT_LINE_HEIGHT);
        }
        cs.showText(lines.get(i));
    }
    cs.endText();
}

    private static void drawBodyText(PDPageContentStream cs, PDFont font, String signerName, String shipName,String productDetails, String trainingDate) throws IOException {
        float fontSize = 10.5f; 
        cs.setFont(font, fontSize);
        cs.setNonStrokingColor(COLOR_BODY_TEXT);
        cs.setLeading(BODY_LINE_HEIGHT); // Set line height for newLine()

        cs.beginText();
        cs.newLineAtOffset(BODY_X, BODY_Y);

        // All of these text elements could be parameterized or moved to configuration
        String signerTitle = "managing direct of Periskal";
        String trainingType = "'Competence Training'";
        
        String line1 = "I, undersigned, " + signerName + ", " + signerTitle + " can confirm that";
        String line2 = "the following crew of the ship " + shipName.toUpperCase() + " has followed the " + trainingType;
        String line3 = "for the " + productDetails + " on";
        String line4 = trainingDate + ".";

        cs.showText(line1);
        cs.newLine();
        cs.showText(line2);
        cs.newLine();
        cs.showText(line3);
        cs.newLine();
        cs.showText(line4);

        cs.endText();
    }

    private static void drawCrewNames(PDPageContentStream cs, PDFont font, List<String> crewNames) throws IOException {
        float fontSize = 14f; // Adjust size for names
        cs.setFont(font, fontSize);
        cs.setNonStrokingColor(COLOR_BODY_TEXT); 

        for (int i = 0; i < crewNames.size(); i++) {
            float currentX = (i % 2 == 0) ? CREW_COL1_X : CREW_COL2_X;
            float currentY = CREW_START_Y - ((i / 2) * CREW_SPACING_Y);

            cs.beginText();
            cs.newLineAtOffset(currentX, currentY);
            cs.showText("* " + crewNames.get(i));
            cs.endText();
        }
    }

    private static void drawSignatory(PDPageContentStream cs, PDFont font, String signerName, String signerTitle, PDImageXObject signatureImg) throws IOException {
        
        if (signatureImg != null) {
            try {
                cs.drawImage(signatureImg, SIGNATURE_IMAGE_X, SIGNATURE_IMAGE_Y, SIGNATURE_IMAGE_WIDTH, SIGNATURE_IMAGE_HEIGHT);
            } catch (IOException e) {
                System.err.println("Error drawing signature image: " + e.getMessage());
            }
        }
        
        
        
        float fontSize = 10f; 
        cs.setFont(font, fontSize);
        cs.setNonStrokingColor(Color.BLACK);

        cs.beginText();
        cs.newLineAtOffset(SIGNATORY_TEXT_X, SIGNATORY_TEXT_Y);
        cs.showText(signerName);
        cs.newLineAtOffset(0, -BODY_LINE_HEIGHT + 2); 
        cs.showText(signerTitle);
        cs.endText();
    }

    private static void drawFooter(PDPageContentStream cs, PDFont font, String companyName, String companyAddress, String companyPhone) throws IOException {
        float fontSize = 9f;
        cs.setFont(font, fontSize);
        cs.setNonStrokingColor(Color.BLACK);
        cs.setLeading(FOOTER_LINE_HEIGHT);

        cs.beginText();
        cs.newLineAtOffset(FOOTER_X, FOOTER_Y);
        cs.showText(companyName);
        cs.newLine();
        cs.showText(companyAddress);
        cs.newLine();
        cs.showText(companyPhone);
        cs.endText();
    }


    // --- Font Loading Helper ---
    private static PDFont loadFont(PDDocument document, String fontPath, PDFont fallbackFont) {
        try (InputStream fontStream = CertificateGeneratorUtil.class.getResourceAsStream(fontPath)) {
            if (fontStream != null) {
                // Embed the font subset
                return PDType0Font.load(document, fontStream, true);
            } else {
                System.err.println("Warning: Font file not found at " + fontPath + ", using fallback: " + fallbackFont.getName());
                return fallbackFont;
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not load font " + fontPath + ", using fallback: " + fallbackFont.getName() + ". Error: " + e.getMessage());
            return fallbackFont;
        }
    }

    // --- Example Usage --- TODO: remove this in production
     public static void main(String[] args) { 

        // --- Test Data ---
        String productName = "DEMO PRODUCT";
        String productdetails = "DEMO PRODUCT DETAILS";
        String shipName = "DEMO SHIPNAME";
        String trainingDate = java.time.LocalDate.now().toString();
        List<String> crewNames = List.of("DEMO CREW", "DEMO CREW2", "DEMO CREW3", "DEMO CREW4");
        String signerName = "Marc Persoons";
        String signerTitle = "Managing Director, Periskal bv";
        String companyName = "PERISKAL bv";
        String companyAddress = "Den Isomo 11, 2990 Wuustwezel, Belgium";
        String companyPhone = "Phone +32 3 669 57 36";


        try {
            System.out.println("Generating certificate (V2)...");
            // Ensure logo image and font files exist at the expected classpath locations!
            byte[] pdfData = CertificateGeneratorUtil.generateCertificatePdf(
                    productName, productdetails,shipName, trainingDate, crewNames, signerName, signerTitle,
                    companyName, companyAddress, companyPhone
            );

            // Save the generated PDF to a file for inspection
            String outputFilePath = "generated_certificate_v2.pdf";
            Files.write(new File(outputFilePath).toPath(), pdfData);

            System.out.println("Certificate generated successfully: " + outputFilePath);

        } catch (IOException e) {
            System.err.println("Error generating certificate: " + e.getMessage());
        }
    }
}
