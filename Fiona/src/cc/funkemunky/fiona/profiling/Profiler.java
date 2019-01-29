package cc.funkemunky.fiona.profiling;

public interface Profiler {
    void start(String name);

    void start();

    void stop(String name, long extense);

    void stop(String name);

    void stop();
}
