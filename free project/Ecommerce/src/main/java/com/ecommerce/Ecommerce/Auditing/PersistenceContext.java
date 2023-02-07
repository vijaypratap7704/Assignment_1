package com.ecommerce.Ecommerce.Auditing;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

//@Configuration
//@EnableJpaAuditing(dateTimeProviderRef = "dateTimeProvider")
//@EnableJpaRepositories("com.springdata.repositories")
//public class PersistenceContext {
//
//    // Other beans
//
//    @Bean
//    DateTimeService currentTimeDateTimeService() {
//        return new CurrentDateTimeService();
//    }
//
//    @Bean
//    DateTimeProvider dateTimeProvider(DateTimeService dateTimeService) {
//        return new AuditingDateTimeProvider(dateTimeService);
//    }
//}

