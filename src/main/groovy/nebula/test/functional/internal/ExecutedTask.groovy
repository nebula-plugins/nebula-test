package nebula.test.functional.internal

interface ExecutedTask {
    String getPath()
    boolean isUpToDate()
    boolean isSkipped()
    boolean isNoSource()
}
