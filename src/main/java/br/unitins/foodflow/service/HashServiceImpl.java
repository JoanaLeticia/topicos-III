package br.unitins.foodflow.service;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HashServiceImpl implements HashService {

    // sequencia aleatória a ser adicionada na senha
    private String salt = "#blahxyz17";
    // contagem de iteracoes
    private Integer iterationCount = 405;
    // comprimento do hash em bits
    private Integer keyLength = 512;

    @Override
    public String getHashSenha(String senha) {
        try {
            byte[] result = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
                    .generateSecret(
                            new PBEKeySpec(senha.toCharArray(), salt.getBytes(), iterationCount, keyLength))
                    .getEncoded();
            return Base64.getEncoder().encodeToString(result);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean verificarSenha(String senhaTextoPuro, String senhaHash) {
        try {
            // Gera o hash da senha fornecida
            String hashParaVerificar = this.getHashSenha(senhaTextoPuro);
            
            // Compara os hashes de forma segura contra timing attacks
            return slowEquals(hashParaVerificar, senhaHash);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean slowEquals(String a, String b) {
        byte[] aBytes = a.getBytes(StandardCharsets.UTF_8);
        byte[] bBytes = b.getBytes(StandardCharsets.UTF_8);
        
        int diff = aBytes.length ^ bBytes.length;
        for (int i = 0; i < aBytes.length && i < bBytes.length; i++) {
            diff |= aBytes[i] ^ bBytes[i];
        }
        return diff == 0;
    }

    public static void main(String[] args) {
        HashService hash = new HashServiceImpl();
        System.out.println(hash.getHashSenha("123456"));
    }

}