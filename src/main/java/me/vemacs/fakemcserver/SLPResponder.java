package me.vemacs.fakemcserver;

import me.vemacs.fakemcserver.data.StatusResponse;

public interface SLPResponder {

    /**
     *
     * @param protocol the protocol of the remote client
     * @return response containing online players and other server information
     */
    public StatusResponse getStatusResponse(int protocol);
}
