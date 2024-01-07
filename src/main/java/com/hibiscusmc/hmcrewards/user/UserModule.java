package com.hibiscusmc.hmcrewards.user;

import com.hibiscusmc.hmcrewards.user.data.UserDatastore;
import com.hibiscusmc.hmcrewards.user.data.UserDatastoreProvider;
import com.hibiscusmc.hmcrewards.util.BukkitAbstractModule;

public final class UserModule extends BukkitAbstractModule {
    @Override
    protected void configure() {
        bind(UserDatastore.class).toProvider(UserDatastoreProvider.class).singleton();
        bind(UserManager.class).to(UserManagerImpl.class).singleton();
    }
}
