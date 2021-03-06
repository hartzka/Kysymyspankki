package kh.kysymyspankki.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import kh.kysymyspankki.database.Database;
import kh.kysymyspankki.domain.Kysymys;

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
        } catch (Exception ex) {
            Logger.getLogger(KysymysDao.class.getName()).log(Level.SEVERE, null, ex);
        }

        return kysymykset;
    }

        @Override
    public Kysymys saveOrUpdate(Kysymys object) throws SQLException {
        
        
        if (object.getKurssi().equals("") || object.getAihe().equals("") || object.getKysymysteksti().equals("")) return null;
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO Kysymys (kurssi, aihe, kysymysteksti) VALUES (?, ?, ?)");
            stmt.setString(1, object.getKurssi());
            stmt.setString(2, object.getAihe());
            stmt.setString(3, object.getKysymysteksti());
            
            stmt.executeUpdate();
        } catch (Exception ex) {
            Logger.getLogger(KysymysDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            return findByName(object.getKysymysteksti());
        } catch (Exception ex) {
            Logger.getLogger(KysymysDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private Kysymys findByName(String name) throws SQLException, Exception {
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
    }   catch (Exception ex) {
            Logger.getLogger(KysymysDao.class.getName()).log(Level.SEVERE, null, ex);
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
