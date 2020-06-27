package commons.lib;

public final class StringUtils {

    private static class HtmlEscape {
        private final String html;
        private final String text;

        private HtmlEscape(String html, String text) {
            this.html = html;
            this.text = text;
        }

        private static HtmlEscape def(String html, String text) {
            return new HtmlEscape(html, text);
        }
    }

    private final static HtmlEscape[] HTML_ESCAPES = {
            HtmlEscape.def("&eacute;", "\u00E9")
    };

    public static String escapeToHtml(String s) {
        String result = s;
        // TODO seems to be not enough optimized
        for (HtmlEscape htmlEscape : HTML_ESCAPES) {
            result = result.replaceAll(htmlEscape.text, htmlEscape.html);
        }
        return result;
    }

    public static String removeQuotes(String s) {
        return s.replaceAll("\"", "");
    }

    public static String removeSquareBracket(String s) {
        return s.replaceAll("\\[\\]", "");
    }


    public static String snakeCase(String s) {
        final StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (c >= 'A' && c <= 'Z') {
                sb.append("_");
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String capitalize(String str, boolean firstCharToo) {
        String[] s = str.toLowerCase().split("_");
        StringBuilder result = new StringBuilder();
        int i = 0;
        for (String part : s) {
            if (i == 0 && !firstCharToo) {
                result.append(part);
            } else {
                result.append(part.substring(0, 1).toUpperCase()).append(part.substring(1));
            }
            i++;
        }
        return result.toString();
    }
}
