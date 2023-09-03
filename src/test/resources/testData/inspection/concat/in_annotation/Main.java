package inspection.concat.in_annotation;

class Main {

    static final String VERSION = "1.0";

    @Deprecated(since = VERSION + "-rc")
    class Api {
    }
}