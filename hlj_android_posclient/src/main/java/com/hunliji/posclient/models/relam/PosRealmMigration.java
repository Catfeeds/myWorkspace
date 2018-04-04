package com.hunliji.posclient.models.relam;

import android.util.Log;

import com.hunliji.hljcommonlibrary.models.realm.CommonRealmMigrationUtil;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * Created by wangtao on 2017/8/15.
 */

public class PosRealmMigration implements RealmMigration {

    public static final int REALM_VERSION = CommonRealmMigrationUtil.COMMON_VERSION;

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        Log.e("RealmMigration:", "oldVersion: " + oldVersion + " newVersion: " + newVersion);
        RealmSchema schema = realm.getSchema();
        while (oldVersion < newVersion) {
            CommonRealmMigrationUtil.migrate(schema, oldVersion, newVersion);
            oldVersion++;
        }
    }
}
