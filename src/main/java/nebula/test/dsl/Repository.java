package nebula.test.dsl;

public sealed class Repository permits BuiltIn, Maven {
}

final class BuiltIn extends Repository {
    public BuiltIn(String functionName) {
        this.functionName = functionName;
    }

    final String functionName;
}

final class Maven extends Repository {
    public Maven(String url) {
        this.url = url;
    }

    final String url;
}