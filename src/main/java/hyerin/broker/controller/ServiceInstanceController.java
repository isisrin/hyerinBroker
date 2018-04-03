package hyerin.broker.controller;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import hyerin.broker.domain.CreateServiceInstanceRequest;
import hyerin.broker.domain.CreateServiceInstanceResponse;
import hyerin.broker.domain.DeleteServiceInstanceRequest;
import hyerin.broker.domain.ErrorMessage;
import hyerin.broker.domain.ServiceDefinition;
import hyerin.broker.domain.ServiceInstance;
import hyerin.broker.domain.UpdateServiceInstanceRequest;
import hyerin.broker.exception.ServiceBrokerException;
import hyerin.broker.exception.ServiceDefinitionDoesNotExistException;
import hyerin.broker.exception.ServiceInstanceDoesNotExistException;
import hyerin.broker.exception.ServiceInstanceExistsException;
import hyerin.broker.exception.ServiceInstanceUpdateNotSupportedException;
import hyerin.broker.service.CatalogService;
import hyerin.broker.service.ServiceInstanceService;

/**
 * 서비스 인스턴스 관련 Provision/Undate Instance/Unprovision API 를 호출 받는 컨트롤러이다.
 * 
 * @author 송창학
 * @date 2015.0629
 */

@Controller
public class ServiceInstanceController extends BaseController {

	public static final String BASE_PATH = "/v2/service_instances";
	
	private static final Logger logger = LoggerFactory.getLogger(ServiceInstanceController.class);
	
	@Autowired
	private ServiceInstanceService service;
	@Autowired
	private CatalogService catalogService;
	
	@Autowired
 	public ServiceInstanceController(ServiceInstanceService service, CatalogService catalogService) {
 		this.service = service;
 		this.catalogService = catalogService;
 	}
	
	@RequestMapping(value = BASE_PATH + "/{instanceId}", method = RequestMethod.PUT)
	public ResponseEntity<CreateServiceInstanceResponse> createServiceInstance( @PathVariable("instanceId") String serviceInstanceId, 
																							@Valid @RequestBody CreateServiceInstanceRequest request) throws
			ServiceDefinitionDoesNotExistException,
			ServiceInstanceExistsException,
			ServiceBrokerException {
		logger.debug("PUT: " + BASE_PATH + "/{instanceId}" 
				+ ", createServiceInstance(), serviceInstanceId = " + serviceInstanceId);
		System.out.println("서비스 아이디 " + request.getServiceDefinitionId());
		ServiceDefinition svc = catalogService.getServiceDefinition(request.getServiceDefinitionId());
		logger.info("이러면 서비스 데피니션은 된 건 가여?" + svc );
		logger.debug("svc..........................");
		if (svc == null) {
			throw new ServiceDefinitionDoesNotExistException(request.getServiceDefinitionId());
		}
		logger.debug("ServiceDefinitionDoesNotExistException");
		
		logger.info("값이 어떻게 되는 거임?" + request.withServiceDefinition(svc));
		logger.info("오늘 안엔 가능할 것 같아!!" + request.withServiceDefinition(svc).and().withServiceInstanceId(serviceInstanceId));
		ServiceInstance instance = service.createServiceInstance(request.withServiceDefinition(svc).and().withServiceInstanceId(serviceInstanceId));
		
		logger.debug("ServiceInstance Created: " + instance.getServiceInstanceId());
		logger.info("인스턴스 만들어짐? " + instance.getServiceInstanceId());
        return new ResponseEntity<CreateServiceInstanceResponse>(
        		new CreateServiceInstanceResponse(instance), 
        		instance.getHttpStatus());
	}
	
	@RequestMapping(value = BASE_PATH + "/{instanceId}", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteServiceInstance(
			@PathVariable("instanceId") String instanceId, 
			@RequestParam("service_id") String serviceId,
			@RequestParam("plan_id") String planId) throws ServiceBrokerException {
		logger.debug( "DELETE: " + BASE_PATH + "/{instanceId}" 
				+ ", deleteServiceInstanceBinding(), serviceInstanceId = " + instanceId 
				+ ", serviceId = " + serviceId
				+ ", planId = " + planId);
		ServiceInstance instance = service.deleteServiceInstance(
				new DeleteServiceInstanceRequest(instanceId, serviceId, planId));
		if (instance == null) {
			return new ResponseEntity<String>("{}", HttpStatus.GONE);
		}
		logger.debug("ServiceInstance Deleted: " + instance.getServiceInstanceId());
        return new ResponseEntity<String>("{}", HttpStatus.OK);
	}
	
	@RequestMapping(value = BASE_PATH + "/{instanceId}", method = RequestMethod.PATCH)
	public ResponseEntity<String> updateServiceInstance(
			@PathVariable("instanceId") String instanceId,
			@Valid @RequestBody UpdateServiceInstanceRequest request) throws 
			ServiceInstanceUpdateNotSupportedException,
			ServiceInstanceDoesNotExistException, 
			ServiceBrokerException {
		
		logger.debug("UPDATE: " + BASE_PATH + "/{instanceId}"
				+ ", updateServiceInstanceBinding(), serviceInstanceId = "
				+ instanceId + ", instanceId = " + instanceId + ", planId = "
				+ request.getPlanId());
		ServiceInstance instance = service.updateServiceInstance(request.withInstanceId(instanceId));
		logger.debug("ServiceInstance updated: " + instance.getServiceInstanceId());
		return new ResponseEntity<String>("{}", HttpStatus.OK);
	}

	
	@ExceptionHandler(ServiceDefinitionDoesNotExistException.class)
	@ResponseBody
	public ResponseEntity<ErrorMessage> handleException(
			ServiceDefinitionDoesNotExistException ex, 
			HttpServletResponse response) {
	    return getErrorResponse(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}
	
	@ExceptionHandler(ServiceInstanceExistsException.class)
	@ResponseBody
	public ResponseEntity<String> handleException(
			ServiceInstanceExistsException ex, 
			HttpServletResponse response) {
	    return new ResponseEntity<String>("{}", HttpStatus.CONFLICT);
	}

	@ExceptionHandler(ServiceInstanceUpdateNotSupportedException.class)
	@ResponseBody
	public ResponseEntity<ErrorMessage> handleException(
			ServiceInstanceUpdateNotSupportedException ex,
			HttpServletResponse response) {
		return getErrorResponse(ex.getMessage(),
				HttpStatus.UNPROCESSABLE_ENTITY);
	}
	
	
}
