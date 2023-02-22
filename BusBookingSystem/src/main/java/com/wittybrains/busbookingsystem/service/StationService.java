package com.wittybrains.busbookingsystem.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wittybrains.busbookingsystem.exception.StationNotFoundException;
import com.wittybrains.busbookingsystem.model.Station;
import com.wittybrains.busbookingsystem.repository.StationRepository;

@Service
public class StationService {

    @Autowired
    private StationRepository stationRepository;

    public Station getStationByCode(String code) {
        if (code == null || code.trim().isEmpty() || code.trim().equals("")) {
            throw new IllegalArgumentException("Code must not be null or empty");
        }

        Optional<Station> optionalStation = stationRepository.findByStationCode(code);
        if (optionalStation.isPresent()) {
            return optionalStation.get();
        } else {
            throw new StationNotFoundException("Station with code " + code + " not found");
        }
    }
}
