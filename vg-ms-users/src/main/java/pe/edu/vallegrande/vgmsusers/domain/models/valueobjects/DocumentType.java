package pe.edu.vallegrande.vgmsusers.domain.models.valueobjects;

public enum DocumentType {

    DNI("DNI", "Documento Nacional de Identidad", "^[0-9]{8}$"),
    CNE("CNE", "Carnet de Extranjería", "^[A-Za-z0-9]{9}$");

    private final String code;
    private final String description;
    private final String pattern;

    private DocumentType(String code, String description, String pattern){
        this.code = code;
        this.description = description;
        this.pattern = pattern;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getPattern() {
        return pattern;
    }

    public boolean isValidFormat(String documentNumber){
        if(documentNumber == null || documentNumber.isBlank()){
            return false;
        }
        return documentNumber.matches(pattern);
    }
}
