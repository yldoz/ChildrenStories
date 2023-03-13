package com.example.alamaudioplayer;

import com.example.alamaudioplayer.model.RepoList;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface JSONPlaceHolder {
    @GET("liststories.php")
    Call<RepoList> getMainList();
}
