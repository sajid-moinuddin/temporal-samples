package io.temporal.samples.childtest;

import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

/** Implementation of the parent workflow that launches and coordinates with a child workflow. */
public class ParentWorkflowImpl implements ParentWorkflow {
  private static final Logger logger = Workflow.getLogger(ParentWorkflowImpl.class);

  @Override
  public String startWorkflow(String input) {
    logger.info("Parent workflow started with input: " + input);

    // Create a child workflow stub
    ChildWorkflow childWorkflow = Workflow.newChildWorkflowStub(ChildWorkflow.class);

    // Execute the child workflow and wait for its completion
    logger.info("Starting child workflow");
    String childResult = childWorkflow.processData("Child data from: " + input);

    // Process the child workflow result
    String result = "Parent processed: " + childResult;
    logger.info("Parent workflow completed with result: " + result);

    return result;
  }
}
