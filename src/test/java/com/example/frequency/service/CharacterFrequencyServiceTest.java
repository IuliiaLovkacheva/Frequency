package com.example.frequency.service;

import com.example.frequency.exception.InputLengthException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootTest
public class CharacterFrequencyServiceTest {
    public static final String CHARACTER_SOURCE = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789/\\<>"
            + ".*&^%$#@!?'\"|~абвгдеёжзиклмнопрстуфхцчшщъыьэюя";
    private static final int EXPECTED_MAX_STRING_LENGTH = 7000;
    @Autowired
    private CharacterFrequencyService service;

    @Test
    void singleCharacter_SingleOccurrence() {
        singleCharacterSuccessTest(1);
    }

    @Test
    void singleCharacter_TwoOccurrences() {
        singleCharacterSuccessTest(2);
    }

    @Test
    void singleCharacter_ManyOccurrences() {
        int randomNum = ThreadLocalRandom.current().nextInt(3, EXPECTED_MAX_STRING_LENGTH - 1);
        singleCharacterSuccessTest(randomNum);
    }

    @Test
    void singleCharacter_MaxStringLengthMinusOne() {
        singleCharacterSuccessTest(EXPECTED_MAX_STRING_LENGTH - 1);
    }

    @Test
    void singleCharacter_MaxStringLength() {
        singleCharacterSuccessTest(EXPECTED_MAX_STRING_LENGTH);
    }

    @Test
    void exceedMaxLengthByOne() {
        String inputString = "a".repeat(EXPECTED_MAX_STRING_LENGTH + 1);
        assertThrows(InputLengthException.class, () -> service.calculateFrequency(inputString));
    }

    @Test
    void exceedMaxLength() {
        int randomNum = ThreadLocalRandom.current().nextInt(2, 100);
        String inputString = "a".repeat(EXPECTED_MAX_STRING_LENGTH + randomNum);
        assertThrows(InputLengthException.class, () -> service.calculateFrequency(inputString));
    }

    @Test
    void twoCharacters_SingleOccurrences() {
        twoCharactersSuccessTest(1, 1);
    }

    @Test
    void twoCharacters_TwoOccurrences() {
        twoCharactersSuccessTest(1, 2);
        twoCharactersSuccessTest(2, 2);
    }

    @Test
    void twoCharacters_ManyOccurrences() {
        int total = ThreadLocalRandom.current().nextInt(4, EXPECTED_MAX_STRING_LENGTH - 1);
        twoCharacterSuccessTestForTotal(total);
    }

    @Test
    void twoCharacters_MaxStringLengthMinusOne() {
        twoCharacterSuccessTestForTotal(EXPECTED_MAX_STRING_LENGTH - 1);
    }

    @Test
    void twoCharacters_MaxStringLength() {
        twoCharacterSuccessTestForTotal(EXPECTED_MAX_STRING_LENGTH);
    }

    @Test
    void multipleCharacters_DifferentFrequencies() {
        StringBuilder builder = new StringBuilder();
        Set<Character> set = new HashSet<>();
        for (int i = 0; i < CHARACTER_SOURCE.length(); ++i) {
            Character ch = CHARACTER_SOURCE.charAt(i);
            set.add(ch);
            appendNTimes(builder, i + 1, ch);
        }
        LinkedHashMap<Character, Integer> map = service.calculateFrequency(shuffleString(builder.toString()));

        assertEquals(set, map.keySet());
        List<Integer> values = map.values().stream().toList();
        assertEquals(CHARACTER_SOURCE.length(), values.size());
        assertDescending(values);
        for (int i = 0; i < CHARACTER_SOURCE.length(); ++i) {
            assertEquals(map.get(CHARACTER_SOURCE.charAt(i)), i + 1);
        }
    }


    @Test
    void variousCharacters_RepeatingFrequencies() {
        StringBuilder builder = new StringBuilder();
        Set<Character> set = new HashSet<>();
        List<Integer> frequencies = new ArrayList<>();
        int MAX_CHARACTER_OCCURRENCES = EXPECTED_MAX_STRING_LENGTH / CHARACTER_SOURCE.length();
        int randomNum = -1;
        for (int i = 0; i < CHARACTER_SOURCE.length(); ++i) {
            Character ch = CHARACTER_SOURCE.charAt(i);
            set.add(ch);
            if (i % 2 == 0) {
                randomNum = ThreadLocalRandom.current().nextInt(1, MAX_CHARACTER_OCCURRENCES + 1);
            }
            frequencies.add(randomNum);
            appendNTimes(builder, randomNum, ch);
        }

        LinkedHashMap<Character, Integer> map = service.calculateFrequency(shuffleString(builder.toString()));

        assertEquals(set, map.keySet());
        List<Integer> values = map.values().stream().toList();
        assertEquals(CHARACTER_SOURCE.length(), values.size());
        assertDescending(values);
        for (int i = 0; i < CHARACTER_SOURCE.length(); ++i) {
            assertEquals(map.get(CHARACTER_SOURCE.charAt(i)), frequencies.get(i));
        }
    }


    private static void appendNTimes(StringBuilder builder, int n, Character ch) {
        while (n > 0) {
            builder.append(ch);
            --n;
        }
    }

    private static void assertDescending(List<Integer> values) {
        int previous = values.get(0);
        for (int i = 1; i < values.size(); i++) {
            int current = values.get(i);
            if (previous < current) {
                fail("Wrong order: " + previous + " < " + current);
            }
            previous = current;
        }
    }

    private static String shuffleString(String string) {
        List<String> characters = Arrays.asList(string.split(""));
        Collections.shuffle(characters);
        StringBuilder afterShuffle = new StringBuilder();
        for (String character : characters)
        {
            afterShuffle.append(character);
        }
        return afterShuffle.toString();
    }

    private void singleCharacterSuccessTest(int repeatNumber) {
        Character ch = getRandomCharacter();
        StringBuilder builder = new StringBuilder();
        appendNTimes(builder, repeatNumber, ch);
        String inputString = builder.toString();
        Set<Character> set = new HashSet<>();
        set.add(ch);

        HashMap<Character, Integer> map = service.calculateFrequency(inputString);

        assertEquals(set, map.keySet());
        assertEquals(repeatNumber, map.get(ch));
    }

    private void twoCharactersSuccessTest(int repeatNumber1, int repeatNumber2) {
        Set<Character> set = new HashSet<>();
        Character ch1 = getRandomCharacter();
        Character ch2 = getRandomCharacter();
        while (ch2.equals(ch1)) {
            ch2 = getRandomCharacter();
        }
        set.add(ch1);
        set.add(ch2);
        StringBuilder builder = new StringBuilder();
        appendNTimes(builder, repeatNumber1, ch1);
        appendNTimes(builder, repeatNumber2, ch2);

        HashMap<Character, Integer> map = service.calculateFrequency(shuffleString(builder.toString()));

        assertEquals(set, map.keySet());
        assertDescending(map.values().stream().toList());
        assertEquals(repeatNumber1, map.get(ch1));
        assertEquals(repeatNumber2, map.get(ch2));
    }

    private static char getRandomCharacter() {
        return CHARACTER_SOURCE.charAt(ThreadLocalRandom.current().nextInt(0, CHARACTER_SOURCE.length()));
    }

    private void twoCharacterSuccessTestForTotal(int total) {
        twoCharactersSuccessTest(1, total - 1);
        int repeatNumber1 = ThreadLocalRandom.current().nextInt(2, total - 1);
        int repeatNumber2 = total - repeatNumber1;
        twoCharactersSuccessTest(repeatNumber1, repeatNumber2);
    }
}
