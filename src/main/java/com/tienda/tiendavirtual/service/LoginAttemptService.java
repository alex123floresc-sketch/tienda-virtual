package com.tienda.tiendavirtual.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private static final int MAX_INTENTOS = 5;
    private final ConcurrentHashMap<String, Integer> intentos = new ConcurrentHashMap<>();

    public void registrarFallo(String email) {
        intentos.merge(email, 1, Integer::sum);
    }

    public void registrarExito(String email) {
        intentos.remove(email);
    }

    public boolean estaBloqueado(String email) {
        return intentos.getOrDefault(email, 0) >= MAX_INTENTOS;
    }

    public int intentosRestantes(String email) {
        return Math.max(0, MAX_INTENTOS - intentos.getOrDefault(email, 0));
    }
}