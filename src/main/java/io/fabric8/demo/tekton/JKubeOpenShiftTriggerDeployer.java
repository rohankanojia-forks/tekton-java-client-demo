package io.fabric8.demo.tekton;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.LocalPortForward;
import io.fabric8.tekton.client.DefaultTektonClient;
import io.fabric8.tekton.client.TektonClient;
import io.fabric8.tekton.pipeline.v1beta1.PipelineRunBuilder;
import io.fabric8.tekton.triggers.v1alpha1.EventListenerBuilder;
import io.fabric8.tekton.triggers.v1alpha1.TriggerBindingBuilder;
import io.fabric8.tekton.triggers.v1alpha1.TriggerSpecBindingBuilder;
import io.fabric8.tekton.triggers.v1alpha1.TriggerTemplateBuilder;

import java.io.IOException;
import java.util.Collections;
import java.util.logging.Logger;

public class JKubeOpenShiftTriggerDeployer {
    private static final String NAMESPACE = "default";
    private static final Logger logger = Logger.getLogger(JKubeOpenShiftTriggerDeployer.class.getSimpleName());

    public static void main(String[] args) {
        try (TektonClient tkn = new DefaultTektonClient()) {
            // Create Trigger template
            tkn.v1alpha1().triggerTemplates().inNamespace(NAMESPACE).resource(new TriggerTemplateBuilder()
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
                    .build()).createOrReplace();
            logger.info("TriggerTemplate Created. OK");

            // Create Trigger Binding
            tkn.v1alpha1().triggerBindings().inNamespace(NAMESPACE).resource(new TriggerBindingBuilder()
                    .withNewMetadata().withName("jkube-pipeline-binding").endMetadata()
                    .withNewSpec()
                    .addNewParam()
                    .withName("gitrepositoryurl")
                    .withValue("$(body.repository.url)")
                    .endParam()
                    .endSpec()
                    .build()).createOrReplace();
            logger.info("TriggerBinding created. OK");

            // Create Trigger Event Listener
            tkn.v1alpha1().eventListeners().inNamespace(NAMESPACE).resource(new EventListenerBuilder()
                    .withNewMetadata().withName("jkube-listener").endMetadata()
                    .withNewSpec()
                    .addNewTrigger()
                    .withName("jkube-trigger")
                    .withBindings(new TriggerSpecBindingBuilder()
                            .withRef("jkube-pipeline-binding")
                            .build())
                    .withNewTemplate()
                    .endTemplate()
                    .endTrigger()
                    .endSpec()
                    .build()).createOrReplace();
            logger.info("Trigger EventListener Created OK.");

            // Port forward eventlistener pod to localhost:8080
            portForwardEventListenerPodToLocalhost();
        }
    }

    private static void portForwardEventListenerPodToLocalhost() {
        try (KubernetesClient kubernetesClient = new KubernetesClientBuilder().build()) {
            Thread.sleep(1 * 1000);
            PodList podList = kubernetesClient.pods().inNamespace(NAMESPACE).withLabel("eventlistener", "jkube-listener").list();
            if (podList != null && !podList.getItems().isEmpty()) {
                Pod pod = podList.getItems().get(0);
                LocalPortForward localPortForward = kubernetesClient.pods().inNamespace(NAMESPACE).withName(pod.getMetadata().getName())
                        .portForward(8080, 8080);
                logger.info("Port forwarded for 10 minutes at http://localhost:"+ 8080);
                Thread.sleep(10 * 60 * 1000);
                localPortForward.close();
            }
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            interruptedException.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
