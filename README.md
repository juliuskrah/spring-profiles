# Spring Profiles Project
Spring [Profiles][Profile] provide a way to segregate parts of your application configuration and make it only available in certain environments. Any `@Component` or `[@Configuration][Configuration]` can be marked with `@Profile` to limit when it is loaded:

```java
@Configuration
@Profile("h2")
@EnableJpaRepositories(basePackageClasses = BaseRepositoryImpl.class)
public class H2Config {

}
```


[Profile]: http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-profiles.html
[Configuration]: http://docs.spring.io/spring/docs/4.3.0.RELEASE/javadoc-api/org/springframework/context/annotation/Configuration.html

