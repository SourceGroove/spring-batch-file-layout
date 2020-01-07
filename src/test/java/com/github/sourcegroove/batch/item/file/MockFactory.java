package com.github.sourcegroove.batch.item.file;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MockFactory {

    public static List<MockRoleRecord> getRoles(){
        List<MockRoleRecord> records = new ArrayList<>();
        records.add(getSystemAdminRole());
        records.add(getUserRole());
        return records;
    }

    public static List<MockUserRecord> getUsers(long count){
        List<MockUserRecord> records = new ArrayList<>();
        for(int i=0; i < count; i++){
            MockUserRecord r = new MockUserRecord();
            r.setRecordType("USER");
            r.setFirstName("First_" + i);
            r.setLastName("Last_" + i);
            r.setDateOfBirth(LocalDate.of(1978, 9, 30));
            records.add(r);
        }
        return records;
    }
    public static List<MockRoleRecord> getRoles(long count){
        List<MockRoleRecord> records = new ArrayList<>();
        for(int i=0; i < count; i++){
            MockRoleRecord r = new MockRoleRecord();
            r.setRecordType("ROLE");
            r.setRoleKey("KEY_" + i);
            r.setRole("ROLE_" + i);
            records.add(r);
        }
        return records;
    }

    public static List<MockUserRecord> getUsers(){
        List<MockUserRecord> records = new ArrayList<>();
        records.add(getNeo());
        records.add(getTrinity());
        return records;
    }

    public static void assertSystemAdminRole(MockRoleRecord role) {
        assertNotNull(role);
        MockRoleRecord r = getSystemAdminRole();
        assertEquals(r.getRoleKey(), role.getRoleKey());
        assertEquals(r.getRole(), role.getRole());
    }
    public static void assertUserRole(MockRoleRecord role) {
        assertNotNull(role);
        MockRoleRecord r = getUserRole();
        assertEquals(r.getRecordType(), role.getRecordType());
        assertEquals(r.getRoleKey(), role.getRoleKey());
        assertEquals(r.getRole(), role.getRole());
    }
    public static void assertNeo(MockUserRecord record) {
        assertNotNull(record);
        MockUserRecord r = getNeo();
        assertEquals(r.getRecordType(), record.getRecordType());
        assertEquals(r.getUsername(), record.getUsername());
        assertEquals(r.getFirstName(), record.getFirstName());
        assertEquals(r.getLastName(), record.getLastName());
        assertEquals(r.getDateOfBirth(), record.getDateOfBirth());
    }
    public static void assertTrinity(MockUserRecord record) {
        assertNotNull(record);
        MockUserRecord r = getTrinity();
        assertEquals(r.getRecordType(), record.getRecordType());
        assertEquals(r.getUsername(), record.getUsername());
        assertEquals(r.getFirstName(), record.getFirstName());
        assertEquals(r.getLastName(), record.getLastName());
        assertEquals(r.getDateOfBirth(), record.getDateOfBirth());
    }

    public static Resource getResource(String file){
        return new ClassPathResource("files/" + file);
    }

    public static MockUserRecord getNeo(){
        MockUserRecord record = new MockUserRecord();
        record.setRecordType("USER");
        record.setUsername("0001");
        record.setFirstName("Neo");
        record.setLastName("Anderson");
        record.setDateOfBirth(LocalDate.of(1978, 9, 30));
        return record;
    }
    public static MockUserRecord getTrinity(){
        MockUserRecord record = new MockUserRecord();
        record.setRecordType("USER");
        record.setUsername("0002");
        record.setFirstName("Trinity");
        record.setLastName("Smith");
        record.setDateOfBirth(LocalDate.of(1980, 6, 27));
        return record;
    }
    public static MockRoleRecord getSystemAdminRole(){
        MockRoleRecord record = new MockRoleRecord();
        record.setRecordType("ROLE");
        record.setRole("SYSTEM_ADMIN");
        record.setRoleKey("000A");
        return record;
    }
    public static MockRoleRecord getUserRole(){
        MockRoleRecord record = new MockRoleRecord();
        record.setRecordType("ROLE");
        record.setRole("USER");
        record.setRoleKey("000B");
        return record;
    }

}
