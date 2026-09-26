package com.examguard;

import com.examguard.config.AsyncSyncConfiguration;
import com.examguard.config.EmbeddedSQL;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { ExamguardApp.class, AsyncSyncConfiguration.class, com.examguard.config.JacksonHibernateConfiguration.class })
@EmbeddedSQL
public @interface IntegrationTest {}
