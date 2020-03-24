package com.redhat.bulkclaim;

import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.UserTaskService;
import org.jbpm.services.api.admin.ProcessInstanceAdminService;
import org.kie.server.services.api.KieServerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BulkClaimTaskService {
	

	private static final Logger logger = LoggerFactory.getLogger(BulkClaimTaskService.class);
	private RuntimeDataService runtimeService;
	private ProcessInstanceAdminService adminService;
	private ProcessService processService;
    private UserTaskService userTaskService;
	private KieServerRegistry context;
	public BulkClaimTaskService(RuntimeDataService runtimeService, ProcessInstanceAdminService adminService,
			ProcessService processService, UserTaskService userTaskService, KieServerRegistry context) {
		this.runtimeService = runtimeService;
		this.adminService = adminService;
		this.processService = processService;
	
		this.userTaskService = userTaskService;
		this.context = context;
	}

	public void bulkClaimTask(String containerId, Long instanceId, String userId) {
		// TODO Auto-generated method stub
		logger.info("Claiming Tasks for {}", instanceId);
		userTaskService.claim(containerId, instanceId, userId);
	}

}
