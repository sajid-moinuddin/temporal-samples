package io.temporal.samples.childtest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.testing.TestWorkflowExtension;
import io.temporal.worker.Worker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class ParentWorkflowTest {

  @RegisterExtension
  public static final TestWorkflowExtension testWorkflowExtension =
      TestWorkflowExtension.newBuilder()
          .setWorkflowTypes(ParentWorkflowImpl.class, ChildWorkflowImpl.class)
          .build();

  @Test
  public void testChildWorkflowExecution(
      TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
    // Given a workflow client
    ParentWorkflow workflow =
        client.newWorkflowStub(
            ParentWorkflow.class,
            WorkflowOptions.newBuilder().setTaskQueue(worker.getTaskQueue()).build());

    // When we execute the workflow with an input
    String result = workflow.startWorkflow("Test Input");

    // Then we expect a specific output that indicates both the parent and child executed
    assertNotNull(result);
    assertEquals(
        "Parent processed: Processed [Child data from: Test Input] by child workflow", result);
  }
}
