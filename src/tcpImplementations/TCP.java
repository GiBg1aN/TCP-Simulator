package tcpImplementations;

public interface TCP {
    void increaseCongestionWindow();

    void decreaseCongestionWindow();

    int size();
}
