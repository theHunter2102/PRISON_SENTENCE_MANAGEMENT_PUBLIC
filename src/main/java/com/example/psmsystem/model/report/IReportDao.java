package com.example.psmsystem.model.report;

import java.util.List;

public interface IReportDao<T> {
    List<T> getAll();
    String timeUpdate();
}
