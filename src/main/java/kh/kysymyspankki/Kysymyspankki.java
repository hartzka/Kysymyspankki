
package kh.kysymyspankki;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import kh.kysymyspankki.dao.KysymysDao;
import kh.kysymyspankki.dao.VastausDao;
import kh.kysymyspankki.database.Database;
import kh.kysymyspankki.domain.Kysymys;
import kh.kysymyspankki.domain.Vastaus;
import spark.ModelAndView;
import spark.Spark;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

public class Kysymyspankki {

    public static void main(String[] args) throws Exception {
        if (System.getenv("PORT") != null) {
    Spark.port(Integer.valueOf(System.getenv("PORT")));
    }
        Database database = new Database("jdbc:sqlite:kysymyspankki.db");
        KysymysDao kys = new KysymysDao(database);
        VastausDao vas = new VastausDao(database);
        boolean muokkaus = true;

        Spark.get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            
            map.put("kysymykset", kys.findAllByKurssi());
            map.put("muokkaus", muokkaus);
            return new ModelAndView(map, "kysymykset");
        }, new ThymeleafTemplateEngine());
        
        Spark.get("/kysymykset/muokkaus", (req, res) -> {
            HashMap map = new HashMap<>();
            
            map.put("kysymykset", kys.findAllByKurssi());
            map.put("muokkaus", muokkaus);
            return new ModelAndView(map, "kysymykset_muokkaus");
        }, new ThymeleafTemplateEngine());

        Spark.post("/kysymykset/lisaa", (req, res) -> {
            Kysymys kysymys = new Kysymys(-1, req.queryParams("kurssi"), req.queryParams("aihe"), req.queryParams("kysymysteksti"));
            kys.saveOrUpdate(kysymys);

            res.redirect("/");
            return "";
        });
        
        Spark.post("/kysymykset/muokkaus/lisaa", (req, res) -> {
            Kysymys kysymys = new Kysymys(-1, req.queryParams("kurssi"), req.queryParams("aihe"), req.queryParams("kysymysteksti"));
            kys.saveOrUpdate(kysymys);

            res.redirect("/kysymykset/muokkaus");
            return "";
        });

        Spark.post("/kysymykset/muokkaus/delete/:id", (req, res) -> {
            Integer kysymysId = Integer.parseInt(req.params(":id"));
            kys.delete(kysymysId);
            res.redirect("/kysymykset/muokkaus");
            return "";
        });

        Spark.post("/vastaukset/lisaa/:id", (req, res) -> {
            Integer id = Integer.parseInt(req.params(":id"));
            int oikein = Integer.parseInt(req.queryParams("oikein"));
            boolean b = false;
            if(oikein == 1) b = true;
            
            Vastaus vastaus = new Vastaus(-1, id, req.queryParams("vastausteksti"), b);
            vas.saveOrUpdate(vastaus);

            res.redirect("/vastaukset/" + id);
            return "";
        });
        
        Spark.post("/vastaukset/tarkistus/lisaa/:id", (req, res) -> {
            Integer id = Integer.parseInt(req.params(":id"));
            int oikein = Integer.parseInt(req.queryParams("oikein"));
            boolean b = false;
            if(oikein == 1) b = true;
            
            Vastaus vastaus = new Vastaus(-1, id, req.queryParams("vastausteksti"), b);
            vas.saveOrUpdate(vastaus);

            res.redirect("/vastaukset/tarkistus/" + id);
            return "";
        });
        
        Spark.post("/vastaukset/muokkaus/lisaa/:id", (req, res) -> {
            Integer id = Integer.parseInt(req.params(":id"));
            int oikein = Integer.parseInt(req.queryParams("oikein"));
            boolean b = false;
            if(oikein == 1) b = true;
            
            Vastaus vastaus = new Vastaus(-1, id, req.queryParams("vastausteksti"), b);
            vas.saveOrUpdate(vastaus);

            res.redirect("/vastaukset/muokkaus/" + id);
            return "";
        });
        
        Spark.post("/vastaukset/muokkaus/delete/:vastaus_id", (req, res) ->  {
        Integer vastaus_id = Integer.parseInt(req.params(":vastaus_id"));
        int kysymys_id = vas.findKysymysId(vastaus_id);
        res.redirect("/vastaukset/muokkaus/" + kysymys_id);
        return "";
    });
    
        Spark.get("/vastaukset/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            Integer kysymysId = Integer.parseInt(req.params(":id"));
            map.put("vastaukset", vas.findAll(kysymysId));
            map.put("kysymys", kys.findOne(kysymysId));
            
            return new ModelAndView(map, "vastaukset");
        }, new ThymeleafTemplateEngine());
        
        Spark.get("/vastaukset/tarkistus/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            Map<String, String> m = req.params();
            String kysymysId = m.get(":id");
            
            Set<String> s = req.queryParams();
            
            HashMap<Integer, Boolean> checkatut = new HashMap<>();
            
            for(String arvo:s) {
                int i = arvo.length() -1;
                while (true) {
                    String a = (arvo.substring(i, i+1));
                    if (!a.equals("1") && !a.equals("2") && !a.equals("3") && !a.equals("4") && !a.equals("5") && !a.equals("6") && !a.equals("7") && !a.equals("8") && !a.equals("9") && !a.equals("0")) {
                        break;
                    }
                    i--;
                }
                i++;
                int ind = Integer.parseInt(arvo.substring(i, arvo.length()));
                if(arvo.contains("ei") && checkatut.get(ind) == null)  checkatut.put(ind, false);
                else checkatut.put(ind, true);
            }
            
                Integer kysym = Integer.parseInt(kysymysId);
            map.put("vastaukset", vas.findAll(kysym));
            map.put("kysymys", kys.findOne(kysym));
            map.put("checkatut", checkatut);
            
            return new ModelAndView(map, "vastaukset_tarkistus");
        }, new ThymeleafTemplateEngine());
       
        
        Spark.get("/vastaukset/muokkaus/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            Integer kysymysId = Integer.parseInt(req.params(":id"));
            map.put("vastaukset", vas.findAll(kysymysId));
            map.put("kysymys", kys.findOne(kysymysId));
           
            return new ModelAndView(map, "vastaukset_muokkaus");
        }, new ThymeleafTemplateEngine());

    
    }
    
}
