package io.temporal.samples.childtest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.testing.TestWorkflowExtension;
import io.temporal.worker.Worker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class ParentWorkflowTest {

  // Test random number activity implementation with fixed values for tests
  private static class TestRandomNumberActivity implements RandomNumberActivity {
    @Override
    public int getFirstNumber() {
      return 10;
    }

    @Override
    public int getSecondNumber() {
      return 20;
    }

    @Override
    public int getThirdNumber() {
      return 30;
    }
  }

  @RegisterExtension
  public static final TestWorkflowExtension testWorkflowExtension =
      TestWorkflowExtension.newBuilder()
          .setWorkflowTypes(ParentWorkflowImpl.class, ChildWorkflowImpl.class)
          .setActivityImplementations(new TestRandomNumberActivity())
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
    assertTrue(result.contains("Parent received sum of random numbers: 60"));
  }
}
