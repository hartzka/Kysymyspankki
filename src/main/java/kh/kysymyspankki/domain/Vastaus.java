package kh.kysymyspankki.domain;

public class Vastaus {

    private Integer id;
    private Integer kysymysid;
    private String vastausteksti;
    private boolean oikein;

    public Vastaus(Integer id, Integer kysymysid, String vastausteksti, boolean oikein) {
        this.id = id;
        this.kysymysid = kysymysid;
        this.vastausteksti = vastausteksti;
        this.oikein = oikein;
    }

    public Integer getId() {
        return id;
    }
    
    public Integer getKysymysid() {
        return kysymysid;
    }

    public String getVastausteksti() {
        return vastausteksti;
    }
    
    public boolean getOikein() {
        return oikein;
    }

}
