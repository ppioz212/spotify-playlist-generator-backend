package com.asuresh.spotifyplaylistcompiler.jdbcdao;

import com.asuresh.spotifyplaylistcompiler.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;

public class JdbcUserDao {
    private final JdbcTemplate jdbcTemplate;

    public JdbcUserDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void createUser(User user) {
        String sql = "INSERT INTO user_profile (id, display_name) " +
                "VALUES (?, ?) ON CONFLICT (id) DO NOTHING;";
        jdbcTemplate.update(sql,
                user.getId(),
                user.getDisplayName());
    }

    public boolean checkUserExist(User user) {
        String sql = "SELECT * FROM user_profile WHERE id = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, user.getId());
        return result.next();
    }
}
