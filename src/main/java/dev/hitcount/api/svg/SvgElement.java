package dev.hitcount.api.svg;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class SvgElement {
    @Builder.Default
    private String style = "flat";
    @Builder.Default
    private String label = "hits";
    @Builder.Default
    private String labelColor = "#555555";
    @Builder.Default
    private String countColor = "#44cc11";
    private int count;

    public String create() {
        if (style.equalsIgnoreCase("pixel")) {
            return "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"1\" height=\"1\"></svg>";
        }

        int labelWidth = measureText(label, 11) + 10;
        int countWidth = measureText(String.valueOf(count), 11) + 20;
        int totalWidth = labelWidth + countWidth;

        switch (style) {
            case "flat-rounded":
                break;
            case "for-the-badge":
                break;
        }

        return String.format(
                "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"%d\" height=\"20\" id=\"flat-square\">\n" +
                        "    <rect width=\"%d\" height=\"20\" fill=\"%s\" />\n" +
                        "    <rect x=\"%d\" width=\"%d\" height=\"20\" fill=\"%s\" />\n" +
                        "    <g fill=\"#fff\" text-anchor=\"middle\" font-size=\"11\" font-family=\"DejaVu Sans,Verdana,Geneva,sans-serif\">\n" +
                        "        <text x=\"%d\" y=\"14\">%s</text>\n" +
                        "        <text x=\"%d\" y=\"14\">%d</text>\n" +
                        "    </g>\n" +
                        "</svg>",
                totalWidth, // Total SVG width
                labelWidth, labelColor, // Label dimensions and color
                labelWidth, countWidth, countColor, // Count dimensions and color
                labelWidth / 2, label, // Label position and value
                labelWidth + countWidth / 2, count // Count position and value
        );
    }

    private int measureText(String text, int fontSize) {
        return (int) (text.length() * fontSize * 0.6);
    }
}
