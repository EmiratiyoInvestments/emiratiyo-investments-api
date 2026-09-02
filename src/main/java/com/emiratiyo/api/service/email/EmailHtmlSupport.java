package com.emiratiyo.api.service.email;

final class EmailHtmlSupport {

    static final String PRIMARY = "#e83f25";
    static final String BLACK = "#000000";
    static final String GREY = "#939393";
    static final String WHITE = "#f7f7f7";

    private EmailHtmlSupport() {
    }

    static void appendDocumentStart(StringBuilder html) {
        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        html.append("<link href='https://fonts.googleapis.com/css2?family=Inter:wght@100..900&display=swap' rel='stylesheet'>");
        html.append("</head>");
        html.append("<body style='margin:0; padding:20px; background-color:")
                .append(WHITE)
                .append("; font-family: Inter, Arial, sans-serif;'>");
        html.append("<div style='max-width:720px; margin:0 auto; background-color:#ffffff; border:1px solid #eeeeee; border-radius:12px; overflow:hidden;'>");
    }

    static void appendHeader(StringBuilder html, String title) {
        html.append("<div style='background-color:")
                .append(BLACK)
                .append("; padding:22px 20px;'>");
        html.append("<div style='color:#ffffff; font-size:16px; letter-spacing:0.3px; font-weight:700;'>")
                .append(escapeHtml(title))
                .append("</div>");
        html.append("<div style='color:")
                .append(GREY)
                .append("; font-size:12px; margin-top:6px;'>Emiratiyo Investments Website</div>");
        html.append("</div>");
    }

    static void appendContentStart(StringBuilder html) {
        html.append("<div style='padding:0;'>");
    }

    static void appendContentEnd(StringBuilder html) {
        html.append("</div>");
    }

    static void appendFooter(StringBuilder html) {
        html.append("<div style='padding:18px 20px; background-color:")
                .append(WHITE)
                .append("; border-top:3px solid ")
                .append(PRIMARY)
                .append(";'>");
        html.append("<div style='color:")
                .append(GREY)
                .append("; font-size:12px;'>You can reply directly to this email to respond to the sender.</div>");
        html.append("</div>");
    }

    static void appendDocumentEnd(StringBuilder html) {
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");
    }

    static void addRow(StringBuilder html, String label, String value) {
        String normalizedValue = value == null ? "" : value;
        html.append("<div style='padding:16px 20px; border-bottom:1px solid #f0f0f0;'>");
        html.append("<div style='color:")
                .append(GREY)
                .append("; font-size:12px; text-transform:uppercase; letter-spacing:0.6px; margin-bottom:6px;'>")
                .append(escapeHtml(label))
                .append("</div>");
        html.append("<div style='color:")
                .append(BLACK)
                .append("; font-size:14px; line-height:1.5; word-break:break-word;'>")
                .append(escapeHtml(normalizedValue))
                .append("</div>");
        html.append("</div>");
    }

    static String escapeHtml(String input) {
        if (input == null) {
            return "";
        }
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
