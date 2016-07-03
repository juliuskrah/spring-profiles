# Spring Profiles Project
[Spring][Spring] [Profiles][Profiles] provide a way to segregate parts of your application configuration and make it only available in certain environments. Any [`@Component`][Component] or [`@Configuration`][Configuration] can be marked with [`@Profile`][Profile] to limit when it is loaded:

```java
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.jipasoft.repository.h2.BaseRepositoryImpl;

@Configuration
@Profile("h2")
@EnableJpaRepositories(basePackageClasses = BaseRepositoryImpl.class)
public class H2Config {

}
```

The focus of this project is to demonstrate how to build a Spring application that runs on multiple database platforms. The database technologies targeted are:

* [H2][H2]
* [MySQL][MySQL]
* [PostgreSQL][PostgreSQL]
* [MongoDB][MongoDb]


[H2]: http://www.h2database.com/html/quickstart.html
[MySQL]: http://dev.mysql.com/doc/
[PostgreSQL]: https://www.postgresql.org/docs/
[MongoDb]: https://docs.mongodb.com/?_ga=1.1231921.1865610331.1455481105
[Spring]: http://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/
[Profiles]: http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-profiles.html
[Configuration]: http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/context/annotation/Configuration.html
[Profile]: http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/context/annotation/Profile.html
[Component]: http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/stereotype/Component.html