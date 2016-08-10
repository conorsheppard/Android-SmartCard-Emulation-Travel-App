package com.example.conorsheppard.SmartTravelCardEmulator;

/**
 * Created by conorsheppard on 18/12/2015.
 */
// allows us to inform the activity, which performs a server request, when the
// server request is completed
interface GetUserCallback {
    // interfaces can only hold abstract methods
    public abstract void done(User returnedUser);

}
