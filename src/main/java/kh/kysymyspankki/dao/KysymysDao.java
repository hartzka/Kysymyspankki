package kh.kysymykset.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import kh.kysymykset.database.Database;
import kh.kysymykset.domain.Kysymys;

public class KysymysDao implements Dao<Kysymys, Integer> {

    private Database database;

    public KysymysDao(Database database) {
        this.database = database;
    }

    @Override
    public Kysymys findOne(Integer key) throws SQLException {
        return findAll().stream().filter(u -> u.getId().equals(key)).findFirst().get();
    }

    @Override
    public List<Kysymys> findAll() throws SQLException {
        List<Kysymys> kysymykset = new ArrayList<>();

        try (Connection conn = database.getConnection();
                ResultSet result = conn.prepareStatement("SELECT id, kurssi, aihe, kysymysteksti FROM Kysymys").executeQuery()) {

            while (result.next()) {
                kysymykset.add(new Kysymys(result.getInt("id"), result.getString("kurssi"), result.getString("aihe"), result.getString("kysymysteksti")));
            }
        }

        return kysymykset;
    }

    /*public List<Kysymys> findNonCompletedForUser(Integer vastusId) throws SQLException {
        String query = "SELECT Task.id, Task.name FROM Task, TaskAssignment\n"
                + "              WHERE Task.id = TaskAssignment.task_id "
                + "                  AND TaskAssignment.user_id = ?\n"
                + "                  AND TaskAssignment.completed = 0";

        List<Kysymys> tasks = new ArrayList<>();

        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet result = stmt.executeQuery();

            while (result.next()) {
                tasks.add(new Kysymys(result.getInt("id"), result.getString("name")));
            }
        }

        return tasks;
    }*/

    /*public List<Kysymys> findAllNotAssigned() throws SQLException {
        List<Kysymys> tasks = new ArrayList<>();

        try (Connection conn = database.getConnection();
                ResultSet result = conn.prepareStatement("SELECT id, name FROM Task WHERE id NOT IN (SELECT task_id FROM TaskAssignment)").executeQuery()) {

            while (result.next()) {
                tasks.add(new Kysymys(result.getInt("id"), result.getString("name")));
            }
        }
        return tasks;
    }*/

    @Override
    public Kysymys saveOrUpdate(Kysymys object) throws SQLException {
        
        /*Kysymys byName = findByName(object.getKurssi());

        if (byName != null) {
            return byName;
        }*/
        if (object.getKurssi().equals("") || object.getAihe().equals("") || object.getKysymysteksti().equals("")) return null;
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO Kysymys (kurssi, aihe, kysymysteksti) VALUES (?, ?, ?)");
            stmt.setString(1, object.getKurssi());
            stmt.setString(2, object.getAihe());
            stmt.setString(3, object.getKysymysteksti());
            
            stmt.executeUpdate();
        }
        return findByName(object.getKysymysteksti());
    }

    private Kysymys findByName(String name) throws SQLException {
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT id, kurssi, aihe, kysymysteksti FROM Kysymys WHERE kurssi = ?");
            stmt.setString(1, name);

            ResultSet result = stmt.executeQuery();
            if (!result.next()) {
                return null;
            }

            return new Kysymys(result.getInt("id"), result.getString("kurssi"), result.getString("aihe"), result.getString("kysymysteksti"));
        }
    }

    @Override
    public void delete(Integer key) throws SQLException {
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM Kysymys WHERE id = ?");
            stmt.setInt(1, key);
            stmt.executeUpdate();
    }
    }

    public HashMap<String, List<Kysymys>> findAllByKurssi() throws SQLException {
        HashMap<String, List<Kysymys>> byKurssi = new HashMap<>();
        
        List<Kysymys> kysymykset = findAll();
        for(Kysymys k : kysymykset) {
            String kurssi = k.getKurssi();
            if (!byKurssi.containsKey(kurssi)) {
                    List<Kysymys> l = new ArrayList<>();
                    l.add(k);
                    byKurssi.put(kurssi, l);
                }
                else byKurssi.get(kurssi).add(k);
        }
        
        return byKurssi;
    }

}
