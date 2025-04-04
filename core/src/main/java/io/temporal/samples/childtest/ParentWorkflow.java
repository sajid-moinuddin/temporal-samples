package io.temporal.samples.childtest;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * ParentWorkflow demonstrates a parent workflow that launches a child workflow and waits for its
 * completion.
 */
@WorkflowInterface
public interface ParentWorkflow {
  /**
   * The main workflow method
   *
   * @param input String input to the workflow
   * @return String output from the workflow
   */
  @WorkflowMethod
  String startWorkflow(String input);
}
