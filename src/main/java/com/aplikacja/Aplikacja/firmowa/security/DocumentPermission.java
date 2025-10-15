package com.aplikacja.Aplikacja.firmowa.security;

import com.aplikacja.Aplikacja.firmowa.Model.Document;
import com.aplikacja.Aplikacja.firmowa.Model.ERoles;
import com.aplikacja.Aplikacja.firmowa.Model.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("documentPermission")
public class DocumentPermission {

    private boolean has(Authentication auth, String role) {
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }

    private boolean isAdmin(Authentication auth)   { return has(auth, "ADMIN_ROLE"); }
    private boolean isManager(Authentication auth) { return has(auth, "MANAGER_ROLE"); }

    public boolean canView(Authentication auth, Document d) {
        return auth != null; // każdy zalogowany
    }

    public boolean canEdit(Authentication auth, Document d, String actorLogin) {
        if (d == null) return false;
        if (isAdmin(auth)) return true;

        // Jeżeli "wrzucił" (d.getUser()) ma rolę ADMIN – manager nie może edytować
        if (wasUploadedByAdmin(d)) return false;

        // Menedżer może edytować, jeśli sam wrzucił lub przypisany (jeśli masz takie pole – tu trzymamy się d.getUser())
        return isManager(auth) && d.getUser() != null &&
                (actorLogin != null && actorLogin.equalsIgnoreCase(d.getUser().getLogin()));
    }

    public boolean canDelete(Authentication auth, Document d, String actorLogin) {
        if (d == null) return false;
        if (isAdmin(auth)) return true;
        if (wasUploadedByAdmin(d)) return false;
        // opcjonalnie: pozwól managerowi usuwać tylko swoje
        return isManager(auth) && d.getUser() != null &&
                actorLogin != null && actorLogin.equalsIgnoreCase(d.getUser().getLogin());
    }

    private boolean wasUploadedByAdmin(Document d) {
        User owner = d.getUser();
        return owner != null && owner.getRoles() != null &&
                owner.getRoles().stream().anyMatch(r -> r.getName() == ERoles.ADMIN_ROLE);
    }
}
