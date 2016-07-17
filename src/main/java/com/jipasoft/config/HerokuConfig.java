package com.jipasoft.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.jipasoft.util.Profiles;

@Profile(Profiles.HEROKU)
@Configuration
public class HerokuConfig {

}
