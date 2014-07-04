package com.bole.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class QuartzConfig {
	@Autowired
	private DataSource dataSource;

	@Autowired
	private DataSourceTransactionManager transactionManager;

	@Bean(destroyMethod = "destroy")
	public SchedulerFactoryBean getQuartzScheduler() {
		SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
		schedulerFactory.setDataSource(dataSource);
		schedulerFactory.setTransactionManager(transactionManager);
		schedulerFactory.setConfigLocation(new ClassPathResource(
				"quartz.properties"));
		schedulerFactory
				.setApplicationContextSchedulerContextKey("applicationContext");
		return schedulerFactory;
	}
}
