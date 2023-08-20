package com.staroot.pingcheck.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;


@Service
public class PingCheckService {

    @Value("${mail.from}")
    private String mailFrom;

    @Value("${mail.to}")
    private String mailTo;

    @Value("${mail.subject}")
    private String mailSubject;

    private final Session mailSession;

    public PingCheckService() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        mailSession = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("starootmaster@gmail.com", "cippxhtgsfxsxcss");
            }
        });
    }

    @Async
    public CompletableFuture<Boolean> performPingCheck(String hostName) {
        boolean isReachable = ping(hostName);
        System.out.println(hostName + " is reachable: " + isReachable);
        return CompletableFuture.completedFuture(isReachable);
    }

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void performPingCheckAndSendEmail() {
        List<String> hostNames = readHostNamesFromFile("hosts.txt");

        int i=0;
        for (String hostName : hostNames) {
            i++;
            System.out.println("hostname ::"+"("+i+") "+hostName);
            CompletableFuture<Boolean> pingResultFuture = performPingCheck(hostName);

            try {
                boolean isReachable = pingResultFuture.get();
                if (!isReachable) {
                    // sendEmail("Ping Check Alert", hostName + " is not reachable!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sendEmail(String subject, String content) {
        try {
            Message message = new MimeMessage(mailSession);
            message.setFrom(new InternetAddress(mailFrom));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailTo));
            message.setSubject(subject);
            message.setText(content);

            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private List<String> readHostNamesFromFile(String fileName) {
        List<String> hostNames = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                hostNames.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hostNames;
    }

    public boolean ping(String hostName) {
        try {
            InetAddress address = InetAddress.getByName(hostName);
            return address.isReachable(3000);
        } catch (UnknownHostException e) {
            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
