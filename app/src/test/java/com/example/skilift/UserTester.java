package com.example.skilift;

import com.example.skilift.models.User;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * JUnit test cases for the methods in the {@link User} for conversion between distance units.
 *
 * @author Anderson Porta
 */
public class UserTester {
    private User user;

    @Before
    public void initialize() { user = new User("u0831946", "Anderson Porta", "8011234567", "anderson.porta@gmail.com"); }

    @Test
    public void getName() { assertEquals("Anderson Porta", user.getName()); }

    @Test
    public void getFirstName() { assertEquals("Anderson", user.getFirstName()); }

    @Test
    public void getPhone() { assertEquals("8011234567", user.getPhone()); }

    @Test
    public void getEmail() { assertEquals("anderson.porta@gmail.com", user.getEmail()); }

    @Test
    public void getuID() { assertEquals("u0831946", user.getuID()); }
}
