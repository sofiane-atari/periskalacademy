package periskal.academy.backend.security;

public class SecurityRoles {

    private SecurityRoles() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    // Individual roles
   public static final String SKIPPER = "hasRole('ROLE_SKIPPER')";
   public static final String FLEETMANAGER = "hasRole('ROLE_FLEETMANAGER')";
   public static final String ADMIN = "hasRole('ROLE_ADMINISTRATOR')";
   public static final String SUPPORT = "hasRole('ROLE_SUPPORT')";

   // Two-role combinations (highest privilege first)
   public static final String ADMIN_SUPPORT = "hasAnyRole('ROLE_ADMINISTRATOR', 'ROLE_SUPPORT')";
   public static final String ADMIN_FLEETMANAGER = "hasAnyRole('ROLE_ADMINISTRATOR', 'ROLE_FLEETMANAGER')";
   public static final String ADMIN_SKIPPER = "hasAnyRole('ROLE_ADMINISTRATOR', 'ROLE_SKIPPER')";
   public static final String SUPPORT_FLEETMANAGER = "hasAnyRole('ROLE_SUPPORT', 'ROLE_FLEETMANAGER')";
   public static final String SUPPORT_SKIPPER = "hasAnyRole('ROLE_SUPPORT', 'ROLE_SKIPPER')";
   public static final String FLEETMANAGER_SKIPPER = "hasAnyRole('ROLE_FLEETMANAGER', 'ROLE_SKIPPER')";

   // Three-role combinations (highest privilege first)
   public static final String ADMIN_SUPPORT_FLEETMANAGER = "hasAnyRole('ROLE_ADMINISTRATOR', 'ROLE_SUPPORT', 'ROLE_FLEETMANAGER')";
   public static final String ADMIN_SUPPORT_SKIPPER = "hasAnyRole('ROLE_ADMINISTRATOR', 'ROLE_SUPPORT', 'ROLE_SKIPPER')";
   public static final String ADMIN_FLEETMANAGER_SKIPPER = "hasAnyRole('ROLE_ADMINISTRATOR', 'ROLE_FLEETMANAGER', 'ROLE_SKIPPER')";
   public static final String SUPPORT_FLEETMANAGER_SKIPPER = "hasAnyRole('ROLE_SUPPORT', 'ROLE_FLEETMANAGER', 'ROLE_SKIPPER')";

   // All roles combination
   public static final String ALL_ROLES = "hasAnyRole('ROLE_ADMINISTRATOR', 'ROLE_SUPPORT', 'ROLE_FLEETMANAGER', 'ROLE_SKIPPER')";
}
