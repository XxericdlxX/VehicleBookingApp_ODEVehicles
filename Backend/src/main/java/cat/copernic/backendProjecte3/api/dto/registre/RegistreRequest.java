package cat.copernic.backendProjecte3.api.dto.registre;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RegistreRequest {
    
    @JsonProperty("nom_complet")
    private String nomComplet;
    
    private String email;
    private String password;
    private String dni;
    
    @JsonProperty("data_caducitat_document")
    private String caducitatDni;
    
    private String nacionalitat;
    private String adreca;
    
    @JsonProperty("numero_targeta_credit")
    private String numTargeta;
    
    @JsonProperty("carnet_conduir")
    private String tipusLlicencia;
    
    @JsonProperty("data_caducitat_carnet_conduir")
    private String caducitatLlicencia;

    @JsonProperty("doc_identitat_base64")
    private String docIdentitatBase64;

    @JsonProperty("doc_carnet_base64")
    private String docCarnetBase64;

    public RegistreRequest() {}

    // --- GETTERS I SETTERS ---

    public String getNomComplet() { return nomComplet; }
    public void setNomComplet(String nomComplet) { this.nomComplet = nomComplet; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getCaducitatDni() { return caducitatDni; }
    public void setCaducitatDni(String caducitatDni) { this.caducitatDni = caducitatDni; }

    public String getNacionalitat() { return nacionalitat; }
    public void setNacionalitat(String nacionalitat) { this.nacionalitat = nacionalitat; }

    public String getAdreca() { return adreca; }
    public void setAdreca(String adreca) { this.adreca = adreca; }

    public String getNumTargeta() { return numTargeta; }
    public void setNumTargeta(String numTargeta) { this.numTargeta = numTargeta; }

    public String getTipusLlicencia() { return tipusLlicencia; }
    public void setTipusLlicencia(String tipusLlicencia) { this.tipusLlicencia = tipusLlicencia; }

    public String getCaducitatLlicencia() { return caducitatLlicencia; }
    public void setCaducitatLlicencia(String caducitatLlicencia) { this.caducitatLlicencia = caducitatLlicencia; }

    public String getDocIdentitatBase64() { return docIdentitatBase64; }
    public void setDocIdentitatBase64(String docIdentitatBase64) { this.docIdentitatBase64 = docIdentitatBase64; }

    public String getDocCarnetBase64() { return docCarnetBase64; }
    public void setDocCarnetBase64(String docCarnetBase64) { this.docCarnetBase64 = docCarnetBase64; }
}
