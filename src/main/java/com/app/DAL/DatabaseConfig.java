package com.app.DAL;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EnableTransactionManagement
public class DatabaseConfig {

	  @Bean
	  public DataSource dataSource() {
	    DriverManagerDataSource dataSource = new DriverManagerDataSource();
	    dataSource.setUrl(env.getProperty("JDBC_DATABASE_URL"));
	    dataSource.setUsername(env.getProperty("JDBC_DATABASE_USERNAME"));
	    dataSource.setPassword(env.getProperty("JDBC_DATABASE_PASSWORD"));
	    return dataSource;
	  }
	
	  @Bean
	  public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
	    
		  LocalContainerEntityManagerFactoryBean entityManagerFactory =
	        new LocalContainerEntityManagerFactoryBean();   
	    
		  entityManagerFactory.setDataSource(dataSource); 
		  entityManagerFactory.setPackagesToScan(env.getProperty("entitymanager.packagesToScan"));
		  HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		  entityManagerFactory.setJpaVendorAdapter(vendorAdapter);
	      
		  return entityManagerFactory;
	  }
	
	  @Bean
	  public JpaTransactionManager transactionManager() {
	    JpaTransactionManager transactionManager = 
	        new JpaTransactionManager();
	    transactionManager.setEntityManagerFactory(
	        entityManagerFactory.getObject());
	    return transactionManager;
	  }
	  
	  @Bean
	  public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
	    return new PersistenceExceptionTranslationPostProcessor();
	  }
	  
	  @Autowired
	  private Environment env;
	
	  @Autowired
	  private DataSource dataSource;
	
	  @Autowired
	  private LocalContainerEntityManagerFactoryBean entityManagerFactory;

}