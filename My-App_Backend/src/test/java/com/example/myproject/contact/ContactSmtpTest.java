package com.example.myproject.contact;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.Session;
import java.util.Properties;
import static org.junit.jupiter.api.Assertions.*;

/** Real JavaMail exchange with a loopback-only SMTP sink, never Gmail. */
class ContactSmtpTest {
    @Test void sendsUtf8MessageThroughLocalSmtp() throws Exception {
        try(ServerSocket server=new ServerSocket(0,1,InetAddress.getByName("127.0.0.1"))){
            server.setSoTimeout(10000); var executor=Executors.newSingleThreadExecutor();
            try {
                Future<String> received=executor.submit(()->{
                    try(Socket socket=server.accept()){
                        socket.setSoTimeout(10000);
                        var in=new BufferedReader(new InputStreamReader(socket.getInputStream(),StandardCharsets.UTF_8));
                        var out=new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),StandardCharsets.UTF_8),true);
                        out.print("220 localhost test sink\r\n");out.flush();
                        String line;StringBuilder message=new StringBuilder();boolean data=false;
                        while((line=in.readLine())!=null){
                            if(data){if(line.equals(".")){data=false;out.print("250 accepted\r\n");out.flush();}else message.append(line).append("\r\n");}
                            else if(line.startsWith("EHLO")||line.startsWith("HELO")){out.print("250 localhost\r\n");out.flush();}
                            else if(line.equals("DATA")){data=true;out.print("354 send data\r\n");out.flush();}
                            else if(line.equals("QUIT")){out.print("221 bye\r\n");out.flush();break;}
                            else{out.print("250 OK\r\n");out.flush();}
                        }return message.toString();
                    }
                });
                var sender=new JavaMailSenderImpl();sender.setHost("127.0.0.1");sender.setPort(server.getLocalPort());sender.setDefaultEncoding("UTF-8");
                sender.getJavaMailProperties().setProperty("mail.smtp.connectiontimeout","5000");sender.getJavaMailProperties().setProperty("mail.smtp.timeout","5000");
                var service=new ContactService(new ContactProperties(),sender);
                service.send(new ContactRequest("Test Äöü","visitor@example.com","","kitchen","Küchenrenovation prüfen, bitte.",true,""));
                String raw=received.get(10,TimeUnit.SECONDS);var message=new MimeMessage(Session.getInstance(new Properties()),new ByteArrayInputStream(raw.getBytes(StandardCharsets.UTF_8)));
                assertEquals(ContactProperties.MAILBOX,message.getFrom()[0].toString());assertEquals(ContactProperties.MAILBOX,message.getAllRecipients()[0].toString());
                assertEquals("visitor@example.com",message.getReplyTo()[0].toString());assertTrue(message.getContent().toString().contains("Küchenrenovation prüfen"));
            }finally{executor.shutdownNow();}
        }
    }
}
