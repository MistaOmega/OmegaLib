package mistaomega.lib.utility;

/**
 * Pipe connection labels to dictate whether to connect to other pipes
 */
public enum ConnectionStatus
{
    NONE,
    BLOCKED,
    CONNECTED,
    ATTACHEDTOINV;

    public boolean isConnectable()
    {
        return this == CONNECTED || this == ATTACHEDTOINV; //Are you connected to another pipe or inventory?
    }
}
