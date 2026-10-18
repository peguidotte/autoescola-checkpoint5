package br.com.fiap3espv.AutoEscola3ESPV.shared.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class GerarSenhaHash {
    public static void main(String[] args) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();

        String hash = encoder.encode("admin");
        System.out.println(hash);
    }
}