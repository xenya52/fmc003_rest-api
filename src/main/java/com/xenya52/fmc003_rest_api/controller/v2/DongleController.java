package com.xenya52.fmc003_rest_api.controller.v2;

import com.xenya52.fmc003_rest_api.entity.dto.GetResponseDto;
import com.xenya52.fmc003_rest_api.entity.model.IoDongleModel;
import com.xenya52.fmc003_rest_api.service.IoDongleByFile;
import com.xenya52.fmc003_rest_api.service.IoDongleService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/dongle")
@ConditionalOnProperty(name = "controller.version", havingValue = "v2")
public class DongleController {

    // Attributes
    @Autowired
    private IoDongleService dongleService;

    @Autowired
    private IoDongleByFile dongleByFile;

    // Methods
    @GetMapping("/items/{listOfIds}")
    public ResponseEntity<List<GetResponseDto>> dongleById(
        @PathVariable List<String> listOfIds
    ) {
        List<GetResponseDto> responseList = new ArrayList<>();

        for (String id : listOfIds) {
            GetResponseDto response = dongleService.getIoDongleById(id);
            responseList.add(response);
        }
        if (responseList.getFirst() == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }

    // Methods
    // Todo make a own mongoService for this without that restapi
    // Todo make an optional param to fetch the file from a specific path
    @GetMapping("/fetch-local-file-into-db")
    public ResponseEntity<String> fetchFile() {
        try {
            List<IoDongleModel> dongleList = dongleByFile.getDongleList();

            // Debug
            System.out.println("Dongle List:");
            for (IoDongleModel dongleModel : dongleList) {
                System.out.println("!");
                System.out.println(
                    dongleModel.getIoWikiIdAndDongleValues().toString()
                );
            }

            for (IoDongleModel dongleModel : dongleList) {
                dongleService.saveIoDongle(dongleModel);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/items/all")
    public ResponseEntity<List<GetResponseDto>> dongleAll() {
        List<GetResponseDto> response = dongleService.getIoDongleList();
        if (response.getFirst() == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
