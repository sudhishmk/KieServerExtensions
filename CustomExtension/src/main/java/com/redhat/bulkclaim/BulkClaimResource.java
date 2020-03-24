package com.redhat.bulkclaim;

import static org.kie.server.remote.rest.common.util.RestUtils.buildConversationIdHeader;
import static org.kie.server.remote.rest.common.util.RestUtils.createResponse;
import static org.kie.server.remote.rest.common.util.RestUtils.getContentType;
import static org.kie.server.remote.rest.common.util.RestUtils.getVariant;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;

import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.UserTaskService;
import org.jbpm.services.api.admin.ProcessInstanceAdminService;
import org.kie.server.api.marshalling.Marshaller;
import org.kie.server.api.marshalling.MarshallerFactory;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.remote.rest.common.Header;
import org.kie.server.services.api.KieServerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("server/containers/custom")
public class BulkClaimResource {
	
	private static final Logger logger = LoggerFactory.getLogger(BulkClaimResource.class);
	private Marshaller marshaller;
	private BulkClaimTaskService bulkClaimTaskService;
	private KieServerRegistry context;
	
	

	public BulkClaimResource() {
		logger.info("No argument constructur");
	}

	public BulkClaimResource(RuntimeDataService runtimeService, ProcessInstanceAdminService adminService,
			ProcessService processService, UserTaskService userTaskService, KieServerRegistry context) {
		logger.info("with argument constructur");
		this.bulkClaimTaskService = new BulkClaimTaskService(runtimeService, adminService,processService, userTaskService, context);
		this.context = context;
		this.marshaller = MarshallerFactory.getMarshaller(MarshallingFormat.JSON,
				BulkClaimResource.class.getClassLoader());
	}
	
	
	@GET
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
	public String Testing() {
		return " this is the base url";
	}
	
	@GET
	@Path("test")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
	public String claimTasks() {
		return " End point is working fine";
	}
	
	
	@PUT
	@Path("/{containerId}/tasks/{taskInstanceId}/states/bulkclaimed")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response claimTasks(@Context HttpHeaders headers, 
			@PathParam("containerId") String containerId,
			@PathParam("taskInstanceId") Long instanceId,
			@QueryParam("user")  String userId) {

		logger.info("claimimg task instance with id {} and userId {}", instanceId, userId);

		Variant v = getVariant(headers);
		Header conversationIdHeader = buildConversationIdHeader(containerId, context, headers);
		String contentType = getContentType(headers);
		MarshallingFormat format = MarshallingFormat.fromType(contentType);
		if (format == null) {
			format = MarshallingFormat.valueOf(contentType);
		}

		try {

			bulkClaimTaskService.bulkClaimTask(containerId,instanceId,userId);
			return createResponse("", v, Response.Status.CREATED, conversationIdHeader);

		} catch (Exception e) {

			// in case marshalling failed return the call container response to
			// keep backward compatibility
			e.printStackTrace();
			String response = "Execution failed with error : " + e.getMessage();
			logger.error("Returning Failure response with content '{}'", response);
			return createResponse(response, v, Response.Status.INTERNAL_SERVER_ERROR);

		}
	}
	
	@POST
	@Path("/{containerId}/tasks/states/bulkclaimed")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response bulkClaimTasks(@Context HttpHeaders headers, 
			@PathParam("containerId") String containerId,
			@QueryParam("user")  String userId,
			TaskIdModel taskIdModel) {

		logger.info("claimimg task instance with id {} and userId {}",  userId);

		Variant v = getVariant(headers);
		Header conversationIdHeader = buildConversationIdHeader(containerId, context, headers);
		String contentType = getContentType(headers);
		MarshallingFormat format = MarshallingFormat.fromType(contentType);
		if (format == null) {
			format = MarshallingFormat.valueOf(contentType);
		}

		try {
			
			for (long instanceId : taskIdModel.getTaskIds()) {
				try {
					logger.info("claimimg task instance with id {}", instanceId);
				bulkClaimTaskService.bulkClaimTask(containerId,instanceId,userId);
				}catch (Exception e) {

					e.printStackTrace();
					String response = "Execution failed with error : " + e.getMessage() +" for task id "+ instanceId;
					logger.error("Returning Failure response with content '{}'", response);

				}
				
			}
			return createResponse("", v, Response.Status.CREATED, conversationIdHeader);

		} catch (Exception e) {

			// in case marshalling failed return the call container response to
			// keep backward compatibility
			e.printStackTrace();
			String response = "Execution failed with error : " + e.getMessage();
			logger.error("Returning Failure response with content '{}'", response);
			return createResponse(response, v, Response.Status.INTERNAL_SERVER_ERROR);

		}
	}

}
