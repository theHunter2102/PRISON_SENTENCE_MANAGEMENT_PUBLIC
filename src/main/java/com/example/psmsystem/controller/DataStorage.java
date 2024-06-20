package com.example.psmsystem.controller;

import com.example.psmsystem.controller.prisoner.PrisonerController;
import com.example.psmsystem.model.prisoner.Prisoner;

import java.util.List;
import java.util.Map;

public class DataStorage {
    private static Map<Integer, Integer> crimesTime;
    private static List<Prisoner> prisoners;
    private static PrisonerController prisonerController;

    public static PrisonerController getPrisonerController() {
        return prisonerController;
    }

    public static void setPrisonerController(PrisonerController prisonerController) {
        DataStorage.prisonerController = prisonerController;
    }

    public static List<Prisoner> getPrisoners() {
        return prisoners;
    }

    public static void setPrisoners(List<Prisoner> prisoners) {
        DataStorage.prisoners = prisoners;
    }

    public static Map<Integer, Integer> getCrimesTime() {
        return crimesTime;
    }

    public static void setCrimesTime(Map<Integer, Integer> crimesTime) {
        DataStorage.crimesTime = crimesTime;
    }



}
