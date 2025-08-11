package com.aplikacja.Aplikacja.firmowa.security;

import com.aplikacja.Aplikacja.firmowa.Model.ERoles;
import com.aplikacja.Aplikacja.firmowa.Model.Meeting;
import com.aplikacja.Aplikacja.firmowa.Model.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("perm")
public class MeetingPermission {

    private boolean hasRole(Authentication auth, ERoles role) {
        if (auth == null) return false;
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_" + role.name()));
    }

    public boolean isAdmin(Authentication auth)   { return hasRole(auth, ERoles.ADMIN_ROLE); }
    public boolean isManager(Authentication auth) { return hasRole(auth, ERoles.MANAGER_ROLE); }
    public boolean isStaff(Authentication auth)   { return hasRole(auth, ERoles.STAFF_ROLE); }
    public boolean isUser(Authentication auth)    { return hasRole(auth, ERoles.USER_ROLE); }

    /** Czy może tworzyć spotkania (user/staff tylko „dla siebie”) */
    public boolean canCreate(Authentication auth) {
        // Każdy może utworzyć: admin/manager/staff/user
        return auth != null && (isAdmin(auth) || isManager(auth) || isStaff(auth) || isUser(auth));
    }

    /** Czy może przypisywać spotkanie innemu użytkownikowi w formularzu */
    public boolean canAssign(Authentication auth) {
        // Admin i Manager mogą wybierać użytkownika (manager nie może przypisać do admina – to filtrowane w kontrolerze)
        return auth != null && (isAdmin(auth) || isManager(auth));
    }

    /** Edycja dowolnego spotkania (wg reguł) */
    public boolean canEdit(Authentication auth, Meeting meeting, String currentLogin) {
        if (auth == null || meeting == null) return false;
        if (isAdmin(auth)) return true;
        if (isManager(auth)) {
            // manager wszystko oprócz spotkań adminów
            User owner = meeting.getUser();
            if (owner == null) return true; // defensywnie
            return owner.getRoles().stream().noneMatch(r -> r.getName() == ERoles.ADMIN_ROLE);
        }
        if (isStaff(auth)) {
            // tylko własne
            return meeting.getUser() != null && meeting.getUser().getLogin().equals(currentLogin);
        }
        // Zwykły user — brak edycji
        return false;
    }

    /** Usuwanie – te same reguły co edycja */
    public boolean canDelete(Authentication auth, Meeting meeting, String currentLogin) {
        return canEdit(auth, meeting, currentLogin);
    }

    /** Czy user może ustawić ownera na danego targetUser (tworzenie/edycja) */
    public boolean canSetOwner(Authentication auth, User targetUser, String currentLogin) {
        if (auth == null) return false;
        if (isAdmin(auth)) return true;
        if (isManager(auth)) {
            // nie może ustawić admina
            return targetUser.getRoles().stream().noneMatch(r -> r.getName() == ERoles.ADMIN_ROLE);
        }
        if (isStaff(auth) || isUser(auth)) {
            // może tylko siebie
            return targetUser.getLogin().equals(currentLogin);
        }
        return false;
    }
}