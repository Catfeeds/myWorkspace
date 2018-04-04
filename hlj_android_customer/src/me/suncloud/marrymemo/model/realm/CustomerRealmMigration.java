package me.suncloud.marrymemo.model.realm;

import android.util.Log;

import com.hunliji.hljcommonlibrary.models.realm.CommonRealmMigrationUtil;


import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * Created by wangtao on 2017/8/15.
 */

public class CustomerRealmMigration implements RealmMigration {

    public static final int REALM_VERSION = CommonRealmMigrationUtil.COMMON_VERSION+1;

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        Log.e("RealmMigration:", "oldVersion: " + oldVersion + " newVersion: " + newVersion);
        RealmSchema schema = realm.getSchema();
        while (oldVersion < newVersion) {
            if (oldVersion < 6) {
                CommonRealmMigrationUtil.migrate(schema, oldVersion, newVersion);
            } else if (oldVersion == 6) {
                schema.create("NewHistoryKeyWord")
                        .addField("id", long.class)
                        .addField("category", String.class)
                        .addField("keyword", String.class)
                        .addField("merchantId", long.class);
            }else {
                CommonRealmMigrationUtil.migrate(schema, oldVersion-1, newVersion-1);
            }
            oldVersion++;
        }
    }
}
