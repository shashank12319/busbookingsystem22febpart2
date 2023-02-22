package com.wittybrains.busbookingsystem.controller;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.format.DateTimeParseException;

import com.wittybrains.busbookingsystem.dto.TravelScheduleDTO;
import com.wittybrains.busbookingsystem.model.Station;
import com.wittybrains.busbookingsystem.repository.StationRepository;
import com.wittybrains.busbookingsystem.service.TravelScheduleService;

import antlr.StringUtils;

@RestController
@RequestMapping("/schedules")
public class TravelScheduleController {

    @Autowired
    private TravelScheduleService travelScheduleService;
    @GetMapping("avalibility")

    
    
    public ResponseEntity<TravelScheduleResponseWrapper> getSchedules(
            @RequestParam("sourceCode") String sourceCode,
            @RequestParam("destinationCode") String destinationCode,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String date) {

        Station source = travelScheduleService.getStationByCode(sourceCode);
        Station destination = travelScheduleService.getStationByCode(destinationCode);

//        if (sourceCode == null || sourceCode.trim().isEmpty() || destinationCode == null || destinationCode.trim().isEmpty()) {
//            TravelScheduleResponseWrapper response = new TravelScheduleResponseWrapper("Source or destination station code is null or empty", null);
//            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//        }
//
//        
//
//        if (source == null || destination == null) {
//            TravelScheduleResponseWrapper response = new TravelScheduleResponseWrapper("Invalid source or destination station code", null);
//            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//        }
        if (sourceCode == null || sourceCode.trim().isEmpty() || destinationCode == null || destinationCode.trim().isEmpty()) {
            TravelScheduleResponseWrapper response = new TravelScheduleResponseWrapper("Source or destination station code is null or empty", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

//        if (source == null || destination == null) {
//            TravelScheduleResponseWrapper response = new TravelScheduleResponseWrapper("Invalid source or destination station code", null);
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//        }


        try {
            LocalDate parsedDate = LocalDate.parse(date);
            List<TravelScheduleDTO> schedules = travelScheduleService.getAvailableSchedules(source, destination, parsedDate);
            if (schedules.isEmpty()) {
                String message = "No bus is available between " + source.getName() + " and " + destination.getName() + " on " + date;
                TravelScheduleResponseWrapper response = new TravelScheduleResponseWrapper(message, schedules);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            } else {
                TravelScheduleResponseWrapper response = new TravelScheduleResponseWrapper("", schedules);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (DateTimeParseException ex) {
            TravelScheduleResponseWrapper response = new TravelScheduleResponseWrapper("Invalid date format. The correct format is ISO date format (yyyy-MM-dd)", null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping
    public ResponseEntity<TravelScheduleResponseWrapper> createSchedule(@RequestBody TravelScheduleDTO travelScheduleDTO) throws ParseException {
        if (travelScheduleService.createSchedule(travelScheduleDTO)) {
            TravelScheduleResponseWrapper response = new TravelScheduleResponseWrapper("Successfully created travel schedule", null);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            TravelScheduleResponseWrapper response = new TravelScheduleResponseWrapper("Unable to create travel schedule", null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
