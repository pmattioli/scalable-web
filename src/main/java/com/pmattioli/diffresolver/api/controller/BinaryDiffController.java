package com.pmattioli.diffresolver.api.controller;

import java.util.Arrays;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pmattioli.diffresolver.api.model.DiffApiRequest;
import com.pmattioli.diffresolver.api.model.DiffApiResponse;
import com.pmattioli.diffresolver.service.DiffResolverService;

@RestController
@RequestMapping("/v1/diff")
public class BinaryDiffController {

    private static final Logger LOG = Logger.getLogger(BinaryDiffController.class.getName());
    private static final String SUCCESS_MESSAGE = "Data was added sucessfully for ID ";

    @Autowired
    private DiffResolverService diffResolver;
    
    @PostMapping("/{diff-id}/left")
    public ResponseEntity<DiffApiResponse> diffLeft(@RequestBody DiffApiRequest request,
            @PathVariable(value = "diff-id") int diffId) {

        LOG.info("Received left-side data for ID " + diffId);

        diffResolver.setLeft(diffId, request.getData());

        return new ResponseEntity(new DiffApiResponse(SUCCESS_MESSAGE + diffId), HttpStatus.OK);
    }

    @PostMapping("/{diff-id}/right")
    public ResponseEntity<DiffApiResponse> diffRight(@RequestBody DiffApiRequest request,
            @PathVariable(value = "diff-id") int diffId) {

        LOG.info("Received right-side data for ID " + diffId);

        diffResolver.setRight(diffId, request.getData());

        return new ResponseEntity(new DiffApiResponse(SUCCESS_MESSAGE + diffId), HttpStatus.OK);

    }

    @GetMapping("/{diff-id}")
    public ResponseEntity<DiffApiResponse> diff(@PathVariable(value = "diff-id") int diffId) {

        LOG.info("Received resolve request for ID " + diffId);

        int[] resolvedOffsetsArray = diffResolver.resolveDiff(diffId);

        return new ResponseEntity<>(resolveToDiffApiResponse(resolvedOffsetsArray), HttpStatus.OK);

    }

    private DiffApiResponse resolveToDiffApiResponse(int[] offsetsArray) {

        int[] filteredOffsetsArray = Arrays.stream(offsetsArray).filter(value -> value != 0).toArray();

        if (filteredOffsetsArray.length == 0){
            return new DiffApiResponse("Both data strings provided are equal");
        }else{
            return new DiffApiResponse(offsetsArray.length, Arrays.toString(filteredOffsetsArray));
        }

    }

}
