package com.example.frequency.controller;

import com.example.frequency.service.CharacterFrequencyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class CharacterFrequencyController {
    private CharacterFrequencyService service;

    public CharacterFrequencyController(CharacterFrequencyService service) {
        this.service = service;
    }

    // TODO possibly use big decimal?
    // TODO test a string with lengths Integer.MAX_VALUE and I.M_V+1
    @GetMapping("/frequency/{string}")
    HashMap<Character, Integer> calculateFrequency(@PathVariable String string) {
        return service.calculateFrequency(string);
    }
}
