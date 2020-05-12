# Fabric8 Tekton Java Client Demo

This Project demonstrates use of Fabric8 Tekton Java Client by creating a simple Pipeline which uses
[Eclipse JKube](https://github.com/eclipse/jkube) to deploy a simple [Random Generator](https://github.com/rohanKanojia/eclipse-jkube-demo-project) Spring Boot
application to a minikube cluster.

You can check all the resource yamls in `src/main/resources` directory. They are loaded and then applied
by `EclipseJKubeDeploymentPipeline` class.

## How to Build
You can build project by just doing a plain `mvn package`

## How to Run
There is only one class in project. You just need to run one command:
```
~/work/repos/tekton-java-client-demo : $ mvn exec:java -Dexec.mainClass=io.fabric8.demo.tekton.EclipseJKubeDeploymentPipeline                                                
[INFO] Scanning for projects...                                                                                                                                              
[INFO]                                                                                                                                                                       
[INFO] ----------------< org.example:tekton-java-client-demo >-----------------                                                                                              
[INFO] Building tekton-java-client-demo 1.0-SNAPSHOT                                                                                                                         
[INFO] --------------------------------[ jar ]---------------------------------                                                                                              
[INFO]                                                                                                                                                                       
[INFO] --- exec-maven-plugin:1.6.0:java (default-cli) @ tekton-java-client-demo ---                                                                                          
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".                                                                                                             
SLF4J: Defaulting to no-operation (NOP) logger implementation                                                                                                                
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.                                                                                           
May 12, 2020 3:05:49 PM io.fabric8.demo.tekton.EclipseJKubeDeploymentPipeline main                                                                                           
INFO: PipelineResource created.                                                                                                                                              
May 12, 2020 3:05:49 PM io.fabric8.demo.tekton.EclipseJKubeDeploymentPipeline main                                                                                           
INFO: Task created.                                                                                                                                                          
WARNING: An illegal reflective access operation has occurred                                                                                                                 
WARNING: Illegal reflective access by com.fasterxml.jackson.databind.util.ClassUtil (file:/home/rohaan/.m2/repository/com/fasterxml/jackson/core/jackson-databind/2.10.3/jack
son-databind-2.10.3.jar) to field java.time.Duration.seconds                                                                                                                 
WARNING: Please consider reporting this to the maintainers of com.fasterxml.jackson.databind.util.ClassUtil                                                                  
WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations                                                                        
WARNING: All illegal access operations will be denied in a future release                                                                                                    
May 12, 2020 3:05:49 PM io.fabric8.demo.tekton.EclipseJKubeDeploymentPipeline main                                                                                           
INFO: Pipeline created.                                                                                                                                                      
May 12, 2020 3:05:50 PM io.fabric8.demo.tekton.EclipseJKubeDeploymentPipeline main                                                                                           
INFO: PipelineRun created    
```

After running the file, you can check for status of created `PipelineRun`. If everything went okay, your PipelineRun would be running:
```
~/work/repos/eclipse-jkube-demo-project : $ tkn pipelinerun list
NAME                      STARTED          DURATION   STATUS
tutorial-pipeline-run-1   19 seconds ago   ---        Running
~/work/repos/eclipse-jkube-demo-project : $ 
```
You can also check logs of running pipeline by doing:
```
~/work/repos/eclipse-jkube-demo-project : $ tkn pipelinerun logs tutorial-pipeline-run-1 -f
[deploy-jkube : git-source-jkube-demo-git-dhmmg] {"level":"info","ts":1589276156.0435505,"caller":"git/git.go:136","msg":"Successfully cloned https://github.com/rohanKanojia
/eclipse-jkube-demo-project @ 058bed285de43aac80b5bf9433b9a3a9c3915e19 (grafted, HEAD, origin/master) in path /workspace/source"}
[deploy-jkube : git-source-jkube-demo-git-dhmmg] {"level":"info","ts":1589276156.0767734,"caller":"git/git.go:177","msg":"Successfully initialized and updated submodules in 
path /workspace/source"}

[deploy-jkube : k8s-resource-k8s-apply] [INFO] Scanning for projects...
[deploy-jkube : k8s-resource-k8s-apply] Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-parent/2.1.8.RELEASE/spri
ng-boot-starter-parent-2.1.8.RELEASE.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-parent/2.1.8.RELEASE/spring-boot-starter-parent-2.1.8.RELEASE.pom 
(9.5 kB at 4.1 kB/s)
[deploy-jkube : k8s-resource-k8s-apply] Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-dependencies/2.1.8.RELEASE/spring
-boot-dependencies-2.1.8.RELEASE.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-dependencies/2.1.8.RELEASE/spring-boot-dependencies-2.1.8.RELEASE.pom (122
 kB at 61 kB/s)
[deploy-jkube : k8s-resource-k8s-apply] Downloading from central: https://repo.maven.apache.org/maven2/com/fasterxml/jackson/jackson-bom/2.9.9.20190807/jackson-bom-2.9.9.201
90807.pom
Downloaded from central: https://repo.maven.apache.org/maven2/com/fasterxml/jackson/jackson-bom/2.9.9.20190807/jackson-bom-2.9.9.20190807.pom (12 kB at 20 kB/s)
[deploy-jkube : k8s-resource-k8s-apply] Downloading from central: https://repo.maven.apache.org/maven2/com/fasterxml/jackson/jackson-parent/2.9.1.2/jackson-parent-2.9.1.2.po
m
Downloaded from central: https://repo.maven.apache.org/maven2/com/fasterxml/jackson/jackson-parent/2.9.1.2/jackson-parent-2.9.1.2.pom (7.9 kB at 2.5 kB/s)
[deploy-jkube : k8s-resource-k8s-apply] Downloading from central: https://repo.maven.apache.org/maven2/com/fasterxml/oss-parent/34/oss-parent-34.pom
Downloaded from central: https://repo.maven.apache.org/maven2/com/fasterxml/oss-parent/34/oss-parent-34.pom (23 kB at 18 kB/s)

...


[deploy-jkube : k8s-resource-k8s-apply] [INFO] k8s: jkube-revision-history: Adding revision history limit to 2
[deploy-jkube : k8s-resource-k8s-apply] [INFO] 
[deploy-jkube : k8s-resource-k8s-apply] [INFO] --- kubernetes-maven-plugin:1.0.0-alpha-3:apply (default-cli) @ random-generator ---
[deploy-jkube : k8s-resource-k8s-apply] [INFO] k8s: Using Kubernetes at https://10.96.0.1:443/ in namespace default with manifest /workspace/source/target/classes/META-INF/jkube/kubernetes.yml 
[deploy-jkube : k8s-resource-k8s-apply] [INFO] k8s: Using namespace: default
[deploy-jkube : k8s-resource-k8s-apply] [INFO] k8s: Creating a Service from kubernetes.yml namespace default name random-generator
[deploy-jkube : k8s-resource-k8s-apply] [INFO] k8s: Created Service: target/jkube/applyJson/default/service-random-generator.json
[deploy-jkube : k8s-resource-k8s-apply] [INFO] k8s: Creating a Deployment from kubernetes.yml namespace default name random-generator
[deploy-jkube : k8s-resource-k8s-apply] [INFO] k8s: Created Deployment: target/jkube/applyJson/default/deployment-random-generator.json
[deploy-jkube : k8s-resource-k8s-apply] [INFO] k8s: HINT: Use the command `kubectl get pods -w` to watch your pods start up
[deploy-jkube : k8s-resource-k8s-apply] [INFO] ------------------------------------------------------------------------
[deploy-jkube : k8s-resource-k8s-apply] [INFO] BUILD SUCCESS
[deploy-jkube : k8s-resource-k8s-apply] [INFO] ------------------------------------------------------------------------
[deploy-jkube : k8s-resource-k8s-apply] [INFO] Total time:  17:45 min
[deploy-jkube : k8s-resource-k8s-apply] [INFO] Finished at: 2020-05-12T09:53:44Z
[deploy-jkube : k8s-resource-k8s-apply] [INFO] ------------------------------------------------------------------------

~/work/repos/eclipse-jkube-demo-project : $ kubectl get pods
NAME                                                   READY   STATUS      RESTARTS   AGE
echo-hello-world-task-run-pod-2nxwj                    0/1     Completed   0          16h
random-generator-68cfbc6778-9pdzc                      1/1     Running     0          113s
tutorial-pipeline-run-1-deploy-jkube-fvcmr-pod-vgfmd   0/2     Completed   0          19m
~/work/repos/eclipse-jkube-demo-project : $ kubectl get svc
NAME               TYPE        CLUSTER-IP     EXTERNAL-IP   PORT(S)          AGE
kubernetes         ClusterIP   10.96.0.1      <none>        443/TCP          16h
random-generator   NodePort    10.96.39.238   <none>        8080:30280/TCP   117s
~/work/repos/eclipse-jkube-demo-project : $ curl `minikube ip`:30280/random | jq .
{
    "id": "73738fd0-b0dc-4047-b247-55a558616865"
}
~/work/repos/eclipse-jkube-demo-project : $ 


```
