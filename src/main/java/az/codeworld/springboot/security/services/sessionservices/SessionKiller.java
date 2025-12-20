package az.codeworld.springboot.security.services.sessionservices;

public interface SessionKiller {
    void invalidateUserSessions(String username);
}
