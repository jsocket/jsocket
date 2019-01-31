package net.jsocket;

/**
 * Often used constants
 */
public interface Constants {
    /**
     * Blacklisted message names
     */
    String[] handleNameBlacklist = {"newClientID", "disconnectMessage"};

    /**
     * Checks if a message name is on the blacklist
     * @param name The message name to be tested
     * @return boolean if the message name is blacklisted
     */
    default boolean isHandleBlacklisted(String name) {
        for (String compare : handleNameBlacklist) {
            if (compare.equals(name)) return true;
        }
        return false;
    }
}
