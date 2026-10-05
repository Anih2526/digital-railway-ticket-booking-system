package com.railway.service;

import com.railway.dao.TrainDao;
import com.railway.model.Train;
import com.railway.model.TrainSearchResult;

import java.time.LocalDate;
import java.util.List;

public class TrainService {

    private final TrainDao trainDao = new TrainDao();

    public List<TrainSearchResult> searchTrains(
            String from,
            String to,
            LocalDate journeyDate,
            String trainQuery) throws Exception {

        if (from == null || from.isBlank()
                || to == null || to.isBlank()) {

            throw new Exception(
                    "Please enter both stations."
            );
        }

        if (journeyDate == null) {

            throw new Exception(
                    "Please choose a journey date."
            );
        }

        return trainDao.searchTrains(
                from.trim(),
                to.trim(),
                journeyDate,
                trainQuery
        );
    }


    public List<Train> getAllTrains()
            throws Exception {

        return trainDao.getAllTrains();
    }


    public void addTrain(
            String trainNumber,
            String trainName)
            throws Exception {

        if (trainNumber == null
                || trainNumber.isBlank()
                || trainName == null
                || trainName.isBlank()) {

            throw new Exception(
                    "Train number and name are required."
            );
        }

        trainDao.addTrain(
                trainNumber.trim(),
                trainName.trim()
        );
    }


    public void updateTrain(
            int trainId,
            String trainNumber,
            String trainName)
            throws Exception {

        if (trainNumber == null
                || trainNumber.isBlank()
                || trainName == null
                || trainName.isBlank()) {

            throw new Exception(
                    "Enter valid train details."
            );
        }

        trainDao.updateTrain(
                trainId,
                trainNumber.trim(),
                trainName.trim()
        );
    }
}