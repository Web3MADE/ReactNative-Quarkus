package org.acme.services;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.microprofile.jwt.Claims;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class JwtTokenService {
    public String generateJwtToken(String issuer, String upn, String role, String birthdate) {
        Set<String> groups = determineGroups(role);

        String token = Jwt.issuer(issuer).upn(upn).groups(groups)
                .claim(Claims.birthdate.name(), birthdate).sign();
        System.out.println("Generated JWT: " + token);
        return token;

    }

    private Set<String> determineGroups(String role) {
        Set<String> groups = new HashSet<>();
        // Add basic "User" role to everyone, modify as needed
        groups.add("User");

        // Additional roles based on input
        switch (role.toUpperCase()) {
            case "ADMIN":
                groups.add("Admin");
                break;
            case "USER":
                // Already added "User" above, no additional roles
                break;
            default:
                System.out.println("Note: Adding default 'User' group only.");
                // Possible to throw an exception or handle it differently
                break;
        }
        return groups;
    }
}
