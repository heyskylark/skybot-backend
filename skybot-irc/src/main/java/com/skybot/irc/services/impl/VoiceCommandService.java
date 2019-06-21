package com.skybot.irc.services.impl;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.common.exception.NotFoundException;
import com.github.twitch4j.helix.domain.CreateClipList;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.skybot.irc.config.SkyBotProperties;
import com.skybot.irc.models.UserPrincipal;
import com.skybot.irc.models.spotify.SpotifyArtistSimplified;
import com.skybot.irc.models.spotify.SpotifyCurrentPlaybackDevice;
import com.skybot.irc.models.spotify.SpotifyCurrentlyPlaying;
import com.skybot.irc.services.ISpotifyClientService;
import com.skybot.irc.services.ITwitchHelixService;
import com.skybot.irc.services.IVoiceCommandService;
import com.skybot.irc.utility.VoiceCommandKeys;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class VoiceCommandService implements IVoiceCommandService {

    private static final double COMMAND_SIMILARITY_THRESHOLD = 0.64;

    private final UserPrincipal userPrincipal;
    private final ITwitchHelixService twitchHelixService;
    private final TwitchClient twitchClient;
    private final ISpotifyClientService spotifyClientService;
    private final SkyBotProperties skyBotProperties;

    @Autowired
    public VoiceCommandService(ITwitchHelixService twitchHelixService,
                               TwitchClient twitchClient,
                               ISpotifyClientService spotifyClientService,
                               UserPrincipal userPrincipal,
                               SkyBotProperties skyBotProperties) {
        this.twitchHelixService = twitchHelixService;
        this.userPrincipal = userPrincipal;
        this.twitchClient = twitchClient;
        this.spotifyClientService = spotifyClientService;
        this.skyBotProperties = skyBotProperties;
    }

    @Override
    public void findCommand(String command) {
        Map<String, Double> viableCommands = new HashMap<>();

        if(command.isEmpty()) {
            log.warn("Voice command empty");
            // Do I didn't get that. response
        } else {
            skyBotProperties.getCommands().forEach((commandValue, commandList) -> commandList.forEach(commandText -> {
                // https://stackoverflow.com/questions/955110/similarity-string-comparison-in-javas
                String longer = command, shorter = commandText;
                if (command.length() < commandText.length()) { // longer should always have greater length
                    longer = commandText;
                    shorter = command;
                }

                int longerLength = longer.length();
                LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
                double similarity = (longerLength -
                        levenshteinDistance.apply(longer.trim().toLowerCase(), shorter.trim().toLowerCase())) /
                        (double) longerLength;
                if(similarity >= COMMAND_SIMILARITY_THRESHOLD) {
                    if(!viableCommands.containsKey(commandValue) || viableCommands.get(commandValue) < similarity) {
                        viableCommands.put(commandValue, similarity);
                        log.info("Threshold met for [{}] and [{}]: Threshold: {}", command, commandText, similarity);
                    }
                }
            }));
        }

        if(viableCommands.isEmpty()) {
            log.warn("Bot command not found for voice command [{}]", command);
            // Do I didn't get that. response
        } else {
            String bestCommand = viableCommands.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .get()
                    .getKey();

            log.info("Using command [{}] winning with threshold [{}]", bestCommand, viableCommands.get(bestCommand));

            mapCommand(VoiceCommandKeys.resolve(bestCommand));
        }
    }

    @Override
    public void createClipSelfAndShare() {
        try {
            CreateClipList createClipList = twitchHelixService.createClipSelf(false);

            if (!createClipList.getData().isEmpty()) {
                createClipList.getData().forEach(clip -> {
                    int indexOfEdit = clip.getEditUrl().lastIndexOf("/edit");
                    String noEditClipUrl = clip.getEditUrl().substring(0, indexOfEdit);
                    twitchClient.getChat().sendMessage(userPrincipal.getLogin(), noEditClipUrl);

                    log.debug("Sent clip url [{}] to channel [{}] chat", noEditClipUrl, userPrincipal.getUserName());
                });
                // Send confirmation and voice audio through websocket to live client
            } else {
                log.error("There was a problem creating the clip, no clips were made.");
                // Websocket "Problem Creating clip"
            }
        } catch(HystrixRuntimeException ex) {
            log.error("ex {}", ex);
            log.error("Error creating clip: {}", ex.getFailureType());
            // Websocket "Problem Creating clip"
        } catch(NotFoundException ex) {
            log.error("ex {}", ex);
        }
    }

    @Override
    public void getCurrentlyPlayingSongAndShare() {
        //TODO use getCurrentlyPlayingSongAndShare as a chat command also...should I move out of this service?
        // Maybe only move related parts out, as this method also will have the bot voice respond on the client
        SpotifyCurrentlyPlaying currentlyPlaying = spotifyClientService.getCurrentSong();
        if(currentlyPlaying != null) {
            StringBuilder twitchSongInfo = new StringBuilder();
            if (currentlyPlaying.getItem() != null) {
                twitchSongInfo.append(currentlyPlaying.getItem().getAlbum().getName())
                        .append(" - ")
                        .append(currentlyPlaying.getItem().getName())
                        .append(" by ");

                List<SpotifyArtistSimplified> artists = currentlyPlaying.getItem().getArtists();
                int numOfArtist = artists.size();
                for (int artistIndex = 0; artistIndex < numOfArtist; artistIndex++) {
                    if (artistIndex < numOfArtist - 1) {
                        twitchSongInfo.append(artists.get(artistIndex).getName()).append(", ");
                    } else {
                        if (numOfArtist > 1) {
                            twitchSongInfo.append("and ");
                        }
                        twitchSongInfo.append(artists.get(artistIndex).getName());
                    }
                }

                TwitchChat twitchChat = twitchClient.getChat();
                twitchChat.sendMessage(userPrincipal.getLogin(), twitchSongInfo.toString());
                twitchChat.sendMessage(userPrincipal.getLogin(),
                        currentlyPlaying.getItem().getExternalUrls().get("spotify").toString());
            }
        }
    }

    private void mapCommand(VoiceCommandKeys command) {
        // For spotify: need to verify if logged in, also need to verify if endpoint was successful
        // Should that be done in SpotifyClientServiceImpl or here???...
        switch (command) {
            case CLIP:
                createClipSelfAndShare();
                break;
            case START_POLL:
                log.info("Starting a poll");
                break;
            case END_POLL:
                log.info("Ending the poll");
                break;
            case SONG_INFO:
                getCurrentlyPlayingSongAndShare();
                break;
            case NEXT_SONG:
                spotifyClientService.nextSong();
                break;
            case PREV_SONG:
                spotifyClientService.previousSong();
                break;
            case PLAY_SONG:
                SpotifyCurrentPlaybackDevice currentPlaybackDevicePlay = spotifyClientService.getCurrentlyPlayingDevice();
                spotifyClientService.playSong(currentPlaybackDevicePlay.getDevice().getId(), null);
                break;
            case PAUSE_SONG:
                SpotifyCurrentPlaybackDevice currentPlaybackDevice = spotifyClientService.getCurrentlyPlayingDevice();
                spotifyClientService.pauseSong(currentPlaybackDevice.getDevice().getId());
                break;
            default:
                log.info("I didn't get that.");
                break;
        }
    }
}
