package com.kangkimleekojangcho.akgimi.sns.application.port;


import com.kangkimleekojangcho.akgimi.sns.domain.Feed;

public interface FeedCommandDbPort {
    void insert(Feed feed);
}