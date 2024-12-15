package com.dev.ForoEscolar.enums;

public enum RoleEnum {
    ROLE_ESTUDIANTE,
    ROLE_PROFESOR,
    ROLE_TUTOR,
    ROLE_ADMINISTRADOR;

    public String getAuthority() {
        return this.name();
    }

    // Método útil para obtener el rol sin el prefijo
    public String getRoleSinPrefijo() {
        return this.name().substring("ROLE_".length());
    }
}
