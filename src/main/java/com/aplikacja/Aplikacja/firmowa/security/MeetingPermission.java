package com.aplikacja.Aplikacja.firmowa.security;

import com.aplikacja.Aplikacja.firmowa.Model.ERoles;
import com.aplikacja.Aplikacja.firmowa.Model.Meeting;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Set;
import org.springframework.security.core.authority.AuthorityUtils;

@Component("perm")
public class MeetingPermission {

    private Set<String> roles(Authentication auth) {
        return auth == null ? Set.of() : AuthorityUtils.authorityListToSet(auth.getAuthorities());
    }

    public boolean canCreate(Authentication auth) {
        Set<String> r = roles(auth);
        return r.contains("ROLE_ADMIN_ROLE") || r.contains("ROLE_MANAGER_ROLE") || r.contains("ROLE_STAFF_ROLE") || r.contains("ROLE_USER_ROLE");
    }

    public boolean canSetOwner(Authentication auth, com.aplikacja.Aplikacja.firmowa.Model.User target, String login) {
        Set<String> r = roles(auth);
        if (r.contains("ROLE_ADMIN_ROLE")) return true;
        if (r.contains("ROLE_MANAGER_ROLE"))
            return target.getRoles().stream().noneMatch(role -> role.getName() == ERoles.ADMIN_ROLE);
        return login != null && target.getLogin().equals(login);
    }

    /** MENEDŻER nie może edytować/usunąć spotkań ADMINA */
    public boolean canEdit(Authentication auth, Meeting meeting, String login) {
        Set<String> r = roles(auth);
        if (r.contains("ROLE_ADMIN_ROLE")) return true;
        if (r.contains("ROLE_MANAGER_ROLE")) {
            // blokada jeśli właścicielem jest ADMIN
            return meeting.getUser() != null && meeting.getUser().getRoles()
                    .stream().noneMatch(role -> role.getName() == ERoles.ADMIN_ROLE);
        }
        // STAFF/USER – tylko własne
        return login != null && meeting.getUser() != null && login.equals(meeting.getUser().getLogin());
    }

    public boolean canDelete(Authentication auth, Meeting meeting, String login) {
        return canEdit(auth, meeting, login); // te same zasady
    }

    /** NOWE: komentarze – menedżer może komentować wszystko (także ADMINA) */
    public boolean canComment(Authentication auth, Meeting meeting, String login) {
        Set<String> r = roles(auth);
        if (r.contains("ROLE_ADMIN_ROLE") || r.contains("ROLE_MANAGER_ROLE")) return true;
        // STAFF/USER: mogą komentować własne
        return login != null && meeting.getUser() != null && login.equals(meeting.getUser().getLogin());
    }

    /** pomocnicze do widoków (czy pokazać sekcję przypisania użytkownika) */
    public boolean canAssign(Authentication auth) {
        Set<String> r = roles(auth);
        return r.contains("ROLE_ADMIN_ROLE") || r.contains("ROLE_MANAGER_ROLE");
    }
}
