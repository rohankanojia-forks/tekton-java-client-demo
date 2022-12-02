package io.fabric8.demo.tekton;

import io.fabric8.tekton.client.DefaultTektonClient;
import io.fabric8.tekton.client.TektonClient;
import io.fabric8.tekton.pipeline.v1beta1.TaskBuilder;
import io.fabric8.tekton.pipeline.v1beta1.Task;

public class TektonClientCrudTask {
    private static final String NAMESPACE = "default";

    public static void main(String[] args) {
        try (TektonClient tkn = new DefaultTektonClient()) {
            // Load Task object from YAML
            Task task = tkn.v1beta1()
                    .tasks()
                    .load(TektonClientCrudTask.class.getResourceAsStream("/say-hello-task.yml")).get();

            // Create Task object into Kubernetes
            tkn.v1beta1().tasks().inNamespace(NAMESPACE).resource(task).createOrReplace();

            // Get Task object from APIServer
            String taskName = task.getMetadata().getName();
            task = tkn.v1beta1().tasks().inNamespace(NAMESPACE)
                    .withName(taskName)
                    .get();

            // Edit Task object, add some dummy label
            tkn.v1beta1().tasks().inNamespace(NAMESPACE).withName(taskName).edit(t -> new TaskBuilder(t)
                    .editOrNewMetadata()
                    .addToAnnotations("context", "demo")
                    .endMetadata()
                    .build());

            // Delete Task object
            tkn.v1beta1().tasks().inNamespace(NAMESPACE).withName(taskName).delete();
        }
    }
}