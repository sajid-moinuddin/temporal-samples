/*
 * Copyright (c) 2020 Temporal Technologies, Inc. All Rights Reserved
 *
 * Copyright 2012-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Modifications copyright (C) 2017 Uber Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not
 * use this file except in compliance with the License. A copy of the License is
 * located at
 *
 * http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

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
    int childResult = childWorkflow.processData("Child data from: " + input);

    // Process the child workflow result
    String result = "Parent received sum of random numbers: " + childResult;
    logger.info("Parent workflow completed with result: " + result);

    return result;
  }
}
