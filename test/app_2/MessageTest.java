/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package app_2;

import org.junit.Test;
import static org.junit.Assert.*;

public class MessageTest {

    @Test
    public void testMessageContent() {

        String message =
                "Did you get the cake?";

        assertEquals(
                "Did you get the cake?",
                message
        );
    }

    @Test
    public void testLongestMessage() {

        String[] messages = {
            "Hi",
            "Where are you? You are late!"
        };

        String longest = "";

        for(String msg : messages) {

            if(msg.length() > longest.length()) {
                longest = msg;
            }
        }

        assertEquals(
                "Where are you? You are late!",
                longest
        );
    }

    @Test
    public void testSearchMessage() {

        String[] messages = {
            "Hello",
            "Dinner at 7"
        };

        String found = "";

        for(String msg : messages) {

            if(msg.equals("Dinner at 7")) {
                found = msg;
            }
        }

        assertEquals(
                "Dinner at 7",
                found
        );
    }
}
