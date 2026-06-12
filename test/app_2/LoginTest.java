/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package app_2;

import org.junit.Test;
import static org.junit.Assert.*;

public class LoginTest {

    @Test
    public void testCheckUsernameValid() {

        Login login = new Login();

        assertTrue(login.checkUsername("Ma_br"));
    }

    @Test
    public void testCheckUsernameInvalid() {

        Login login = new Login();

        assertFalse(login.checkUsername("Nthabiseng"));
    }

    @Test
    public void testCheckPasswordValid() {

        Login login = new Login();

        assertTrue(login.checkPassword("Nthabi2@"));
    }

    @Test
    public void testCheckPasswordInvalid() {

        Login login = new Login();

        assertFalse(login.checkPassword("password"));
    }

    @Test
    public void testCheckPhoneNumberValid() {

        Login login = new Login();

        assertTrue(login.checkPhoneNumber("+27712897800"));
    }

    @Test
    public void testRegisterUser() {

        Login login = new Login();

        String result =
                login.registerUser(
                        "Ma_br",
                        "Nthabi2@",
                        "+27712897800"
                );

        assertEquals(
                "User successfully registered.",
                result
        );
    }

    @Test
    public void testLoginUser() {

        Login login = new Login();

        login.registerUser(
                "Ma_br",
                "Nthabi2@",
                "+27712897800"
        );

        assertTrue(
                login.loginUser(
                        "Ma_br",
                        "Nthabi2@"
                )
        );
    }

    @Test
    public void testLoginUserFail() {

        Login login = new Login();

        login.registerUser(
                "Ma_br",
                "Nthabi2@",
                "+27712897800"
        );

        assertFalse(
                login.loginUser(
                        "Wrong",
                        "Wrong"
                )
        );
    }
}
