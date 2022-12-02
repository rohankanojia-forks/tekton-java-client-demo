package io.fabric8.demo.tekton;

import io.fabric8.tekton.client.DefaultTektonClient;
import io.fabric8.tekton.client.TektonClient;
import io.fabric8.tekton.pipeline.v1beta1.ArrayOrString;
import io.fabric8.tekton.pipeline.v1beta1.PipelineRunBuilder;
import io.fabric8.tekton.triggers.v1alpha1.TriggerTemplateBuilder;

import java.util.Collections;

public class SimpleTriggerTemplate {
    private static final String NAMESPACE = "default";

    public static void main(String[] args) {
        try (TektonClient tkn = new DefaultTektonClient()) {
            tkn.v1alpha1().triggerTemplates().inNamespace(NAMESPACE).resource(new TriggerTemplateBuilder()
                    .withNewMetadata().withName("pipeline-template").endMetadata()
                    .withNewSpec()
                    .addNewParam()
                    .withName("gitrepositoryurl")
                    .withDescription("The git repository url")
                    .endParam()
                    .addNewParam()
                    .withName("gitrevision")
                    .withDescription("The git revision")
                    .endParam()
                    .addNewParam()
                    .withName("message")
                    .withDescription("The message to print")
                    .withDefault("This is default message")
                    .endParam()
                    .addNewParam()
                    .withName("contenttype")
                    .withDescription(" The Content-Type of the event")
                    .endParam()
                    .withResourcetemplates(Collections.singletonList(new PipelineRunBuilder()
                            .withNewMetadata().withGenerateName("simple-pipeline-run-").endMetadata()
                            .withNewSpec()
                            .withNewPipelineRef().withName("simple-pipeline").endPipelineRef()
                            .addNewParam()
                            .withName("message")
                            .withValue(new ArrayOrString("$(tt.params.message)"))
                            .endParam()
                            .addNewParam()
                            .withName("contenttype")
                            .withValue(new ArrayOrString("$(tt.params.contenttype)"))
                            .endParam()
                            .addNewResource()
                            .withName("git-source")
                            .withNewResourceSpec()
                            .withType("git")
                            .addNewParam()
                            .withName("revision")
                            .withValue("$(tt.params.gitrevision)")
                            .endParam()
                            .addNewParam()
                            .withName("url")
                            .withValue("$(tt.params.gitrepositoryurl)")
                            .endParam()
                            .endResourceSpec()
                            .endResource()
                            .endSpec()
                            .build()))
                    .endSpec()
                    .build()).createOrReplace();
        }
    }
}