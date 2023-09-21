package com.example.frequency.service;

import com.example.frequency.exception.InputLengthException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootTest
public class CharacterFrequencyServiceTest {
    public static final String CHARACTER_SOURCE = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789/\\<>"
            + ".*&^%$#@!?'\"|~абвгдеёжзиклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
    private static final int EXPECTED_MAX_STRING_LENGTH = 7000;
    @Autowired
    private CharacterFrequencyService service;

    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    void singleCharacter_SmallFrequency(int repeatNumber) {
        singleCharacterSuccessTest(repeatNumber);
    }

    @Test
    void singleCharacter_ManyOccurrences() {
        int randomNum = ThreadLocalRandom.current().nextInt(3, EXPECTED_MAX_STRING_LENGTH - 1);
        singleCharacterSuccessTest(randomNum);
    }

    @ParameterizedTest
    @ValueSource(ints = {EXPECTED_MAX_STRING_LENGTH - 1, EXPECTED_MAX_STRING_LENGTH})
    void singleCharacter_MaxStringLength(int length) {
        singleCharacterSuccessTest(length);
    }

    @Test
    void exceedMaxLengthByOne() {
        Character ch = getRandomCharacter();
        StringBuilder builder = new StringBuilder();
        appendNTimes(builder, EXPECTED_MAX_STRING_LENGTH + 1, ch);
        assertThrows(InputLengthException.class, () -> service.calculateFrequency(builder.toString()));
    }

    @Test
    void exceedMaxLength() {
        Character ch = getRandomCharacter();
        StringBuilder builder = new StringBuilder();
        int randomNum = ThreadLocalRandom.current().nextInt(2, 100);
        appendNTimes(builder, EXPECTED_MAX_STRING_LENGTH + randomNum, ch);
        assertThrows(InputLengthException.class, () -> service.calculateFrequency(builder.toString()));
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

    @ParameterizedTest
    @ValueSource(ints = {EXPECTED_MAX_STRING_LENGTH - 1, EXPECTED_MAX_STRING_LENGTH})
    void twoCharacters_MaxStringLength(int length) {
        twoCharacterSuccessTestForTotal(length);
    }

    @Test
    void twoCharacters_MaxStringLength() {
        twoCharacterSuccessTestForTotal(EXPECTED_MAX_STRING_LENGTH);
    }

    @Test
    void multipleCharacters_DifferentFrequencies() {
        StringBuilder builder = new StringBuilder();
        Set<Character> characterSet = new HashSet<>();
        List<Character> charactersInOrder = new ArrayList<>();
        final int MAX_CHARACTER_NUMBER = 117;
        int characterNumber = ThreadLocalRandom.current().nextInt(3, MAX_CHARACTER_NUMBER + 1);
        for (int i = 0; i < characterNumber; ++i) {
            Character ch = getRandomCharacter();
            while (characterSet.contains(ch)) {
                ch = getRandomCharacter();
            }
            characterSet.add(ch);
            charactersInOrder.add(ch);
            appendNTimes(builder, i + 1, ch);
        }
        LinkedHashMap<Character, Integer> map = service.calculateFrequency(shuffleString(builder.toString()));

        assertEquals(characterSet, map.keySet());
        List<Integer> values = map.values().stream().toList();
        assertEquals(characterNumber, values.size());
        assertDescending(values);
        for (int i = 0; i < characterNumber; ++i) {
            assertEquals(map.get(charactersInOrder.get(characterNumber - i - 1)), characterNumber - i);
        }
    }


    @Test
    void multipleCharacters_RepeatingFrequencies() {
        StringBuilder builder = new StringBuilder();
        Set<Character> set = new HashSet<>();
        List<Integer> frequencies = new ArrayList<>();
        List<Character> charactersInOrder = new ArrayList<>();
        int characterNumber = ThreadLocalRandom.current().nextInt(3, CHARACTER_SOURCE.length());
        int maxCharacterOccurrences = EXPECTED_MAX_STRING_LENGTH / characterNumber;
        int count = 0;
        int numberOfCharacters;
        while (count < characterNumber - 2) {
            numberOfCharacters = ThreadLocalRandom.current().nextInt(1, characterNumber - 1 - count);
            addCharactersWithRepeatingFrequencies(builder, set, frequencies, charactersInOrder, maxCharacterOccurrences, numberOfCharacters);
            count += numberOfCharacters;
        }
        // add the last two characters separately to guarantee presence of matching frequencies
        numberOfCharacters = 2;
        addCharactersWithRepeatingFrequencies(builder, set, frequencies, charactersInOrder, maxCharacterOccurrences, numberOfCharacters);


        LinkedHashMap<Character, Integer> map = service.calculateFrequency(shuffleString(builder.toString()));

        assertEquals(set, map.keySet());
        List<Integer> values = map.values().stream().toList();
        assertEquals(characterNumber, values.size());
        assertDescending(values);
        for (int i = 0; i < characterNumber; ++i) {
            assertEquals(map.get(charactersInOrder.get(i)), frequencies.get(i));
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {EXPECTED_MAX_STRING_LENGTH - 1, EXPECTED_MAX_STRING_LENGTH})
    void multipleCharacters_maxStringLength(int length) {
        multipleCharacterSuccessTest(length);
    }

    private static void addCharactersWithRepeatingFrequencies(StringBuilder builder, Set<Character> set,
                                                             List<Integer> frequencies, List<Character> charactersInOrder,
                                                             int maxOccurrences, int numberOfCharacters) {
        int frequency = ThreadLocalRandom.current().nextInt(1, maxOccurrences + 1);
        while (numberOfCharacters > 0) {
            char ch = getRandomCharacter();
            while (set.contains(ch)) {
                ch = getRandomCharacter();
            }
            appendNTimes(builder, frequency, ch);
            set.add(ch);
            frequencies.add(frequency);
            charactersInOrder.add(ch);
            --numberOfCharacters;
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

    private void multipleCharacterSuccessTest(int total) {
        StringBuilder builder = new StringBuilder();
        Set<Character> set = new HashSet<>();
        List<Character> charactersInOrder = new ArrayList<>();
        List<Integer> frequencies = new ArrayList<>();
        int count = 0;
        while (count < total) {
            char ch = getRandomCharacter();
            while (set.contains(ch)) {
                ch = getRandomCharacter();
            }
            int frequency = ThreadLocalRandom.current().nextInt(1, total - count + 1);
            appendNTimes(builder, frequency, ch);
            set.add(ch);
            frequencies.add(frequency);
            charactersInOrder.add(ch);
            count += frequency;
        }

        HashMap<Character, Integer> map = service.calculateFrequency(shuffleString(builder.toString()));

        assertEquals(set, map.keySet());
        assertDescending(map.values().stream().toList());
        for (int i = 0; i < frequencies.size(); ++i) {
            assertEquals(map.get(charactersInOrder.get(i)), frequencies.get(i));
        }
    }
}
