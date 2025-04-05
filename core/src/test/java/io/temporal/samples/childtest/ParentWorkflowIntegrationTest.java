package io.temporal.samples.childtest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.temporal.api.filter.v1.WorkflowExecutionFilter;
import io.temporal.api.workflowservice.v1.ListClosedWorkflowExecutionsRequest;
import io.temporal.api.workflowservice.v1.ListOpenWorkflowExecutionsRequest;
import io.temporal.api.workflowservice.v1.WorkflowServiceGrpc;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Integration test for ParentWorkflow that runs against a real Temporal server. This test assumes a
 * local Temporal server is already running.
 */
public class ParentWorkflowIntegrationTest {
  private static final String WORKFLOW_TASK_QUEUE = "ParentWorkflowIntegrationTestQueue";
  private static final Logger logger = LoggerFactory.getLogger(ParentWorkflowIntegrationTest.class);

  private WorkflowServiceStubs workflowServiceStubs;
  private WorkflowClient workflowClient;
  private WorkerFactory factory;
  private Worker workflowWorker;
  private Worker activityWorker;

  /**
   * Clean up existing workflows on the Temporal server that match our test workflow ID pattern to
   * start with a clean state.
   */
  private void cleanupExistingWorkflows() {
    try {
      String namespace = workflowClient.getOptions().getNamespace();
      WorkflowServiceGrpc.WorkflowServiceBlockingStub blockingStub =
          workflowServiceStubs.blockingStub();
      List<String> workflowsToTerminate = new ArrayList<>();

      // Find open workflows matching our test pattern
      var openRequest =
          ListOpenWorkflowExecutionsRequest.newBuilder()
              .setNamespace(namespace)
              .setExecutionFilter(
                  WorkflowExecutionFilter.newBuilder()
                      .setWorkflowId("parent-workflow-integration-test-")
                      .build())
              .build();

      var openResponse = blockingStub.listOpenWorkflowExecutions(openRequest);
      openResponse
          .getExecutionsList()
          .forEach(execution -> workflowsToTerminate.add(execution.getExecution().getWorkflowId()));

      // Find closed workflows matching our test pattern
      var closedRequest =
          ListClosedWorkflowExecutionsRequest.newBuilder()
              .setNamespace(namespace)
              .setExecutionFilter(
                  WorkflowExecutionFilter.newBuilder()
                      .setWorkflowId("parent-workflow-integration-test-")
                      .build())
              .build();

      var closedResponse = blockingStub.listClosedWorkflowExecutions(closedRequest);
      closedResponse
          .getExecutionsList()
          .forEach(execution -> workflowsToTerminate.add(execution.getExecution().getWorkflowId()));

      // Terminate any found workflows
      for (String workflowId : workflowsToTerminate) {
        try {
          WorkflowStub workflowStub = workflowClient.newUntypedWorkflowStub(workflowId);
          workflowStub.terminate("Cleaning up test workflows before new test run");
          logger.info("Terminated workflow: {}", workflowId);
        } catch (Exception e) {
          logger.warn("Failed to terminate workflow {}: {}", workflowId, e.getMessage());
        }
      }

      if (!workflowsToTerminate.isEmpty()) {
        logger.info("Terminated {} existing test workflows", workflowsToTerminate.size());
      }
    } catch (Exception e) {
      logger.error("Error during workflow cleanup", e);
    }
  }

  @BeforeEach
  public void setUp() {
    // Get a Workflow service stub connecting to the already running server
    workflowServiceStubs = WorkflowServiceStubs.newInstance();

    // Get a Workflow service client
    workflowClient = WorkflowClient.newInstance(workflowServiceStubs);

    // Clean up existing workflows before running test
    cleanupExistingWorkflows();

    // Create Worker Factory
    factory = WorkerFactory.newInstance(workflowClient);

    // Create workflow worker on the workflow task queue
    workflowWorker = factory.newWorker(WORKFLOW_TASK_QUEUE);

    // Register workflow implementations with the workflow worker
    workflowWorker.registerWorkflowImplementationTypes(
        ParentWorkflowImpl.class, ChildWorkflowImpl.class);

    // Create dedicated worker for the RandomNumberActivity task queue
    activityWorker = factory.newWorker(RandomNumberActivity.TASK_QUEUE);

    // Register activity implementation with the activity worker
    activityWorker.registerActivitiesImplementations(new RandomNumberActivityImpl());

    // Start the worker factory
    factory.start();
  }

  @AfterEach
  public void tearDown() {
    // Properly shutdown resources
    if (factory != null) {
      factory.shutdown();
    }
  }

  @Test
  public void testParentAndChildWorkflowExecution() {
    // Create workflow stub with a unique ID
    String workflowId = "parent-workflow-integration-test-" + System.currentTimeMillis();
    logger.info("Starting test workflow with ID: {}", workflowId);

    ParentWorkflow workflow =
        workflowClient.newWorkflowStub(
            ParentWorkflow.class,
            WorkflowOptions.newBuilder()
                .setTaskQueue(WORKFLOW_TASK_QUEUE)
                .setWorkflowId(workflowId)
                .build());

    // Execute the workflow and get results
    String result = workflow.startWorkflow("Integration Test Input");
    logger.info("Workflow execution completed with result: {}", result);

    // Verify the results
    assertNotNull(result);
    assertTrue(result.contains("Parent received sum of random numbers:"));
  }
}
