package io.fabric8.demo.tekton;

import io.fabric8.tekton.client.DefaultTektonClient;
import io.fabric8.tekton.client.TektonClient;
import io.fabric8.tekton.pipeline.v1beta1.Pipeline;

public class LoadPipelineAndCreate {
    public static void main(String[] args) {
        try (TektonClient tkn = new DefaultTektonClient()) {
            // Load YAML into object
            Pipeline pipeline = tkn.v1beta1().pipelines()
                    .load(LoadPipelineAndCreate.class.getResourceAsStream("/simple-pipeline.yml"))
                    .get();
            // Apply object onto Kubernetes APIServer
            tkn.v1beta1().pipelines().inNamespace("default").createOrReplace(pipeline);
        }
    }
}