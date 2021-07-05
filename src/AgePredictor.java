import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

public class AgePredictor {

    public AgePredictor() {

    }

    public static void main(String[] args) {
        if(args.length < 1) {
            System.out.println("Error, usage: java AgePredictor inputFile");
            return;
        }
//        String path = args[0];
//        File file = new File(path);
//
//        Properties properties = new Properties();
//        String container = properties.getProperty("");
//
//
        //Create new initial properties
        Properties properties = new Properties();
        //Open reader to read given file
        try (FileReader in = new FileReader(args[0])) {
            //load properties from file
            properties.load(in);
        } catch (IOException e) {
            System.out.println("There was an error with the provided file. Ensure the file is valid.");
            System.out.println("Error: " + e.getLocalizedMessage());
        }

        // print out what you just read
        System.out.println("Properties:");
        Enumeration<?> propertyNames = properties.propertyNames();
        while(propertyNames.hasMoreElements()) {
            String name = propertyNames.nextElement().toString();
            System.out.println(name + ": " + properties.getProperty(name));
        }
        System.out.println("---------------------------------------");

    }
}