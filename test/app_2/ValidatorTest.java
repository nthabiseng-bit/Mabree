/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package app_2;

import org.junit.Test;
import static org.junit.Assert.*;

public class ValidatorTest {

    @Test
    public void testValidateUsernameValid() {

        ValidationResult result =
                Validator.validateUsername("Ma_br");

        assertTrue(result.success);
    }

    @Test
    public void testValidateUsernameInvalid() {

        ValidationResult result =
                Validator.validateUsername("Nthabiseng");

        assertFalse(result.success);
    }

    @Test
    public void testValidatePasswordValid() {

        ValidationResult result =
                Validator.validatePassword("Nthabi2@");

        assertTrue(result.success);
    }

    @Test
    public void testValidatePasswordInvalid() {

        ValidationResult result =
                Validator.validatePassword("password");

        assertFalse(result.success);
    }

    @Test
    public void testValidatePhoneValid() {

        ValidationResult result =
                Validator.validatePhone("+27712897800");

        assertTrue(result.success);
    }

    @Test
    public void testValidatePhoneInvalid() {

        ValidationResult result =
                Validator.validatePhone("0712897800");

        assertFalse(result.success);
    }
}
