package com.example.bckim.tabwithnavi;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ExecutionError;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MyLoader {

    /** Global instance of the HTTP transport. */
    private final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    /** Global instance of the JSON factory. */
    private final JsonFactory JSON_FACTORY = new JacksonFactory();

    /** Global instance of YouTube object to make all API requests. */
    private YouTube youtube;

    //private Context context;

    private GoogleClientSecrets build() {
        GoogleClientSecrets.Details details = new GoogleClientSecrets.Details();
        details.setClientId("17725036663-opg03jhbtvhtpji2nent4d4nf2etj76u.apps.googleusercontent.com");
        details.setClientSecret("t0x2NCF6TT79Y988EAC6_HDh");
        GoogleClientSecrets googleClientSecrets = new GoogleClientSecrets();
        googleClientSecrets.setInstalled(details);
        return googleClientSecrets;
    }

    private Credential authorize(List<String> scopes) throws Exception {
        GoogleClientSecrets clientSecrets = build();

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, scopes).setClientId("17725036663-opg03jhbtvhtpji2nent4d4nf2etj76u.apps.googleusercontent.com")
                .build();

        LocalServerReceiver localReceiver = new LocalServerReceiver.Builder().setPort(8080).build();

        return new AuthorizationCodeInstalledApp(flow, localReceiver).authorize("user");
    }

    private void prettyPrint(int size, Iterator<PlaylistItem> playlistEntries) {
        System.out.println("=============================================================");
        System.out.println("\t\tTotal Videos Uploaded: " + size);
        System.out.println("=============================================================\n");

        while (playlistEntries.hasNext()) {
            PlaylistItem playlistItem = playlistEntries.next();
            System.out.println(" video name  = " + playlistItem.getSnippet().getTitle());
            System.out.println(" video id    = " + playlistItem.getContentDetails().getVideoId());
            System.out.println(" upload date = " + playlistItem.getSnippet().getPublishedAt());

            System.out.println(" url = " + playlistItem.getSnippet().getThumbnails().getDefault().getUrl());

            System.out.println("\n-------------------------------------------------------------\n");
        }
    }

    public MyLoader() {
        //super(context);
        //this.context = context;

        Log.d("gandhi","MyLoader()");

        List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube");
        try {
            Credential credential = authorize(scopes);

            youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(
                    "TabWithNavi").build();

            if(youtube == null)
                Log.d("gandhi","youtube null");
            else
                Log.d("gandhi","youtube not null");


            //YouTube.Channels.List channelRequest = youtube.channels().list("contentDetails");
            //channelRequest.setMine(true);

            //channelRequest.setFields("items/contentDetails,nextPageToken,pageInfo");
            //ChannelListResponse channelResult = channelRequest.execute();

        } catch (GoogleJsonResponseException e) {
            e.printStackTrace();
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());

        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    public Iterator<PlaylistItem> reqData() {
        List<PlaylistItem> playlistItemList = new ArrayList<PlaylistItem>();

        YouTube.PlaylistItems.List playlistItemRequest = null;
        try {
            playlistItemRequest = youtube.playlistItems().list("id,contentDetails,snippet");
            //playlistItemRequest.setKey("AIzaSyC4-Nq569YEgn7duuyfpd5LVsut1qffBvk");
        } catch (IOException e) {
            Log.d("gandhi","null Exce");
            e.printStackTrace();
        }

        playlistItemRequest.setPlaylistId("PL7MQjbfOyOE0nisdJuVQfuyV2GTYZze59");
        playlistItemRequest.setMaxResults(3L);

        playlistItemRequest.setFields(
                "items(contentDetails/videoId,snippet/title,snippet/publishedAt,snippet/thumbnails/default),nextPageToken,pageInfo");

        String nextToken = "";

        PlaylistItemListResponse playlistItemResult = null;
        try {
            playlistItemResult = playlistItemRequest.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        playlistItemList.addAll(playlistItemResult.getItems());

        prettyPrint(playlistItemList.size(), playlistItemList.iterator());

        return playlistItemList.iterator();

    }



}