package commons.lib.documentation;

import commons.lib.FileUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

public class Documentation {
    private final List<Fragment> fragments = new ArrayList<>();
    private String fileName;

    public Fragment title(int level, String title) {
        final Fragment fragment = new Fragment();
        fragment.setTitleLevel(level);
        fragment.setContent(title);
        fragment.setNewLine(true);
        fragments.add(fragment);
        return fragment;
    }

    public Fragment text(String text, boolean newLine) {
        final Fragment fragment = new Fragment();
        fragment.setContent(text);
        fragment.setNewLine(newLine);
        fragments.add(fragment);
        return fragment;
    }

    public Fragment italic(String text) {
        final Fragment fragment = new Fragment();
        fragment.setItalic(true);
        fragment.setContent(text);
        fragments.add(fragment);
        return fragment;
    }

    public Fragment bold(String text) {
        final Fragment fragment = new Fragment();
        fragment.setBold(true);
        fragment.setContent(text);
        fragments.add(fragment);
        return fragment;
    }

    public Fragment code(String text) {
        final Fragment fragment = new Fragment();
        fragment.setCode(true);
        fragment.setContent(text);
        fragments.add(fragment);
        return fragment;
    }

    public Fragment quote(String text) {
        final Fragment fragment = new Fragment();
        fragment.setQuote(true);
        fragment.setContent(text);
        fragment.setNewLine(true);
        fragments.add(fragment);
        return fragment;
    }

    public Fragment pinPoint(String text) {
        final Fragment fragment = new Fragment();
        fragment.setPinPoint(true);
        fragment.setContent(text);
        fragment.setNewLine(true);
        fragments.add(fragment);
        return fragment;
    }

    public Fragment separator() {
        final Fragment fragment = new Fragment();
        fragment.setEndWithHorizontalRule(true);
        fragments.add(fragment);
        fragment.setNewLine(true);
        return fragment;
    }

    public Fragment link(String url) {
        final Fragment fragment = new Fragment();
        try {
            fragment.setExternalLink(new URL(url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return fragment;
    }

    public Fragment linkDoc(String docName) {
        final Fragment fragment = new Fragment();
        fragment.setMdRelativePath(docName);
        fragments.add(fragment);
        return fragment;
    }
    public Fragment exampleCode(String reference) throws IOException {
        final StringTokenizer tokenizer = new StringTokenizer(reference, ":");
        String javaClassName = tokenizer.nextToken();
        int startingLine = Integer.parseInt(tokenizer.nextToken());
        int endintLine = Integer.parseInt(tokenizer.nextToken());
        String title = tokenizer.nextToken();
        text(title, true);
        String fileContent = FileUtils.readFile(Paths.get(javaClassName.replace(".", "/")));
        String[] split = fileContent.split("\n");
        Objects.checkIndex(endintLine, split.length);
        StringBuilder builder = new StringBuilder();
        for (int i = startingLine; i < endintLine; i++) {
            builder.append(split[i]);
        }

        final Fragment fragment = new Fragment();
        fragment.setCode(true);
        fragment.setContent(builder.toString());
        fragments.add(fragment);
        return fragment;
    }

/*
    public static Fragment title(int level, String title) {
        final Fragment fragment = new Fragment();
        fragment.setTitleLevel(level);
        fragment.setContent(title);
        return fragment;
    }

    public static Fragment italic(String text) {
        final Fragment fragment = new Fragment();
        fragment.setItalic(true);
        fragment.setContent(text);
        return fragment;
    }

    public static Fragment bold(String text) {
        final Fragment fragment = new Fragment();
        fragment.setBold(true);
        fragment.setContent(text);
        return fragment;
    }

    public static Fragment code(String text) {
        final Fragment fragment = new Fragment();
        fragment.setCode(true);
        fragment.setContent(text);
        return fragment;
    }

    public static Fragment quote(String text) {
        final Fragment fragment = new Fragment();
        fragment.setQuote(true);
        fragment.setContent(text);
        return fragment;
    }

    public static Fragment pinPoint(String text) {
        final Fragment fragment = new Fragment();
        fragment.setContent(text);
        return fragment;
    }

    public static Fragment separator() {
        final Fragment fragment = new Fragment();
        fragment.setEndWithHorizontalRule(true);
        return fragment;
    }

    public static Fragment link(String url) {
        final Fragment fragment = new Fragment();
        try {
            fragment.setExternalLink(new URL(url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return fragment;
    }

    public static Fragment linkDoc(String docName) {
        final Fragment fragment = new Fragment();
        fragment.setMdRelativePath(docName);
        return fragment;
    }*/

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<Fragment> getFragments() {
        return fragments;
    }

    public static class Fragment {
        private String content = "";
        private boolean isPinPoint;
        private Integer numPoint;
        private boolean code;
        private boolean bold;
        private boolean italic;
        private boolean quote;
        private String mdRelativePath;
        private URL externalLink;
        private boolean newLine;
        private int titleLevel;
        private boolean endWithHorizontalRule;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public boolean isBold() {
            return bold;
        }

        public void setBold(boolean bold) {
            this.bold = bold;
        }

        public boolean isItalic() {
            return italic;
        }

        public void setItalic(boolean italic) {
            this.italic = italic;
        }

        public String getMdRelativePath() {
            return mdRelativePath;
        }

        public void setMdRelativePath(String mdRelativePath) {
            this.mdRelativePath = mdRelativePath;
        }

        public URL getExternalLink() {
            return externalLink;
        }

        public void setExternalLink(URL externalLink) {
            this.externalLink = externalLink;
        }

        public boolean isNewLine() {
            return newLine;
        }

        public void setNewLine(boolean newLine) {
            this.newLine = newLine;
        }

        public boolean isPinPoint() {
            return isPinPoint;
        }

        public void setPinPoint(boolean pinPoint) {
            isPinPoint = pinPoint;
        }


        public int getTitleLevel() {
            return titleLevel;
        }

        public void setTitleLevel(int titleLevel) {
            this.titleLevel = titleLevel;
        }

        public Integer getNumPoint() {
            return numPoint;
        }

        public void setNumPoint(Integer numPoint) {
            this.numPoint = numPoint;
        }

        public boolean isCode() {
            return code;
        }

        public void setCode(boolean code) {
            this.code = code;
        }

        public boolean isEndWithHorizontalRule() {
            return endWithHorizontalRule;
        }

        public void setEndWithHorizontalRule(boolean endWithHorizontalRule) {
            this.endWithHorizontalRule = endWithHorizontalRule;
        }

        public boolean isQuote() {
            return quote;
        }

        public void setQuote(boolean quote) {
            this.quote = quote;
        }
    }
}

