package com.redhat.bulkclaim;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.UserTaskService;
import org.jbpm.services.api.admin.ProcessInstanceAdminService;
import org.kie.server.services.api.KieServerApplicationComponentsService;
import org.kie.server.services.api.KieServerRegistry;
import org.kie.server.services.api.SupportedTransports;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomBulkClaimExtension implements KieServerApplicationComponentsService {

		private static final String OWNER_EXTENSION = "jBPM"; 
		
		Logger logger = LoggerFactory.getLogger(CustomBulkClaimExtension.class);

	    public Collection<Object> getAppComponents(String extension, SupportedTransports type, Object... services) {	        // Do not accept calls from extensions other than the owner extension:
	        if ( !OWNER_EXTENSION.equals(extension) ) {
	            return Collections.emptyList();
	        }

	    logger.info("Registering CustomBulkClaimExtension");
		
		RuntimeDataService runtimeService = null;
		ProcessInstanceAdminService adminService = null;
		ProcessService processService = null;
		UserTaskService userTaskService = null;
		 KieServerRegistry context = null;

		for (Object object : services) {
			logger.info("found service {}", object);
			if (RuntimeDataService.class.isAssignableFrom(object.getClass())) {
				runtimeService = (RuntimeDataService) object;
				logger.info("Found runtimeService  service {}", runtimeService);
				continue;
			} else if (ProcessInstanceAdminService.class.isAssignableFrom(object.getClass())) {
				adminService = (ProcessInstanceAdminService) object;
				logger.info("Found admin  service {}", adminService);
				continue;
			}
			
			else if (ProcessService.class.isAssignableFrom(object.getClass())) {
				processService = (ProcessService) object;
				logger.info("Found process  service {}", processService);
				continue;
			}else if (UserTaskService.class.isAssignableFrom(object.getClass())) {
				userTaskService = (UserTaskService) object;
				logger.info("Found task  service {}", userTaskService);
				continue;
			}else if (KieServerRegistry.class.isAssignableFrom(object.getClass())) {
				context = (KieServerRegistry) object;
				logger.info("Found KieServerRegistry  service {}", context);
				continue;
			}
		}

	        List<Object> components = new ArrayList<Object>(1);
	        if( SupportedTransports.REST.equals(type) ) {
	            components.add(new BulkClaimResource(runtimeService,adminService,processService, userTaskService, context));  
	        }
	        logger.info("CustomBulkClaimExtension registered");

	        return components;
	    }


}
