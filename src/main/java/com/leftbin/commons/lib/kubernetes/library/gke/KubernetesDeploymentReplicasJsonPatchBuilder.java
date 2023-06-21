package com.leftbin.commons.lib.kubernetes.library.gke;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.kubernetes.client.custom.V1Patch;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class KubernetesDeploymentReplicasJsonPatchBuilder {
    //with help from chatgpt
    public V1Patch build(int replicas) throws JsonProcessingException {
        // create an ObjectMapper for reading and writing JSON
        var objectMapper = new ObjectMapper();

        // create an empty ObjectNode for storing the patch operations
        var patch = objectMapper.createObjectNode();
        var a = new ArrayList<ObjectNode>();
        // add a patch operation to update the deployment's replicas field
        patch.put("op", "replace");
        patch.put("path", "/spec/replicas");
        patch.put("value", replicas);
        a.add(patch);
        // convert the patch to a JSON string
        String jsonPatchString = objectMapper.writeValueAsString(a);
        // create a V1Patch object from the JSON patch string
        return new V1Patch(jsonPatchString);
    }
}
