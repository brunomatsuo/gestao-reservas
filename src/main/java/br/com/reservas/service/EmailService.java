package br.com.reservas.service;

public interface EmailService {
    void sendMail(String to, String subject, String text);
}
