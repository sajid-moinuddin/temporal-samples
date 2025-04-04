package io.temporal.samples.childtest;

import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

/** Implementation of the child workflow that processes data. */
public class ChildWorkflowImpl implements ChildWorkflow {
  private static final Logger logger = Workflow.getLogger(ChildWorkflowImpl.class);

  @Override
  public String processData(String input) {
    logger.info("Child workflow processing: " + input);

    // Simulate some processing with a sleep
    Workflow.sleep(1000);

    String result = "Processed [" + input + "] by child workflow";
    logger.info("Child workflow completed with result: " + result);

    return result;
  }
}
