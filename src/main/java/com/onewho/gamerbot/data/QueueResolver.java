package com.onewho.gamerbot.data;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;
import java.util.function.Consumer;

public interface QueueResolver {
    void resolve(QueueData queue, Guild guild, LeagueData league, List<Contestant> contestants,
                        TextChannel pairsChannel, Consumer<String> debug);
}
