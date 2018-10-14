package kh.kysymyspankki.domain;

public class Kysymys {

    private Integer id;
    private String kurssi;
    private String aihe;
    private String kysymysteksti;

    public Kysymys(Integer id, String kurssi, String aihe, String kysymysteksti) {
        this.id = id;
        this.kurssi = kurssi;
        this.aihe = aihe;
        this.kysymysteksti = kysymysteksti;
    }

    public Integer getId() {
        return id;
    }

    public String getKurssi() {
        return kurssi;
    }
    
    public String getAihe() {
        return aihe;
    }
    
    public String getKysymysteksti() {
        return kysymysteksti;
    }

}
