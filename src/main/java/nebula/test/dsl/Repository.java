package nebula.test.dsl;

// TODO: use sealed class in java 17
// public sealed class Repository permits BuiltIn, Maven {
// }

public abstract class Repository {
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