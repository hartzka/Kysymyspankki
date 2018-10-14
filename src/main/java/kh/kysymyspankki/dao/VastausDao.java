package kh.kysymykset.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import kh.kysymykset.database.Database;
import kh.kysymykset.domain.Vastaus;

public class VastausDao implements Dao<Vastaus, Integer> {

    private Database database;

    public VastausDao(Database database) {
        this.database = database;
    }

    @Override
    public Vastaus findOne(Integer key) throws SQLException {
        return findAll().stream().filter(u -> u.getId().equals(key)).findFirst().get();
    }

    @Override
    public List<Vastaus> findAll() throws SQLException {
        List<Vastaus> vast = new ArrayList<>();

        try (Connection conn = database.getConnection();
                ResultSet result = conn.prepareStatement("SELECT id, kysymys_id, vastausteksti, oikein FROM Vastaus").executeQuery()) {

            while (result.next()) {
                vast.add(new Vastaus(result.getInt("id"), result.getInt("kysymys_id"), result.getString("vastausteksti"), result.getBoolean("oikein")));
            }
        }

        return vast;
    }
    
    public List<Vastaus> findAll(int kysymysid) throws SQLException {
        List<Vastaus> vast = findAll();
        List<Vastaus> byKysymys = new ArrayList<>();
        for (Vastaus v : vast) {
            if (v.getKysymysid() == kysymysid) {
                byKysymys.add(v);
            }
        }
        return byKysymys;
    }

    @Override
    public Vastaus saveOrUpdate(Vastaus object) throws SQLException {
        
        if (object.getVastausteksti().equals("") || object.getKysymysid() == 0) return null;
        Vastaus byName = findByName(object.getVastausteksti(), object.getId(), object.getKysymysid());

        if (byName != null) {
            return byName;
        }

        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO Vastaus (kysymys_id, vastausteksti, oikein) VALUES (?, ?, ?)");
            stmt.setInt(1, object.getKysymysid());
            stmt.setString(2, object.getVastausteksti());
            stmt.setBoolean(3, object.getOikein());
            stmt.executeUpdate();
        }

        return null;

    }

    private Vastaus findByName(String vastausteksti, int id, int kysymysId) throws SQLException {
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT kysymys_id, vastausteksti, oikein FROM Vastaus, Kysymys WHERE vastausteksti = ? AND Kysymys.id = Vastaus.kysymys_id AND Kysymys.id = " + kysymysId);
            stmt.setString(1, vastausteksti);
            ResultSet result = stmt.executeQuery();
            if (!result.next()) {
                return null;
            }

            return new Vastaus(-1, result.getInt("kysymys_id"), result.getString("vastausteksti"), result.getBoolean("oikein"));
        }
    }

    @Override
    public void delete(Integer key) throws SQLException {
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM Vastaus WHERE id = ?");
            stmt.setInt(1, key);
            stmt.executeUpdate();
    }
    }
    
        public int findKysymysId(Integer key) throws SQLException {
            int kysymys_id = 0;
        
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT kysymys_id FROM Vastaus, Kysymys WHERE Vastaus.id = ?");
            stmt.setInt(1, key);
            ResultSet res = stmt.executeQuery();
            if(res.next()) {
                kysymys_id = res.getInt("kysymys_id");
            }
    }
            delete(key);
        return kysymys_id;
}
}
