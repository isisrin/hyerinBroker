package hyerin.broker.service;

import hyerin.broker.domain.CreateServiceInstanceBindingRequest;
import hyerin.broker.domain.DeleteServiceInstanceBindingRequest;
import hyerin.broker.domain.ServiceInstanceBinding;
import hyerin.broker.exception.ServiceBrokerException;
import hyerin.broker.exception.ServiceInstanceBindingExistsException;

/**
 * 서비스 인스턴스 바인딩 관련 서비스가 제공해야하는 메소드를 정의한 인터페이스 클래스
 * 
 * @author 송창학
 * @date 2015.0629
 */

public interface ServiceInstanceBindingService {

	/**
	 * Create a new binding to a service instance.
	 * @param createServiceInstanceBindingRequest containing parameters sent from Cloud Controller
	 * @return The newly created ServiceInstanceBinding
	 * @throws ServiceInstanceBindingExistsException if the same binding already exists
	 * @throws ServiceBrokerException on internal failure
	 */
	ServiceInstanceBinding createServiceInstanceBinding(
			CreateServiceInstanceBindingRequest createServiceInstanceBindingRequest)
			throws ServiceInstanceBindingExistsException, ServiceBrokerException;

	/**
	 * Delete the service instance binding. If a binding doesn't exist, 
	 * return null.
	 * @param deleteServiceInstanceBindingRequest containing parameters sent from Cloud Controller
     * @return The deleted ServiceInstanceBinding or null if one does not exist
     * @throws ServiceBrokerException on internal failure
	 */
	ServiceInstanceBinding deleteServiceInstanceBinding(
			DeleteServiceInstanceBindingRequest deleteServiceInstanceBindingRequest) 
	        throws ServiceBrokerException;
	
}
