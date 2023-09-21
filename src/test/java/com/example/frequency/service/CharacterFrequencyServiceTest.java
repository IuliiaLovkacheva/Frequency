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
    private static final int EXPECTED_MAX_STRING_LENGTH = 7000;
    @Autowired
    private CharacterFrequencyService service;

    // randomised vs specific tests
    // various amounts of letters and frequencies - one letter, zero???, 2, 2-9999999(whatever max int is), MAX_INT,
    // MAX_INT - 1
    // same stuff for frequencies (check that the stuff that's not present in the string IS NOT present in the map)
    // need a bunch of positive cases
    // order tests - with a bunch of similar values (5,5,2,2,1), w/ a bunch of different ones
    // which characters work? Is unicode allowed? Can I send it kanji and get a good result? Cyrillics?
    // do characters from different sets - cyrillics + kanji + arabic + latin - work together?
    // 7982 is max length that tomcat will allow
    @Test
    void checkSingleCharacterSingleOccurrence() {
        HashMap<Character, Integer> map = service.calculateFrequency("a");
        Set<Character> set = new HashSet<>();
        set.add('a');
        assertEquals(set, map.keySet());
        assertEquals(1, map.get('a'));
    }

    @Test
    void checkSingleCharacterTwoTimes() {
        HashMap<Character, Integer> map = service.calculateFrequency("aa");
        Set<Character> set = new HashSet<>();
        set.add('a');
        assertEquals(set, map.keySet());
        assertEquals(2, map.get('a'));
    }

    @Test
    void checkSingleCharacterManyTimes() {
        int randomNum = ThreadLocalRandom.current().nextInt(3, EXPECTED_MAX_STRING_LENGTH - 1);
        String inputString = "a".repeat(randomNum);
        Set<Character> set = new HashSet<>();
        set.add('a');

        HashMap<Character, Integer> map = service.calculateFrequency(inputString);

        assertEquals(set, map.keySet());
        assertEquals(randomNum, map.get('a'));
    }

    @Test
    void checkUpperLengthBoundaryMinusOne() {
        String inputString = "a".repeat(EXPECTED_MAX_STRING_LENGTH - 1);
        Set<Character> set = new HashSet<>();
        set.add('a');

        HashMap<Character, Integer> map = service.calculateFrequency(inputString);

        assertEquals(set, map.keySet());
        assertEquals(EXPECTED_MAX_STRING_LENGTH - 1, map.get('a'));
    }

    @Test
    void checkUpperLengthBoundary() {
        String inputString = "a".repeat(EXPECTED_MAX_STRING_LENGTH);
        Set<Character> set = new HashSet<>();
        set.add('a');

        HashMap<Character, Integer> map = service.calculateFrequency(inputString);

        assertEquals(set, map.keySet());
        assertEquals(EXPECTED_MAX_STRING_LENGTH, map.get('a'));
    }

    @Test
    void checkUpperLengthBoundaryPlusOne() {
        String inputString = "a".repeat(EXPECTED_MAX_STRING_LENGTH + 1);
        assertThrows(InputLengthException.class, () -> service.calculateFrequency(inputString));
    }

    @Test
    void checkExceedingBoundary() {
        int randomNum = ThreadLocalRandom.current().nextInt(2, 100);
        String inputString = "a".repeat(EXPECTED_MAX_STRING_LENGTH + randomNum);
        assertThrows(InputLengthException.class, () -> service.calculateFrequency(inputString));
    }

//    private void
}
