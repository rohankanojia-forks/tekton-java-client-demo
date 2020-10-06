package io.fabric8.demo.tekton;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.tekton.client.DefaultTektonClient;
import io.fabric8.tekton.client.TektonClient;
import io.fabric8.tekton.pipeline.v1beta1.PipelineRunBuilder;
import io.fabric8.tekton.triggers.v1alpha1.TriggerSpecBindingBuilder;

import java.util.Collections;
import java.util.logging.Logger;

public class JKubeOpenShiftTriggerDeployer {
    private static final String NAMESPACE = "default";
    private static final Logger logger = Logger.getLogger(JKubeOpenShiftTriggerDeployer.class.getSimpleName());

    public static void main(String[] args) {
        try (TektonClient tkn = new DefaultTektonClient()) {
            // Create Trigger template
            tkn.v1alpha1().triggerTemplates().inNamespace(NAMESPACE).createOrReplaceWithNew()
                    .withNewMetadata().withName("jkube-openshift-pipeline-template").endMetadata()
                    .withNewSpec()
                    .addNewParam()
                        .withName("gitrepositoryurl")
                        .withDescription("The git repository url")
                    .endParam()
                    .withResourcetemplates(Collections.singletonList(new PipelineRunBuilder()
                            .withNewMetadata().withGenerateName("jkube-openshift-pipeline-run-").endMetadata()
                            .withNewSpec()
                            .withNewPipelineRef()
                            .withName("jkube-openshift-deploy-pipeline")
                            .endPipelineRef()
                            .addNewResource()
                            .withName("app-git")
                            .withNewResourceSpec()
                            .withType("git")
                            .addNewParam()
                            .withName("url")
                            .withValue("$(tt.params.gitrepositoryurl)")
                            .endParam()
                            .endResourceSpec()
                            .endResource()
                            .endSpec()
                    .build()))
                    .endSpec()
                    .done();
            logger.info("TriggerTemplate Created. OK");

            // Create Trigger Binding
            tkn.v1alpha1().triggerBindings().inNamespace(NAMESPACE).createOrReplaceWithNew()
                    .withNewMetadata().withName("jkube-pipeline-binding").endMetadata()
                    .withNewSpec()
                    .addNewParam()
                    .withName("gitrepositoryurl")
                    .withValue("$(body.repository.url)")
                    .endParam()
                    .endSpec()
                    .done();
            logger.info("TriggerBinding created. OK");

            // Create Trigger Event Listener
            tkn.v1alpha1().eventListeners().inNamespace(NAMESPACE).createOrReplaceWithNew()
                    .withNewMetadata().withName("jkube-listener").endMetadata()
                    .withNewSpec()
                    .addNewTrigger()
                    .withName("jkube-trigger")
                    .withBindings(new TriggerSpecBindingBuilder()
                            .withRef("jkube-pipeline-binding")
                            .build())
                    .withNewTemplate()
                    .withName("jkube-openshift-pipeline-template")
                    .endTemplate()
                    .endTrigger()
                    .endSpec()
                    .done();
            logger.info("Trigger EventListener Created OK.");

            // Port forward eventlistener pod to localhost:8080
            portForwardEventListenerPodToLocalhost();
        }
    }

    private static void portForwardEventListenerPodToLocalhost() {
        try (KubernetesClient kubernetesClient = new DefaultKubernetesClient()) {
            Thread.sleep(1 * 1000);
            PodList podList = kubernetesClient.pods().inNamespace(NAMESPACE).withLabel("eventlistener", "jkube-listener").list();
            if (podList != null && !podList.getItems().isEmpty()) {
                Pod pod = podList.getItems().get(0);
                kubernetesClient.pods().inNamespace(NAMESPACE).withName(pod.getMetadata().getName())
                        .portForward(8080, 8080);
                logger.info("Port forwarded for 10 minutes at http://localhost:"+ 8080);
                Thread.sleep(10 * 60 * 1000);
            }
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            interruptedException.printStackTrace();
        }
    }
}
