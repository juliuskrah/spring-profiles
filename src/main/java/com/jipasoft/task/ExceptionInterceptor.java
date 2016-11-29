/*
* Copyright 2016, Julius Krah
* by the @authors tag. See the LICENCE in the distribution for a
* full listing of individual contributors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* http://www.apache.org/licenses/LICENSE-2.0
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.jipasoft.task;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.inject.Inject;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import lombok.extern.slf4j.Slf4j;

/**
 * Exception interceptor (AOP)
 * 
 * @author Julius Krah
 *
 */
@Slf4j
public class ExceptionInterceptor implements MethodInterceptor {
	@Inject
	private MailSender mailSender;
	@Value("${spring.user.email}")
	private String[] to;

	@Override
	public Object invoke(MethodInvocation method) throws Throwable {
		Object result = null;
		try {
			result = method.proceed();
		} catch (Exception e) {
			// @formatter:off
			// Building stack trace string
			StringWriter stackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(stackTrace));
			// Building e-mail
			SimpleMailMessage email = new SimpleMailMessage();
			email.setTo(to);
			email.setSubject("[Spring Profiles] Exception in '" + method.getMethod().getName() + "' method");
			email.setText(
				"Exception in: " + method.getMethod().getName() + "\n\n" +
				"Class: " + method.getMethod().getDeclaringClass().getName() + "\n\n" +
				"Message: " + e.getMessage() + "\n\n" +
				"StackTrace:\n" + stackTrace.getBuffer().toString()
			);
			// Sending e-mail
			try {
				this.mailSender.send(email);
			} catch (MailException mailException) {
				log.error(mailException.getMessage());
			}
			throw e;
			// @formatter:on

		}
		return result;
	}
}
