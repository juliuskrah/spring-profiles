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

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;

/**
 * Asynchronous mail sender
 * 
 * @author Julius Krah
 *
 */
public class AsyncMailSender extends JavaMailSenderImpl {

	@Async
	@Override
	public void send(SimpleMailMessage simpleMessage) throws MailException {
		super.send(simpleMessage);
	}

	@Async
	@Override
	public void send(SimpleMailMessage... simpleMessages) throws MailException {
		super.send(simpleMessages);
	}
}
