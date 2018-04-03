package hyerin.broker.service;

import org.springframework.stereotype.Service;

import hyerin.broker.domain.Catalog;
import hyerin.broker.domain.ServiceDefinition;
import hyerin.broker.exception.ServiceBrokerException;

/**
 * Catalog 서비스가 제공해야하는 메소드를 정의한 인터페이스 클래스
 * 
 * @author 송창학
 * @date 2015.0629
 */

public interface CatalogService {

	/**
	 * @return The catalog of services provided by this broker.
	 */
	Catalog getCatalog() throws ServiceBrokerException;

	/**
	 * @param serviceId  The id of the service in the catalog
	 * @return The service definition or null if it doesn't exist
	 */
	ServiceDefinition getServiceDefinition(String serviceId) throws ServiceBrokerException;
	
}
