/*
 * MIT License
 *
 * Copyright (c) 2019 Marvin (DerFrZocker)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
