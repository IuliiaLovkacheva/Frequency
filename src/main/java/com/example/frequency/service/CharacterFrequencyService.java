package com.example.frequency.service;

import com.example.frequency.exception.InputLengthException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CharacterFrequencyService {
    private static final int MAX_STRING_LENGTH = 7000;
    public LinkedHashMap<Character, Integer> calculateFrequency(String string) {
        if (string.length() > MAX_STRING_LENGTH) {
            throw new InputLengthException();
        }
        HashMap<Character, Integer> frequencyData = new HashMap<>();
        for (int i = 0; i < string.length(); ++i) {
            Character ch = string.charAt(i);
            if (frequencyData.containsKey(ch)) {
                frequencyData.put(ch, frequencyData.get(ch) + 1);
            } else {
                frequencyData.put(ch, 1);
            }
        }
        LinkedHashMap<Character, Integer> result = new LinkedHashMap<>();
        frequencyData.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEach(e -> result.put(e.getKey(),
                e.getValue()));
        return result;
    }
}
