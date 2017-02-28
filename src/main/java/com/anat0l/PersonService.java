package com.anat0l;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class PersonService {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    private static class PersonRowMapper implements RowMapper<Person>{
        @Override
        public Person mapRow(ResultSet resultSet, int i) throws SQLException {
            Person person = new Person();
            person.setId(resultSet.getLong("id"));
            person.setName(resultSet.getString("name"));
            person.setAge(resultSet.getInt("age"));
            person.setAdmin(resultSet.getInt("isAdmin"));
            person.setCreatedDate(resultSet.getTimestamp("createdDate"));
            return person;
        }
    }

    public List<Person> getAllPerson() {
        return jdbcTemplate.query(
                "SELECT id, name, age, isAdmin, createdDate FROM persons",new PersonRowMapper());
//                (rs, rowNum) -> new Person(rs.getLong("id"),
//                        rs.getString("name"), rs.getString("career")));
    }

    public void updatePerson(Person person) {
        final String sql = "UPDATE persons SET name=?, age=?, isAdmin=? WHERE id=?";
        jdbcTemplate.update(sql, person.getName(), person.getAge(),person.getAdmin(), person.getId());
    }

    public void insertPerson(Person person) {
        final String sql = "INSERT INTO persons (name,age,isAdmin) VALUES (?,?,?)";
        final String name = person.getName();
        final int career = person.getAge();
        final int isAdmin = person.getAdmin();
        jdbcTemplate.update(sql,new Object[]{name, career, isAdmin});

    }
    public Person getPersonById(int id) {
        //SELECT id,name,career FROM persons WHERE id = value
        final String sql = "SELECT * FROM persons WHERE id = ?";
        Person person = jdbcTemplate.queryForObject(sql, new PersonRowMapper(), id);
        return person;
    }

    public void removePersonById(int id) {
        //DELETE FROM persons WHERE id = value
        String sql = "DELETE FROM persons WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

}