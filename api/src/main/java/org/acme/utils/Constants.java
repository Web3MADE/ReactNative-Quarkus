package org.acme.utils;

public class Constants {
    public enum Role {
        USER, ADMIN
    }

    public static final String CONTAINER_NAME = "container-quarkus-azure-storage-blob-async";
    public static final String JWT_ISSUER_URL = "https://example.com/issuer";

    public static final String JWT_BIRTHDATE = "2001-07-13";


    private Constants() {}
}
