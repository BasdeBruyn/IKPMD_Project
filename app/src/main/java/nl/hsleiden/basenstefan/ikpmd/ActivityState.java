package nl.hsleiden.basenstefan.ikpmd;

public enum ActivityState {
    SEARCH,
    LIST,
    MOVIE_SEARCH,
    MOVIE_LIST,
    LOGIN;

    private static ActivityState state;

    public static ActivityState getState() { return state; }
    public static void setState(ActivityState state) { ActivityState.state = state; }
}
