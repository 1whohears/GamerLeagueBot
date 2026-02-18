package com.onewho.gamerbot.data;

public enum QueueType {
    BIG_TEAM(QueueData::createTeamSet),
    MULTI_SOLO(QueueData::createMultipleSoloSets);
    final QueueResolver resolver;
    QueueType(QueueResolver resolver) {
        this.resolver = resolver;
    }
}
