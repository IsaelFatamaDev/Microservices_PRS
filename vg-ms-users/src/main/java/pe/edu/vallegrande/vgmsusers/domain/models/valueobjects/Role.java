package pe.edu.vallegrande.vgmsusers.domain.models.valueobjects;

public enum Role {

    SUPER_ADMIN("Super Administrador"),
    ADMIN("Administrador"),
    CLIENT("Cliente"),
    OPERATOR("Operario");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean hasAdminprivileges(){
        return this.equals(SUPER_ADMIN) || this.equals(ADMIN);
    }

    public boolean canManageUsers(){
        return this.equals(SUPER_ADMIN) || this.equals(ADMIN);
    }
}
