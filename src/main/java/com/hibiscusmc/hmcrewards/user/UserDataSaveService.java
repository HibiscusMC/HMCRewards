package com.hibiscusmc.hmcrewards.user;

import com.hibiscusmc.hmcrewards.user.data.UserDatastore;
import com.hibiscusmc.hmcrewards.util.Service;
import team.unnamed.inject.Inject;

public final class UserDataSaveService implements Service {
    @Inject private UserManager userManager;
    @Inject private UserDatastore userDatastore;

    @Override
    public void start() {
    }

    @Override
    public void stop() {
        for (final var user : userManager.cached()) {
            userDatastore.save(user);
        }
        userManager.clearCache();
    }
}