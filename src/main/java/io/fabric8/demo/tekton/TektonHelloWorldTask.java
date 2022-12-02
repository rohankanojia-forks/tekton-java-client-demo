package io.fabric8.demo.tekton;

import io.fabric8.tekton.client.DefaultTektonClient;
import io.fabric8.tekton.client.TektonClient;
import io.fabric8.tekton.pipeline.v1beta1.TaskBuilder;
import io.fabric8.tekton.pipeline.v1beta1.TaskRunBuilder;

public class TektonHelloWorldTask {
    private static final String NAMESPACE = "default";
    public static void main(String[] args) {
        try (TektonClient tkn = new DefaultTektonClient()) {
            // Create Task
            tkn.v1beta1().tasks().inNamespace(NAMESPACE).resource(new TaskBuilder()
                    .withNewMetadata().withName("echo-hello-world").endMetadata()
                    .withNewSpec()
                    .addNewStep()
                    .withName("echo")
                    .withImage("alpine:3.12")
                    .withCommand("echo")
                    .withArgs("Hello World")
                    .endStep()
                    .endSpec()
                    .build()).createOrReplace();

            // Create TaskRun
            tkn.v1beta1().taskRuns().inNamespace(NAMESPACE).resource(new TaskRunBuilder()
                    .withNewMetadata().withName("echo-hello-world-task-run").endMetadata()
                    .withNewSpec()
                    .withNewTaskRef()
                    .withName("echo-hello-world")
                    .endTaskRef()
                    .endSpec()
                    .build()).createOrReplace();
        }
    }
}