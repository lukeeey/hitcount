package dev.hitcount.api.svg;

import io.javalin.http.Context;

import java.util.regex.Pattern;

public class SvgUtils {
    private static final int MAX_LABEL_LENGTH = 30;
    private static final Pattern HEX_PATTERN = Pattern.compile("\\p{XDigit}+");

    public static SvgElement createSvg(Context ctx, int hitCount) {
        SvgElement.SvgElementBuilder svg = SvgElement.builder();
        String label, labelColor, countColor, style;

        svg.count(hitCount);

        if ((label = ctx.queryParam("label")) != null) {
            String truncatedLabel = label.substring(0, Math.min(label.length(), MAX_LABEL_LENGTH));
            svg.label(truncatedLabel);
        }
        if ((labelColor = ctx.queryParam("labelColor")) != null) {
            if (HEX_PATTERN.matcher(labelColor).matches()) {
                svg.labelColor("#" + labelColor);
            }
        }
        if ((countColor = ctx.queryParam("countColor")) != null) {
            if (HEX_PATTERN.matcher(countColor).matches()) {
                svg.countColor("#" + countColor);
            }
        }
        if ((style = ctx.queryParam("style")) != null) {
            if (style.equalsIgnoreCase("flat")) {
                svg.style(style);
            }
        }
        return svg.build();
    }
}
