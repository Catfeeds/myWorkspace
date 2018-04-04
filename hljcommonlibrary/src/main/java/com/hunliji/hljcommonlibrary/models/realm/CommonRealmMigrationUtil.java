package com.hunliji.hljcommonlibrary.models.realm;

import android.util.Log;

import java.util.Date;

import io.realm.FieldAttribute;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

/**
 * Created by wangtao on 2017/8/15.
 */

public class CommonRealmMigrationUtil {

    public static final int COMMON_VERSION = 7;

    public static void migrate(RealmSchema schema, long oldVersion, long newVersion) {
        Log.e("RealmMigration:", "oldVersion: " + oldVersion + " newVersion: " + newVersion);
        if (oldVersion == 0) {
            RealmObjectSchema wsChatSchema = schema.get("WSChat");
            if (!wsChatSchema.hasField("extStr")) {
                wsChatSchema.addField("extStr", String.class);
            }
            if (!wsChatSchema.hasField("source")) {
                wsChatSchema.addField("source", int.class);
            }
        } else if (oldVersion == 1) {
            schema.create("ChannelStick")
                    .addField("channel", String.class, FieldAttribute.PRIMARY_KEY)
                    .addField("userId", long.class)
                    .addField("stickAt", Date.class);
            schema.get("WSChatAuthor")
                    .addField("remarkName", String.class);
        } else if (oldVersion == 2) {
            if (schema.get("HttpLogBlock") == null) {
                schema.create("HttpLogBlock")
                        .addField("id", long.class)
                        .addField("message", String.class);
            }
        } else if (oldVersion == 3) {
            if (schema.get("ChatDraft") == null) {
                schema.create("ChatDraft")
                        .addField("key", String.class, FieldAttribute.PRIMARY_KEY)
                        .addField("content", String.class)
                        .addField("date", Date.class);
            }
        } else if (oldVersion == 5) {
            if (schema.get("WSCity") == null) {
                schema.create("WSCity")
                        .addField("id", long.class)
                        .addField("name", String.class);
            }
            if (!schema.get("WSChatAuthor")
                    .hasField("city")) {
                schema.get("WSChatAuthor")
                        .addRealmObjectField("city", schema.get("WSCity"));
            }
            if (!schema.get("WSChat")
                    .hasField("wsChannelKey")) {
                schema.get("WSChat")
                        .addField("wsChannelKey", String.class);
            }
            if (!schema.get("WSChat")
                    .hasIndex("kind")) {
                schema.get("WSChat")
                        .addIndex("kind");
            }
            if (!schema.get("WSChat")
                    .hasIndex("userId")) {
                schema.get("WSChat")
                        .addIndex("userId");
            }
            if (!schema.get("WSChat")
                    .hasIndex("isNew")) {
                schema.get("WSChat")
                        .addIndex("isNew");
            }
            if (schema.get("WSChannel") == null) {
                schema.create("WSChannel")
                        .addField("key",
                                String.class,
                                FieldAttribute.INDEXED,
                                FieldAttribute.PRIMARY_KEY)
                        .addField("userId", long.class, FieldAttribute.INDEXED)
                        .addRealmObjectField("chatUser", schema.get("WSChatAuthor"))
                        .addRealmObjectField("lastMessage", schema.get("WSChat"))
                        .addField("unreadMessageCount", int.class)
                        .addField("unreadTrackCount", int.class)
                        .addRealmObjectField("stick", schema.get("ChannelStick"))
                        .addField("time", Date.class)
                        .addRealmObjectField("draft", schema.get("ChatDraft"));
            }
        } else if (oldVersion == 6) {
            if (schema.get("ExtendMember") == null) {
                schema.create("ExtendMember")
                        .addField("userId", long.class)
                        .addField("hljMemberPrivilege", int.class)
                        .addField("specialty", int.class);
            }
            if (!schema.get("WSChatAuthor")
                    .hasField("extend")) {
                schema.get("WSChatAuthor")
                        .addRealmObjectField("extend", schema.get("ExtendMember"));
            }
        }
    }
}
