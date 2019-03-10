package de.derfrzocker.ore.control.utils;

import java.util.Random;

public class TestUtil {

    private static final String CHAR_LIST = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

    private static final Random random = new Random(7823478923789435345L);

    public static String generateRandomString(int lenght) {
        final StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < lenght; i++)
            stringBuilder.append(CHAR_LIST.charAt(getRandomNumber()));

        return stringBuilder.toString();
    }

    private static int getRandomNumber() {
        final int randomInt;
        randomInt = random.nextInt(CHAR_LIST.length());
        if (randomInt == 0)
            return randomInt;
        else
            return randomInt - 1;
    }

    public static String toMixedCase(String string) {
        if (string == null || string.trim().isEmpty())
            return string;

        final StringBuilder stringBuilder = new StringBuilder(string.toLowerCase());

        for (int i = 0; i < string.length(); i += 2)
            stringBuilder.setCharAt(i, Character.toUpperCase(string.charAt(i)));

        return stringBuilder.toString();
    }

}
