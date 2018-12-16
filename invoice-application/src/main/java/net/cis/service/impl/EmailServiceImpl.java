package net.cis.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import net.cis.service.EmailService;
/**
 * Created by NhanNguyen on 19/10/2018
 */
@Service
public class EmailServiceImpl implements EmailService {

	@Autowired
	MailSender mailSender;
	
	@Override
	public void send(String title, String content) {
		SimpleMailMessage emailObj = new SimpleMailMessage();
		emailObj.setFrom("automailer@cis.net.vn");
		emailObj.setTo("operation_iparking@cis.net.vn");
		emailObj.setSubject(title);
		emailObj.setText(content);
		mailSender.send(emailObj);
		
	}

	

}
