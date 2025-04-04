package io.temporal.samples.childtest;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This class demonstrates how to run the ParentWorkflow sample with a real Temporal server. */
public class ParentWorkflowStarter {
  private static final String TASK_QUEUE = "ParentWorkflowTaskQueue";
  private static final Logger logger = LoggerFactory.getLogger(ParentWorkflowStarter.class);

  public static void main(String[] args) {
    // Get a Workflow service stub
    WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();

    // Get a Workflow client
    WorkflowClient client = WorkflowClient.newInstance(service);

    // Create worker factory and a worker
    WorkerFactory factory = WorkerFactory.newInstance(client);
    Worker worker = factory.newWorker(TASK_QUEUE);

    // Register workflow implementations with the worker
    worker.registerWorkflowImplementationTypes(ParentWorkflowImpl.class, ChildWorkflowImpl.class);

    // Start the worker factory (all workers)
    System.out.println("Starting workers...");
    factory.start();

    // Create a workflow stub
    ParentWorkflow workflow =
        client.newWorkflowStub(
            ParentWorkflow.class,
            WorkflowOptions.newBuilder()
                .setTaskQueue(TASK_QUEUE)
                .setWorkflowId("parent-workflow-" + System.currentTimeMillis())
                .build());

    // Execute the workflow and get the results
    System.out.println("Starting workflow...");
    String result = workflow.startWorkflow("Sample Input");

    // Print the workflow results
    System.out.println("Workflow completed with result: " + result);

    System.out.println("Press Enter to terminate...");
    try {
      System.in.read();
    } catch (Exception e) {
      logger.error("Error reading input", e);
    }

    // Shutdown the worker factory
    System.out.println("Shutting down...");
    factory.shutdown();
    System.exit(0);
  }
}
