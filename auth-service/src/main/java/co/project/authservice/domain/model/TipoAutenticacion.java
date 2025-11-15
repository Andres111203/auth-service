package co.project.authservice.domain.model;

public enum TipoAutenticacion {
    TRADITIONAL,
    OAUTH_GOOGLE,
    OAUTH_FACEBOOK;

    public boolean esTradicional(){
        return this == TRADITIONAL;
    }

    public boolean esOauth(){
        return this != TRADITIONAL;
    }
}
