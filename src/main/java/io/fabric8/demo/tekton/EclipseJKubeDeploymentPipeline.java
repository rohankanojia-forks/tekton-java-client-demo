package io.fabric8.demo.tekton;

import io.fabric8.tekton.client.DefaultTektonClient;
import io.fabric8.tekton.client.TektonClient;
import io.fabric8.tekton.pipeline.v1beta1.Pipeline;
import io.fabric8.tekton.pipeline.v1beta1.PipelineRun;
import io.fabric8.tekton.pipeline.v1beta1.PipelineRunBuilder;
import io.fabric8.tekton.pipeline.v1beta1.Task;
import io.fabric8.tekton.resource.v1alpha1.PipelineResource;

import java.util.logging.Logger;

public class EclipseJKubeDeploymentPipeline {

    public static final String JKUBE_DEMO_PROJECT_PIPELINERESOURCE_YML = "/jkube-demo-project-pipelineresource.yml";
    public static final String JKUBE_DEPLOYMENT_TASK_YML = "/jkube-deployment-task.yml";
    public static final String JKUBE_DEPLOYMENT_PIPELINE_YML = "/jkube-deployment-pipeline.yml";
    public static final String JKUBE_DEPLOYMENT_PIPELINE_RUN = "tutorial-pipeline-run-1";
    public static final Logger logger = Logger.getLogger(EclipseJKubeDeploymentPipeline.class.getSimpleName());

    public static void main(String[] args) {
        try (TektonClient tkn = new DefaultTektonClient()) {
            String namespace = "default";

            // Create PipelineResource for Project Git
            PipelineResource demoProjectGitResource = tkn.v1alpha1().pipelineResources()
                    .load(EclipseJKubeDeploymentPipeline.class.getResourceAsStream(JKUBE_DEMO_PROJECT_PIPELINERESOURCE_YML)).get();
            tkn.v1alpha1().pipelineResources().inNamespace(namespace).create(demoProjectGitResource);
            logger.info("PipelineResource created.");

            // Create Task for JKube project Deployment
            Task demoJKubeProjectDeploymentTask = tkn.v1beta1().tasks()
                    .load(EclipseJKubeDeploymentPipeline.class.getResourceAsStream(JKUBE_DEPLOYMENT_TASK_YML)).get();
            tkn.v1beta1().tasks().inNamespace(namespace).create(demoJKubeProjectDeploymentTask);
            logger.info("Task created.");

            // Create Pipeline for JKube project Deployment using above created Task
            Pipeline demoJKubeProjectPipeline = tkn.v1beta1().pipelines()
                    .load(EclipseJKubeDeploymentPipeline.class.getResourceAsStream(JKUBE_DEPLOYMENT_PIPELINE_YML)).get();
            tkn.v1beta1().pipelines().inNamespace(namespace).create(demoJKubeProjectPipeline);
            logger.info("Pipeline created.");

            // Create PipelineRun for above created Pipeline
            PipelineRun demoJKubeProjectPipelineRun = new PipelineRunBuilder()
                    .withNewMetadata().withName(JKUBE_DEPLOYMENT_PIPELINE_RUN                                                                     ).endMetadata()
                    .withNewSpec()
                    .withServiceAccountName("default")
                    .withNewPipelineRef()
                    .withName("tutorial-pipeline")
                    .endPipelineRef()
                    .addNewResource()
                    .withName("source-repo")
                    .withNewResourceRef()
                    .withName("jkube-demo-git")
                    .endResourceRef()
                    .endResource()
                    .endSpec()
                    .build();
            tkn.v1beta1().pipelineRuns().inNamespace(namespace).create(demoJKubeProjectPipelineRun);
            logger.info("PipelineRun created");
        }
    }
}
