package commons.lib.extra.server.http.handler.testLib;

public class Body {

	@JsonField
    private final String toto;

    public Body(String toto) {
        this.toto = toto;
    }

    public String getToto() {
        return toto;
    }

    @Override
    public String toString() {
        return "Body{" +
                "toto='" + toto + '\'' +
                '}';
    }
}
