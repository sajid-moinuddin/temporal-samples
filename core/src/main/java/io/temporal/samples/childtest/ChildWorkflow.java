package io.temporal.samples.childtest;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/** A simple child workflow interface that processes a string */
@WorkflowInterface
public interface ChildWorkflow {
  /**
   * Child workflow method that takes a string input and returns a processed result
   *
   * @param input String input to process
   * @return Processed string result
   */
  @WorkflowMethod
  String processData(String input);
}
