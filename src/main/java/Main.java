import Service.Service;
import java.io.IOException;

public class Main {
    private static final int PORT = 8080;

    public static void main(String[] args) {
        try {
            Service service = new Service(PORT);
            service.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
