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

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;
import org.slf4j.Logger;

/**
 * Implementation of the child workflow that processes data by generating and summing random
 * numbers.
 */
public class ChildWorkflowImpl implements ChildWorkflow {
  private static final Logger logger = Workflow.getLogger(ChildWorkflowImpl.class);

  // Define common activity options
  private final ActivityOptions activityOptions =
      ActivityOptions.newBuilder()
          .setStartToCloseTimeout(Duration.ofSeconds(5))
          .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(3).build())
          .build();

  // Create activity clients for each number activity
  private final FirstNumberActivity firstNumberActivity =
      Workflow.newActivityStub(FirstNumberActivity.class, activityOptions);

  private final SecondNumberActivity secondNumberActivity =
      Workflow.newActivityStub(SecondNumberActivity.class, activityOptions);

  private final ThirdNumberActivity thirdNumberActivity =
      Workflow.newActivityStub(ThirdNumberActivity.class, activityOptions);

  @Override
  public int processData(String input) {
    logger.info("Child workflow processing: " + input);

    // Execute the three activities to get random numbers
    int num1 = firstNumberActivity.getFirstNumber();
    logger.info("Received first random number: {}", num1);

    int num2 = secondNumberActivity.getSecondNumber();
    logger.info("Received second random number: {}", num2);

    int num3 = thirdNumberActivity.getThirdNumber();
    logger.info("Received third random number: {}", num3);

    // Calculate the sum
    int sum = num1 + num2 + num3;

    logger.info("Child workflow completed with result: {}", sum);

    return sum;
  }
}
