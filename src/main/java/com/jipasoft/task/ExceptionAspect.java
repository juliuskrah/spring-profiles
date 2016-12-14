package com.jipasoft.task;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZonedDateTime;

import javax.inject.Inject;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class ExceptionAspect {

	@Inject
	private MailSender mailSender;
	@Value("${spring.user.email}")
	private String[] to;

	@Pointcut("within(com.jipasoft.web..*)")
	public void mailingPointcut() {
	}

	@AfterThrowing(pointcut = "mailingPointcut()", throwing = "e")
	public void mailAfterThrowing(JoinPoint joinPoint, Throwable e) {
		// @formatter:off
		// Building stack trace string
		StringWriter stackTrace = new StringWriter();
		e.printStackTrace(new PrintWriter(stackTrace));
		// Building e-mail
		SimpleMailMessage email = new SimpleMailMessage();
		email.setTo(to);
		email.setSubject("[Spring Profiles Aspect] Exception in '" + joinPoint.getSignature().getName() + "' method");
		email.setText(
			"Exception in: " + joinPoint.getSignature().getName() + "\n\n" +
			"Class: " + joinPoint.getSignature().getDeclaringTypeName()  + "\n\n" +
			"Time: " + ZonedDateTime.now() + "\n\n" +
			"Message: " + e.getMessage() + "\n\n" +
			"StackTrace:\n" + stackTrace.getBuffer().toString()
		);
		// Sending e-mail
		try {
			this.mailSender.send(email);
		} catch (MailException mailException) {
			log.error(mailException.getMessage());
		}
	}
}
