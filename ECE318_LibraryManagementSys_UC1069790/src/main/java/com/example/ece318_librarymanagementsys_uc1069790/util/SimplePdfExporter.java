package com.example.ece318_librarymanagementsys_uc1069790.util;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class SimplePdfExporter {

    private SimplePdfExporter() {
    }

    public static void export(String title, List<String> lines, Path destination) throws IOException {
        List<String> contentLines = new ArrayList<>();
        contentLines.add(title);
        contentLines.add("");
        contentLines.addAll(lines);

        byte[] pdfBytes = buildPdfDocument(contentLines);
        try (OutputStream out = Files.newOutputStream(destination, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
            out.write(pdfBytes);
        }
    }

    private static byte[] buildPdfDocument(List<String> lines) {
        StringBuilder textBuilder = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            String line = escapeText(lines.get(i));
            if (i == 0) {
                textBuilder.append("(").append(line).append(") Tj\n");
            } else {
                textBuilder.append("T* (" + line + ") Tj\n");
            }
        }

        String contentStream = "BT\n" +
                "/F1 12 Tf\n" +
                "14 TL\n" +
                "50 780 Td\n" +
                textBuilder +
                "ET\n";

        byte[] contentBytes = contentStream.getBytes(StandardCharsets.US_ASCII);

        List<Integer> offsets = new ArrayList<>();
        String header = "%PDF-1.4\n";
        byte[] headerBytes = header.getBytes(StandardCharsets.US_ASCII);

        ByteArrayBuilder builder = new ByteArrayBuilder();
        builder.append(headerBytes);

        offsets.add(builder.length());
        builder.append(object(1, "<< /Type /Catalog /Pages 2 0 R >>\n"));

        offsets.add(builder.length());
        builder.append(object(2, "<< /Type /Pages /Kids [3 0 R] /Count 1 >>\n"));

        offsets.add(builder.length());
        builder.append(object(3, "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 5 0 R >> >> /Contents 4 0 R >>\n"));

        offsets.add(builder.length());
        builder.append(streamObject(4, contentBytes));

        offsets.add(builder.length());
        builder.append(object(5, "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\n"));

        int xrefOffset = builder.length();
        builder.append(String.format("xref\n0 %d\n", offsets.size() + 1).getBytes(StandardCharsets.US_ASCII));
        builder.append("0000000000 65535 f \n".getBytes(StandardCharsets.US_ASCII));
        for (int offset : offsets) {
            builder.append(String.format("%010d 00000 n \n", offset).getBytes(StandardCharsets.US_ASCII));
        }
        builder.append(String.format("trailer << /Size %d /Root 1 0 R >>\n", offsets.size() + 1).getBytes(StandardCharsets.US_ASCII));
        builder.append(String.format("startxref\n%d\n", xrefOffset).getBytes(StandardCharsets.US_ASCII));
        builder.append("%%EOF".getBytes(StandardCharsets.US_ASCII));

        return builder.toByteArray();
    }

    private static byte[] object(int id, String body) {
        String object = id + " 0 obj\n" + body + "endobj\n";
        return object.getBytes(StandardCharsets.US_ASCII);
    }

    private static byte[] streamObject(int id, byte[] stream) {
        String header = id + " 0 obj\n<< /Length " + stream.length + " >>\nstream\n";
        String footer = "endstream\nendobj\n";
        ByteArrayBuilder builder = new ByteArrayBuilder();
        builder.append(header.getBytes(StandardCharsets.US_ASCII));
        builder.append(stream);
        builder.append(footer.getBytes(StandardCharsets.US_ASCII));
        return builder.toByteArray();
    }

    private static String escapeText(String text) {
        return text.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
    }

    private static final class ByteArrayBuilder {
        private final List<byte[]> chunks = new ArrayList<>();
        private int length;

        void append(byte[] bytes) {
            chunks.add(bytes);
            length += bytes.length;
        }

        int length() {
            return length;
        }

        byte[] toByteArray() {
            byte[] result = new byte[length];
            int offset = 0;
            for (byte[] chunk : chunks) {
                System.arraycopy(chunk, 0, result, offset, chunk.length);
                offset += chunk.length;
            }
            return result;
        }
    }
}
