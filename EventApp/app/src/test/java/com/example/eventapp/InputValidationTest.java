package com.example.eventapp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


import com.example.eventapp.helpers.CheckCustomizeProfileData;

import org.junit.Before;
import org.junit.Test;

public class InputValidationTest {
    private CheckCustomizeProfileData checkCustomizeProfileData;

    @Before
    public void setUp() {
        checkCustomizeProfileData = new CheckCustomizeProfileData();
    }

    @Test
    public void testDoesNotContainsSpace() {
        assertFalse(checkCustomizeProfileData.doesNotContainsSpace("username with space"));
        assertTrue(checkCustomizeProfileData.doesNotContainsSpace("usernameWithoutSpace"));
    }

    @Test
    public void testLimitEventDescription() {
        assertFalse(checkCustomizeProfileData.limitEventDescription("A very long event description that exceeds the limit of 100 characters badia ba dasidbas dhasudasd aduavsd "));
        assertTrue(checkCustomizeProfileData.limitEventDescription("Short event description"));
    }

    @Test
    public void testContainsLetters() {
        assertFalse(checkCustomizeProfileData.containsLetters("12345abc"));
        assertTrue(checkCustomizeProfileData.containsLetters("123456"));
    }
}
