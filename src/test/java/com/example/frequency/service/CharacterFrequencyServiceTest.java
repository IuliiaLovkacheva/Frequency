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
        HashMap<Character, Integer> map = service.calculateFrequency("a");
        Set<Character> set = new HashSet<>();
        set.add('a');
        assertEquals(set, map.keySet());
        assertEquals(1, map.get('a'));
    }

    @Test
    void singleCharacter_TwoTimes() {
        HashMap<Character, Integer> map = service.calculateFrequency("aa");
        Set<Character> set = new HashSet<>();
        set.add('a');
        assertEquals(set, map.keySet());
        assertEquals(2, map.get('a'));
    }

    @Test
    void singleCharacter_ManyTimes() {
        int randomNum = ThreadLocalRandom.current().nextInt(3, EXPECTED_MAX_STRING_LENGTH - 1);
        String inputString = "a".repeat(randomNum);
        Set<Character> set = new HashSet<>();
        set.add('a');

        HashMap<Character, Integer> map = service.calculateFrequency(inputString);

        assertEquals(set, map.keySet());
        assertEquals(randomNum, map.get('a'));
    }

    @Test
    void singleCharacter_MaxStringLengthMinusOne() {
        String inputString = "a".repeat(EXPECTED_MAX_STRING_LENGTH - 1);
        Set<Character> set = new HashSet<>();
        set.add('a');

        HashMap<Character, Integer> map = service.calculateFrequency(inputString);

        assertEquals(set, map.keySet());
        assertEquals(EXPECTED_MAX_STRING_LENGTH - 1, map.get('a'));
    }

    @Test
    void singleCharacter_MaxStringLength() {
        String inputString = "a".repeat(EXPECTED_MAX_STRING_LENGTH);
        Set<Character> set = new HashSet<>();
        set.add('a');

        HashMap<Character, Integer> map = service.calculateFrequency(inputString);

        assertEquals(set, map.keySet());
        assertEquals(EXPECTED_MAX_STRING_LENGTH, map.get('a'));
    }

    @Test
    void singleCharacter_exceedMaxLengthByOne() {
        String inputString = "a".repeat(EXPECTED_MAX_STRING_LENGTH + 1);
        assertThrows(InputLengthException.class, () -> service.calculateFrequency(inputString));
    }

    @Test
    void singleCharacter_exceedMaxLength() {
        int randomNum = ThreadLocalRandom.current().nextInt(2, 100);
        String inputString = "a".repeat(EXPECTED_MAX_STRING_LENGTH + randomNum);
        assertThrows(InputLengthException.class, () -> service.calculateFrequency(inputString));
    }

    @Test
    void variousCharacters_DifferentFrequencies() {
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
        String afterShuffle = "";
        for (String character : characters)
        {
            afterShuffle += character;
        }
        return afterShuffle;
    }
}
