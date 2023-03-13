package com.example.alamaudioplayer.model;

import java.util.List;

public class RepoList {
    public String Result;
    public List<Rows> Rows;

    public String getResult() {
        return Result;
    }

    public void setResult(String result) {
        Result = result;
    }

    public List<Rows> getRows() {
        return Rows;
    }

    public void setRows(List<Rows> rows) {
        this.Rows = rows;
    }
}
