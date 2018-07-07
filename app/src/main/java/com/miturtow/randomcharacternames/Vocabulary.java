package com.miturtow.randomcharacternames;

import java.util.*;

class Vocabulary {

    private static Random random = new Random();
    private static final List<String> VOWELS = Arrays.asList(
            "aeiou".split("(?!^)")
    );
    private static final List<String> CONSONANTS = Arrays.asList(
            "bcdfghklmnprstvwxz".split("(?!^)")
    );
    private static final List<String> VOWEL_PAIRS = Arrays.asList(
            "ae", "ea", "ui", "ai", "oo", "ya"
    );
    private static final List<String> CONSONANT_PAIRS = Arrays.asList(
            "sk", "sh", "sm", "th", "kh", "zh", "ph", "ch", "st", "tr", "nz", "dr", "br", "gr", "kr"
    );
    private static final Map<String, ArrayList<String>> BLACKLIST = new HashMap<>();

    static {
        populateBlacklist();
    }

    public Vocabulary() {
    }

    public static String getVowel() {
        return VOWELS.get(random.nextInt(VOWELS.size()));
    }

    public static String getAllowedVowel(ArrayList<String> allowed) {
        String letter = VOWELS.get(random.nextInt(VOWELS.size()));
        if (allowed.contains(letter)) {
            return letter;
        } else return getAllowedVowel(allowed);
    }

    public static String getVowelPair() {
        return VOWEL_PAIRS.get(random.nextInt(VOWEL_PAIRS.size()));
    }

    public static String getConsonant() {
        return CONSONANTS.get(random.nextInt(CONSONANTS.size()));
    }

    public static String getConsonantPair() {
        return CONSONANT_PAIRS.get(random.nextInt(CONSONANT_PAIRS.size()));
    }

    public static boolean isVowelLast(String letter) {
        return VOWELS.contains(letter) || VOWEL_PAIRS.contains(letter);
    }

    private static void populateBlacklist() {
        BLACKLIST.put("j", new ArrayList<String>());
        BLACKLIST.get("j").add("y");
        BLACKLIST.put("q", new ArrayList<String>());
        BLACKLIST.get("q").add("i");
        BLACKLIST.get("q").add("u");
        BLACKLIST.get("q").add("y");
        BLACKLIST.put("u", new ArrayList<String>());
        BLACKLIST.get("u").add("j");
        BLACKLIST.get("u").add("q");
        BLACKLIST.put("y", new ArrayList<String>());
        BLACKLIST.get("y").add("h");
        BLACKLIST.get("y").add("j");
        BLACKLIST.get("y").add("q");
        BLACKLIST.get("y").add("w");
    }

    public static boolean notInBlackList(String newLetter, String previousLetter) {
        if (BLACKLIST.containsKey(previousLetter)) {
            return !BLACKLIST.get(previousLetter).contains(newLetter);
        } else return true;
    }
}
