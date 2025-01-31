package it.webdev.pw5.itsincom.service;

import io.vertx.ext.auth.impl.hash.SHA512;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class HashCalculator {
    public String hash(String psw) {
        SHA512 sha512 = new SHA512();
        return sha512.hash(null, psw);
    }
}
