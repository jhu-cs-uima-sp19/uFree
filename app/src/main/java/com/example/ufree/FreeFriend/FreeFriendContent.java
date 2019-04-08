package com.example.ufree.FreeFriend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class FreeFriendContent {

    /**
     * An array of free freinds.
     */
    public static final List<FreeFriend> FREE_FREINDS_LIST = new ArrayList<FreeFriend>();

    /**
     * A map of free friends, by ID.
     */
    public static final Map<String, FreeFriend> FREE_FREINDS_MAP = new HashMap<String, FreeFriend>();

    private static final int COUNT = 25;

    static {
        // Add some sample FREE_FREINDS_LIST.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createFreeFriend(i));
        }
    }

    private static void addItem(FreeFriend item) {
        FREE_FREINDS_LIST.add(item);
        FREE_FREINDS_MAP.put(item.id, item);
    }

    private static FreeFriend createFreeFriend(int position) {
        return new FreeFriend(String.valueOf(position), "Item " + position, makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A free friend.
     */
    public static class FreeFriend {
        public final String id;
        public final String content;
        public final String details;

        public FreeFriend(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
