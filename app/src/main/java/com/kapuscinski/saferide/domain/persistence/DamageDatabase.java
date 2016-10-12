/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.domain.persistence;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = DamageDatabase.DATABASE_NAME, version = DamageDatabase.DATABASE_VERSION)
public class DamageDatabase {

    public static final String DATABASE_NAME = "damage_db";
    public static final int DATABASE_VERSION = 1;
}
