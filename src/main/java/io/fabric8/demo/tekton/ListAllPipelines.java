package io.fabric8.demo.tekton;

import io.fabric8.tekton.client.DefaultTektonClient;
import io.fabric8.tekton.client.TektonClient;

public class ListAllPipelines {
    public static void main(String[] args) {
        try (TektonClient tkn = new DefaultTektonClient()) {
            tkn.v1beta1()
                    .pipelines()
                    .inNamespace("default")
                    .list()
                    .getItems()
                    .forEach(p -> System.out.println(p.getMetadata().getName()));
        }
    }
}