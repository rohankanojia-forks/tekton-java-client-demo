package io.fabric8.demo.tekton;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.tekton.client.TektonClient;

public class TektonClientAdaptDemo {
  public static void main(String[] args) {
    try (KubernetesClient client = new KubernetesClientBuilder().build()) {
      TektonClient tektonClient = client.adapt(TektonClient.class);
      if (client.isAdaptable(TektonClient.class)) {
      //if (tektonClient.isSupported()) {
        System.out.println("Adapting to TektonClient");
      } else {
        System.out.println("Sorry, could not adapt to TektonClient");
      }
    }
  }
}
