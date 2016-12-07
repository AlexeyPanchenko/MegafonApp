package com.example.megafonbalance;

import com.example.megafonbalance.model.User;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void userTest() throws Exception {
        String name = "Name1";
        String phone = "159965578";
        String wigetKey = "165651dsf6165df651s6f15";

        User user = new User(name, phone, wigetKey);

        assertEquals(name, user.getName());
        assertEquals(phone, user.getPhone());
        assertEquals(wigetKey, user.getWigetKey());

        String name1 = "Name2";
        String phone1 = "789652";
        String wigetKey1 = "ery322rm325678mk79k";

        User user1 = new User(name1, phone1, wigetKey1);

        assertNotSame(user, user1);
        assertNotEquals(user.getName(), user1.getName());
        assertNotEquals(user.getPhone(), user1.getPhone());
        assertNotEquals(user.getWigetKey(), user1.getWigetKey());

    }
}