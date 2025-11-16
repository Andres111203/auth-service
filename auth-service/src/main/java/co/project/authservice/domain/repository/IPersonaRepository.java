package co.project.authservice.domain.repository;

import co.project.authservice.domain.model.Persona;

public interface IPersonaRepository {
     Persona savePersona(Persona persona);
     Persona updatePersona(Persona persona);
     Persona findPersonaByIdPersona(String idPersona);
     Persona findPersonaByEmail(String email);
     void deletePersona(Persona persona);
}
