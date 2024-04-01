package com.example.eventapp;

import static junit.framework.TestCase.assertEquals;
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

    @Test
    public void testProfilePicDetermination(){
        // Case when first character is between 'a' and 'h'
        assertEquals("First", CheckCustomizeProfileData.determineProfilePic("apple"));
        assertEquals("First", CheckCustomizeProfileData.determineProfilePic("Apple"));

        // Case when first character is 'i' or 'j'
        assertEquals("Second", CheckCustomizeProfileData.determineProfilePic("ice"));
        assertEquals("Second", CheckCustomizeProfileData.determineProfilePic("jack"));

        // Case when first character is outside 'a' to 'j'
        assertEquals("Third", CheckCustomizeProfileData.determineProfilePic("kite"));
        assertEquals("Third", CheckCustomizeProfileData.determineProfilePic("Zebra"));

        // Case when username is an empty string
        assertEquals("", CheckCustomizeProfileData.determineProfilePic(""));
    }
}
