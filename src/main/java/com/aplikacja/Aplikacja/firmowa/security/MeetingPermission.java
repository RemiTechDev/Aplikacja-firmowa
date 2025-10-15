package com.aplikacja.Aplikacja.firmowa.security;

import com.aplikacja.Aplikacja.firmowa.Model.ERoles;
import com.aplikacja.Aplikacja.firmowa.Model.Meeting;
import com.aplikacja.Aplikacja.firmowa.Model.User;
import com.aplikacja.Aplikacja.firmowa.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component("perm") // <<< NAJWAŻNIEJSZE: bean o nazwie 'perm' dla Thymeleaf: ${@perm....}
@RequiredArgsConstructor
public class MeetingPermission {

    private final UserRepository userRepository;

    // === POMOCNICZE ===
    private boolean hasAuth(Authentication auth, String authority) {
        if (auth == null) return false;
        for (GrantedAuthority ga : auth.getAuthorities()) {
            if (authority.equals(ga.getAuthority())) return true;
        }
        return false;
    }

    private boolean isAdmin(Authentication auth)   { return hasAuth(auth, "ROLE_" + ERoles.ADMIN_ROLE.name()); }
    private boolean isManager(Authentication auth) { return hasAuth(auth, "ROLE_" + ERoles.MANAGER_ROLE.name()); }
    private boolean isStaff(Authentication auth)   { return hasAuth(auth, "ROLE_" + ERoles.STAFF_ROLE.name()); }
    private boolean isUser(Authentication auth)    { return hasAuth(auth, "ROLE_" + ERoles.USER_ROLE.name()); }

    /** Użytkownik może tworzyć swoje spotkania/dokumenty, menedżer i admin też */
    public boolean canCreate(Authentication auth) {
        return isAdmin(auth) || isManager(auth) || isStaff(auth) || isUser(auth);
    }

    /** Tylko admin/manager może przypisywać właściciela w formularzu */
    public boolean canAssign(Authentication auth) {
        return isAdmin(auth) || isManager(auth);
    }

    /**
     * Edycja:
     * - ADMIN: zawsze tak
     * - MANAGER: nie może modyfikować spotkań/dok. założonych przez ADMINA
     * - USER/STAFF: nie (zgodnie z Twoją zasadą: mogą tworzyć i komentować, ale nie edytować/usuwać/zmieniać statusu)
     */
    public boolean canEdit(Authentication auth, Meeting meeting, String currentLogin) {
        if (auth == null || meeting == null) return false;
        if (isAdmin(auth)) return true;

        if (isManager(auth)) {
            // blokada modyfikacji pozycji admina
            User owner = meeting.getUser();
            if (owner != null && owner.getRoles().stream().anyMatch(r -> r.getName() == ERoles.ADMIN_ROLE)) {
                return false;
            }
            // manager może edytować pozostałe
            return true;
        }
        // staff/user – brak edycji
        return false;
    }

    /** Kasowanie: te same zasady co edycja */
    public boolean canDelete(Authentication auth, Meeting meeting, String currentLogin) {
        return canEdit(auth, meeting, currentLogin);
    }

    /**
     * Ustawienie właściciela:
     * - ADMIN: dowolny
     * - MANAGER: nie może ustawić na ADMINA
     * - USER/STAFF: może tylko na siebie (opcjonalnie – tu: tylko siebie)
     */
    public boolean canSetOwner(Authentication auth, User target, String currentLogin) {
        if (auth == null || target == null) return false;

        if (isAdmin(auth)) return true;

        if (isManager(auth)) {
            // nie ustawiaj na admina
            return target.getRoles().stream().noneMatch(r -> r.getName() == ERoles.ADMIN_ROLE);
        }

        if (isUser(auth) || isStaff(auth)) {
            // tylko na siebie
            return currentLogin != null && currentLogin.equalsIgnoreCase(target.getLogin());
        }

        return false;
    }
}