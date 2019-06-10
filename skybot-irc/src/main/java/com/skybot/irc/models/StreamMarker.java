package com.skybot.irc.models;

import lombok.Data;

import java.util.Date;

@Data
public class StreamMarker {

    private Long id;

    private Date createdAt;

    private String description;

    private Integer positionSeconds;
}
