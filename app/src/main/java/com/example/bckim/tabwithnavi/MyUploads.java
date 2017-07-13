package com.example.bckim.tabwithnavi;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.testing.auth.oauth2.MockTokenServerTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.common.collect.Lists;


public class MyUploads {

    /** Global instance of the HTTP transport. */
    private final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    /** Global instance of the JSON factory. */
    private final JsonFactory JSON_FACTORY = new JacksonFactory();

    /** Global instance of YouTube object to make all API requests. */
    private YouTube youtube;

    private GoogleClientSecrets build() {
        GoogleClientSecrets.Details details = new GoogleClientSecrets.Details();
        details.setClientId("17725036663-opg03jhbtvhtpji2nent4d4nf2etj76u.apps.googleusercontent.com");
        details.setClientSecret("t0x2NCF6TT79Y988EAC6_HDh");
        GoogleClientSecrets googleClientSecrets = new GoogleClientSecrets();
        googleClientSecrets.setInstalled(details);
        return googleClientSecrets;
    }

    private Credential authorize(List<String> scopes) throws Exception {

        /*
        File file = new File("client_secrets.json");
        InputStream is = null;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Load client secrets.
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JSON_FACTORY,
                //new InputStreamReader(MyUploads.class.getResourceAsStream("/client_secrets.json")));
                new InputStreamReader(is));
        */
        GoogleClientSecrets clientSecrets = build();

        // Checks that the defaults have been replaced (Default = "Enter X here").
        if (clientSecrets.getDetails().getClientId().startsWith("Enter")
                || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
            System.out.println(
                    "Enter Client ID and Secret from https://code.google.com/apis/console/?api=youtube"
                            + "into youtube-cmdline-myuploads-sample/src/main/resources/client_secrets.json");
            System.exit(1);
        }

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, scopes).setClientId("17725036663-opg03jhbtvhtpji2nent4d4nf2etj76u.apps.googleusercontent.com")
                .build();

        // Build the local server and bind it to port 9000
        LocalServerReceiver localReceiver = new LocalServerReceiver.Builder().setPort(8080).build();

        // Authorize.
        return new AuthorizationCodeInstalledApp(flow, localReceiver).authorize("user");
    }

    public static void main(String[] args) {
        MyUploads mu = new MyUploads();
        Iterator<PlaylistItem> playlistEntries =  mu.reqData();

        while (playlistEntries.hasNext()) {
            PlaylistItem playlistItem = playlistEntries.next();
            System.out.println(playlistItem.getSnippet().getTitle());
        }
    }

    //public static void main(String[] args) {
    private void subMain(){
        // Scope required to upload to YouTube.
        List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube");

        try {
            // Authorization.
            Credential credential = authorize(scopes);

            // YouTube object used to make all API requests.
            youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(
                    "youtube-cmdline-myuploads-sample").build();

            YouTube.Channels.List channelRequest = youtube.channels().list("contentDetails");
            channelRequest.setMine(true);
      /*
       * Limits the results to only the data we needo which makes things more efficient.
       */
            channelRequest.setFields("items/contentDetails,nextPageToken,pageInfo");
            ChannelListResponse channelResult = channelRequest.execute();

      /*
       * Gets the list of channels associated with the user. This sample only pulls the uploaded
       * videos for the first channel (default channel for user).
       */
            List<Channel> channelsList = channelResult.getItems();

            if (channelsList != null) {
                // Gets user's default channel id (first channel in list).
                String uploadPlaylistId =
                        channelsList.get(0).getContentDetails().getRelatedPlaylists().getUploads();

                // List to store all PlaylistItem items associated with the uploadPlaylistId.
                List<PlaylistItem> playlistItemList = new ArrayList<PlaylistItem>();

                YouTube.PlaylistItems.List playlistItemRequest =
                        youtube.playlistItems().list("id,contentDetails,snippet");

                playlistItemRequest.setPlaylistId("PL7MQjbfOyOE0nisdJuVQfuyV2GTYZze59");
                playlistItemRequest.setMaxResults(3L);

                // This limits the results to only the data we need and makes things more efficient.
                playlistItemRequest.setFields(
                        //"items(contentDetails/videoId,snippet/title,snippet/publishedAt),nextPageToken,pageInfo");
                        "items(contentDetails/videoId,snippet/title,snippet/publishedAt,snippet/thumbnails/default),nextPageToken,pageInfo");

                String nextToken = "";

                // Loops over all search page results returned for the uploadPlaylistId.
        /*
        do {
          long startTime = System.currentTimeMillis();

          playlistItemRequest.setPageToken(nextToken);
          PlaylistItemListResponse playlistItemResult = playlistItemRequest.execute();

          playlistItemList.addAll(playlistItemResult.getItems());

          nextToken = playlistItemResult.getNextPageToken();

          long endTime = System.currentTimeMillis();
          long elapsedTime = endTime - startTime;
          System.out.println("Total elapsed time = " + elapsedTime);

        } while (nextToken != null);
        */

                PlaylistItemListResponse playlistItemResult = playlistItemRequest.execute();
                playlistItemList.addAll(playlistItemResult.getItems());

                //nextToken = playlistItemResult.getNextPageToken();
                //playlistItemRequest.setPageToken(nextToken);
                //playlistItemResult = playlistItemRequest.execute();
                //playlistItemList.addAll(playlistItemResult.getItems());

                prettyPrint(playlistItemList.size(), playlistItemList.iterator());
            }

        } catch (GoogleJsonResponseException e) {
            e.printStackTrace();
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /*
     * Method that prints all the PlaylistItems in an Iterator.
     *
     * @param size size of list
     *
     * @param iterator of Playlist Items from uploaded Playlist
     */
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

    public MyUploads() {
        List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube");
        try {
            Credential credential = authorize(scopes);

            youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(
                    "youtube-cmdline-myuploads-sample").build();

            YouTube.Channels.List channelRequest = youtube.channels().list("contentDetails");
            channelRequest.setMine(true);

            channelRequest.setFields("items/contentDetails,nextPageToken,pageInfo");
            ChannelListResponse channelResult = channelRequest.execute();

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
        } catch (IOException e) {
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