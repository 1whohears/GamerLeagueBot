package com.onewho.gamerbot.data;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

public class SetDisplayData {
	
	private static int setsPerMessage = 20;
	
	private GuildData guild;
	private String date = "";
	private List<Long> messageIds = new ArrayList<Long>();
	
	public SetDisplayData(JsonObject data) {
		guild = LeagueData.getGuildDataById(data.get("guild id").getAsLong());
		date = data.get("date").getAsString();
		JsonArray midsa = data.get("message ids").getAsJsonArray();
		for (int i = 0; i < midsa.size(); ++i) messageIds.add(midsa.get(i).getAsLong());
	}
	
	public SetDisplayData(GuildData guild, String date) {
		this.guild = guild;
		this.date = date;
	}
	
	public JsonObject getJson() {
		JsonObject data = new JsonObject();
		data.addProperty("guild id", guild.getId());
		data.addProperty("date", date);
		JsonArray midsa = new JsonArray();
		for (Long id : messageIds) midsa.add(id);
		data.add("message ids", midsa);
		return data;
	}
	
	public String getDate() {
		return date;
	}
	
	public void updateMessages(TextChannel channel) {
		channel.sendMessage("__**"+date+" sets!**__").complete();
		List<SetData> sets = guild.getSetsAtWeekOfDate(date);
		int messageIndex = -1;
		for (int i = 0; i < sets.size(); ++i) {
			if (i % setsPerMessage == 0) {
				++messageIndex;
				if (messageIds.size() <= messageIndex) {
					MessageCreateData mcd = new MessageCreateBuilder()
							.addContent("loading...").build();
					messageIds.add(channel.sendMessage(mcd).complete().getIdLong());
				} 
			}
			SetData set = sets.get(i);
			String status = set.getStatus();
			MessageCreateData mcd = new MessageCreateBuilder()
					.addContent("id:"+set.getId()+" ")
					.mentionUsers(set.getP1Id())
					.addContent(" "+set.getP1score()+" ")
					.mentionUsers(set.getP2Id())
					.addContent(" "+set.getP2score()+" status: "+status)
					.build();
			MessageEditData med = new MessageEditBuilder()
					.applyCreateData(mcd).build();
			channel.editMessageById(messageIds.get(messageIndex), med).queue();
		}
	}
	
}
