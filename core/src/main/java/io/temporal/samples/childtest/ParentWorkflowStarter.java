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

    // Create a worker for activities on the number activities task queue
    Worker activitiesWorker = factory.newWorker(FirstNumberActivity.TASK_QUEUE);

    // Register activity implementations
    activitiesWorker.registerActivitiesImplementations(
        new FirstNumberActivityImpl(),
        new SecondNumberActivityImpl(),
        new ThirdNumberActivityImpl());

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
