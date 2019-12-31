package com.github.sourcegroove.batch.item.file;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MockFactory {

    public static List<MockUserRecord> getUsers(){
        List<MockUserRecord> records = new ArrayList<>();
        records.add(getNeo());
        records.add(getTrinity());
        return records;
    }
    public static MockUserRecord getNeo(){
        MockUserRecord r1 = new MockUserRecord();
        r1.setUsername("user0001");
        r1.setFirstName("Neo");
        r1.setLastName("Anderson");
        r1.setDateOfBirth(LocalDate.of(1978, 9, 30));
        return r1;
    }
    public static MockUserRecord getTrinity(){
        MockUserRecord r2 = new MockUserRecord();
        r2.setUsername("user0002");
        r2.setFirstName("Trinity");
        r2.setLastName("Smith");
        r2.setDateOfBirth(LocalDate.of(1980, 6, 27));
        return r2;
    }

    public static void assertSystemAdmin(MockRoleRecord role) {
        assertNotNull(role);
        assertEquals("role000A", role.getRoleKey());
        assertEquals("SYSTEM_ADMIN", role.getRole());
    }
    public static void assertUser(MockRoleRecord role) {
        assertNotNull(role);
        assertEquals("role000B", role.getRoleKey());
        assertEquals("USER", role.getRole());
    }
    public static void assertNeo(MockUserRecord user) {
        assertNotNull(user);
        assertEquals("user0001", user.getUsername());
        assertEquals("Neo", user.getFirstName());
        assertEquals("Anderson", user.getLastName());
        assertEquals(LocalDate.of(1978, 9, 30), user.getDateOfBirth());
    }
    public static void assertTrinity(MockUserRecord user) {
        assertNotNull(user);
        assertEquals("user0002", user.getUsername());
        assertEquals("Trinity", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        assertEquals(LocalDate.of(1980, 6, 27), user.getDateOfBirth());
    }

    public static Resource getResource(String file){
        return new ClassPathResource("files/" + file);
    }
}
