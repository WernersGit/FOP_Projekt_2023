package projekt;

public class Main {

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        startWithGUI();
        //startWithoutGUI(); //can bew used instead to run a simulation without a gui
    }

    private static void startWithGUI() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        @SuppressWarnings("deprecation") //used to avoid incompatibilities with jagr when using reflections to create a new instance.
        Projekt projekt = (Projekt) Class.forName("projekt.GUIProjektImpl").newInstance();
        projekt.start();
    }

    @SuppressWarnings("unused")
    private static void startWithoutGUI() {
        Projekt projekt = new BasicProjektImpl();
        projekt.start();
    }

}
