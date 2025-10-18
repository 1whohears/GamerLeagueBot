package com.onewho.gamerbot.data;

import com.google.gson.JsonObject;

public interface Storable {
    JsonObject getJson();
    JsonObject getBackupJson();
    void readBackup(JsonObject data);
}
