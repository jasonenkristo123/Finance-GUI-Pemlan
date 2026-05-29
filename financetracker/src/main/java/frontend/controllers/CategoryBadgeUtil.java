package frontend.controllers;

import javafx.scene.control.Label;

public class CategoryBadgeUtil {

    private CategoryBadgeUtil() {
        
    }

    /**
     * Returns an emoji representing the given transaction category.
     *
     * @param category the category name (case-insensitive)
     * @return a single emoji string
     */
    public static String getEmoji(String category) {
        if (category == null) return "📍";
        return switch (category.toLowerCase()) {
            case "salary"                    -> "💰";
            case "software"                  -> "💻";
            case "revenue"                   -> "🏦";
            case "travel"                    -> "✈";
            case "marketing"                 -> "📢";
            case "office"                    -> "🏢";
            case "housing"                   -> "🏠";
            case "food & dining", "food"     -> "🍔";
            case "transport"                 -> "🚗";
            default                          -> "📍";
        };
    }

    /**
     * Creates a styled Label badge for the given category, ready to be set
     * as a TableCell graphic.
     *
     * @param category the category name
     * @return a Label with the appropriate CSS style classes applied
     */
    public static Label createBadge(String category) {
        String emoji = getEmoji(category);
        Label badge = new Label(emoji + "  " + category);
        String styleClass = "badge-" + category.toLowerCase().replace(" & ", "-");
        badge.getStyleClass().addAll("category-badge", styleClass);
        return badge;
    }
}
