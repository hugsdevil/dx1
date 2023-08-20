package com.career.dx1.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.career.dx1.domain.MailAttachement;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

@Service
@Profile("!default")
public class MailService {
    @Value("${spring.sendgrid.api-key}")
    private String apiKey;

    public void sendMail(String from, String subject, String to, String content, MailAttachement attachement) throws Exception {
        SendGrid sg = new SendGrid(apiKey);

        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");

        Mail mail = new Mail(new Email(from), // from
            subject, // subject
            new Email(to), // to
            new Content("text/html", content));

        if (attachement != null) {
            mail.addAttachments(new Attachments.Builder(
                attachement.getFileName(), attachement.getContent()).build());
        }

        try {
            request.setBody(mail.build());

            Response r = sg.api(request);
            switch (r.getStatusCode()) {
            case 401:
                throw new RuntimeException("authorized fail");
            case 202:
                break;
            default:
                throw new RuntimeException(
                    String.format("send fail: status %d %s", r.getStatusCode(), r.getBody()));
            }
        } catch (Exception e) {
            throw e;
        }
    }
}
