package br.com.fiap.campusgigs.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

// Mesmo padrão ensinado no tutorial de JWT: as chaves RSA usadas para assinar
// e validar os tokens são carregadas via application.yml (rsa.private-key / rsa.public-key).
@ConfigurationProperties(prefix = "rsa")
public record RsaKeyProperties(RSAPublicKey publicKey, RSAPrivateKey privateKey) {}
