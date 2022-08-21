package de.fhwedel.pimpl;

import java.util.Properties;
import java.util.Random;

import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import de.fhwedel.pimpl.model.Customer;
import de.fhwedel.pimpl.model.Room;
import de.fhwedel.pimpl.model.RoomCategory;
import de.fhwedel.pimpl.repos.CustomerRepo;
import de.fhwedel.pimpl.repos.RoomCategoryRepo;
import de.fhwedel.pimpl.repos.RoomRepo;

@Configuration
@EnableJpaRepositories
@EnableTransactionManagement
public class PIMPLPersistence {
	
	

	@Bean
	public Object exampleData(PIMPLConfig config, CustomerRepo cs, RoomRepo rr, RoomCategoryRepo rcr) {
		if (config.isRegenerate()) {
			for (int i = 0; i < 5; i++) {
				Customer c = Customer.createRandomCustomer();
				cs.save(c);
			}	
			
			RoomCategory exampleCatOne = new RoomCategory("Einzelraum", 1, 100, 75);
			rcr.save(exampleCatOne);
			RoomCategory exampleCatTwo = new RoomCategory("Doppelraum", 2, 150, 150);
			rcr.save(exampleCatTwo);
			
			RoomCategory[] roomCats = {exampleCatOne, exampleCatTwo};
			
			Random rng = new Random();
		
			for (int i = 0; i < 10; i++) {
				Room r = Room.createExampleRooms("Raum " + (i + 1));
				r.setRoomCategory(roomCats[Math.abs(rng.nextInt()) % 2]);
				rr.save(r);		
			}
		}
		return new Object();
	}
	
	
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(PIMPLConfig config) {
		Properties props = new Properties();

		if (config.isRegenerate()) {
			props.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.DROP_AND_CREATE);
			props.put(PersistenceUnitProperties.DDL_GENERATION_MODE, PersistenceUnitProperties.DDL_DATABASE_GENERATION);
		}
		props.put(PersistenceUnitProperties.WEAVING, "" + config.getEclipselink().isWeaving());
		props.put(PersistenceUnitProperties.BATCH_WRITING, config.getEclipselink().getJdbcBatchWriting());
		props.put(PersistenceUnitProperties.TARGET_DATABASE, config.getEclipselink().getTargetDatabase());
		props.put(PersistenceUnitProperties.LOGGING_LEVEL, config.getEclipselink().getLoggingLevel());
		props.put(PersistenceUnitProperties.JDBC_URL, config.getDatasource().getUrl());
		props.put(PersistenceUnitProperties.JDBC_USER, config.getDatasource().getUsername());
		props.put(PersistenceUnitProperties.JDBC_PASSWORD, config.getDatasource().getPassword());
		props.put(PersistenceUnitProperties.JDBC_DRIVER, config.getDatasource().getDriver());

		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		EclipseLinkJpaVendorAdapter vendorAdapter = new EclipseLinkJpaVendorAdapter();
		factory.setJpaVendorAdapter(vendorAdapter);
		factory.setPersistenceUnitName(config.getPersistenceunit());
		factory.setJpaProperties(props);
		factory.setPackagesToScan(config.getModelPackage());

		return factory;
	}

	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory);
		return transactionManager;
	}

}
